package br.com.medflow.dominio.financeiro.folhapagamento;

/**
 * PADRÃO STRATEGY - Factory
 * Responsável por criar a estratégia de cálculo apropriada
 * baseada no tipo de registro e tipo de vínculo da folha de pagamento.
 */
public class CalculoFolhaStrategyFactory {

    /**
     * Cria a estratégia de cálculo apropriada para o tipo de registro e vínculo.
     *
     * @param tipoRegistro O tipo de registro da folha de pagamento
     * @param tipoVinculo O tipo de vínculo do funcionário
     * @return A estratégia de cálculo apropriada
     * @throws IllegalArgumentException se algum parâmetro for nulo ou não suportado
     */
    public static CalculoFolhaStrategy criar(TipoRegistro tipoRegistro, TipoVinculo tipoVinculo) {
        if (tipoRegistro == null) {
            throw new IllegalArgumentException("Tipo de registro não pode ser nulo");
        }
        if (tipoVinculo == null) {
            throw new IllegalArgumentException("Tipo de vínculo não pode ser nulo");
        }

        // AJUSTE nunca tem desconto, independente do vínculo
        if (tipoRegistro == TipoRegistro.AJUSTE) {
            return new CalculoAjusteStrategy();
        }

        // PAGAMENTO: depende do tipo de vínculo
        switch (tipoVinculo) {
            case CLT:
                return new CalculoCLTStrategy();
            case ESTAGIARIO:
            case PJ:
                return new CalculoSemDescontoStrategy();
            default:
                throw new IllegalArgumentException("Tipo de vínculo não suportado: " + tipoVinculo);
        }
    }
}