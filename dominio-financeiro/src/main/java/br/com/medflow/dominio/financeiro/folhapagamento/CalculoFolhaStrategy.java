package br.com.medflow.dominio.financeiro.folhapagamento;

import java.math.BigDecimal;

/**
 * PADRÃO STRATEGY
 * Interface que define o contrato para diferentes estratégias de cálculo de folha de pagamento.
 */
public interface CalculoFolhaStrategy {

    /**
     * Calcula o valor líquido da folha de pagamento.
     *
     * @param folhaPagamento A folha de pagamento
     * @return O valor líquido calculado
     */
    BigDecimal calcularValorLiquido(FolhaPagamento folhaPagamento);
}