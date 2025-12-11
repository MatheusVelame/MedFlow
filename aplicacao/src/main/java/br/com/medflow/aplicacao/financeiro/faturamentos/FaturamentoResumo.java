package br.com.medflow.aplicacao.financeiro.faturamentos;

import br.com.medflow.dominio.financeiro.faturamentos.StatusFaturamento;
import br.com.medflow.dominio.financeiro.faturamentos.TipoProcedimento;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * DTO para exibir um resumo dos faturamentos em listas ou grids.
 */
public class FaturamentoResumo {
    private final String id;
    private final String pacienteId;
    private final TipoProcedimento tipoProcedimento;
    private final String descricaoProcedimento;
    private final BigDecimal valor;
    private final String metodoPagamento;
    private final StatusFaturamento status;
    private final LocalDateTime dataHoraFaturamento;

    public FaturamentoResumo(String id, String pacienteId, TipoProcedimento tipoProcedimento,
                             String descricaoProcedimento, BigDecimal valor, String metodoPagamento,
                             StatusFaturamento status, LocalDateTime dataHoraFaturamento) {
        this.id = id;
        this.pacienteId = pacienteId;
        this.tipoProcedimento = Objects.requireNonNull(tipoProcedimento);
        this.descricaoProcedimento = descricaoProcedimento;
        this.valor = valor;
        this.metodoPagamento = metodoPagamento;
        this.status = Objects.requireNonNull(status);
        this.dataHoraFaturamento = dataHoraFaturamento;
    }

    // Getters
    public String getId() { return id; }
    public String getPacienteId() { return pacienteId; }
    public TipoProcedimento getTipoProcedimento() { return tipoProcedimento; }
    public String getDescricaoProcedimento() { return descricaoProcedimento; }
    public BigDecimal getValor() { return valor; }
    public String getMetodoPagamento() { return metodoPagamento; }
    public StatusFaturamento getStatus() { return status; }
    public LocalDateTime getDataHoraFaturamento() { return dataHoraFaturamento; }
}
