package br.com.medflow.apresentacao.administracao.funcionarios;

import br.com.medflow.dominio.administracao.funcionarios.StatusFuncionario;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class FuncionarioAtualizacaoCompletaFormulario {

    @NotBlank(message = "O nome é obrigatório.")
    private String nome;

    @NotBlank(message = "A função é obrigatória.")
    private String funcao;

    @NotBlank(message = "O contato é obrigatório.")
    private String contato;
    
    @NotNull(message = "O status é obrigatório.")
    private StatusFuncionario status;
    
    @NotNull(message = "O ID do responsável é obrigatório.")
    private Integer responsavelId;

    public FuncionarioAtualizacaoCompletaFormulario() {}

    // Getters e Setters
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    
    public String getFuncao() { return funcao; }
    public void setFuncao(String funcao) { this.funcao = funcao; }
    
    public String getContato() { return contato; }
    public void setContato(String contato) { this.contato = contato; }
    
    public StatusFuncionario getStatus() { return status; }
    public void setStatus(StatusFuncionario status) { this.status = status; }
    
    public Integer getResponsavelId() { return responsavelId; }
    public void setResponsavelId(Integer responsavelId) { this.responsavelId = responsavelId; }
}

