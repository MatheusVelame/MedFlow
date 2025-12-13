package br.com.medflow.apresentacao.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import br.com.medflow.apresentacao.atendimento.exames.ErroNegocioResponse;
import br.com.medflow.dominio.atendimento.exames.EntidadeNaoEncontradaException;
import br.com.medflow.dominio.atendimento.exames.ConflitoNegocioException;
import br.com.medflow.dominio.atendimento.exames.ValidacaoNegocioException;
import br.com.medflow.dominio.atendimento.exames.ExcecaoDominio;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntidadeNaoEncontradaException.class)
    @ResponseBody
    public ResponseEntity<ErroNegocioResponse> handleNotFound(EntidadeNaoEncontradaException ex) {
        String message = ex.getMessage();
        String code = extractCode(message, "ENTIDADE_NAO_ENCONTRADA");
        String userMessage = extractUserMessage(message);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErroNegocioResponse(code, userMessage));
    }

    @ExceptionHandler(ConflitoNegocioException.class)
    @ResponseBody
    public ResponseEntity<ErroNegocioResponse> handleConflict(ConflitoNegocioException ex) {
        String message = ex.getMessage();
        String code = extractCode(message, "CONFLITO_NEGOCIO");
        String userMessage = extractUserMessage(message);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErroNegocioResponse(code, userMessage));
    }

    @ExceptionHandler(ValidacaoNegocioException.class)
    @ResponseBody
    public ResponseEntity<ErroNegocioResponse> handleBadRequest(ValidacaoNegocioException ex) {
        String message = ex.getMessage();
        String code = extractCode(message, "VALIDACAO_NEGOCIO");
        String userMessage = extractUserMessage(message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErroNegocioResponse(code, userMessage));
    }

    @ExceptionHandler(ExcecaoDominio.class)
    @ResponseBody
    public ResponseEntity<ErroNegocioResponse> handleGenericDomain(ExcecaoDominio ex) {
        String message = ex.getMessage();
        String code = extractCode(message, "ERRO_DOMINIO");
        String userMessage = extractUserMessage(message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErroNegocioResponse(code, userMessage));
    }

    // Fallback
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<ErroNegocioResponse> handleOther(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErroNegocioResponse("ERRO_INTERNO", "Erro interno no servidor"));
    }

    private String extractCode(String message, String defaultCode) {
        if (message == null) return defaultCode;
        int idx = message.indexOf(":" );
        if (idx > 0) return message.substring(0, idx).trim();
        return defaultCode;
    }

    private String extractUserMessage(String message) {
        if (message == null) return "";
        int idx = message.indexOf(":" );
        if (idx > 0 && idx + 1 < message.length()) return message.substring(idx + 1).trim();
        return message;
    }
}
