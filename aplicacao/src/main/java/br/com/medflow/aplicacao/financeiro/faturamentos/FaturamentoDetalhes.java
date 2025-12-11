package br.com.medflow.aplicacao.financeiro.faturamentos;

import br.com.medflow.dominio.financeiro.faturamentos.StatusFaturamento;
import br.com.medflow.dominio.financeiro.faturamentos.TipoProcedimento;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO para exibir todos os detalhes de um faturamento, incluindo histórico.
 */
public class FaturamentoDetalhes {
    private final String id;
    private final String pacienteId;
    private final TipoProcedimento tipoProcedimento;
    private final String descricaoProcedimento;
    private final BigDecimal valor;
    private final String metodoPagamento;
    private final StatusFaturamento status;
    private final LocalDateTime dataHoraFaturamento;
    private final String usuarioResponsavel;
    private final String observacoes;
    private final BigDecimal valorPadrao;
    private final String justificativaValorDiferente;
    private final List<HistoricoDetalhes> historico;

    public FaturamentoDetalhes(
        String id, 
        String pacienteId, 
        TipoProcedimento tipoProcedimento,
        String descricaoProcedimento, 
        BigDecimal valor, 
        String metodoPagamento,
        StatusFaturamento status, 
        LocalDateTime dataHoraFaturamento,
        String usuarioResponsavel,
        String observacoes,
        BigDecimal valorPadrao,
        String justificativaValorDiferente,
        List<HistoricoDetalhes> historico) {
        
        this.id = id;
        this.pacienteId = pacienteId;
        this.tipoProcedimento = tipoProcedimento;
        this.descricaoProcedimento = descricaoProcedimento;
        this.valor = valor;
        this.metodoPagamento = metodoPagamento;
        this.status = status;
        this.dataHoraFaturamento = dataHoraFaturamento;
        this.usuarioResponsavel = usuarioResponsavel;
        this.observacoes = observacoes;
        this.valorPadrao = valorPadrao;
        this.justificativaValorDiferente = justificativaValorDiferente;
        this.historico = historico;
    }

    /** Detalhes de uma entrada do histórico. */
    public record HistoricoDetalhes(
        String acao,
        String descricao,
        String responsavel,
        LocalDateTime dataHora) {}
        
    // Getters
    public String getId() { return id; }
    public String getPacienteId() { return pacienteId; }
    public TipoProcedimento getTipoProcedimento() { return tipoProcedimento; }
    public String getDescricaoProcedimento() { return descricaoProcedimento; }
    public BigDecimal getValor() { return valor; }
    public String getMetodoPagamento() { return metodoPagamento; }
    public StatusFaturamento getStatus() { return status; }
    public LocalDateTime getDataHoraFaturamento() { return dataHoraFaturamento; }
    public String getUsuarioResponsavel() { return usuarioResponsavel; }
    public String getObservacoes() { return observacoes; }
    public BigDecimal getValorPadrao() { return valorPadrao; }
    public String getJustificativaValorDiferente() { return justificativaValorDiferente; }
    public List<HistoricoDetalhes> getHistorico() { return historico; }
}
