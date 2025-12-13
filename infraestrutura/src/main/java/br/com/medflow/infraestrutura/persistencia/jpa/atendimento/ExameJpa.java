package br.com.medflow.infraestrutura.persistencia.jpa.atendimento;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import br.com.medflow.dominio.atendimento.exames.StatusExame;

@Entity
@Table(name = "exames")
public class ExameJpa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long pacienteId;
    private Long medicoId;
    private String tipoExame;
    private LocalDateTime dataHora;

    @Enumerated(EnumType.STRING)
    private StatusExame status;

    private Long responsavelId;

    @OneToMany(mappedBy = "exame", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HistoricoExameJpa> historico = new ArrayList<>();

    @Deprecated // Uso exclusivo do Hibernate
    public ExameJpa() {
    }

    public ExameJpa(Long id, Long pacienteId, Long medicoId, String tipoExame, LocalDateTime dataHora, StatusExame status, Long responsavelId) {
        this.id = id;
        this.pacienteId = pacienteId;
        this.medicoId = medicoId;
        this.tipoExame = tipoExame;
        this.dataHora = dataHora;
        this.status = status;
        this.responsavelId = responsavelId;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getPacienteId() { return pacienteId; }
    public void setPacienteId(Long pacienteId) { this.pacienteId = pacienteId; }
    public Long getMedicoId() { return medicoId; }
    public void setMedicoId(Long medicoId) { this.medicoId = medicoId; }
    public String getTipoExame() { return tipoExame; }
    public void setTipoExame(String tipoExame) { this.tipoExame = tipoExame; }
    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }
    public StatusExame getStatus() { return status; }
    public void setStatus(StatusExame status) { this.status = status; }
    public Long getResponsavelId() { return responsavelId; }
    public void setResponsavelId(Long responsavelId) { this.responsavelId = responsavelId; }

    public List<HistoricoExameJpa> getHistorico() {
        return historico;
    }

    public void setHistorico(List<HistoricoExameJpa> historico) {
        this.historico = historico;
    }
}