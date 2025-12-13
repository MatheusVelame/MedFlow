package br.com.medflow.aplicacao.prontuario.dto.response;

import com.medflow.dominio.prontuario.StatusProntuario;
import java.time.LocalDateTime;

/**
 * DTO de response para prontuário.
 */
public class ProntuarioResponse {
    private final String id;
    private final String pacienteId;
    private final String atendimentoId;
    private final StatusProntuario status;
    private final LocalDateTime dataHoraCriacao;
    private final String profissionalResponsavel;
    private final String observacoesIniciais;

    public ProntuarioResponse(String id, String pacienteId, String atendimentoId,
                             StatusProntuario status, LocalDateTime dataHoraCriacao,
                             String profissionalResponsavel, String observacoesIniciais) {
        this.id = id;
        this.pacienteId = pacienteId;
        this.atendimentoId = atendimentoId;
        this.status = status;
        this.dataHoraCriacao = dataHoraCriacao;
        this.profissionalResponsavel = profissionalResponsavel;
        this.observacoesIniciais = observacoesIniciais;
    }

    // Getters
    public String getId() { return id; }
    public String getPacienteId() { return pacienteId; }
    public String getAtendimentoId() { return atendimentoId; }
    public StatusProntuario getStatus() { return status; }
    public LocalDateTime getDataHoraCriacao() { return dataHoraCriacao; }
    public String getProfissionalResponsavel() { return profissionalResponsavel; }
    public String getObservacoesIniciais() { return observacoesIniciais; }
}
