package br.com.medflow.apresentacao.referencia.tiposExames;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public class AtualizarValorFormulario {

    @NotNull(message = "O novo valor é obrigatório.")
    @PositiveOrZero(message = "O valor deve ser maior ou igual a zero.")
    private Double novoValor;

    @NotNull(message = "O ID do responsável é obrigatório.")
    private Integer responsavelId;

    public AtualizarValorFormulario() {}

    // Getters e Setters
    public Double getNovoValor() { return novoValor; }
    public void setNovoValor(Double novoValor) { this.novoValor = novoValor; }
    
    public Integer getResponsavelId() { return responsavelId; }
    public void setResponsavelId(Integer responsavelId) { this.responsavelId = responsavelId; }
}