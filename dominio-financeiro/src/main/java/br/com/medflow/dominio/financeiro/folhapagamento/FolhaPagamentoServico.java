package br.com.medflow.dominio.financeiro.folhapagamento;

import static org.apache.commons.lang3.Validate.notNull;

import java.math.BigDecimal;
import java.util.List;

public class FolhaPagamentoServico {
    private final FolhaPagamentoRepositorio repositorio;

    public FolhaPagamentoServico(FolhaPagamentoRepositorio repositorio) {
        notNull(repositorio, "O repositório de folha de pagamento não pode ser nulo");
        this.repositorio = repositorio;
    }

    public FolhaPagamento registrar(int funcionarioId, String periodoReferencia, TipoRegistro tipoRegistro,
                                    BigDecimal salarioBase, BigDecimal beneficios, String metodoPagamento,
                                    TipoVinculo tipoVinculo, UsuarioResponsavelId responsavelId,
                                    boolean funcionarioAtivo) {

        // RN1: Funcionário deve estar ativo
        if (!funcionarioAtivo) {
            throw new IllegalArgumentException("Não é permitido registrar folha de pagamento para funcionários inativos.");
        }

        // RN2: Não pode haver duplicidade para Pagamento no mesmo período
        if (tipoRegistro == TipoRegistro.PAGAMENTO) {
            var existente = repositorio.obterPorFuncionarioEPeriodo(funcionarioId, periodoReferencia, TipoRegistro.PAGAMENTO);
            if (existente.isPresent()) {
                throw new IllegalArgumentException("Já existe uma folha de pagamento registrada para este funcionário no período informado.");
            }
        }

        var novaFolha = new FolhaPagamento(funcionarioId, periodoReferencia, tipoRegistro,
                salarioBase, beneficios, metodoPagamento, tipoVinculo, responsavelId);
        repositorio.salvar(novaFolha);
        return novaFolha;
    }

    public FolhaPagamento atualizarValores(FolhaPagamentoId id, BigDecimal novoSalarioBase,
                                           BigDecimal novosBeneficios, UsuarioResponsavelId responsavelId) {
        var folha = obter(id);

        folha.atualizarValores(novoSalarioBase, novosBeneficios, responsavelId);
        repositorio.salvar(folha);
        return folha;
    }

    public void alterarStatus(FolhaPagamentoId id, StatusFolha novoStatus, UsuarioResponsavelId responsavelId) {
        var folha = obter(id);

        folha.alterarStatus(novoStatus, responsavelId);
        repositorio.salvar(folha);
    }

    public void remover(FolhaPagamentoId id, UsuarioResponsavelId responsavelId) {
        var folha = obter(id);

        if (!folha.podeSerRemovido()) {
            String statusUpper = folha.getStatus().name();
            String statusFormatado = statusUpper.substring(0, 1).toUpperCase() + statusUpper.substring(1).toLowerCase();

            String mensagem = "Não é permitido remover registros com status '" + statusFormatado + "', pois fazem parte do histórico financeiro.";
            throw new IllegalStateException(mensagem);
        }

        repositorio.remover(id);
    }

    public FolhaPagamento obter(FolhaPagamentoId id) {
        return repositorio.obter(id);
    }

    public List<FolhaPagamento> pesquisarPorFuncionario(int funcionarioId) {
        return repositorio.pesquisarPorFuncionario(funcionarioId);
    }

    public List<FolhaPagamento> pesquisarPorPeriodo(String periodoReferencia) {
        return repositorio.pesquisarPorPeriodo(periodoReferencia);
    }

    public List<FolhaPagamento> pesquisarPorStatus(StatusFolha status) {
        return repositorio.pesquisarPorStatus(status);
    }

    public List<FolhaPagamento> pesquisarTodos() {
        return repositorio.pesquisar();
    }
}