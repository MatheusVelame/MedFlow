package br.com.medflow.dominio.catalogo.medicamentos;

/**
 * Interface do Aggregate (Coleção) no padrão de projeto comportamental Iterator.
 * Define o método para criar uma instância do Iterator.
 */
public interface ColecaoHistorico {
    /**
     * Cria e retorna um Iterador para percorrer a coleção de histórico.
     * @return Uma instância de IteradorHistorico para HistoricoEntrada.
     */
    IteradorHistorico<HistoricoEntrada> criarIterador();
}