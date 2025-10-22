package com.medflow.dominio.prontuario;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Representa um registro imutável de atualização de um prontuário.
 * Cada atualização está vinculada a um atendimento específico.
 */
public class HistoricoAtualizacao {
    private final String id;
    private final String prontuarioId;
    private final String atendimentoId;
    private final LocalDateTime dataHoraAtualizacao;
    private final String profissionalResponsavel;
    private final String observacoes;
    private final StatusProntuario status;

    public HistoricoAtualizacao(String id, String prontuarioId, String atendimentoId, 
                               LocalDateTime dataHoraAtualizacao, String profissionalResponsavel, 
                               String observacoes, StatusProntuario status) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("ID da atualização é obrigatório.");
        }
        if (prontuarioId == null || prontuarioId.trim().isEmpty()) {
            throw new IllegalArgumentException("ID do prontuário é obrigatório.");
        }
        if (atendimentoId == null || atendimentoId.trim().isEmpty()) {
            throw new IllegalArgumentException("ID do atendimento é obrigatório.");
        }
        if (dataHoraAtualizacao == null) {
            throw new IllegalArgumentException("Data e hora da atualização são obrigatórias.");
        }
        if (profissionalResponsavel == null || profissionalResponsavel.trim().isEmpty()) {
            throw new IllegalArgumentException("Profissional responsável é obrigatório.");
        }
        if (observacoes == null || observacoes.trim().isEmpty()) {
            throw new IllegalArgumentException("Observações são obrigatórias.");
        }
        if (status == null) {
            throw new IllegalArgumentException("Status é obrigatório.");
        }

        this.id = id;
        this.prontuarioId = prontuarioId;
        this.atendimentoId = atendimentoId;
        this.dataHoraAtualizacao = dataHoraAtualizacao;
        this.profissionalResponsavel = profissionalResponsavel;
        this.observacoes = observacoes;
        this.status = status;
    }

    // Getters
    public String getId() { return id; }
    public String getProntuarioId() { return prontuarioId; }
    public String getAtendimentoId() { return atendimentoId; }
    public LocalDateTime getDataHoraAtualizacao() { return dataHoraAtualizacao; }
    public String getProfissionalResponsavel() { return profissionalResponsavel; }
    public String getObservacoes() { return observacoes; }
    public StatusProntuario getStatus() { return status; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        HistoricoAtualizacao that = (HistoricoAtualizacao) obj;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "HistoricoAtualizacao{" +
                "id='" + id + '\'' +
                ", prontuarioId='" + prontuarioId + '\'' +
                ", atendimentoId='" + atendimentoId + '\'' +
                ", dataHoraAtualizacao=" + dataHoraAtualizacao +
                ", profissionalResponsavel='" + profissionalResponsavel + '\'' +
                '}';
    }
}
