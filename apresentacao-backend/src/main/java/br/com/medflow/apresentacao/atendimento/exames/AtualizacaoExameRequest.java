package br.com.medflow.apresentacao.atendimento.exames;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;

public record AtualizacaoExameRequest(
    @NotNull
    Long medicoId,

    @NotBlank
    String tipoExame,

    @NotNull
    LocalDateTime dataHora,

    @NotNull
    Long responsavelId
) {}