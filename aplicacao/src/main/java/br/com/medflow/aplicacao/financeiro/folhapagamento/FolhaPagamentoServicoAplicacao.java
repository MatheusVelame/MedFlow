package br.com.medflow.aplicacao.financeiro.folhapagamento;

import br.com.medflow.dominio.financeiro.folhapagamento.*;
import java.math.BigDecimal;
import java.util.List;

/**
 * Serviço de aplicação que coordena os use cases.
 * USA o Strategy do domínio para calcular valores.
 */
public class FolhaPagamentoServicoAplicacao {

    private final FolhaPagamentoServico servicoDominio;
    private final FolhaPagamentoRepositorioAplicacao repositorioAplicacao;

    public FolhaPagamentoServicoAplicacao(FolhaPagamentoServico servicoDominio,
                                          FolhaPagamentoRepositorioAplicacao repositorioAplicacao) {
        this.servicoDominio = servicoDominio;
        this.repositorioAplicacao = repositorioAplicacao;
    }

    /**
     * Registra uma nova folha de pagamento.
     */
    public FolhaPagamentoDetalhes registrar(int funcionarioId, String periodoReferencia,
                                            TipoRegistro tipoRegistro, BigDecimal salarioBase,
                                            BigDecimal beneficios, String metodoPagamento,
                                            int usuarioResponsavelId, boolean funcionarioAtivo) {

        UsuarioResponsavelId responsavelId = new UsuarioResponsavelId(usuarioResponsavelId);

        FolhaPagamento folha = servicoDominio.registrar(
                funcionarioId, periodoReferencia, tipoRegistro,
                salarioBase, beneficios, metodoPagamento,
                responsavelId, funcionarioAtivo
        );

        return converterParaDetalhes(folha);
    }

    /**
     * Atualiza valores de uma folha.
     */
    public FolhaPagamentoDetalhes atualizarValores(int folhaPagamentoId,
                                                   BigDecimal novoSalarioBase,
                                                   BigDecimal novosBeneficios,
                                                   int usuarioResponsavelId) {

        FolhaPagamentoId folhaId = new FolhaPagamentoId(folhaPagamentoId);
        UsuarioResponsavelId responsavelId = new UsuarioResponsavelId(usuarioResponsavelId);

        FolhaPagamento folha = servicoDominio.atualizarValores(
                folhaId, novoSalarioBase, novosBeneficios, responsavelId
        );

        return converterParaDetalhes(folha);
    }

    /**
     * Altera o status de uma folha.
     */
    public FolhaPagamentoDetalhes alterarStatus(int folhaPagamentoId,
                                                StatusFolha novoStatus,
                                                int usuarioResponsavelId) {

        FolhaPagamentoId folhaId = new FolhaPagamentoId(folhaPagamentoId);
        UsuarioResponsavelId responsavelId = new UsuarioResponsavelId(usuarioResponsavelId);

        servicoDominio.alterarStatus(folhaId, novoStatus, responsavelId);

        FolhaPagamento folha = servicoDominio.obter(folhaId);
        return converterParaDetalhes(folha);
    }

    /**
     * Remove uma folha.
     */
    public void remover(int folhaPagamentoId, int usuarioResponsavelId) {
        FolhaPagamentoId folhaId = new FolhaPagamentoId(folhaPagamentoId);
        UsuarioResponsavelId responsavelId = new UsuarioResponsavelId(usuarioResponsavelId);

        servicoDominio.remover(folhaId, responsavelId);
    }

    /**
     * Obtém detalhes de uma folha.
     */
    public FolhaPagamentoDetalhes obterDetalhes(int folhaPagamentoId) {
        return repositorioAplicacao.obterDetalhes(folhaPagamentoId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Folha não encontrada: " + folhaPagamentoId));
    }

    /**
     * Lista todos os resumos.
     */
    public List<FolhaPagamentoResumo> listarTodos() {
        return repositorioAplicacao.listarResumos();
    }

    /**
     * Lista por funcionário.
     */
    public List<FolhaPagamentoResumo> listarPorFuncionario(int funcionarioId) {
        return repositorioAplicacao.listarPorFuncionario(funcionarioId);
    }

    /**
     * Lista por status.
     */
    public List<FolhaPagamentoResumo> listarPorStatus(StatusFolha status) {
        return repositorioAplicacao.listarPorStatus(status);
    }

    /**
     * Converte entidade de domínio para DTO usando Strategy do domínio.
     */
    private FolhaPagamentoDetalhes converterParaDetalhes(FolhaPagamento folha) {
        // USA o Strategy do domínio
        CalculoFolhaStrategy strategy = CalculoFolhaStrategyFactory.criar(
                folha.getTipoRegistro()
        );

        BigDecimal valorLiquido = strategy.calcularValorLiquido(folha);

        return new FolhaPagamentoDetalhes(
                folha.getId().getId(),
                folha.getFuncionarioId(),
                folha.getPeriodoReferencia(),
                folha.getTipoRegistro(),
                folha.getSalarioBase(),
                folha.getBeneficios(),
                folha.getMetodoPagamento(),
                folha.getStatus(),
                valorLiquido
        );
    }
}