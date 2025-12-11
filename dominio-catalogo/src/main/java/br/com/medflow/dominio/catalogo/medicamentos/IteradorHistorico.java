package br.com.medflow.dominio.catalogo.medicamentos;

/**
 * Interface do Iterator (Iterador) no padrão de projeto comportamental Iterator.
 * Permite percorrer uma coleção de forma sequencial sem expor sua estrutura interna.
 */
public interface IteradorHistorico<T> {
    /**
     * Verifica se há um próximo elemento na iteração.
     * @return true se houver mais elementos, false caso contrário.
     */
    boolean temProximo(); // hasNext()
    
    /**
     * Retorna o próximo elemento da coleção.
     * @return O próximo elemento (HistoricoEntrada, neste caso).
     */
    T proximo();          // next()
}