package br.com.medflow.infraestrutura.persistencia.jpa.atendimento;

import java.time.LocalDateTime;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "consulta") // Assumindo o nome da tabela no DB
public class ConsultaJpa {

    @Id
    private Integer id;
    private Integer pacienteId;
    private String pacienteNome;
    private Integer medicoId;
    private String medicoNome;
    private LocalDateTime dataHora;
    private String observacoes;
    private String status;
    private LocalDateTime dataCriacao;
    
    // Construtor padrão (JPA exige)
    protected ConsultaJpa() {
    }

    // Construtor completo para a criação ou mapeamento
    public ConsultaJpa(Integer id, Integer pacienteId, String pacienteNome, Integer medicoId, String medicoNome,
                       LocalDateTime dataHora, String observacoes, String status, LocalDateTime dataCriacao) {
        this.id = id;
        this.pacienteId = pacienteId;
        this.pacienteNome = pacienteNome;
        this.medicoId = medicoId;
        this.medicoNome = medicoNome;
        this.dataHora = dataHora;
        this.observacoes = observacoes;
        this.status = status;
        this.dataCriacao = dataCriacao;
    }

    // Getters
    public Integer getId() {
        return id;
    }

    public Integer getPacienteId() {
        return pacienteId;
    }

    public String getPacienteNome() {
        return pacienteNome;
    }

    public Integer getMedicoId() {
        return medicoId;
    }

    public String getMedicoNome() {
        return medicoNome;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }
}