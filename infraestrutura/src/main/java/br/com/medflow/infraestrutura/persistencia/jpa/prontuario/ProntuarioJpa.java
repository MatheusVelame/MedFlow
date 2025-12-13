package br.com.medflow.infraestrutura.persistencia.jpa.prontuario;

import com.medflow.dominio.prontuario.StatusProntuario;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "prontuarios")
public class ProntuarioJpa {
    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "paciente_id", nullable = false)
    private Integer pacienteId;

    @Column(name = "atendimento_id", length = 36)
    private String atendimentoId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusProntuario status;

    @Column(name = "data_hora_criacao", nullable = false)
    private LocalDateTime dataHoraCriacao;

    @Column(name = "profissional_responsavel", nullable = false, length = 255)
    private String profissionalResponsavel;

    @Column(name = "observacoes_iniciais", columnDefinition = "TEXT")
    private String observacoesIniciais;

    public ProntuarioJpa() {}

    // Getters e Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Integer getPacienteId() { return pacienteId; }
    public void setPacienteId(Integer pacienteId) { this.pacienteId = pacienteId; }

    public String getAtendimentoId() { return atendimentoId; }
    public void setAtendimentoId(String atendimentoId) { this.atendimentoId = atendimentoId; }

    public StatusProntuario getStatus() { return status; }
    public void setStatus(StatusProntuario status) { this.status = status; }

    public LocalDateTime getDataHoraCriacao() { return dataHoraCriacao; }
    public void setDataHoraCriacao(LocalDateTime dataHoraCriacao) { this.dataHoraCriacao = dataHoraCriacao; }

    public String getProfissionalResponsavel() { return profissionalResponsavel; }
    public void setProfissionalResponsavel(String profissionalResponsavel) { 
        this.profissionalResponsavel = profissionalResponsavel; 
    }

    public String getObservacoesIniciais() { return observacoesIniciais; }
    public void setObservacoesIniciais(String observacoesIniciais) { 
        this.observacoesIniciais = observacoesIniciais; 
    }
}
