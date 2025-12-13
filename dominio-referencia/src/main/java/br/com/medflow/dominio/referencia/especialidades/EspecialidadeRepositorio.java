package br.com.medflow.dominio.referencia.especialidades;

import java.util.List;
import java.util.Optional;

public interface EspecialidadeRepositorio {

    void salvar(Especialidade especialidade);

    Optional<Especialidade> buscarPorNome(String nome);

    Optional<Especialidade> buscarPorId(Integer id);

    boolean existePorNome(String nome);

    void remover(Especialidade especialidade);

    // Permite obter todas as especialidades para listagem (necessário para a camada de apresentação)
    List<Especialidade> buscarTodos();
}