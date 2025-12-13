package br.com.medflow.dominio.atendimento.consultas;

/**
 * Interface do Aggregate (Coleção) no padrão de projeto comportamental Iterator.
 * Define o método para criar uma instância do Iterator.
 */
public interface ColecaoHistorico<T> {
    
    /**
     * Cria e retorna um Iterador para percorrer a coleção de histórico.
     */
    IteradorHistorico<T> criarIterador();
}