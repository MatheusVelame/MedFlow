package br.com.medflow.apresentacao.referencia.especialidades;

import br.com.medflow.dominio.referencia.especialidades.Especialidade;

public record EspecialidadeDetalhes(
    Integer id,
    String nome,
    String descricao,
    String status,
    boolean possuiVinculoHistorico
) {
    public EspecialidadeDetalhes(Especialidade especialidade) {
        this(
            especialidade.getId(),
            especialidade.getNome(),
            especialidade.getDescricao(),
            // Null-safe: evita NPE caso status seja nulo
            (especialidade.getStatus() != null) ? especialidade.getStatus().name() : null,
            especialidade.isPossuiVinculoHistorico()
        );
    }
}