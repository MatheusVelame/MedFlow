package br.com.medflow.apresentacao.atendimento.exames;

import java.time.LocalDateTime;
import br.com.medflow.dominio.atendimento.exames.Exame;

public record ExameResponse(
    Long id,
    Long pacienteId,
    Long medicoId,
    String tipoExame,
    LocalDateTime dataHora,
    String status
) {
    public static ExameResponse de(Exame exame) {
        return new ExameResponse(
            exame.getId().getValor(),
            exame.getPacienteId(),
            exame.getMedicoId(),
            exame.getTipoExame(),
            exame.getDataHora(),
            exame.getStatus().name()
        );
    }
}