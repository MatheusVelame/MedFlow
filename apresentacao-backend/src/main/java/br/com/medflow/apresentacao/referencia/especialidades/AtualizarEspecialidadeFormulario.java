package br.com.medflow.apresentacao.referencia.especialidades;

import jakarta.validation.constraints.Size;

public class AtualizarEspecialidadeFormulario {

    @Size(max = 100)
    private String novoNome;

    @Size(max = 255)
    private String novaDescricao;

    public AtualizarEspecialidadeFormulario() {}

    public String getNovoNome() { return novoNome; }
    public void setNovoNome(String novoNome) { this.novoNome = novoNome; }

    public String getNovaDescricao() { return novaDescricao; }
    public void setNovaDescricao(String novaDescricao) { this.novaDescricao = novaDescricao; }
}
