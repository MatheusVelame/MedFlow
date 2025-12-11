package br.com.medflow.dominio.financeiro.folhapagamento;

import java.util.List;
import java.util.Optional;

public interface FolhaPagamentoRepositorio {
    void salvar(FolhaPagamento folhaPagamento);
    FolhaPagamento obter(FolhaPagamentoId id);
    Optional<FolhaPagamento> obterPorFuncionarioEPeriodo(int funcionarioId, String periodoReferencia, TipoRegistro tipoRegistro);
    List<FolhaPagamento> pesquisarPorFuncionario(int funcionarioId);
    List<FolhaPagamento> pesquisarPorPeriodo(String periodoReferencia);
    List<FolhaPagamento> pesquisarPorStatus(StatusFolha status);
    List<FolhaPagamento> pesquisar();
    void remover(FolhaPagamentoId id);
}