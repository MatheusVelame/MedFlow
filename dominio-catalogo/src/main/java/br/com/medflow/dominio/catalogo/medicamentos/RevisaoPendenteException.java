package br.com.medflow.dominio.catalogo.medicamentos;

public class RevisaoPendenteException extends IllegalStateException {
    
    private static final long serialVersionUID = 1L;

    public RevisaoPendenteException(String mensagem) {
        super(mensagem);
    }
}