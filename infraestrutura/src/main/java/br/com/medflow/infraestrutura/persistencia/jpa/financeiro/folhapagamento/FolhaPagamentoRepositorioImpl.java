package br.com.medflow.infraestrutura.persistencia.jpa.financeiro.folhapagamento;

import br.com.medflow.dominio.financeiro.folhapagamento.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementação JPA do repositório de domínio.
 */
@Component
public class FolhaPagamentoRepositorioImpl implements FolhaPagamentoRepositorio {

    private final FolhaPagamentoJpaRepository jpaRepository;

    public FolhaPagamentoRepositorioImpl(FolhaPagamentoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public void salvar(FolhaPagamento folhaPagamento) {
        if (folhaPagamento.getId() == null) {
            FolhaPagamentoJpa jpa = new FolhaPagamentoJpa(folhaPagamento);
            FolhaPagamentoJpa salvo = jpaRepository.save(jpa);
            folhaPagamento.setId(new FolhaPagamentoId(salvo.getId()));
        } else {
            FolhaPagamentoJpa jpa = jpaRepository.findById(folhaPagamento.getId().getId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Folha não encontrada: " + folhaPagamento.getId().getId()));
            jpa.atualizarDe(folhaPagamento);
            jpaRepository.save(jpa);
        }
    }

    @Override
    public FolhaPagamento obter(FolhaPagamentoId id) {
        return jpaRepository.findById(id.getId())
                .map(FolhaPagamentoJpa::paraDominio)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Folha de pagamento não encontrada com ID: " + id.getId()));
    }

    @Override
    public Optional<FolhaPagamento> obterPorFuncionarioEPeriodo(
            int funcionarioId, String periodoReferencia, TipoRegistro tipoRegistro) {
        return jpaRepository.findByFuncionarioIdAndPeriodoReferenciaAndTipoRegistro(
                funcionarioId, periodoReferencia, tipoRegistro
        ).map(FolhaPagamentoJpa::paraDominio);
    }

    @Override
    public List<FolhaPagamento> pesquisarPorFuncionario(int funcionarioId) {
        return jpaRepository.findByFuncionarioId(funcionarioId).stream()
                .map(FolhaPagamentoJpa::paraDominio)
                .collect(Collectors.toList());
    }

    @Override
    public List<FolhaPagamento> pesquisarPorPeriodo(String periodoReferencia) {
        return jpaRepository.findByPeriodoReferencia(periodoReferencia).stream()
                .map(FolhaPagamentoJpa::paraDominio)
                .collect(Collectors.toList());
    }

    @Override
    public List<FolhaPagamento> pesquisarPorStatus(StatusFolha status) {
        return jpaRepository.findByStatus(status).stream()
                .map(FolhaPagamentoJpa::paraDominio)
                .collect(Collectors.toList());
    }

    @Override
    public List<FolhaPagamento> pesquisar() {
        return jpaRepository.findAll().stream()
                .map(FolhaPagamentoJpa::paraDominio)
                .collect(Collectors.toList());
    }

    @Override
    public void remover(FolhaPagamentoId id) {
        if (!jpaRepository.existsById(id.getId())) {
            throw new IllegalArgumentException(
                    "Folha de pagamento com ID " + id.getId() + " não está no repositório.");
        }
        jpaRepository.deleteById(id.getId());
    }
}