// Localização: apresentacao-backend/src/main/java/br/com/medflow/apresentacao/catalogo/medicamentos/UsoPrincipalFormulario.java

package br.com.medflow.apresentacao.catalogo.medicamentos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class UsoPrincipalFormulario {

    @NotBlank(message = "O novo uso principal é obrigatório.")
    private String novoUsoPrincipal;

    @NotNull(message = "O ID do responsável é obrigatório.")
    private Integer responsavelId;

    // Construtores, Getters e Setters
    public UsoPrincipalFormulario() {}

    public String getNovoUsoPrincipal() {
        return novoUsoPrincipal;
    }

    public void setNovoUsoPrincipal(String novoUsoPrincipal) {
        this.novoUsoPrincipal = novoUsoPrincipal;
    }

    public Integer getResponsavelId() {
        return responsavelId;
    }

    public void setResponsavelId(Integer responsavelId) {
        this.responsavelId = responsavelId;
    }
}