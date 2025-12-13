package br.com.medflow.dominio.financeiro.folhapagamento;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * PADRÃO STRATEGY - Implementação Concreta
 * Estratégia de cálculo para folhas de PAGAMENTO de funcionários CLT.
 * Aplica descontos de INSS (11%) e IRRF (15%).
 */
public class CalculoCLTStrategy implements CalculoFolhaStrategy {

    private static final BigDecimal ALIQUOTA_INSS = new BigDecimal("0.11");
    private static final BigDecimal ALIQUOTA_IRRF = new BigDecimal("0.15");

    @Override
    public BigDecimal calcularValorLiquido(FolhaPagamento folhaPagamento) {
        BigDecimal valorBruto = calcularValorBruto(folhaPagamento);
        BigDecimal descontos = calcularDescontos(folhaPagamento);

        return valorBruto
                .subtract(descontos)
                .setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calcularValorBruto(FolhaPagamento folhaPagamento) {
        return folhaPagamento.getSalarioBase()
                .add(folhaPagamento.getBeneficios())
                .setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calcularDescontos(FolhaPagamento folhaPagamento) {
        BigDecimal salarioBase = folhaPagamento.getSalarioBase();

        // INSS: 11% do salário base
        BigDecimal descontoINSS = salarioBase
                .multiply(ALIQUOTA_INSS)
                .setScale(2, RoundingMode.HALF_UP);

        // Base para IRRF: salário - INSS
        BigDecimal baseIRRF = salarioBase.subtract(descontoINSS);

        // IRRF: 15% da base após INSS
        BigDecimal descontoIRRF = baseIRRF
                .multiply(ALIQUOTA_IRRF)
                .setScale(2, RoundingMode.HALF_UP);

        return descontoINSS.add(descontoIRRF);
    }
}