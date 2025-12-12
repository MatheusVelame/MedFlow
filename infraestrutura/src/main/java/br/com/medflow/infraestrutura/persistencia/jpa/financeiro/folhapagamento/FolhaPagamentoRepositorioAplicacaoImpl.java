package br.com.medflow.infraestrutura.persistencia.jpa.financeiro.folhapagamento;

import br.com.medflow.aplicacao.financeiro.folhapagamento.*;
import br.com.medflow.dominio.financeiro.folhapagamento.*;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementação JPA do repositório de aplicação.
 */
@Component
public class FolhaPagamentoRepositorioAplicacaoImpl implements FolhaPagamentoRepositorioAplicacao {

    private final FolhaPagamentoJpaRepository jpaRepository;

    public FolhaPagamentoRepositorioAplicacaoImpl(FolhaPagamentoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<FolhaPagamentoDetalhes> obterDetalhes(int id) {
        return jpaRepository.findById(id)
                .map(this::converterParaDetalhes);
    }

    @Override
    public List<FolhaPagamentoResumo> listarResumos() {
        return jpaRepository.findAll().stream()
                .map(this::converterParaResumo)
                .collect(Collectors.toList());
    }

    @Override
    public List<FolhaPagamentoResumo> listarPorFuncionario(int funcionarioId) {
        return jpaRepository.findByFuncionarioId(funcionarioId).stream()
                .map(this::converterParaResumo)
                .collect(Collectors.toList());
    }

    @Override
    public List<FolhaPagamentoResumo> listarPorStatus(StatusFolha status) {
        return jpaRepository.findByStatus(status).stream()
                .map(this::converterParaResumo)
                .collect(Collectors.toList());
    }

    private FolhaPagamentoDetalhes converterParaDetalhes(FolhaPagamentoJpa jpa) {
        BigDecimal valorLiquido = calcularValorLiquido(jpa);

        return new FolhaPagamentoDetalhes(
                jpa.getId(),
                jpa.getFuncionarioId(),
                jpa.getPeriodoReferencia(),
                jpa.getTipoRegistro(),
                jpa.getSalarioBase(),
                jpa.getBeneficios(),
                jpa.getMetodoPagamento(),
                jpa.getStatus(),
                valorLiquido
        );
    }

    private FolhaPagamentoResumo converterParaResumo(FolhaPagamentoJpa jpa) {
        BigDecimal valorLiquido = calcularValorLiquido(jpa);

        return new FolhaPagamentoResumo(
                jpa.getId(),
                jpa.getFuncionarioId(),
                jpa.getPeriodoReferencia(),
                valorLiquido,
                jpa.getStatus()
        );
    }

    private BigDecimal calcularValorLiquido(FolhaPagamentoJpa jpa) {
        FolhaPagamento folhaDominio = jpa.paraDominio();

        CalculoFolhaStrategy strategy = CalculoFolhaStrategyFactory.criar(
                jpa.getTipoRegistro()
        );

        return strategy.calcularValorLiquido(folhaDominio);
    }
}