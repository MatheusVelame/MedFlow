// Localização: apresentacao-backend/src/main/java/br/com/medflow/apresentacao/catalogo/medicamentos/UsuarioResponsavelFormulario.java

package br.com.medflow.apresentacao.catalogo.medicamentos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class UsuarioResponsavelFormulario {
    @NotNull(message = "O ID do responsável é obrigatório.")
    @Positive(message = "O ID do responsável deve ser positivo.")
    private Integer responsavelId;

    // Getters e Setters
    public Integer getResponsavelId() { return responsavelId; }
    public void setResponsavelId(Integer responsavelId) { this.responsavelId = responsavelId; }
}