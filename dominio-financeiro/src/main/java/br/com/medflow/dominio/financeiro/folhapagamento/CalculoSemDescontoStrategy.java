package br.com.medflow.dominio.financeiro.folhapagamento;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * PADRÃO STRATEGY - Implementação Concreta
 * Estratégia de cálculo para folhas de PAGAMENTO de Estagiários e PJ.
 * Não aplica descontos.
 */
public class CalculoSemDescontoStrategy implements CalculoFolhaStrategy {

    @Override
    public BigDecimal calcularValorLiquido(FolhaPagamento folhaPagamento) {
        return folhaPagamento.getSalarioBase()
                .add(folhaPagamento.getBeneficios())
                .setScale(2, RoundingMode.HALF_UP);
    }
}