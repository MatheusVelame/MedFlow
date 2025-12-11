package br.com.medflow.infraestrutura.persistencia.jpa.prontuario;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "historico_clinico")
public class HistoricoClinicoJpa {
    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "prontuario_id", nullable = false, length = 36)
    private String prontuarioId;

    @Column(name = "paciente_id", nullable = false, length = 36)
    private String pacienteId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String sintomas;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String diagnostico;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String conduta;

    @Column(name = "data_hora_registro", nullable = false)
    private LocalDateTime dataHoraRegistro;

    @Column(name = "profissional_responsavel", nullable = false, length = 255)
    private String profissionalResponsavel;

    @ElementCollection
    @CollectionTable(name = "historico_clinico_anexos", joinColumns = @JoinColumn(name = "historico_id"))
    @Column(name = "anexo_referenciado")
    private List<String> anexosReferenciados = new ArrayList<>();

    public HistoricoClinicoJpa() {}

    // Getters e Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getProntuarioId() { return prontuarioId; }
    public void setProntuarioId(String prontuarioId) { this.prontuarioId = prontuarioId; }

    public String getPacienteId() { return pacienteId; }
    public void setPacienteId(String pacienteId) { this.pacienteId = pacienteId; }

    public String getSintomas() { return sintomas; }
    public void setSintomas(String sintomas) { this.sintomas = sintomas; }

    public String getDiagnostico() { return diagnostico; }
    public void setDiagnostico(String diagnostico) { this.diagnostico = diagnostico; }

    public String getConduta() { return conduta; }
    public void setConduta(String conduta) { this.conduta = conduta; }

    public LocalDateTime getDataHoraRegistro() { return dataHoraRegistro; }
    public void setDataHoraRegistro(LocalDateTime dataHoraRegistro) { 
        this.dataHoraRegistro = dataHoraRegistro; 
    }

    public String getProfissionalResponsavel() { return profissionalResponsavel; }
    public void setProfissionalResponsavel(String profissionalResponsavel) { 
        this.profissionalResponsavel = profissionalResponsavel; 
    }

    public List<String> getAnexosReferenciados() { return anexosReferenciados; }
    public void setAnexosReferenciados(List<String> anexosReferenciados) { 
        this.anexosReferenciados = anexosReferenciados; 
    }
}
