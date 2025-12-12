// Localização: apresentacao-backend/src/main/java/br/com/medflow/apresentacao/atendimento/consultas/AgendamentoFormulario.java

package br.com.medflow.apresentacao.atendimento.consultas;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class AgendamentoFormulario {
    
    @NotNull(message = "A data e hora são obrigatórias.")
    @FutureOrPresent(message = "O agendamento deve ser para o futuro ou presente.")
    private LocalDateTime dataHora;

    @NotBlank(message = "A descrição é obrigatória.")
    private String descricao;
    
    @NotNull(message = "O ID do paciente é obrigatório.")
    private Integer pacienteId;
    
    @NotNull(message = "O ID do médico é obrigatório.")
    private Integer medicoId;
    
    // NOVO CAMPO: ID do usuário responsável pela ação
    @NotNull(message = "O ID do usuário responsável é obrigatório.")
    private Integer usuarioId;

    // Getters e Setters
    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public Integer getPacienteId() { return pacienteId; }
    public void setPacienteId(Integer pacienteId) { this.pacienteId = pacienteId; }
    public Integer getMedicoId() { return medicoId; }
    public void setMedicoId(Integer medicoId) { this.medicoId = medicoId; }
    public Integer getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Integer usuarioId) { this.usuarioId = usuarioId; }
}