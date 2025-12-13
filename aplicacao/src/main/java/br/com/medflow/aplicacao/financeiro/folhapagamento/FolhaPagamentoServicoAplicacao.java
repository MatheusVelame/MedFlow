package br.com.medflow.aplicacao.financeiro.folhapagamento;

import br.com.medflow.dominio.financeiro.folhapagamento.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class FolhaPagamentoServicoAplicacao {

    private final FolhaPagamentoServico servicoDominio;
    private final FolhaPagamentoRepositorioAplicacao repositorioAplicacao;

    public FolhaPagamentoServicoAplicacao(FolhaPagamentoServico servicoDominio,
                                          FolhaPagamentoRepositorioAplicacao repositorioAplicacao) {
        this.servicoDominio = servicoDominio;
        this.repositorioAplicacao = repositorioAplicacao;
    }

    public FolhaPagamentoDetalhes registrar(int funcionarioId, String periodoReferencia,
                                            TipoRegistro tipoRegistro, BigDecimal salarioBase,
                                            BigDecimal beneficios, String metodoPagamento,
                                            TipoVinculo tipoVinculo, int usuarioResponsavelId,
                                            boolean funcionarioAtivo) {
        UsuarioResponsavelId responsavelId = new UsuarioResponsavelId(usuarioResponsavelId);

        FolhaPagamento folha = servicoDominio.registrar(
                funcionarioId, periodoReferencia, tipoRegistro,
                salarioBase, beneficios, metodoPagamento, tipoVinculo,
                responsavelId, funcionarioAtivo
        );

        return repositorioAplicacao.obterDetalhes(folha.getId().getId())
                .orElseThrow(() -> new IllegalStateException("Erro ao recuperar folha criada"));
    }

    public FolhaPagamentoDetalhes obterDetalhes(int id) {
        return repositorioAplicacao.obterDetalhes(id)
                .orElseThrow(() -> new IllegalArgumentException("Folha não encontrada"));
    }

    public List<FolhaPagamentoResumo> listarTodos() {
        return repositorioAplicacao.listarResumos();
    }

    public List<FolhaPagamentoResumo> listarPorFuncionario(int funcionarioId) {
        return repositorioAplicacao.listarPorFuncionario(funcionarioId);
    }

    public List<FolhaPagamentoResumo> listarPorStatus(StatusFolha status) {
        return repositorioAplicacao.listarPorStatus(status);
    }

    public FolhaPagamentoDetalhes atualizarValores(int id, BigDecimal novoSalarioBase,
                                                   BigDecimal novosBeneficios, int usuarioResponsavelId) {
        UsuarioResponsavelId responsavelId = new UsuarioResponsavelId(usuarioResponsavelId);
        FolhaPagamentoId folhaId = new FolhaPagamentoId(id);

        servicoDominio.atualizarValores(folhaId, novoSalarioBase, novosBeneficios, responsavelId);

        return repositorioAplicacao.obterDetalhes(id)
                .orElseThrow(() -> new IllegalStateException("Erro ao recuperar folha atualizada"));
    }

    public FolhaPagamentoDetalhes alterarStatus(int id, StatusFolha novoStatus, int usuarioResponsavelId) {
        UsuarioResponsavelId responsavelId = new UsuarioResponsavelId(usuarioResponsavelId);
        FolhaPagamentoId folhaId = new FolhaPagamentoId(id);

        servicoDominio.alterarStatus(folhaId, novoStatus, responsavelId);

        return repositorioAplicacao.obterDetalhes(id)
                .orElseThrow(() -> new IllegalStateException("Erro ao recuperar folha atualizada"));
    }

    public void remover(int id, int usuarioResponsavelId) {
        UsuarioResponsavelId responsavelId = new UsuarioResponsavelId(usuarioResponsavelId);
        FolhaPagamentoId folhaId = new FolhaPagamentoId(id);

        servicoDominio.remover(folhaId, responsavelId);
    }
}