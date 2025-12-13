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
        // Adicionado .longValue() para corrigir o erro de tipo
        return jpaRepository.existsByMedicoId(medicoId.longValue());
    }

    // --- Métodos Auxiliares de Mapeamento (Converter) ---

    private ExameJpa paraJpa(Exame dominio) {
        if (dominio == null) return null;

        ExameJpa exameJpa = mapeador.map(dominio, ExameJpa.class);

        // Mapear histórico: converter cada entrada de domínio em JPA e associar ao exameJpa
        var historicoDominio = dominio.getHistorico();
        if (historicoDominio != null) {
            var historicoJpa = historicoDominio.stream()
                .map(h -> {
                    var he = mapeador.map(h, HistoricoExameJpa.class);
                    he.setExame(exameJpa);
                    return he;
                })
                .collect(Collectors.toList());
            exameJpa.setHistorico(historicoJpa);
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