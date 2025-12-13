package br.com.medflow.apresentacao.atendimento.exames;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import br.com.medflow.dominio.atendimento.exames.Exame;
import io.swagger.v3.oas.annotations.media.Schema;

public record ExameResponse(
    Long id,
    Long pacienteId,
    Long medicoId,
    String tipoExame,
    LocalDateTime dataHora,
    @Schema(description = "Status do exame (controlado pelo backend). Valores possíveis: AGENDADO, EM_ANDAMENTO, PENDENTE, REALIZADO, CANCELADO", example = "AGENDADO")
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