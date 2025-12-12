package br.com.medflow.aplicacao.excecoes;

/**
 * Exceção lançada quando um recurso não é encontrado.
 * Deve resultar em resposta HTTP 404.
 */
public class RecursoNaoEncontradoException extends RuntimeException {
    
    public RecursoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
    
    public RecursoNaoEncontradoException(String recurso, String id) {
        super(String.format("%s com ID '%s' não encontrado", recurso, id));
    }
}
