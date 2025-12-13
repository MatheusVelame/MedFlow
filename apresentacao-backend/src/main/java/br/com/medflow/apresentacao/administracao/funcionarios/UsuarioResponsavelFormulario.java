package br.com.medflow.apresentacao.administracao.funcionarios;

import jakarta.validation.constraints.NotNull;

// Usado em comandos de ação (mudarStatus)
public class UsuarioResponsavelFormulario {
    
    @NotNull(message = "O ID do responsável é obrigatório.")
    private Integer responsavelId;

    public UsuarioResponsavelFormulario() {}

    public Integer getResponsavelId() { return responsavelId; }
    public void setResponsavelId(Integer responsavelId) { this.responsavelId = responsavelId; }
}



