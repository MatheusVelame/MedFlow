package br.com.medflow.infraestrutura.persistencia.jpa.prontuario;

import com.medflow.dominio.prontuario.HistoricoAtualizacao;
import com.medflow.dominio.prontuario.HistoricoClinico;
import com.medflow.dominio.prontuario.Prontuario;

/**
 * Decorator abstrato para ProntuarioMapeador.
 * Permite adicionar funcionalidades ao mapeamento sem modificar a implementação base.
 */
public abstract class ProntuarioMapeadorDecorator implements ProntuarioMapeadorBase.ProntuarioMapeador {

    protected final ProntuarioMapeadorBase.ProntuarioMapeador mapeador;

    public ProntuarioMapeadorDecorator(ProntuarioMapeadorBase.ProntuarioMapeador mapeador) {
        this.mapeador = mapeador;
    }

    @Override
    public ProntuarioJpa toJpa(Prontuario prontuario) {
        return mapeador.toJpa(prontuario);
    }

    @Override
    public Prontuario toDomain(ProntuarioJpa jpa) {
        return mapeador.toDomain(jpa);
    }

    @Override
    public HistoricoClinicoJpa toJpaHistorico(HistoricoClinico historico, String prontuarioId) {
        return mapeador.toJpaHistorico(historico, prontuarioId);
    }

    @Override
    public HistoricoClinico toDomainHistorico(HistoricoClinicoJpa jpa) {
        return mapeador.toDomainHistorico(jpa);
    }

    @Override
    public HistoricoAtualizacaoJpa toJpaAtualizacao(HistoricoAtualizacao atualizacao) {
        return mapeador.toJpaAtualizacao(atualizacao);
    }

    @Override
    public HistoricoAtualizacao toDomainAtualizacao(HistoricoAtualizacaoJpa jpa) {
        return mapeador.toDomainAtualizacao(jpa);
    }
}
