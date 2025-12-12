package br.com.medflow.dominio.atendimento.consultas;

/**
 * Interface do Iterator (Iterador) no padrão de projeto comportamental Iterator.
 */
public interface IteradorHistorico<T> {
    
    boolean temProximo(); // Verifica se há mais elementos
    
    T proximo();          // Retorna o próximo elemento
}