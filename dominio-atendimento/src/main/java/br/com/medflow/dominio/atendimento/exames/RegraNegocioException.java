package br.com.medflow.dominio.atendimento.exames;

public class RegraNegocioException extends RuntimeException {
 public RegraNegocioException(String mensagem) {
     super(mensagem);
 }
}
