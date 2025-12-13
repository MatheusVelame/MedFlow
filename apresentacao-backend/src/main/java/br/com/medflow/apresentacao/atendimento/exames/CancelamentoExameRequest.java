package br.com.medflow.apresentacao.atendimento.exames;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;

public record CancelamentoExameRequest(
    @NotBlank
    @Schema(description = "Motivo do cancelamento", required = true)
    String motivo,

    @NotNull
    Long responsavelId
) {}