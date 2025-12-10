// Localização: infraestrutura/src/main/java/br/com/medflow/infraestrutura/persistencia/jpa/atendimento/ConsultaJpa.java

package br.com.medflow.infraestrutura.persistencia.jpa.atendimento;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "consultas")
public class ConsultaJpa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(nullable = false)
    private String status; 

    @Column(name = "paciente_id", nullable = false)
    private Integer pacienteId;

    @Column(name = "medico_id", nullable = false)
    private Integer medicoId;

    // Construtor padrão exigido pelo JPA
    public ConsultaJpa() {}

    // Getters e Setters (necessários pelo JPA)
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Integer getPacienteId() { return pacienteId; }
    public void setPacienteId(Integer pacienteId) { this.pacienteId = pacienteId; }
    public Integer getMedicoId() { return medicoId; }
    public void setMedicoId(Integer medicoId) { this.medicoId = medicoId; }
}