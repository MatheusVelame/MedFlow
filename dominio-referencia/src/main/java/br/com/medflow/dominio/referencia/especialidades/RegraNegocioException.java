package br.com.medflow.dominio.referencia.especialidades;

public class RegraNegocioException extends RuntimeException {

    public RegraNegocioException(String mensagem) {
        super(mensagem);
    }
    
    // Construtor adicional para conformidade com a estrutura do projeto, se necessário.
    public RegraNegocioException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}