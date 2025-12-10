package br.com.medflow.aplicacao.atendimento.consultas;

import java.time.LocalDateTime;

// DTO para exibição em listagens/tabelas (campos resumidos)
public class ConsultaResumo {
    
    private final Integer id;
    private final String pacienteNome;
    private final String medicoNome;
    private final LocalDateTime dataHora;
    private final String status;
    
    public ConsultaResumo(Integer id, String pacienteNome, String medicoNome, 
                            LocalDateTime dataHora, String status) {
        this.id = id;
        this.pacienteNome = pacienteNome;
        this.medicoNome = medicoNome;
        this.dataHora = dataHora;
        this.status = status;
    }

    // Getters
    public Integer getId() {
        return id;
    }

    public String getPacienteNome() {
        return pacienteNome;
    }

    public String getMedicoNome() {
        return medicoNome;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public String getStatus() {
        return status;
    }
}