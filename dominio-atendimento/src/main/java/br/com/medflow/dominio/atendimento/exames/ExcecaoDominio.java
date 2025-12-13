package br.com.medflow.dominio.atendimento.exames;

public class ExcecaoDominio extends RuntimeException {
    public ExcecaoDominio(String message) {
        super(message);
    }
}