package br.com.medflow.apresentacao.atendimento.exames;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resposta padronizada para erros de negócio")
public record ErroNegocioResponse(
    @Schema(description = "Código do erro de negócio (Ex: HORARIO_CONFLITO, MEDICO_INDISPONIVEL)")
    String codigo,
    @Schema(description = "Mensagem legível descrevendo o erro")
    String mensagem
) {}
