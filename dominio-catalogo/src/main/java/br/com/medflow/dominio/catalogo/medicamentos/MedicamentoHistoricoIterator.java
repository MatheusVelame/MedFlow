package br.com.medflow.dominio.catalogo.medicamentos;

import java.util.List;

/**
 * Implementação concreta do Iterador para a coleção de Histórico do Medicamento.
 * Recebe a lista interna do Medicamento e permite a navegação externa.
 */
public class MedicamentoHistoricoIterator implements IteradorHistorico<HistoricoEntrada> {
    
    private final List<HistoricoEntrada> historico;
    private int posicao;

    /**
     * O construtor recebe a coleção interna do Agregado, encapsulando-a.
     * @param historico Lista imutável de HistoricoEntrada a ser percorrida.
     */
    public MedicamentoHistoricoIterator(List<HistoricoEntrada> historico) {
        this.historico = historico;
        this.posicao = 0;
    }

    @Override
    public boolean temProximo() {
        return posicao < historico.size();
    }

    @Override
    public HistoricoEntrada proximo() {
        if (!temProximo()) {
            throw new IndexOutOfBoundsException("Não há mais itens no histórico.");
        }
        HistoricoEntrada entrada = historico.get(posicao);
        posicao++;
        return entrada;
    }
}