package com.medflow.dominio.prontuario;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Representa o prontuário de um paciente, contendo histórico clínico e atualizações.
 */
public class Prontuario {
    private final String id;
    private final String pacienteId;
    private final String atendimentoId;
    private final LocalDateTime dataHoraCriacao;
    private final String profissionalResponsavel;
    private final String observacoesIniciais;
    private StatusProntuario status;
    private List<HistoricoClinico> historicoClinico;
    private List<HistoricoAtualizacao> historicoAtualizacoes;

    public Prontuario(String id, String pacienteId, String atendimentoId, 
                     LocalDateTime dataHoraCriacao, String profissionalResponsavel, 
                     String observacoesIniciais, StatusProntuario status) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("ID do prontuário é obrigatório.");
        }
        if (pacienteId == null || pacienteId.trim().isEmpty()) {
            throw new IllegalArgumentException("ID do paciente é obrigatório.");
        }
        if (dataHoraCriacao == null) {
            throw new IllegalArgumentException("Data e hora de criação são obrigatórias.");
        }
        if (profissionalResponsavel == null || profissionalResponsavel.trim().isEmpty()) {
            throw new IllegalArgumentException("Profissional responsável é obrigatório.");
        }
        if (status == null) {
            throw new IllegalArgumentException("Status é obrigatório.");
        }

        this.id = id;
        this.pacienteId = pacienteId;
        this.atendimentoId = atendimentoId;
        this.dataHoraCriacao = dataHoraCriacao;
        this.profissionalResponsavel = profissionalResponsavel;
        this.observacoesIniciais = observacoesIniciais;
        this.status = status;
        this.historicoClinico = new ArrayList<>();
        this.historicoAtualizacoes = new ArrayList<>();
    }

    // Getters
    public String getId() { return id; }
    public String getPacienteId() { return pacienteId; }
    public String getAtendimentoId() { return atendimentoId; }
    public LocalDateTime getDataHoraCriacao() { return dataHoraCriacao; }
    public String getProfissionalResponsavel() { return profissionalResponsavel; }
    public String getObservacoesIniciais() { return observacoesIniciais; }
    public StatusProntuario getStatus() { return status; }
    public List<HistoricoClinico> getHistoricoClinico() { return Collections.unmodifiableList(historicoClinico); }
    public List<HistoricoAtualizacao> getHistoricoAtualizacoes() { return Collections.unmodifiableList(historicoAtualizacoes); }

    public void adicionarHistoricoClinico(HistoricoClinico novoRegistro) {
        if (novoRegistro == null) {
            throw new IllegalArgumentException("O registro de histórico clínico não pode ser nulo.");
        }
        if (!this.pacienteId.equals(novoRegistro.getPacienteId())) {
            throw new IllegalArgumentException("O registro não pertence a este prontuário.");
        }
        this.historicoClinico.add(novoRegistro);
        // Ordenar por data e hora
        this.historicoClinico.sort((h1, h2) -> h1.getDataHoraRegistro().compareTo(h2.getDataHoraRegistro()));
    }

    public void adicionarAtualizacao(HistoricoAtualizacao atualizacao) {
        if (atualizacao == null) {
            throw new IllegalArgumentException("A atualização não pode ser nula.");
        }
        if (!this.id.equals(atualizacao.getProntuarioId())) {
            throw new IllegalArgumentException("A atualização não pertence a este prontuário.");
        }
        this.historicoAtualizacoes.add(atualizacao);
        // Ordenar por data e hora
        this.historicoAtualizacoes.sort((a1, a2) -> a1.getDataHoraAtualizacao().compareTo(a2.getDataHoraAtualizacao()));
    }

    public void inativar() {
        this.status = StatusProntuario.INATIVADO;
    }

    public void arquivar() {
        this.status = StatusProntuario.ARQUIVADO;
    }

    public void reativar() {
        this.status = StatusProntuario.ATIVO;
    }

    public void excluirLogicamente() {
        this.status = StatusProntuario.EXCLUIDO;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Prontuario that = (Prontuario) obj;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Prontuario{" +
                "id='" + id + '\'' +
                ", pacienteId='" + pacienteId + '\'' +
                ", atendimentoId='" + atendimentoId + '\'' +
                ", status=" + status +
                ", dataHoraCriacao=" + dataHoraCriacao +
                '}';
    }
}
