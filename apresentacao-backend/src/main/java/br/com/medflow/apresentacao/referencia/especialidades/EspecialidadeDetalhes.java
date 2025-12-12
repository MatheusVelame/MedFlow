package br.com.medflow.apresentacao.referencia.especialidades;

import br.com.medflow.dominio.referencia.especialidades.Especialidade;

public record EspecialidadeDetalhes(
    String id,
    String nome,
    String descricao,
    String status,
    boolean possuiVinculoHistorico
) {
    public EspecialidadeDetalhes(Especialidade especialidade) {
        this(
            especialidade.getNome(),
            especialidade.getNome(),
            especialidade.getDescricao(),
            especialidade.getStatus().name(),
            especialidade.isPossuiVinculoHistorico()
        );
    }
}
