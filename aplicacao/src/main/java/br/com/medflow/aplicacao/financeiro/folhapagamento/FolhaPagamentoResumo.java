package br.com.medflow.aplicacao.financeiro.folhapagamento;

import br.com.medflow.dominio.financeiro.folhapagamento.StatusFolha;
import java.math.BigDecimal;

/**
 * DTO com resumo de Folha de Pagamento para listagens.
 */
public class FolhaPagamentoResumo {
    private final int id;
    private final int funcionarioId;
    private final String periodoReferencia;
    private final BigDecimal valorLiquido;
    private final StatusFolha status;

    public FolhaPagamentoResumo(int id, int funcionarioId, String periodoReferencia,
                                BigDecimal valorLiquido, StatusFolha status) {
        this.id = id;
        this.funcionarioId = funcionarioId;
        this.periodoReferencia = periodoReferencia;
        this.valorLiquido = valorLiquido;
        this.status = status;
    }

    public int getId() { return id; }
    public int getFuncionarioId() { return funcionarioId; }
    public String getPeriodoReferencia() { return periodoReferencia; }
    public BigDecimal getValorLiquido() { return valorLiquido; }
    public StatusFolha getStatus() { return status; }
}