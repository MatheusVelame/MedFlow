package br.com.medflow.dominio.referencia.especialidades;

import java.util.Optional;

public interface EspecialidadeRepositorio {

    void salvar(Especialidade especialidade);

    Optional<Especialidade> buscarPorNome(String nome);

    boolean existePorNome(String nome);

    void remover(Especialidade especialidade);
}
