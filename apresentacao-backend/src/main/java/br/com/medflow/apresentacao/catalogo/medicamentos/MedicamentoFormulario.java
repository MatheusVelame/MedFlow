// Localização: apresentacao-backend/src/main/java/br/com/medflow/apresentacao/catalogo/medicamentos/MedicamentoFormulario.java

package br.com.medflow.apresentacao.catalogo.medicamentos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class MedicamentoFormulario {

    @NotBlank(message = "O nome é obrigatório.")
    private String nome;

    @NotBlank(message = "O uso principal é obrigatório.")
    private String usoPrincipal;

    private String contraindicacoes;
    
    @NotNull(message = "O ID do responsável é obrigatório.")
    @Positive(message = "O ID do responsável deve ser positivo.")
    private Integer responsavelId;

    // Getters e Setters
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getUsoPrincipal() { return usoPrincipal; }
    public void setUsoPrincipal(String usoPrincipal) { this.usoPrincipal = usoPrincipal; }
    public String getContraindicacoes() { return contraindicacoes; }
    public void setContraindicacoes(String contraindicacoes) { this.contraindicacoes = contraindicacoes; }
    public Integer getResponsavelId() { return responsavelId; }
    public void setResponsavelId(Integer responsavelId) { this.responsavelId = responsavelId; }
}