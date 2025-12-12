package br.com.medflow.infraestrutura.persistencia.jpa.prontuario;

import com.medflow.dominio.prontuario.HistoricoAtualizacao;
import com.medflow.dominio.prontuario.HistoricoClinico;
import com.medflow.dominio.prontuario.Prontuario;

/**
 * Mapeador base para conversão entre entidades de domínio e JPA.
 * Contém a lógica de mapeamento sem decorators.
 */
public class ProntuarioMapeadorBase {

    /**
     * Interface para o mapeador (permite decorators).
     */
    public interface ProntuarioMapeador {
        ProntuarioJpa toJpa(Prontuario prontuario);
        Prontuario toDomain(ProntuarioJpa jpa);
        HistoricoClinicoJpa toJpaHistorico(HistoricoClinico historico, String prontuarioId);
        HistoricoClinico toDomainHistorico(HistoricoClinicoJpa jpa);
        HistoricoAtualizacaoJpa toJpaAtualizacao(HistoricoAtualizacao atualizacao);
        HistoricoAtualizacao toDomainAtualizacao(HistoricoAtualizacaoJpa jpa);
    }

    /**
     * Implementação base do mapeador.
     */
    public static class Impl implements ProntuarioMapeador {
        @Override
        public ProntuarioJpa toJpa(Prontuario prontuario) {
            ProntuarioJpa jpa = new ProntuarioJpa();
            jpa.setId(prontuario.getId());
            // Converter String (domínio) para Integer (JPA)
            jpa.setPacienteId(Integer.parseInt(prontuario.getPacienteId()));
            jpa.setAtendimentoId(prontuario.getAtendimentoId());
            jpa.setStatus(prontuario.getStatus());
            jpa.setDataHoraCriacao(prontuario.getDataHoraCriacao());
            jpa.setProfissionalResponsavel(prontuario.getProfissionalResponsavel());
            jpa.setObservacoesIniciais(prontuario.getObservacoesIniciais());
            return jpa;
        }

        @Override
        public Prontuario toDomain(ProntuarioJpa jpa) {
            // Este método será implementado no repositório base que tem acesso aos repositórios JPA
            throw new UnsupportedOperationException("Use o método do repositório base");
        }

        @Override
        public HistoricoClinicoJpa toJpaHistorico(HistoricoClinico historico, String prontuarioId) {
            HistoricoClinicoJpa jpa = new HistoricoClinicoJpa();
            jpa.setId(historico.getId());
            jpa.setProntuarioId(prontuarioId);
            // Converter String (domínio) para Integer (JPA)
            jpa.setPacienteId(Integer.parseInt(historico.getPacienteId()));
            jpa.setSintomas(historico.getSintomas());
            jpa.setDiagnostico(historico.getDiagnostico());
            jpa.setConduta(historico.getConduta());
            jpa.setDataHoraRegistro(historico.getDataHoraRegistro());
            jpa.setProfissionalResponsavel(historico.getProfissionalResponsavel());
            jpa.setAnexosReferenciados(historico.getAnexosReferenciados());
            return jpa;
        }

        @Override
        public HistoricoClinico toDomainHistorico(HistoricoClinicoJpa jpa) {
            return new HistoricoClinico(
                    jpa.getId(),
                    String.valueOf(jpa.getPacienteId()), // Converter Integer (JPA) para String (domínio)
                    jpa.getSintomas(),
                    jpa.getDiagnostico(),
                    jpa.getConduta(),
                    jpa.getDataHoraRegistro(),
                    jpa.getProfissionalResponsavel(),
                    jpa.getAnexosReferenciados()
            );
        }

        @Override
        public HistoricoAtualizacaoJpa toJpaAtualizacao(HistoricoAtualizacao atualizacao) {
            HistoricoAtualizacaoJpa jpa = new HistoricoAtualizacaoJpa();
            jpa.setId(atualizacao.getId());
            jpa.setProntuarioId(atualizacao.getProntuarioId());
            jpa.setAtendimentoId(atualizacao.getAtendimentoId());
            jpa.setDataHoraAtualizacao(atualizacao.getDataHoraAtualizacao());
            jpa.setProfissionalResponsavel(atualizacao.getProfissionalResponsavel());
            jpa.setObservacoes(atualizacao.getObservacoes());
            jpa.setStatus(atualizacao.getStatus());
            return jpa;
        }

        @Override
        public HistoricoAtualizacao toDomainAtualizacao(HistoricoAtualizacaoJpa jpa) {
            return new HistoricoAtualizacao(
                    jpa.getId(),
                    jpa.getProntuarioId(),
                    jpa.getAtendimentoId(),
                    jpa.getDataHoraAtualizacao(),
                    jpa.getProfissionalResponsavel(),
                    jpa.getObservacoes(),
                    jpa.getStatus()
            );
        }
    }
}
