// Localização: apresentacao-backend/src/main/java/br/com/medflow/apresentacao/catalogo/medicamentos/UsuarioResponsavelFormulario.java

package br.com.medflow.apresentacao.catalogo.medicamentos;

import jakarta.validation.constraints.NotNull;

public class UsuarioResponsavelFormulario {

    @NotNull(message = "O ID do responsável é obrigatório.")
    private Integer responsavelId;

    // Construtores, Getters e Setters
    public UsuarioResponsavelFormulario() {}

    public Integer getResponsavelId() {
        return responsavelId;
    }

    public void setResponsavelId(Integer responsavelId) {
        this.responsavelId = responsavelId;
    }
}