package br.com.medflow.apresentacao.atendimento.exames;

import java.time.LocalDateTime;

public record AtualizacaoExameRequest(
    Long medicoId,
    String tipoExame,
    LocalDateTime dataHora,
    Long responsavelId
) {}