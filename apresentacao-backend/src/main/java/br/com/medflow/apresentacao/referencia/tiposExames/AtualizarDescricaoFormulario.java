package br.com.medflow.apresentacao.referencia.tiposExames;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class AtualizarDescricaoFormulario {

    @NotBlank(message = "A nova descrição é obrigatória.")
    private String novaDescricao;

    @NotNull(message = "O ID do responsável é obrigatório.")
    private Integer responsavelId;

    public AtualizarDescricaoFormulario() {}

    // Getters e Setters
    public String getNovaDescricao() { return novaDescricao; }
    public void setNovaDescricao(String novaDescricao) { this.novaDescricao = novaDescricao; }
    
    public Integer getResponsavelId() { return responsavelId; }
    public void setResponsavelId(Integer responsavelId) { this.responsavelId = responsavelId; }
}