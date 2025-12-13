package br.com.medflow.apresentacao.atendimento.exames;

public record CancelamentoExameRequest(
    String motivo,
    Long responsavelId
) {}