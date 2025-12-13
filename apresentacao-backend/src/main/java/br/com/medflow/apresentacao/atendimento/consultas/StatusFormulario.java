// Localização: apresentacao-backend/src/main/java/br/com/medflow/apresentacao/atendimento/consultas/StatusFormulario.java

package br.com.medflow.apresentacao.atendimento.consultas;

import br.com.medflow.dominio.atendimento.consultas.StatusConsulta;
import jakarta.validation.constraints.NotNull;

public class StatusFormulario {
    
    @NotNull(message = "O novo status é obrigatório.")
    private StatusConsulta novoStatus;
    
    // NOVO CAMPO: ID do usuário responsável pela ação
    @NotNull(message = "O ID do usuário responsável é obrigatório.")
    private Integer usuarioId;

    // Getters e Setters
    public StatusConsulta getNovoStatus() { return novoStatus; }
    public void setNovoStatus(StatusConsulta novoStatus) { this.novoStatus = novoStatus; }
    public Integer getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Integer usuarioId) { this.usuarioId = usuarioId; }
}