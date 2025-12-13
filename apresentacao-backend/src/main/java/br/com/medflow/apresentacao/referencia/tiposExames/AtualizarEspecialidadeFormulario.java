package br.com.medflow.apresentacao.referencia.tiposExames;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class AtualizarEspecialidadeFormulario {

    @NotBlank(message = "A nova especialidade é obrigatória.")
    private String novaEspecialidade;

    @NotNull(message = "O ID do responsável é obrigatório.")
    private Integer responsavelId;

    public AtualizarEspecialidadeFormulario() {}

    // Getters e Setters
    public String getNovaEspecialidade() { return novaEspecialidade; }
    public void setNovaEspecialidade(String novaEspecialidade) { this.novaEspecialidade = novaEspecialidade; }
    
    public Integer getResponsavelId() { return responsavelId; }
    public void setResponsavelId(Integer responsavelId) { this.responsavelId = responsavelId; }
}