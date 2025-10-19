package br.com.medflow.dominio.financeiro.faturamentos;

import java.util.List;
import java.util.Optional;

public interface FaturamentoRepositorio {
    void salvar(Faturamento faturamento);
    Faturamento obter(FaturamentoId id);
    List<Faturamento> pesquisar();
    List<Faturamento> pesquisarPorPaciente(PacienteId pacienteId);
    List<Faturamento> pesquisarPorStatus(StatusFaturamento status);
    List<Faturamento> pesquisarPorTipoProcedimento(TipoProcedimento tipoProcedimento);
    List<Faturamento> pesquisarPorPeriodo(java.time.LocalDateTime dataInicio, java.time.LocalDateTime dataFim);
    List<Faturamento> pesquisarExcluindoRemovidos();
    List<Faturamento> pesquisarApenasRemovidos();
    Optional<Faturamento> obterPorId(String id);
}
