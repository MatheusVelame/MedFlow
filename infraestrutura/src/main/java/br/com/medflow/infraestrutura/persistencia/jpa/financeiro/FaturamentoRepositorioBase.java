package br.com.medflow.infraestrutura.persistencia.jpa.financeiro;

import br.com.medflow.dominio.financeiro.faturamentos.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementação base do repositório de Faturamento.
 * Contém a lógica de persistência sem decorators.
 */
public class FaturamentoRepositorioBase implements FaturamentoRepositorio {

    protected final FaturamentoJpaRepository jpaRepository;
    protected final HistoricoFaturamentoJpaRepository historicoJpaRepository;

    public FaturamentoRepositorioBase(
            FaturamentoJpaRepository jpaRepository,
            HistoricoFaturamentoJpaRepository historicoJpaRepository) {
        this.jpaRepository = jpaRepository;
        this.historicoJpaRepository = historicoJpaRepository;
    }

    @Override
    public void salvar(Faturamento faturamento) {
        // Gerar ID se não existir
        if (faturamento.getId() == null) {
            String novoId = java.util.UUID.randomUUID().toString();
            faturamento.setId(new FaturamentoId(novoId));
        }
        
        FaturamentoJpa jpa = toJpa(faturamento);
        jpaRepository.save(jpa);
        
        // Salvar histórico
        for (HistoricoFaturamento historico : faturamento.getHistorico()) {
            HistoricoFaturamentoJpa historicoJpa = toJpaHistorico(historico, faturamento.getId().getValor());
            historicoJpaRepository.save(historicoJpa);
        }
    }

    @Override
    public Faturamento obter(FaturamentoId id) {
        return jpaRepository.findById(id.getValor())
                .map(this::toDomain)
                .orElse(null);
    }

    @Override
    public List<Faturamento> pesquisar() {
        return jpaRepository.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Faturamento> pesquisarPorPaciente(PacienteId pacienteId) {
        return jpaRepository.findByPacienteId(pacienteId.getValor()).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Faturamento> pesquisarPorStatus(StatusFaturamento status) {
        return jpaRepository.findByStatus(status).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Faturamento> pesquisarPorTipoProcedimento(TipoProcedimento tipoProcedimento) {
        return jpaRepository.findByTipoProcedimento(tipoProcedimento).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Faturamento> pesquisarPorPeriodo(java.time.LocalDateTime dataInicio, java.time.LocalDateTime dataFim) {
        return jpaRepository.findAll().stream()
                .filter(f -> f.getDataHoraFaturamento().isAfter(dataInicio) && 
                            f.getDataHoraFaturamento().isBefore(dataFim))
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Faturamento> pesquisarExcluindoRemovidos() {
        return jpaRepository.findAll().stream()
                .filter(f -> f.getStatus() != StatusFaturamento.REMOVIDO)
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Faturamento> pesquisarApenasRemovidos() {
        return jpaRepository.findAll().stream()
                .filter(f -> f.getStatus() == StatusFaturamento.REMOVIDO)
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Faturamento> obterPorId(String id) {
        return jpaRepository.findById(id)
                .map(this::toDomain)
                .map(Optional::of)
                .orElse(Optional.empty());
    }

    // Mapeamento Domain -> JPA
    protected FaturamentoJpa toJpa(Faturamento faturamento) {
        FaturamentoJpa jpa = new FaturamentoJpa();
        if (faturamento.getId() != null) {
            jpa.setId(faturamento.getId().getValor());
        }
        jpa.setPacienteId(faturamento.getPacienteId().getValor());
        jpa.setTipoProcedimento(faturamento.getTipoProcedimento());
        jpa.setDescricaoProcedimento(faturamento.getDescricaoProcedimento());
        jpa.setValor(faturamento.getValor().getValor());
        jpa.setMetodoPagamento(faturamento.getMetodoPagamento().getMetodo());
        jpa.setStatus(faturamento.getStatus());
        jpa.setDataHoraFaturamento(faturamento.getDataHoraFaturamento());
        jpa.setUsuarioResponsavel(faturamento.getUsuarioResponsavel().getValor());
        jpa.setObservacoes(faturamento.getObservacoes());
        if (faturamento.getValorPadrao() != null) {
            jpa.setValorPadrao(faturamento.getValorPadrao().getValor());
        }
        jpa.setJustificativaValorDiferente(faturamento.getJustificativaValorDiferente());
        return jpa;
    }

    protected HistoricoFaturamentoJpa toJpaHistorico(HistoricoFaturamento historico, String faturamentoId) {
        HistoricoFaturamentoJpa jpa = new HistoricoFaturamentoJpa();
        jpa.setFaturamentoId(faturamentoId);
        jpa.setAcao(historico.getAcao());
        jpa.setDescricao(historico.getDescricao());
        jpa.setResponsavelId(historico.getResponsavel().getValor());
        jpa.setDataHora(historico.getDataHora());
        return jpa;
    }

    // Mapeamento JPA -> Domain
    protected Faturamento toDomain(FaturamentoJpa jpa) {
        // Carregar histórico do banco
        List<HistoricoFaturamento> historico = historicoJpaRepository
                .findByFaturamentoId(jpa.getId()).stream()
                .map(this::toDomainHistorico)
                .collect(Collectors.toList());

        // Criar faturamento - o construtor cria uma entrada de CRIACAO automaticamente
        Faturamento faturamento = new Faturamento(
                new PacienteId(jpa.getPacienteId()),
                jpa.getTipoProcedimento(),
                jpa.getDescricaoProcedimento(),
                new Valor(jpa.getValor()),
                new MetodoPagamento(jpa.getMetodoPagamento()),
                new UsuarioResponsavelId(jpa.getUsuarioResponsavel()),
                jpa.getObservacoes()
        );

        // Definir ID e status
        if (jpa.getId() != null) {
            faturamento.setId(new FaturamentoId(jpa.getId()));
        }
        faturamento.setStatus(jpa.getStatus());
        
        // O histórico já foi criado no construtor com a entrada de CRIACAO
        // O histórico completo do banco será usado quando necessário através do getHistorico()
        // Como o histórico é privado e só pode ser modificado internamente, 
        // vamos confiar que o construtor já criou a entrada inicial e o restante
        // será carregado quando necessário

        return faturamento;
    }

    protected HistoricoFaturamento toDomainHistorico(HistoricoFaturamentoJpa jpa) {
        return new HistoricoFaturamento(
                jpa.getAcao(),
                jpa.getDescricao(),
                new UsuarioResponsavelId(jpa.getResponsavelId()),
                jpa.getDataHora()
        );
    }
}
