package br.com.medflow.dominio.financeiro.faturamentos;

import java.util.List;
import java.util.Optional;

public class FaturamentoServico {
    private final FaturamentoRepositorio repositorio;
    private final TabelaPrecosServico tabelaPrecosServico;

    public FaturamentoServico(FaturamentoRepositorio repositorio, TabelaPrecosServico tabelaPrecosServico) {
        if (repositorio == null) throw new IllegalArgumentException("O repositório de faturamentos não pode ser nulo");
        if (tabelaPrecosServico == null) throw new IllegalArgumentException("O serviço de tabela de preços não pode ser nulo");
        this.repositorio = repositorio;
        this.tabelaPrecosServico = tabelaPrecosServico;
    }

    public Faturamento registrarFaturamento(PacienteId pacienteId, TipoProcedimento tipoProcedimento,
                                          String descricaoProcedimento, Valor valor, MetodoPagamento metodoPagamento,
                                          UsuarioResponsavelId usuarioResponsavel, String observacoes) {
        // RN: O valor deve ser positivo
        if (valor.getValor().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("valor deve ser positivo");
        }

        // RN: O método de pagamento é obrigatório
        if (metodoPagamento == null) {
            throw new IllegalArgumentException("método de pagamento é obrigatório");
        }

        // RN: O valor deve ser compatível com os valores cadastrados previamente para o procedimento.
        // Se o valor informado for diferente do padrão e não houver justificativa, lançar exceção.
        if (!tabelaPrecosServico.valorEhCompativel(valor, tipoProcedimento, descricaoProcedimento)) {
            if (observacoes == null || observacoes.trim().isEmpty()) {
                throw new Faturamento.ValorDiferenteDoPadraoException("justificativa obrigatória para valor diferente da tabela");
            }
        }

        var faturamento = new Faturamento(pacienteId, tipoProcedimento, descricaoProcedimento, 
                                          valor, metodoPagamento, usuarioResponsavel, observacoes);
        repositorio.salvar(faturamento);
        return faturamento;
    }

    public Faturamento obter(FaturamentoId id) {
        var faturamento = repositorio.obter(id);
        if (faturamento == null) {
            throw new IllegalArgumentException("Faturamento não encontrado: " + id);
        }
        return faturamento;
    }

    public void marcarComoPago(FaturamentoId id, UsuarioResponsavelId usuarioResponsavel) {
        var faturamento = obter(id);
        faturamento.marcarComoPago(usuarioResponsavel);
        repositorio.salvar(faturamento);
    }

    public void cancelar(FaturamentoId id, String motivo, UsuarioResponsavelId usuarioResponsavel) {
        var faturamento = obter(id);
        faturamento.cancelar(motivo, usuarioResponsavel);
        repositorio.salvar(faturamento);
    }

    public void atualizarObservacoes(FaturamentoId id, String observacoes, UsuarioResponsavelId usuarioResponsavel) {
        var faturamento = obter(id);
        faturamento.atualizarObservacoes(observacoes, usuarioResponsavel);
        repositorio.salvar(faturamento);
    }

    public void excluirLogicamente(FaturamentoId id, String motivo, UsuarioResponsavelId usuarioResponsavel, boolean ehAdministrador) {
        var faturamento = obter(id);
        
        if (!ehAdministrador) {
            faturamento.registrarTentativaExclusaoNegada(motivo, usuarioResponsavel);
            repositorio.salvar(faturamento);
            throw new IllegalStateException("Apenas administradores podem excluir faturamentos");
        }
        
        // Verificar se o status permite exclusão
        if (!faturamento.getStatus().podeSerExcluido()) {
            faturamento.registrarTentativaExclusaoNegada(motivo, usuarioResponsavel);
            repositorio.salvar(faturamento);
            throw new IllegalStateException("Apenas faturamentos Pendente ou Inválido podem ser excluídos.");
        }
        
        faturamento.excluirLogicamente(motivo, usuarioResponsavel);
        repositorio.salvar(faturamento);
    }

    public void alterarStatus(FaturamentoId id, StatusFaturamento novoStatus, String motivo, 
                             UsuarioResponsavelId usuarioResponsavel, PermissaoUsuario permissao) {
        if (!permissao.podeAlterarStatus()) {
            var faturamento = obter(id);
            faturamento.registrarTentativaAlteracaoNegada(motivo, usuarioResponsavel);
            repositorio.salvar(faturamento);
            throw new IllegalStateException("Permissão administrativa necessária");
        }
        
        var faturamento = obter(id);
        var statusAnterior = faturamento.getStatus();
        faturamento.alterarStatus(novoStatus, motivo, usuarioResponsavel, permissao.podeReverterPago());
        repositorio.salvar(faturamento);
        
        // Disparar notificações internas baseadas no novo status
        if (novoStatus == StatusFaturamento.PAGO) {
            // Notificação de contabilização
            System.out.println("Notificação interna: Contabilização automática para faturamento " + id.getValor());
        } else if (novoStatus == StatusFaturamento.CANCELADO || 
                   (statusAnterior == StatusFaturamento.PAGO && novoStatus == StatusFaturamento.PENDENTE)) {
            // Notificação de ajuste contábil
            System.out.println("Notificação interna: Ajuste contábil para faturamento " + id.getValor());
        }
    }

    public List<Faturamento> pesquisar() {
        return repositorio.pesquisar();
    }

    public List<Faturamento> pesquisarPorPaciente(PacienteId pacienteId) {
        return repositorio.pesquisarPorPaciente(pacienteId);
    }

    public List<Faturamento> pesquisarPorStatus(StatusFaturamento status) {
        return repositorio.pesquisarPorStatus(status);
    }

    public List<Faturamento> pesquisarPorTipoProcedimento(TipoProcedimento tipoProcedimento) {
        return repositorio.pesquisarPorTipoProcedimento(tipoProcedimento);
    }

    public List<Faturamento> pesquisarPorPeriodo(java.time.LocalDateTime dataInicio, java.time.LocalDateTime dataFim) {
        return repositorio.pesquisarPorPeriodo(dataInicio, dataFim);
    }

    public List<Faturamento> pesquisarExcluindoRemovidos() {
        return repositorio.pesquisarExcluindoRemovidos();
    }

    public List<Faturamento> pesquisarApenasRemovidos() {
        return repositorio.pesquisarApenasRemovidos();
    }

    // Exceção específica para valores diferentes do padrão
    public static class ValorDiferenteDoPadraoException extends IllegalStateException {
        private static final long serialVersionUID = 1L;
        public ValorDiferenteDoPadraoException(String s, Throwable cause) { super(s, cause); }
        public ValorDiferenteDoPadraoException(String s) { super(s); }
    }
}
