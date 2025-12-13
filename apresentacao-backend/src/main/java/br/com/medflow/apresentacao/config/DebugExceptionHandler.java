package br.com.medflow.apresentacao.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Handler temporário (apenas para debug local) que retorna o stacktrace
 * no corpo da resposta para facilitar diagnóstico de erros 500.
 * REMOVA/COMENTE em ambientes de produção.
 */
@ControllerAdvice
public class DebugExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleAll(Exception ex) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        pw.flush();
        String stack = sw.toString();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(stack);
    }
}
