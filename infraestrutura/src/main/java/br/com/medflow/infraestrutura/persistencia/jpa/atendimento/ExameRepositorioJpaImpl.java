package br.com.medflow.infraestrutura.persistencia.jpa.atendimento;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import br.com.medflow.dominio.atendimento.exames.Exame;
import br.com.medflow.dominio.atendimento.exames.ExameId;
import br.com.medflow.dominio.atendimento.exames.ExameRepositorio;
import br.com.medflow.dominio.atendimento.exames.UsuarioResponsavelId;

@Repository
public class ExameRepositorioJpaImpl implements ExameRepositorio {

    private final ExameJpaRepository jpaRepository;

    public ExameRepositorioJpaImpl(ExameJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
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

    // --- Métodos Auxiliares de Mapeamento (Converter) ---

    private ExameJpa paraJpa(Exame dominio) {
        if (dominio == null) return null;

        Long id = (dominio.getId() != null) ? dominio.getId().getValor() : null;

        // Tentativa de extrair um UsuarioResponsavelId do histórico mais recente (se existir)
        Long responsavel = null;
        try {
            if (dominio.getHistorico() != null && !dominio.getHistorico().isEmpty()) {
                var ultima = dominio.getHistorico().get(dominio.getHistorico().size() - 1);
                if (ultima != null && ultima.getUsuario() != null) {
                    responsavel = ultima.getUsuario().getValor();
                }
            }
        } catch (Exception e) {
            // Se não for possível extrair, manter responsavel como null
        }

        return new ExameJpa(
            id,
            dominio.getPacienteId(),
            dominio.getMedicoId(),
            dominio.getTipoExame(),
            dominio.getDataHora(),
            dominio.getStatus(),
            responsavel
        );
    }

    private Exame paraDominio(ExameJpa jpa) {
        if (jpa == null) return null;

        // Reconstruir o histórico como vazio aqui; se houver uma tabela de histórico separada, o repositório deve montar isso.
        java.util.List<br.com.medflow.dominio.atendimento.exames.HistoricoEntrada> historico = java.util.Collections.emptyList();

        ExameId id = (jpa.getId() != null) ? new ExameId(jpa.getId()) : null;
        UsuarioResponsavelId responsavelId = (jpa.getResponsavelId() != null) ? new UsuarioResponsavelId(jpa.getResponsavelId()) : null;

        return new Exame(
            id,
            jpa.getPacienteId(),
            jpa.getMedicoId(),
            jpa.getTipoExame(),
            jpa.getDataHora(),
            jpa.getStatus(),
            historico,
            false,
            false,
            null
        );
    }
}