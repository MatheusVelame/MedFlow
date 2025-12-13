package br.com.medflow.infraestrutura.persistencia.jpa.financeiro;

import br.com.medflow.aplicacao.financeiro.faturamentos.FaturamentoDetalhes;
import br.com.medflow.aplicacao.financeiro.faturamentos.FaturamentoRepositorioAplicacao;
import br.com.medflow.aplicacao.financeiro.faturamentos.FaturamentoResumo;
import br.com.medflow.dominio.financeiro.faturamentos.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementação final do repositório de Faturamento usando Decorator Pattern.
 * Compõe os decorators (auditoria) sobre a implementação base.
 */
@Component
public class FaturamentoRepositorioImpl implements FaturamentoRepositorio, FaturamentoRepositorioAplicacao {

    private final FaturamentoRepositorio repositorioDecorado;

    public FaturamentoRepositorioImpl(FaturamentoRepositorioBase repositorioBase) {
        // Aplica os decorators em cascata: base -> auditoria
        this.repositorioDecorado = new FaturamentoRepositorioAuditoriaDecorator(repositorioBase);
    }

    // =====================================================================
    // IMPLEMENTAÇÃO DO DOMAIN REPOSITORY (PORTA DE ESCRITA/CUD)
    // =====================================================================

    @Override
    public void salvar(Faturamento faturamento) {
        repositorioDecorado.salvar(faturamento);
    }

    @Override
    public Faturamento obter(FaturamentoId id) {
        return repositorioDecorado.obter(id);
    }

    @Override
    public List<Faturamento> pesquisar() {
        return repositorioDecorado.pesquisar();
    }

    @Override
    public List<Faturamento> pesquisarPorPaciente(PacienteId pacienteId) {
        return repositorioDecorado.pesquisarPorPaciente(pacienteId);
    }


    @Override
    public List<Faturamento> pesquisarPorPeriodo(java.time.LocalDateTime dataInicio, java.time.LocalDateTime dataFim) {
        return repositorioDecorado.pesquisarPorPeriodo(dataInicio, dataFim);
    }

    @Override
    public List<Faturamento> pesquisarExcluindoRemovidos() {
        return repositorioDecorado.pesquisarExcluindoRemovidos();
    }

    @Override
    public List<Faturamento> pesquisarApenasRemovidos() {
        return repositorioDecorado.pesquisarApenasRemovidos();
    }

    @Override
    public Optional<Faturamento> obterPorId(String id) {
        return repositorioDecorado.obterPorId(id);
    }

    // =====================================================================
    // IMPLEMENTAÇÃO DO APPLICATION REPOSITORY (PORTA DE LEITURA/QUERY)
    // =====================================================================

    @Override
    public List<FaturamentoResumo> pesquisarResumos() {
        return pesquisarExcluindoRemovidos().stream()
                .map(this::toResumoDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<FaturamentoDetalhes> obterDetalhesPorId(String id) {
        return obterPorId(id)
                .map(this::toDetalhesDto);
    }

    @Override
    public List<Faturamento> pesquisarPorStatus(StatusFaturamento status) {
        return repositorioDecorado.pesquisarPorStatus(status);
    }

    @Override
    public List<Faturamento> pesquisarPorTipoProcedimento(TipoProcedimento tipoProcedimento) {
        return repositorioDecorado.pesquisarPorTipoProcedimento(tipoProcedimento);
    }

    // Implementação dos métodos da interface de aplicação (com mesmo nome mas retornando DTOs)
    @Override
    public List<FaturamentoResumo> pesquisarResumosPorStatus(StatusFaturamento status) {
        return pesquisarPorStatus(status).stream()
                .map(this::toResumoDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<FaturamentoResumo> pesquisarResumosPorTipoProcedimento(TipoProcedimento tipoProcedimento) {
        return pesquisarPorTipoProcedimento(tipoProcedimento).stream()
                .map(this::toResumoDto)
                .collect(Collectors.toList());
    }

    // Mapeamento Domain -> DTO
    private FaturamentoResumo toResumoDto(Faturamento faturamento) {
        return new FaturamentoResumo(
                faturamento.getId().getValor(),
                faturamento.getPacienteId().getValor(),
                faturamento.getTipoProcedimento(),
                faturamento.getDescricaoProcedimento(),
                faturamento.getValor().getValor(),
                faturamento.getMetodoPagamento().getMetodo(),
                faturamento.getStatus(),
                faturamento.getDataHoraFaturamento()
        );
    }

    private FaturamentoDetalhes toDetalhesDto(Faturamento faturamento) {
        List<FaturamentoDetalhes.HistoricoDetalhes> historico = faturamento.getHistorico().stream()
                .map(h -> new FaturamentoDetalhes.HistoricoDetalhes(
                        h.getAcao().getDescricao(),
                        h.getDescricao(),
                        h.getResponsavel().getValor(),
                        h.getDataHora()
                ))
                .collect(Collectors.toList());

        return new FaturamentoDetalhes(
                faturamento.getId().getValor(),
                faturamento.getPacienteId().getValor(),
                faturamento.getTipoProcedimento(),
                faturamento.getDescricaoProcedimento(),
                faturamento.getValor().getValor(),
                faturamento.getMetodoPagamento().getMetodo(),
                faturamento.getStatus(),
                faturamento.getDataHoraFaturamento(),
                faturamento.getUsuarioResponsavel().getValor(),
                faturamento.getObservacoes(),
                faturamento.getValorPadrao() != null ? faturamento.getValorPadrao().getValor() : null,
                faturamento.getJustificativaValorDiferente(),
                historico
        );
    }
}
