package br.com.medflow.aplicacao.financeiro.folhapagamento;

import br.com.medflow.dominio.financeiro.folhapagamento.StatusFolha;
import java.util.List;
import java.util.Optional;

/**
 * Interface do repositório na camada de aplicação.
 */
public interface FolhaPagamentoRepositorioAplicacao {

    Optional<FolhaPagamentoDetalhes> obterDetalhes(int id);

    List<FolhaPagamentoResumo> listarResumos();

    List<FolhaPagamentoResumo> listarPorFuncionario(int funcionarioId);

    List<FolhaPagamentoResumo> listarPorStatus(StatusFolha status);
}