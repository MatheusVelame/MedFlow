package br.com.medflow.apresentacao.atendimento.exames;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;

import io.swagger.v3.oas.annotations.media.Schema;

public record AtualizacaoExameRequest(
    @NotNull
    @Schema(description = "ID do médico responsável (novo)")
    Long medicoId,

    @NotBlank
    @Schema(description = "Tipo do exame (novo)")
    String tipoExame,

    @NotNull
    @Schema(description = "Nova data e hora do exame (ISO-8601)")
    LocalDateTime dataHora,

    @NotNull
    @Schema(description = "ID do usuário/responsável que está realizando a alteração")
    Long responsavelId,

    @Schema(description = "Observações/Notas sobre a alteração (opcional)")
    String observacoes
) {}