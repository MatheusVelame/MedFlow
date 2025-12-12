package br.com.medflow.aplicacao.financeiro.faturamentos;

import br.com.medflow.aplicacao.excecoes.RecursoNaoEncontradoException;
import static org.apache.commons.lang3.Validate.notNull;

import java.util.List;
import br.com.medflow.dominio.financeiro.faturamentos.StatusFaturamento;
import br.com.medflow.dominio.financeiro.faturamentos.TipoProcedimento;

/**
 * Serviço de Aplicação (Use Case Handler) para Faturamentos,
 * focado em operações de LEITURA (Queries).
 */
public class FaturamentoServicoAplicacao {

    private final FaturamentoRepositorioAplicacao repositorio;

    public FaturamentoServicoAplicacao(FaturamentoRepositorioAplicacao repositorio) {
        notNull(repositorio, "O repositório não pode ser nulo"); 

        this.repositorio = repositorio;
    }

    public List<FaturamentoResumo> pesquisarResumos() {
        return repositorio.pesquisarResumos();
    }

    public FaturamentoDetalhes obterDetalhes(String id) {
        return repositorio.obterDetalhesPorId(id)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Faturamento", id));
    }

    public List<FaturamentoResumo> pesquisarPorStatus(StatusFaturamento status) {
        return repositorio.pesquisarResumosPorStatus(status);
    }

    public List<FaturamentoResumo> pesquisarPorTipoProcedimento(TipoProcedimento tipoProcedimento) {
        return repositorio.pesquisarResumosPorTipoProcedimento(tipoProcedimento);
    }
}
