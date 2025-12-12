package br.com.medflow.aplicacao.administracao.funcionarios;

import br.com.medflow.dominio.administracao.funcionarios.StatusFuncionario;

public class FuncionarioResumo {

    private final Integer id;
    private final String nome;
    private final String funcao;
    private final String contato;
    private final StatusFuncionario status;

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
}
