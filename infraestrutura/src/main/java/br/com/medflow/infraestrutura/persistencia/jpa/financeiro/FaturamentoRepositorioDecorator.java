package br.com.medflow.infraestrutura.persistencia.jpa.financeiro;

import br.com.medflow.dominio.financeiro.faturamentos.*;
import java.util.List;
import java.util.Optional;

/**
 * Decorator abstrato para FaturamentoRepositorio.
 * Implementa o padrão Decorator permitindo adicionar funcionalidades
 * sem modificar a implementação base.
 */
public abstract class FaturamentoRepositorioDecorator implements FaturamentoRepositorio {

    protected final FaturamentoRepositorio repositorio;

    public FaturamentoRepositorioDecorator(FaturamentoRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    @Override
    public void salvar(Faturamento faturamento) {
        repositorio.salvar(faturamento);
    }

    @Override
    public Faturamento obter(FaturamentoId id) {
        return repositorio.obter(id);
    }

    @Override
    public List<Faturamento> pesquisar() {
        return repositorio.pesquisar();
    }

    @Override
    public List<Faturamento> pesquisarPorPaciente(PacienteId pacienteId) {
        return repositorio.pesquisarPorPaciente(pacienteId);
    }

    @Override
    public List<Faturamento> pesquisarPorStatus(StatusFaturamento status) {
        return repositorio.pesquisarPorStatus(status);
    }

    @Override
    public List<Faturamento> pesquisarPorTipoProcedimento(TipoProcedimento tipoProcedimento) {
        return repositorio.pesquisarPorTipoProcedimento(tipoProcedimento);
    }

    @Override
    public List<Faturamento> pesquisarPorPeriodo(java.time.LocalDateTime dataInicio, java.time.LocalDateTime dataFim) {
        return repositorio.pesquisarPorPeriodo(dataInicio, dataFim);
    }

    @Override
    public List<Faturamento> pesquisarExcluindoRemovidos() {
        return repositorio.pesquisarExcluindoRemovidos();
    }

    @Override
    public List<Faturamento> pesquisarApenasRemovidos() {
        return repositorio.pesquisarApenasRemovidos();
    }

    @Override
    public Optional<Faturamento> obterPorId(String id) {
        return repositorio.obterPorId(id);
    }
}
