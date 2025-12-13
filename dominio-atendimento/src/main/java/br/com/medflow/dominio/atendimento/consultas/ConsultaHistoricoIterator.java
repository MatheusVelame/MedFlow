package br.com.medflow.dominio.atendimento.consultas;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * Implementação concreta do Iterador para a coleção de Histórico da Consulta.
 * Permite percorrer o histórico sequencialmente.
 */
public class ConsultaHistoricoIterator implements IteradorHistorico<HistoricoConsultaEntrada> {

    private final List<HistoricoConsultaEntrada> historico;
    private int posicao;

    /**
     * O construtor recebe a coleção interna do Agregado, encapsulando-a (espera-se cópia defensiva).
     */
    public ConsultaHistoricoIterator(List<HistoricoConsultaEntrada> historico) {
        this.historico = historico;
        this.posicao = 0;
    }

    @Override
    public boolean temProximo() {
        return posicao < historico.size();
    }

    @Override
    public HistoricoConsultaEntrada proximo() {
        if (!temProximo()) {
            // Tratamento de erro (critério de robustez)
            throw new NoSuchElementException("Não há mais itens no histórico da Consulta.");
        }
        HistoricoConsultaEntrada entrada = historico.get(posicao);
        posicao++;
        return entrada;
    }
}