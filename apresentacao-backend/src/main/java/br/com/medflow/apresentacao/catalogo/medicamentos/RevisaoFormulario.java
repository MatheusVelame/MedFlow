package br.com.medflow.apresentacao.catalogo.medicamentos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

// Usado para solicitar uma Revisão Crítica de Contraindicações
public class RevisaoFormulario {
    
    @NotBlank(message = "O novo valor da contraindicação é obrigatório.")
    private String novaContraindicacao;
    
    @NotNull(message = "O ID do responsável é obrigatório.")
    private Integer responsavelId;

    public RevisaoFormulario() {}

    public String getNovaContraindicacao() { return novaContraindicacao; }
    public void setNovaContraindicacao(String novaContraindicacao) { this.novaContraindicacao = novaContraindicacao; }
    public Integer getResponsavelId() { return responsavelId; }
    public void setResponsavelId(Integer responsavelId) { this.responsavelId = responsavelId; }
}