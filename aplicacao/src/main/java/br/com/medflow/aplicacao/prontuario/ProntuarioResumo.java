package br.com.medflow.aplicacao.prontuario;

import com.medflow.dominio.prontuario.StatusProntuario;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * DTO para exibir um resumo dos prontuários em listas ou grids.
 */
public class ProntuarioResumo {
    private final String id;
    private final String pacienteId;
    private final String atendimentoId;
    private final StatusProntuario status;
    private final LocalDateTime dataHoraCriacao;
    private final String profissionalResponsavel;

    public ProntuarioResumo(String id, String pacienteId, String atendimentoId, 
                           StatusProntuario status, LocalDateTime dataHoraCriacao, 
                           String profissionalResponsavel) {
        this.id = id;
        this.pacienteId = pacienteId;
        this.atendimentoId = atendimentoId;
        this.status = Objects.requireNonNull(status);
        this.dataHoraCriacao = dataHoraCriacao;
        this.profissionalResponsavel = profissionalResponsavel;
    }

    // Getters
    public String getId() { return id; }
    public String getPacienteId() { return pacienteId; }
    public String getAtendimentoId() { return atendimentoId; }
    public StatusProntuario getStatus() { return status; }
    public LocalDateTime getDataHoraCriacao() { return dataHoraCriacao; }
    public String getProfissionalResponsavel() { return profissionalResponsavel; }
}
