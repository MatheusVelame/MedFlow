package br.com.medflow.infraestrutura.persistencia.jpa.prontuario;

import com.medflow.dominio.prontuario.StatusProntuario;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "historico_atualizacoes")
public class HistoricoAtualizacaoJpa {
    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "prontuario_id", nullable = false, length = 36)
    private String prontuarioId;

    @Column(name = "atendimento_id", nullable = false, length = 36)
    private String atendimentoId;

    @Column(name = "data_hora_atualizacao", nullable = false)
    private LocalDateTime dataHoraAtualizacao;

    @Column(name = "profissional_responsavel", nullable = false, length = 255)
    private String profissionalResponsavel;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String observacoes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusProntuario status;

    public HistoricoAtualizacaoJpa() {}

    // Getters e Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getProntuarioId() { return prontuarioId; }
    public void setProntuarioId(String prontuarioId) { this.prontuarioId = prontuarioId; }

    public String getAtendimentoId() { return atendimentoId; }
    public void setAtendimentoId(String atendimentoId) { this.atendimentoId = atendimentoId; }

    public LocalDateTime getDataHoraAtualizacao() { return dataHoraAtualizacao; }
    public void setDataHoraAtualizacao(LocalDateTime dataHoraAtualizacao) { 
        this.dataHoraAtualizacao = dataHoraAtualizacao; 
    }

    public String getProfissionalResponsavel() { return profissionalResponsavel; }
    public void setProfissionalResponsavel(String profissionalResponsavel) { 
        this.profissionalResponsavel = profissionalResponsavel; 
    }

    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }

    public StatusProntuario getStatus() { return status; }
    public void setStatus(StatusProntuario status) { this.status = status; }
}
