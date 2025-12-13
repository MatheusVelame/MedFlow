package br.com.medflow.apresentacao.atendimento.exames;

import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;

public record RegistroResultadoRequest(
    @Schema(description = "Descrição do resultado. Use 'PENDENTE' para marcar como pendente.")
    String descricao,

    @Schema(description = "Marcar se o exame deve ser vinculado ao laudo (ex.: arquivo anexado)")
    boolean vincularLaudo,

    @Schema(description = "Marcar se o exame deve ser vinculado ao prontuário do paciente")
    boolean vincularProntuario,

    @NotNull
    Long responsavelId
) {}
