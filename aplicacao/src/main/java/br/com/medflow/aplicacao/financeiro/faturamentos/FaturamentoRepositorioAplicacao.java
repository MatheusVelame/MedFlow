package br.com.medflow.aplicacao.financeiro.faturamentos;

import br.com.medflow.dominio.financeiro.faturamentos.StatusFaturamento;
import br.com.medflow.dominio.financeiro.faturamentos.TipoProcedimento;
import java.util.List;
import java.util.Optional;

/**
 * Interface (Porta) para acesso de dados de LEITURA (Queries) na camada de Aplicação.
 * Será implementada pela camada de Infraestrutura (o Adapter).
 */
public interface FaturamentoRepositorioAplicacao {

    /**
     * Pesquisa e retorna um resumo de todos os faturamentos.
     * @return Lista de FaturamentoResumo.
     */
    List<FaturamentoResumo> pesquisarResumos();

    /**
     * Obtém os detalhes completos de um faturamento específico.
     * @param id O ID do faturamento.
     * @return Optional contendo FaturamentoDetalhes ou vazio se não encontrado.
     */
    Optional<FaturamentoDetalhes> obterDetalhesPorId(String id);

    /**
     * Pesquisa faturamentos por status.
     * @param status O status pelo qual filtrar.
     * @return Lista de FaturamentoResumo.
     */
    List<FaturamentoResumo> pesquisarResumosPorStatus(StatusFaturamento status);

    /**
     * Pesquisa faturamentos por tipo de procedimento.
     * @param tipoProcedimento O tipo de procedimento pelo qual filtrar.
     * @return Lista de FaturamentoResumo.
     */
    List<FaturamentoResumo> pesquisarResumosPorTipoProcedimento(TipoProcedimento tipoProcedimento);
}
