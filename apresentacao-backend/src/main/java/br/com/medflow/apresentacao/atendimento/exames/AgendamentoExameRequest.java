package br.com.medflow.apresentacao.atendimento.exames;

import java.time.LocalDateTime;

public record AgendamentoExameRequest(
    Long pacienteId,
    Long medicoId,
    String tipoExame,
    LocalDateTime dataHora,
    Long responsavelId
) {}
