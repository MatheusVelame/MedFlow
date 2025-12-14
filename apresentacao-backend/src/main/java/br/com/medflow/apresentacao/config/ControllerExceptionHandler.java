package br.com.medflow.apresentacao.config;

import br.com.medflow.dominio.referencia.especialidades.RegraNegocioException;
import br.com.medflow.apresentacao.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<ErrorResponse> handleRegraNegocio(RegraNegocioException ex) {
        // Compatibilidade: nem todas as versões de RegraNegocioException expõem getErrors()
        // Usamos reflection para tentar obter o mapa de erros se disponível.
        Map<String, String> errors = null;
        try {
            java.lang.reflect.Method m = ex.getClass().getMethod("getErrors");
            Object o = m.invoke(ex);
            if (o instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, String> maybe = (Map<String, String>) o;
                errors = maybe;
            }
        } catch (NoSuchMethodException | IllegalAccessException | java.lang.reflect.InvocationTargetException e) {
            // Método não disponível ou inacessível: mantemos errors = null
            errors = null;
        }

        ErrorResponse body = new ErrorResponse(ex.getMessage(), errors == null || errors.isEmpty() ? null : errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException e) {
        // Retorna HTTP 400 (Bad Request) com a mensagem de validação no corpo
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", e.getMessage()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrity(DataIntegrityViolationException ex) {
        // Tenta extrair a causa raiz e inspecionar a mensagem para identificar o campo
        Throwable root = ex;
        while (root.getCause() != null) root = root.getCause();
        String rootMsg = root.getMessage() != null ? root.getMessage() : ex.getMessage();

        // Mapear heurística para campo 'nome' quando violação de unicidade na tabela de especialidades
        Map<String, String> errors = new HashMap<>();
        String lower = rootMsg == null ? "" : rootMsg.toLowerCase();
        if (lower.contains("unique") || lower.contains("constraint") || lower.contains("23505") || lower.contains("duplicate")) {
            // Se aparece o nome da tabela ou da coluna, mapeia para o campo 'nome'
            if (lower.contains("especialidade") || lower.contains("especialidades") || lower.contains("nome")) {
                errors.put("nome", "Já existe uma especialidade com este nome");
            }
        }

        ErrorResponse body = new ErrorResponse("Violação de integridade de dados", errors.isEmpty() ? null : errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        // Handler genérico: retorna 500 com mensagem amigável
        String msg = ex.getMessage() != null ? ex.getMessage() : "Erro interno no servidor";
        ErrorResponse body = new ErrorResponse(msg, null);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}