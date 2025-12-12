package br.com.medflow.apresentacao.config;

import br.com.medflow.dominio.referencia.especialidades.RegraNegocioException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<String> handleRegraNegocio(RegraNegocioException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}
