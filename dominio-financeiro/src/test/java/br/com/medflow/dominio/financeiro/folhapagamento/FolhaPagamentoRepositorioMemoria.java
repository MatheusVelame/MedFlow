package br.com.medflow.dominio.financeiro.folhapagamento;

import static org.apache.commons.lang3.Validate.notNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class FolhaPagamentoRepositorioMemoria implements FolhaPagamentoRepositorio {
    private Map<FolhaPagamentoId, FolhaPagamento> folhasPagamento = new HashMap<>();
    private int sequenciaId = 0;

    @Override
    public void salvar(FolhaPagamento folhaPagamento) {
        notNull(folhaPagamento, "A folha de pagamento não pode ser nula");

        if (folhaPagamento.getId() == null) {
            sequenciaId++;
            FolhaPagamentoId novoId = new FolhaPagamentoId(sequenciaId);

            FolhaPagamento nova = new FolhaPagamento(
                    novoId,
                    folhaPagamento.getFuncionarioId(),
                    folhaPagamento.getPeriodoReferencia(),
                    folhaPagamento.getTipoRegistro(),
                    folhaPagamento.getSalarioBase(),
                    folhaPagamento.getBeneficios(),
                    folhaPagamento.getMetodoPagamento(),
                    folhaPagamento.getTipoVinculo(),
                    folhaPagamento.getStatus()
            );
            folhasPagamento.put(novoId, nova);

            folhaPagamento.setId(novoId);

        } else {
            folhasPagamento.put(folhaPagamento.getId(), folhaPagamento);
        }
    }

    @Override
    public FolhaPagamento obter(FolhaPagamentoId id) {
        notNull(id, "O id da folha de pagamento não pode ser nulo");
        var folha = folhasPagamento.get(id);

        return Optional.ofNullable(folha)
                .orElseThrow(() -> new IllegalArgumentException("Folha de pagamento não encontrada com ID: " + id.getId()));
    }

    @Override
    public Optional<FolhaPagamento> obterPorFuncionarioEPeriodo(int funcionarioId, String periodoReferencia, TipoRegistro tipoRegistro) {
        return folhasPagamento.values().stream()
                .filter(f -> f.getFuncionarioId() == funcionarioId &&
                        f.getPeriodoReferencia().equals(periodoReferencia) &&
                        f.getTipoRegistro() == tipoRegistro)
                .findFirst();
    }

    @Override
    public List<FolhaPagamento> pesquisarPorFuncionario(int funcionarioId) {
        return folhasPagamento.values().stream()
                .filter(f -> f.getFuncionarioId() == funcionarioId)
                .collect(Collectors.toList());
    }

    @Override
    public List<FolhaPagamento> pesquisarPorPeriodo(String periodoReferencia) {
        return folhasPagamento.values().stream()
                .filter(f -> f.getPeriodoReferencia().equals(periodoReferencia))
                .collect(Collectors.toList());
    }

    @Override
    public List<FolhaPagamento> pesquisarPorStatus(StatusFolha status) {
        return folhasPagamento.values().stream()
                .filter(f -> f.getStatus() == status)
                .collect(Collectors.toList());
    }

    @Override
    public List<FolhaPagamento> pesquisar() {
        return List.copyOf(folhasPagamento.values());
    }

    @Override
    public void remover(FolhaPagamentoId id) {
        notNull(id, "O ID para remoção não pode ser nulo.");

        if (!folhasPagamento.containsKey(id)) {
            throw new IllegalArgumentException("Folha de pagamento com ID " + id.toString() + " não está no repositório.");
        }
        folhasPagamento.remove(id);
    }

    public void clear() {
        folhasPagamento.clear();
        sequenciaId = 0;
    }
}