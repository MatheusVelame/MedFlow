// Localização: aplicacao/src/main/java/br/com/medflow/aplicacao/atendimento/consultas/ConsultaResumo.java

package br.com.medflow.aplicacao.atendimento.consultas;

import br.com.medflow.dominio.atendimento.consultas.StatusConsulta;
import java.time.LocalDateTime;

/**
 * DTO para exibir um resumo das informações da consulta em listas ou grids.
 */
public class ConsultaResumo {
    private final Integer id;
    private final LocalDateTime dataHora;
    private final String nomePaciente;
    private final String nomeMedico;
    private final StatusConsulta status;

    public ConsultaResumo(Integer id, LocalDateTime dataHora, String nomePaciente, String nomeMedico, StatusConsulta status) {
        this.id = id;
        this.dataHora = dataHora;
        this.nomePaciente = nomePaciente;
        this.nomeMedico = nomeMedico;
        this.status = status;
    }

    // Getters
    public Integer getId() { return id; }
    public LocalDateTime getDataHora() { return dataHora; }
    public String getNomePaciente() { return nomePaciente; }
    public String getNomeMedico() { return nomeMedico; }
    public StatusConsulta getStatus() { return status; }
}