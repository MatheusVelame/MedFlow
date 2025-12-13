package br.com.medflow.apresentacao.referencia.especialidades;

import br.com.medflow.dominio.referencia.especialidades.Especialidade;

public record EspecialidadeResumo(
    Integer id,
    String nome,
    String descricao,
    String status
) {
    public EspecialidadeResumo(Especialidade especialidade) {
        this(
            especialidade.getId(),
            especialidade.getNome(),
            especialidade.getDescricao(),
            especialidade.getStatus().name()
        );
    }
}