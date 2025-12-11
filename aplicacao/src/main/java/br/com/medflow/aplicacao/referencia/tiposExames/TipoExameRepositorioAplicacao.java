package br.com.medflow.aplicacao.referencia.tiposExames;

import java.util.List;
import java.util.Optional;

import br.com.medflow.dominio.referencia.tiposExames.StatusTipoExame;

public interface TipoExameRepositorioAplicacao {
    
    List<TipoExameResumo> pesquisarResumos();
    
    Optional<TipoExameDetalhes> obterDetalhesPorId(Integer id);
    
    List<TipoExameResumo> findByStatus(StatusTipoExame status);
    
    List<TipoExameResumo> pesquisarTiposExamesInativos();
}