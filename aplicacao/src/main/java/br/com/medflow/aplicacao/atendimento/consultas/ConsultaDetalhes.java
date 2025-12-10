package br.com.medflow.aplicacao.atendimento.consultas;

import java.time.LocalDateTime;

public class ConsultaDetalhes {
    
    private final Integer id;
    private final Integer pacienteId;
    private final String pacienteNome;
    private final Integer medicoId;
    private final String medicoNome;
    private final LocalDateTime dataHora;
    private final String observacoes;
    private final String status;
    private final LocalDateTime dataCriacao;
    
    public ConsultaDetalhes(Integer id, Integer pacienteId, String pacienteNome, Integer medicoId, String medicoNome, 
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