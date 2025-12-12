package br.com.medflow.dominio.financeiro.folhapagamento;

/**
 * PADRÃO STRATEGY - Factory
 * Responsável por criar a estratégia de cálculo apropriada
 * baseada no tipo de registro da folha de pagamento.
 */
public class CalculoFolhaStrategyFactory {

    /**
     * Cria a estratégia de cálculo apropriada para o tipo de registro.
     *
     * @param tipoRegistro O tipo de registro da folha de pagamento
     * @return A estratégia de cálculo apropriada
     * @throws IllegalArgumentException se o tipo de registro for nulo ou não suportado
     */
    public static CalculoFolhaStrategy criar(TipoRegistro tipoRegistro) {
        if (tipoRegistro == null) {
            throw new IllegalArgumentException("Tipo de registro não pode ser nulo");
        }

        switch (tipoRegistro) {
            case PAGAMENTO:
                return new CalculoPagamentoStrategy();
            case AJUSTE:
                return new CalculoAjusteStrategy();
            default:
                throw new IllegalArgumentException("Tipo de registro não suportado: " + tipoRegistro);
        }
    }
}