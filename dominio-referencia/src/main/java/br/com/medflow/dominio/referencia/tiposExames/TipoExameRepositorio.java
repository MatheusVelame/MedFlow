package br.com.medflow.dominio.referencia.tiposExames;

import java.util.List;
import java.util.Optional;

public interface TipoExameRepositorio {
    void salvar(TipoExame tipoExame);
    TipoExame obter(TipoExameId id);
    Optional<TipoExame> obterPorCodigo(String codigo);
    List<TipoExame> pesquisar();
    List<TipoExame> pesquisarComFiltroInativo();
    void excluir(TipoExameId id);
}
   