// Localização: apresentacao-backend/src/main/java/br/com/medflow/apresentacao/catalogo/medicamentos/RevisaoFormulario.java

package br.com.medflow.apresentacao.catalogo.medicamentos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class RevisaoFormulario {

    @NotBlank(message = "A nova contraindicação é obrigatória.")
    private String novaContraindicacao;

    @NotNull(message = "O ID do responsável é obrigatório.")
    private Integer responsavelId;

    // Construtores, Getters e Setters
    public RevisaoFormulario() {}

    public String getNovaContraindicacao() {
        return novaContraindicacao;
    }

    public void setNovaContraindicacao(String novaContraindicacao) {
        this.novaContraindicacao = novaContraindicacao;
    }

    public Integer getResponsavelId() {
        return responsavelId;
    }

    public void setResponsavelId(Integer responsavelId) {
        this.responsavelId = responsavelId;
    }
}