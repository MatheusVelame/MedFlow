package br.com.medflow.aplicacao.prontuario;

import com.medflow.dominio.prontuario.StatusProntuario;
import java.time.LocalDateTime;

/**
 * DTO para exibir um item do histórico de atualizações em respostas.
 */
public class AtualizacaoItemResponse {
    private final String id;
    private final String atendimentoId;
    private final LocalDateTime dataHoraAtualizacao;
    private final String profissionalResponsavel;
    private final String observacoes;
    private final StatusProntuario status;
    private final String prontuarioId; // Adicionado para identificar o prontuário
    private final String pacienteId; // Adicionado para identificar o paciente

    public AtualizacaoItemResponse(String id, String atendimentoId,
                                  LocalDateTime dataHoraAtualizacao, String profissionalResponsavel,
                                  String observacoes, StatusProntuario status) {
        this(id, atendimentoId, dataHoraAtualizacao, profissionalResponsavel, observacoes, status, null, null);
    }

    public AtualizacaoItemResponse(String id, String atendimentoId,
                                  LocalDateTime dataHoraAtualizacao, String profissionalResponsavel,
                                  String observacoes, StatusProntuario status, String prontuarioId, String pacienteId) {
        this.id = id;
        this.atendimentoId = atendimentoId;
        this.dataHoraAtualizacao = dataHoraAtualizacao;
        this.profissionalResponsavel = profissionalResponsavel;
        this.observacoes = observacoes;
        this.status = status;
        this.prontuarioId = prontuarioId;
        this.pacienteId = pacienteId;
    }

    // Getters
    public String getId() { return id; }
    public String getAtendimentoId() { return atendimentoId; }
    public LocalDateTime getDataHoraAtualizacao() { return dataHoraAtualizacao; }
    public String getProfissionalResponsavel() { return profissionalResponsavel; }
    public String getObservacoes() { return observacoes; }
    public StatusProntuario getStatus() { return status; }
    public String getProntuarioId() { return prontuarioId; }
    public String getPacienteId() { return pacienteId; }
}


