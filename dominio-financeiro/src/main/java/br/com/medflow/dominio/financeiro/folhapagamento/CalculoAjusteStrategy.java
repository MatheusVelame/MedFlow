package br.com.medflow.dominio.financeiro.folhapagamento;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * PADRÃO STRATEGY - Implementação Concreta
 * Estratégia de cálculo para folhas de AJUSTE.
 * Não aplica descontos, pois ajustes são valores complementares.
 */
public class CalculoAjusteStrategy implements CalculoFolhaStrategy {

    @Override
    public BigDecimal calcularValorLiquido(FolhaPagamento folhaPagamento) {
        // Para ajustes, valor líquido = salário base + benefícios (sem descontos)
        return folhaPagamento.getSalarioBase()
                .add(folhaPagamento.getBeneficios())
                .setScale(2, RoundingMode.HALF_UP);
    }
}