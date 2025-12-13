package br.com.medflow.dominio.referencia.especialidades;

import java.util.Collections;
import java.util.Map;

public class RegraNegocioException extends RuntimeException {

    private final Map<String, String> errors;

    public RegraNegocioException(String mensagem) {
        super(mensagem);
        this.errors = Collections.emptyMap();
    }

    public RegraNegocioException(String mensagem, Throwable causa) {
        super(mensagem, causa);
        this.errors = Collections.emptyMap();
    }

    // Novo construtor que aceita um mapa de erros por campo
    public RegraNegocioException(String mensagem, Map<String, String> errors) {
        super(mensagem);
        this.errors = errors == null ? Collections.emptyMap() : errors;
    }

    public Map<String, String> getErrors() {
        return errors;
    }
}