package br.com.medflow.infraestrutura.persistencia.jpa.financeiro;

import br.com.medflow.dominio.financeiro.faturamentos.StatusFaturamento;
import br.com.medflow.dominio.financeiro.faturamentos.TipoProcedimento;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "faturamentos")
public class FaturamentoJpa {
    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "paciente_id", nullable = false)
    private Integer pacienteId;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_procedimento", nullable = false)
    private TipoProcedimento tipoProcedimento;

    @Column(name = "descricao_procedimento", nullable = false, length = 255)
    private String descricaoProcedimento;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

    @Column(name = "metodo_pagamento", nullable = false, length = 50)
    private String metodoPagamento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusFaturamento status;

    @Column(name = "data_hora_faturamento", nullable = false)
    private LocalDateTime dataHoraFaturamento;

    @Column(name = "usuario_responsavel", nullable = false, length = 36)
    private String usuarioResponsavel;

    @Column(columnDefinition = "TEXT")
    private String observacoes;

    @Column(name = "valor_padrao", precision = 10, scale = 2)
    private BigDecimal valorPadrao;

    @Column(name = "justificativa_valor_diferente", columnDefinition = "TEXT")
    private String justificativaValorDiferente;

    public FaturamentoJpa() {}

    // Getters e Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Integer getPacienteId() { return pacienteId; }
    public void setPacienteId(Integer pacienteId) { this.pacienteId = pacienteId; }

    public TipoProcedimento getTipoProcedimento() { return tipoProcedimento; }
    public void setTipoProcedimento(TipoProcedimento tipoProcedimento) { this.tipoProcedimento = tipoProcedimento; }

    public String getDescricaoProcedimento() { return descricaoProcedimento; }
    public void setDescricaoProcedimento(String descricaoProcedimento) { this.descricaoProcedimento = descricaoProcedimento; }

    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }

    public String getMetodoPagamento() { return metodoPagamento; }
    public void setMetodoPagamento(String metodoPagamento) { this.metodoPagamento = metodoPagamento; }

    public StatusFaturamento getStatus() { return status; }
    public void setStatus(StatusFaturamento status) { this.status = status; }

    public LocalDateTime getDataHoraFaturamento() { return dataHoraFaturamento; }
    public void setDataHoraFaturamento(LocalDateTime dataHoraFaturamento) { this.dataHoraFaturamento = dataHoraFaturamento; }

    public String getUsuarioResponsavel() { return usuarioResponsavel; }
    public void setUsuarioResponsavel(String usuarioResponsavel) { this.usuarioResponsavel = usuarioResponsavel; }

    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }

    public BigDecimal getValorPadrao() { return valorPadrao; }
    public void setValorPadrao(BigDecimal valorPadrao) { this.valorPadrao = valorPadrao; }

    public String getJustificativaValorDiferente() { return justificativaValorDiferente; }
    public void setJustificativaValorDiferente(String justificativaValorDiferente) { 
        this.justificativaValorDiferente = justificativaValorDiferente; 
    }
}
