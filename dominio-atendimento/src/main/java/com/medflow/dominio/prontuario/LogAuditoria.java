package com.medflow.dominio.prontuario;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Representa um registro imutável de auditoria para ações administrativas em prontuários.
 */
public class LogAuditoria {
    private final String id;
    private final String objetoId;
    private final String acao;
    private final String usuarioResponsavel;
    private final LocalDateTime dataHoraAcao;
    private final String motivo;
    private final String detalhesAdicionais;

    public LogAuditoria(String id, String objetoId, String acao, String usuarioResponsavel, 
                       LocalDateTime dataHoraAcao, String motivo, String detalhesAdicionais) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("ID do log é obrigatório.");
        }
        if (objetoId == null || objetoId.trim().isEmpty()) {
            throw new IllegalArgumentException("ID do objeto auditado é obrigatório.");
        }
        if (acao == null || acao.trim().isEmpty()) {
            throw new IllegalArgumentException("Ação é obrigatória.");
        }
        if (usuarioResponsavel == null || usuarioResponsavel.trim().isEmpty()) {
            throw new IllegalArgumentException("Usuário responsável é obrigatório.");
        }
        if (dataHoraAcao == null) {
            throw new IllegalArgumentException("Data e hora da ação são obrigatórias.");
        }
        if (motivo == null || motivo.trim().isEmpty()) {
            throw new IllegalArgumentException("Motivo da ação é obrigatório.");
        }

        this.id = id;
        this.objetoId = objetoId;
        this.acao = acao;
        this.usuarioResponsavel = usuarioResponsavel;
        this.dataHoraAcao = dataHoraAcao;
        this.motivo = motivo;
        this.detalhesAdicionais = detalhesAdicionais;
    }

    // Getters
    public String getId() { return id; }
    public String getObjetoId() { return objetoId; }
    public String getAcao() { return acao; }
    public String getUsuarioResponsavel() { return usuarioResponsavel; }
    public LocalDateTime getDataHoraAcao() { return dataHoraAcao; }
    public String getMotivo() { return motivo; }
    public String getDetalhesAdicionais() { return detalhesAdicionais; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        LogAuditoria that = (LogAuditoria) obj;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "LogAuditoria{" +
                "id='" + id + '\'' +
                ", objetoId='" + objetoId + '\'' +
                ", acao='" + acao + '\'' +
                ", usuarioResponsavel='" + usuarioResponsavel + '\'' +
                ", dataHoraAcao=" + dataHoraAcao +
                ", motivo='" + motivo + '\'' +
                '}';
    }
}
