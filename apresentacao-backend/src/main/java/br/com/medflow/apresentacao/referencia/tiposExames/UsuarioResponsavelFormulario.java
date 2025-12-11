package br.com.medflow.apresentacao.referencia.tiposExames;

import jakarta.validation.constraints.NotNull;

public class UsuarioResponsavelFormulario {
    
    @NotNull(message = "O ID do responsável é obrigatório.")
    private Integer responsavelId;

    public UsuarioResponsavelFormulario() {}

    // Getters e Setters
    public Integer getResponsavelId() { return responsavelId; }
    public void setResponsavelId(Integer responsavelId) { this.responsavelId = responsavelId; }
}