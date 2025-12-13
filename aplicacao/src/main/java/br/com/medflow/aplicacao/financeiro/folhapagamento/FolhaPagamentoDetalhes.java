package br.com.medflow.aplicacao.financeiro.folhapagamento;

import br.com.medflow.dominio.financeiro.folhapagamento.StatusFolha;
import br.com.medflow.dominio.financeiro.folhapagamento.TipoRegistro;
import java.math.BigDecimal;

/**
 * DTO com detalhes completos de Folha de Pagamento.
 */
public class FolhaPagamentoDetalhes {
    private final int id;
    private final int funcionarioId;
    private final String periodoReferencia;
    private final TipoRegistro tipoRegistro;
    private final BigDecimal salarioBase;
    private final BigDecimal beneficios;
    private final String metodoPagamento;
    private final StatusFolha status;
    private final BigDecimal valorLiquido;

    public FolhaPagamentoDetalhes(int id, int funcionarioId, String periodoReferencia,
                                  TipoRegistro tipoRegistro, BigDecimal salarioBase,
                                  BigDecimal beneficios, String metodoPagamento,
                                  StatusFolha status, BigDecimal valorLiquido) {
        this.id = id;
        this.funcionarioId = funcionarioId;
        this.periodoReferencia = periodoReferencia;
        this.tipoRegistro = tipoRegistro;
        this.salarioBase = salarioBase;
        this.beneficios = beneficios;
        this.metodoPagamento = metodoPagamento;
        this.status = status;
        this.valorLiquido = valorLiquido;
    }

    public int getId() { return id; }
    public int getFuncionarioId() { return funcionarioId; }
    public String getPeriodoReferencia() { return periodoReferencia; }
    public TipoRegistro getTipoRegistro() { return tipoRegistro; }
    public BigDecimal getSalarioBase() { return salarioBase; }
    public BigDecimal getBeneficios() { return beneficios; }
    public String getMetodoPagamento() { return metodoPagamento; }
    public StatusFolha getStatus() { return status; }
    public BigDecimal getValorLiquido() { return valorLiquido; }
}