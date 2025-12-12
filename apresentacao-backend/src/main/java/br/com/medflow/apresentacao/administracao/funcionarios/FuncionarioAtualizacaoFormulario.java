package br.com.medflow.apresentacao.administracao.funcionarios;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class FuncionarioAtualizacaoFormulario {

    @NotBlank(message = "O nome é obrigatório.")
    private String novoNome;

    @NotBlank(message = "A função é obrigatória.")
    private String novaFuncao;

    @NotBlank(message = "O contato é obrigatório.")
    private String novoContato;
    
    @NotNull(message = "O ID do responsável é obrigatório.")
    private Integer responsavelId;

    public FuncionarioAtualizacaoFormulario() {}

    // Getters e Setters
    public String getNovoNome() { return novoNome; }
    public void setNovoNome(String novoNome) { this.novoNome = novoNome; }
    
    public String getNovaFuncao() { return novaFuncao; }
    public void setNovaFuncao(String novaFuncao) { this.novaFuncao = novaFuncao; }
    
    public String getNovoContato() { return novoContato; }
    public void setNovoContato(String novoContato) { this.novoContato = novoContato; }
    
    public Integer getResponsavelId() { return responsavelId; }
    public void setResponsavelId(Integer responsavelId) { this.responsavelId = responsavelId; }
}

