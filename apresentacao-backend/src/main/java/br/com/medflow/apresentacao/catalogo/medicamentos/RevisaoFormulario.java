// Localização: apresentacao-backend/src/main/java/br/com/medflow/apresentacao/catalogo/medicamentos/RevisaoFormulario.java

package br.com.medflow.apresentacao.catalogo.medicamentos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class RevisaoFormulario {
    
    @NotBlank(message = "A nova contraindicação é obrigatória.")
    private String novaContraindicacao;
    
    @NotNull(message = "O ID do responsável é obrigatório.")
    @Positive(message = "O ID do responsável deve ser positivo.")
    private Integer responsavelId;

    // Getters e Setters
    public String getNovaContraindicacao() { return novaContraindicacao; }
    public void setNovaContraindicacao(String novaContraindicacao) { this.novaContraindicacao = novaContraindicacao; }
    public Integer getResponsavelId() { return responsavelId; }
    public void setResponsavelId(Integer responsavelId) { this.responsavelId = responsavelId; }
}