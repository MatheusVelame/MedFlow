package br.com.medflow.aplicacao.referencia.tiposExames;

import static org.apache.commons.lang3.Validate.notNull;

import java.util.List;

import br.com.medflow.dominio.referencia.tiposExames.StatusTipoExame;

public class TipoExameServicoAplicacao {
    
    private final TipoExameRepositorioAplicacao repositorio;
    
    public TipoExameServicoAplicacao(TipoExameRepositorioAplicacao repositorio) {
        notNull(repositorio, "O repositório não pode ser nulo");
        this.repositorio = repositorio;
    }
    
    public List<TipoExameResumo> pesquisarResumos() {
        return repositorio.pesquisarResumos();
    }
    
    public TipoExameDetalhes obterDetalhes(Integer id) {
        return repositorio.obterDetalhesPorId(id)
            .orElseThrow(() -> new RuntimeException("Tipo de exame não encontrado"));
    }
    
    public List<TipoExameResumo> pesquisarTiposExamesInativos() {
        return repositorio.pesquisarTiposExamesInativos();
    }
    
    public List<TipoExameResumo> pesquisarPorStatus(StatusTipoExame status) {
        return repositorio.findByStatus(status);
    }
}