package br.com.medflow.dominio.financeiro.faturamentos;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Faturamento {
    private FaturamentoId id;
    private PacienteId pacienteId;
    private TipoProcedimento tipoProcedimento;
    private String descricaoProcedimento;
    private Valor valor;
    private MetodoPagamento metodoPagamento;
    private StatusFaturamento status;
    private LocalDateTime dataHoraFaturamento;
    private UsuarioResponsavelId usuarioResponsavel;
    private String observacoes;
    private Valor valorPadrao; // Valor padrão do procedimento no momento do faturamento
    private String justificativaValorDiferente; // Justificativa se o valor for diferente do padrão

    private List<HistoricoFaturamento> historico = new ArrayList<>();

    public Faturamento(PacienteId pacienteId, TipoProcedimento tipoProcedimento, 
                      String descricaoProcedimento, Valor valor, MetodoPagamento metodoPagamento,
                      UsuarioResponsavelId usuarioResponsavel, String observacoes) {
        this.id = null; // Será definido pelo repositório
        if (pacienteId == null) throw new IllegalArgumentException("ID do paciente é obrigatório");
        if (tipoProcedimento == null) throw new IllegalArgumentException("Tipo do procedimento é obrigatório");
        if (descricaoProcedimento == null || descricaoProcedimento.trim().isEmpty()) 
            throw new IllegalArgumentException("Descrição do procedimento é obrigatória");
        if (valor == null) throw new IllegalArgumentException("Valor é obrigatório");
        if (metodoPagamento == null) throw new IllegalArgumentException("Método de pagamento é obrigatório");
        if (usuarioResponsavel == null) throw new IllegalArgumentException("Usuário responsável é obrigatório");
        
        this.pacienteId = pacienteId;
        this.tipoProcedimento = tipoProcedimento;
        this.descricaoProcedimento = descricaoProcedimento;
        this.valor = valor;
        this.metodoPagamento = metodoPagamento;
        this.usuarioResponsavel = usuarioResponsavel;
        this.observacoes = observacoes;
        this.dataHoraFaturamento = LocalDateTime.now();
        
        // Se o método de pagamento for dinheiro ou débito, marcar como pago automaticamente
        if (metodoPagamento.ehPagamentoAutomatico()) {
            this.status = StatusFaturamento.PAGO;
            adicionarEntradaHistorico(AcaoHistoricoFaturamento.CRIACAO, 
                                      "Faturamento criado e marcado como pago automaticamente (método: " + metodoPagamento.getMetodo() + ")", 
                                      usuarioResponsavel);
            adicionarEntradaHistorico(AcaoHistoricoFaturamento.PAGAMENTO, 
                                      "Faturamento marcado como pago automaticamente", 
                                      usuarioResponsavel);
        } else {
            this.status = StatusFaturamento.PENDENTE;
            adicionarEntradaHistorico(AcaoHistoricoFaturamento.CRIACAO, 
                                      "Faturamento criado com status Pendente", 
                                      usuarioResponsavel);
        }
    }

    // Getters
    public FaturamentoId getId() { return id; }
    public PacienteId getPacienteId() { return pacienteId; }
    public TipoProcedimento getTipoProcedimento() { return tipoProcedimento; }
    public String getDescricaoProcedimento() { return descricaoProcedimento; }
    public Valor getValor() { return valor; }
    public MetodoPagamento getMetodoPagamento() { return metodoPagamento; }
    public StatusFaturamento getStatus() { return status; }
    public LocalDateTime getDataHoraFaturamento() { return dataHoraFaturamento; }
    public UsuarioResponsavelId getUsuarioResponsavel() { return usuarioResponsavel; }
    public String getObservacoes() { return observacoes; }
    public Valor getValorPadrao() { return valorPadrao; }
    public String getJustificativaValorDiferente() { return justificativaValorDiferente; }
    public List<HistoricoFaturamento> getHistorico() { return new ArrayList<>(historico); }

    // Setters (apenas para o ID, que é definido pelo repositório)
    public void setId(FaturamentoId id) { this.id = id; }
    
    public void setStatus(StatusFaturamento status) { this.status = status; }

    public void marcarComoPago(UsuarioResponsavelId usuarioResponsavel) {
        if (this.status != StatusFaturamento.PENDENTE) {
            throw new IllegalStateException("Apenas faturamentos pendentes podem ser marcados como pagos.");
        }
        
        this.status = StatusFaturamento.PAGO;
        adicionarEntradaHistorico(AcaoHistoricoFaturamento.PAGAMENTO, 
                                  "Faturamento marcado como pago", 
                                  usuarioResponsavel);
    }

    public void cancelar(String motivo, UsuarioResponsavelId usuarioResponsavel) {
        if (this.status == StatusFaturamento.PAGO) {
            throw new IllegalStateException("Faturamentos pagos não podem ser cancelados.");
        }
        
        this.status = StatusFaturamento.CANCELADO;
        adicionarEntradaHistorico(AcaoHistoricoFaturamento.CANCELAMENTO,
            "Faturamento cancelado" + (motivo != null ? ". Motivo: " + motivo : ""),
            usuarioResponsavel);
    }

    public void atualizarObservacoes(String novasObservacoes, UsuarioResponsavelId usuarioResponsavel) {
        if (novasObservacoes == null || novasObservacoes.trim().isEmpty()) {
            return;
        }
        
        this.observacoes = novasObservacoes;
        adicionarEntradaHistorico(AcaoHistoricoFaturamento.ATUALIZACAO,
            "Observações atualizadas: " + novasObservacoes, usuarioResponsavel);
    }

    public void excluirLogicamente(String motivo, UsuarioResponsavelId usuarioResponsavel) {
        if (!this.status.podeSerExcluido()) {
            throw new IllegalStateException("Apenas faturamentos Pendente ou Inválido podem ser excluídos.");
        }
        
        if (motivo == null || motivo.trim().isEmpty()) {
            throw new IllegalArgumentException("Motivo da exclusão é obrigatório.");
        }
        
        StatusFaturamento statusAnterior = this.status;
        this.status = StatusFaturamento.REMOVIDO;
        
        String observacaoExclusao = String.format("Excluído logicamente. Status anterior: %s. Motivo: %s", 
            statusAnterior.getDescricao(), motivo);
        
        if (this.observacoes != null && !this.observacoes.trim().isEmpty()) {
            this.observacoes += " | " + observacaoExclusao;
        } else {
            this.observacoes = observacaoExclusao;
        }
        
        adicionarEntradaHistorico(AcaoHistoricoFaturamento.EXCLUSAO_LOGICA,
            String.format("Faturamento excluído logicamente. Status anterior: %s. Motivo: %s", 
                statusAnterior.getDescricao(), motivo), usuarioResponsavel);
    }

    public void registrarTentativaExclusaoNegada(String motivo, UsuarioResponsavelId usuarioResponsavel) {
        adicionarEntradaHistorico(AcaoHistoricoFaturamento.TENTATIVA_EXCLUSAO_NEGADA,
            String.format("Tentativa de exclusão negada. Status atual: %s. Motivo informado: %s", 
                this.status.getDescricao(), motivo), usuarioResponsavel);
    }

    public void alterarStatus(StatusFaturamento novoStatus, String motivo, UsuarioResponsavelId usuarioResponsavel, boolean podeReverterPago) {
        if (novoStatus == null) {
            throw new IllegalArgumentException("Novo status é obrigatório");
        }
        
        // Validação especial para reversão de status "Pago"
        if (this.status == StatusFaturamento.PAGO && novoStatus != StatusFaturamento.PAGO) {
            if (!podeReverterPago) {
                throw new IllegalStateException("Reversão de status 'Pago' requer permissão especial");
            }
        }
        
        StatusFaturamento statusAnterior = this.status;
        this.status = novoStatus;
        
        String descricaoAlteracao = String.format("Status alterado de '%s' para '%s'", 
            statusAnterior.getDescricao(), novoStatus.getDescricao());
        
        if (motivo != null && !motivo.trim().isEmpty()) {
            descricaoAlteracao += ". Motivo: " + motivo;
        }
        
        adicionarEntradaHistorico(AcaoHistoricoFaturamento.ALTERACAO_STATUS, descricaoAlteracao, usuarioResponsavel);
    }

    public void registrarTentativaAlteracaoNegada(String motivo, UsuarioResponsavelId usuarioResponsavel) {
        adicionarEntradaHistorico(AcaoHistoricoFaturamento.TENTATIVA_ALTERACAO_NEGADA,
            String.format("Tentativa de alteração de status negada. Status atual: %s. Motivo informado: %s", 
                this.status.getDescricao(), motivo), usuarioResponsavel);
    }

    private void adicionarEntradaHistorico(AcaoHistoricoFaturamento acao, String descricao, 
                                         UsuarioResponsavelId responsavelId) {
        var entrada = new HistoricoFaturamento(acao, descricao, responsavelId, LocalDateTime.now());
        this.historico.add(entrada);
    }

    // Exceção específica para valores diferentes do padrão
    public static class ValorDiferenteDoPadraoException extends IllegalStateException {
        private static final long serialVersionUID = 1L;
        public ValorDiferenteDoPadraoException(String s, Throwable cause) { super(s, cause); }
        public ValorDiferenteDoPadraoException(String s) { super(s); }
    }
}
