package br.com.medflow.infraestrutura.persistencia.jpa.atendimento;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.List;

import org.springframework.stereotype.Repository;

import br.com.medflow.dominio.atendimento.exames.Exame;
import br.com.medflow.dominio.atendimento.exames.ExameId;
import br.com.medflow.dominio.atendimento.exames.ExameRepositorio;
import br.com.medflow.dominio.atendimento.exames.UsuarioResponsavelId;
import br.com.medflow.infraestrutura.persistencia.jpa.JpaMapeador;

@Repository
public class ExameRepositorioJpaImpl implements ExameRepositorio {

    private final ExameJpaRepository jpaRepository;
    private final JpaMapeador mapeador;

    public ExameRepositorioJpaImpl(ExameJpaRepository jpaRepository, JpaMapeador mapeador) {
        this.jpaRepository = jpaRepository;
        this.mapeador = mapeador;
    }

    @Override
    public Optional<Exame> obterPorId(ExameId id) {
        if (id == null) return Optional.empty();
        return jpaRepository.findById(id.getValor()).map(this::paraDominio);
    }

    @Override
    public Exame salvar(Exame exame) {
        ExameJpa entidadeJpa = paraJpa(exame);
        entidadeJpa = jpaRepository.save(entidadeJpa);
        return paraDominio(entidadeJpa);
    }

    @Override
    public void excluir(Exame exame) {
        // Exclusão física conforme contrato do repositório
        if (exame.getId() != null) {
            jpaRepository.deleteById(exame.getId().getValor());
        }
    }

    @Override
    public Optional<Exame> obterAgendamentoConflitante(Long pacienteId, java.time.LocalDateTime dataHora, ExameId idExcluido) {
        Long idIgnorar = (idExcluido != null) ? idExcluido.getValor() : null;
        var conflitos = jpaRepository.encontrarConflitos(pacienteId, dataHora, idIgnorar);
        return conflitos.stream().findFirst().map(this::paraDominio);
    }
    
    @Override
    public boolean existsByPacienteId(Long pacienteId) {
        return jpaRepository.existsByPacienteId(pacienteId);
    }

    @Override
    public List<Exame> listarTodos() {
        return jpaRepository.findAll().stream().map(this::paraDominio).collect(Collectors.toList());
    }

    @Override
    public boolean existePorMedicoId(Integer medicoId) {
        if (medicoId == null) return false;
        return jpaRepository.existsByMedicoId(medicoId.longValue());
    }

    // --- Métodos Auxiliares de Mapeamento (Converter) ---

    private ExameJpa paraJpa(Exame dominio) {
        if (dominio == null) return null;

        // Construção manual do JPA a partir do domínio para evitar conversões ambíguas do ModelMapper
        Long id = (dominio.getId() != null) ? dominio.getId().getValor() : null;
        ExameJpa exameJpa = new ExameJpa(
            id,
            dominio.getPacienteId(),
            dominio.getMedicoId(),
            dominio.getTipoExame(),
            dominio.getDataHora(),
            dominio.getStatus(),
            null // responsavelId será calculado a partir do histórico abaixo
        );

        // Mapear histórico: converter cada entrada de domínio em JPA e associar ao exameJpa
        var historicoDominio = dominio.getHistorico();
        if (historicoDominio != null && !historicoDominio.isEmpty()) {
            var historicoJpa = historicoDominio.stream()
                .map(h -> {
                    var he = new HistoricoExameJpa();
                    he.setAcao(h.getAcao());
                    he.setDescricao(h.getDescricao());
                    he.setDataHora(h.getDataHora());
                    if (h.getUsuario() != null && h.getUsuario().getValor() != null) {
                        he.setResponsavelId(h.getUsuario().getValor());
                    }
                    he.setExame(exameJpa);
                    return he;
                })
                .collect(Collectors.toList());
            exameJpa.setHistorico(historicoJpa);

            // Extrair responsavelId do último histórico, se presente
            var ultima = historicoDominio.get(historicoDominio.size() - 1);
            if (ultima != null && ultima.getUsuario() != null) {
                exameJpa.setResponsavelId(ultima.getUsuario().getValor());
            }
        }

        return exameJpa;
    }

    private Exame paraDominio(ExameJpa jpa) {
        if (jpa == null) return null;

        // Mapear historico JPA -> domínio
        var historicoDominio = jpa.getHistorico().stream()
            .map(h -> mapeador.map(h, br.com.medflow.dominio.atendimento.exames.HistoricoEntrada.class))
            .collect(Collectors.toList());

        ExameId id = (jpa.getId() != null) ? new ExameId(jpa.getId()) : null;

        return new Exame(
            id,
            jpa.getPacienteId(),
            jpa.getMedicoId(),
            jpa.getTipoExame(),
            jpa.getDataHora(),
            jpa.getStatus(),
            historicoDominio,
            false,
            false,
            null
        );
    }
}