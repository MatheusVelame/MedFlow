package br.com.medflow.apresentacao.atendimento.exames;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request para agendamento de exame. Note: o campo 'status' é controlado pelo backend e não deve ser enviado pelo cliente.")
public record AgendamentoExameRequest(
    @NotNull
    @Schema(description = "ID do paciente (não alterável após criação)")
    Long pacienteId,

    @NotNull
    @Schema(description = "ID do médico")
    Long medicoId,

    @NotBlank
    @Schema(description = "Tipo do exame (código)")
    String tipoExame,

    @NotNull
    @Schema(description = "Data e hora do exame (ISO-8601)")
    LocalDateTime dataHora,

    @NotNull
    @Schema(description = "ID do usuário/responsável que está realizando a operação")
    Long responsavelId
) {}