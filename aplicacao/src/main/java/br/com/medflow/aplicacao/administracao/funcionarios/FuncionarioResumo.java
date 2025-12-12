package br.com.medflow.aplicacao.administracao.funcionarios;

import br.com.medflow.dominio.administracao.funcionarios.StatusFuncionario;

public class FuncionarioResumo {

    private Integer id;
    private String nome;
    private String funcao;
    private String contato;
    private StatusFuncionario status;
    
    // CONSTRUTOR PROTEGIDO ADICIONADO PARA SATISFAZER O MODELMAPPER
    protected FuncionarioResumo() {
    }
    
    public FuncionarioResumo(Integer id, String nome, String funcao, String contato, StatusFuncionario status) {
        this.id = id;
        this.nome = nome;
        this.funcao = funcao;
        this.contato = contato;
        this.status = status;
    }

    // Getters
    public Integer getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getFuncao() {
        return funcao;
    }

    public String getContato() {
        return contato;
    }

    public StatusFuncionario getStatus() {
        return status;
    }
    
    // Setters para o ModelMapper
    public void setId(Integer id) {
        this.id = id;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setFuncao(String funcao) {
        this.funcao = funcao;
    }

    public void setContato(String contato) {
        this.contato = contato;
    }

    public void setStatus(StatusFuncionario status) {
        this.status = status;
    }
}
