package br.com.medflow.apresentacao.atendimento.exames;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import br.com.medflow.dominio.atendimento.exames.Exame;

public record ExameDetalheResponse(
    Long id,
    Long pacienteId,
    Long medicoId,
    String tipoExame,
    LocalDateTime dataHora,
    String status,
    List<HistoricoEntradaResponse> historico
) {
    public static ExameDetalheResponse de(Exame exame) {
        var historico = exame.getHistorico().stream()
            .map(HistoricoEntradaResponse::de)
            .collect(Collectors.toList());

        return new ExameDetalheResponse(
            exame.getId().getValor(),
            exame.getPacienteId(),
            exame.getMedicoId(),
            exame.getTipoExame(),
            exame.getDataHora(),
            exame.getStatus().name(),
            historico
        );
    }
}
