package br.com.medflow.apresentacao.atendimento.exames;

import java.time.LocalDateTime;

import br.com.medflow.dominio.atendimento.exames.HistoricoEntrada;

public record HistoricoEntradaResponse(
    LocalDateTime dataHora,
    String acao,
    String descricao,
    Long responsavelId
) {
    public static HistoricoEntradaResponse de(HistoricoEntrada h) {
        return new HistoricoEntradaResponse(
            h.getDataHora(),
            h.getAcao().name(),
            h.getDescricao(),
            (h.getUsuario() != null) ? h.getUsuario().getValor() : null
        );
    }
}
