package br.com.medflow.aplicacao.prontuario;

import com.medflow.dominio.prontuario.StatusProntuario;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO para exibir todos os detalhes de um prontuário, incluindo histórico clínico e atualizações.
 */
public class ProntuarioDetalhes {
    private final String id;
    private final String pacienteId;
    private final String atendimentoId;
    private final StatusProntuario status;
    private final LocalDateTime dataHoraCriacao;
    private final String profissionalResponsavel;
    private final String observacoesIniciais;
    private final List<HistoricoItemDetalhes> historicoClinico;
    private final List<AtualizacaoDetalhes> historicoAtualizacoes;

    public ProntuarioDetalhes(
        String id, 
        String pacienteId, 
        String atendimentoId,
        StatusProntuario status, 
        LocalDateTime dataHoraCriacao,
        String profissionalResponsavel,
        String observacoesIniciais,
        List<HistoricoItemDetalhes> historicoClinico,
        List<AtualizacaoDetalhes> historicoAtualizacoes) {
        
        this.id = id;
        this.pacienteId = pacienteId;
        this.atendimentoId = atendimentoId;
        this.status = status;
        this.dataHoraCriacao = dataHoraCriacao;
        this.profissionalResponsavel = profissionalResponsavel;
        this.observacoesIniciais = observacoesIniciais;
        this.historicoClinico = historicoClinico;
        this.historicoAtualizacoes = historicoAtualizacoes;
    }

    /** Detalhes de um item do histórico clínico. */
    public record HistoricoItemDetalhes(
        String id,
        String sintomas,
        String diagnostico,
        String conduta,
        LocalDateTime dataHoraRegistro,
        String profissionalResponsavel,
        List<String> anexosReferenciados) {}

    /** Detalhes de uma atualização do prontuário. */
    public record AtualizacaoDetalhes(
        String id,
        String atendimentoId,
        LocalDateTime dataHoraAtualizacao,
        String profissionalResponsavel,
        String observacoes,
        StatusProntuario status) {}
        
    // Getters
    public String getId() { return id; }
    public String getPacienteId() { return pacienteId; }
    public String getAtendimentoId() { return atendimentoId; }
    public StatusProntuario getStatus() { return status; }
    public LocalDateTime getDataHoraCriacao() { return dataHoraCriacao; }
    public String getProfissionalResponsavel() { return profissionalResponsavel; }
    public String getObservacoesIniciais() { return observacoesIniciais; }
    public List<HistoricoItemDetalhes> getHistoricoClinico() { return historicoClinico; }
    public List<AtualizacaoDetalhes> getHistoricoAtualizacoes() { return historicoAtualizacoes; }
}
