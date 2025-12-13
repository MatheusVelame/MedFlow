package br.com.medflow.apresentacao.atendimento.exames;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CancelamentoExameRequest(
    @NotBlank
    String motivo,

    @NotNull
    Long responsavelId
) {}