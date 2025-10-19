package br.com.medflow.dominio.financeiro.faturamentos;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class FaturamentoRepositorioMemoria implements FaturamentoRepositorio {
    private final Map<String, Faturamento> faturamentos = new ConcurrentHashMap<>();
    private final AtomicLong contadorId = new AtomicLong(1);

    @Override
    public void salvar(Faturamento faturamento) {
        if (faturamento.getId() == null) {
            faturamento.setId(new FaturamentoId("FAT-" + String.format("%04d", contadorId.getAndIncrement())));
        }
        faturamentos.put(faturamento.getId().getValor(), faturamento);
    }

    @Override
    public Faturamento obter(FaturamentoId id) {
        return faturamentos.get(id.getValor());
    }

    @Override
    public List<Faturamento> pesquisar() {
        return new ArrayList<>(faturamentos.values());
    }

    @Override
    public List<Faturamento> pesquisarPorPaciente(PacienteId pacienteId) {
        return faturamentos.values().stream()
                .filter(f -> f.getPacienteId().equals(pacienteId))
                .toList();
    }

    @Override
    public List<Faturamento> pesquisarPorStatus(StatusFaturamento status) {
        return faturamentos.values().stream()
                .filter(f -> f.getStatus() == status)
                .toList();
    }

    @Override
    public List<Faturamento> pesquisarPorTipoProcedimento(TipoProcedimento tipoProcedimento) {
        return faturamentos.values().stream()
                .filter(f -> f.getTipoProcedimento() == tipoProcedimento)
                .toList();
    }

    @Override
    public List<Faturamento> pesquisarPorPeriodo(LocalDateTime dataInicio, LocalDateTime dataFim) {
        return faturamentos.values().stream()
                .filter(f -> {
                    LocalDateTime dataFaturamento = f.getDataHoraFaturamento();
                    return !dataFaturamento.isBefore(dataInicio) && !dataFaturamento.isAfter(dataFim);
                })
                .toList();
    }

    @Override
    public List<Faturamento> pesquisarExcluindoRemovidos() {
        return faturamentos.values().stream()
                .filter(f -> f.getStatus() != StatusFaturamento.REMOVIDO)
                .toList();
    }

    @Override
    public List<Faturamento> pesquisarApenasRemovidos() {
        return faturamentos.values().stream()
                .filter(f -> f.getStatus() == StatusFaturamento.REMOVIDO)
                .toList();
    }

    @Override
    public Optional<Faturamento> obterPorId(String id) {
        return Optional.ofNullable(faturamentos.get(id));
    }

    public void clear() {
        faturamentos.clear();
        contadorId.set(1);
    }
}
