package br.com.medflow.apresentacao.administracao.funcionarios;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class FuncionarioFormulario {

    @NotBlank(message = "O nome é obrigatório.")
    private String nome;

    @NotBlank(message = "A função é obrigatória.")
    private String funcao;

    @NotBlank(message = "O contato é obrigatório.")
    private String contato;
    
    @NotNull(message = "O ID do responsável é obrigatório para a auditoria.")
    private Integer responsavelId;

    public FuncionarioFormulario() {}

    // Getters e Setters
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    
    public String getFuncao() { return funcao; }
    public void setFuncao(String funcao) { this.funcao = funcao; }
    
    public String getContato() { return contato; }
    public void setContato(String contato) { this.contato = contato; }
    
    public Integer getResponsavelId() { return responsavelId; }
    public void setResponsavelId(Integer responsavelId) { this.responsavelId = responsavelId; }
}



