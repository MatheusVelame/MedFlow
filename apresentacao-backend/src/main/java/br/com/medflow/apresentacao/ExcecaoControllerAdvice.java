package br.com.medflow.apresentacao;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ExcecaoControllerAdvice {

    // Trata exceções de Domínio/Validação (IllegalArgumentException)
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErroResposta handleIllegalArgumentException(IllegalArgumentException ex) {
        return new ErroResposta(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
    }

    // Trata exceções de Domínio/Estado (IllegalStateException)
    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST) 
    @ResponseBody
    public ErroResposta handleIllegalStateException(IllegalStateException ex) {
        return new ErroResposta(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
    }
}

// Classe DTO simples para formatar a resposta JSON
class ErroResposta {
    private final int status;
    private final String mensagem;

    public ErroResposta(int status, String mensagem) {
        this.status = status;
        this.mensagem = mensagem;
    }

    public int getStatus() {
        return status;
    }

    public String getMensagem() {
        return mensagem;
    }
}