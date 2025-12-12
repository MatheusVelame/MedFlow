package br.com.medflow.infraestrutura.persistencia.jpa.prontuario;

import com.medflow.dominio.prontuario.HistoricoAtualizacao;
import com.medflow.dominio.prontuario.HistoricoClinico;
import com.medflow.dominio.prontuario.Prontuario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Decorator para adicionar validação ao mapeamento de Prontuário.
 */
public class ProntuarioMapeadorValidacaoDecorator extends ProntuarioMapeadorDecorator {

    private static final Logger logger = LoggerFactory.getLogger(ProntuarioMapeadorValidacaoDecorator.class);

    public ProntuarioMapeadorValidacaoDecorator(ProntuarioMapeadorBase.Impl mapeadorBase) {
        super(mapeadorBase);
    }

    @Override
    public ProntuarioJpa toJpa(Prontuario prontuario) {
        validarProntuario(prontuario);
        ProntuarioJpa jpa = mapeador.toJpa(prontuario);
        validarProntuarioJpa(jpa);
        return jpa;
    }

    @Override
    public HistoricoClinicoJpa toJpaHistorico(HistoricoClinico historico, String prontuarioId) {
        validarHistoricoClinico(historico);
        HistoricoClinicoJpa jpa = mapeador.toJpaHistorico(historico, prontuarioId);
        validarHistoricoClinicoJpa(jpa);
        return jpa;
    }

    @Override
    public HistoricoAtualizacaoJpa toJpaAtualizacao(HistoricoAtualizacao atualizacao) {
        validarHistoricoAtualizacao(atualizacao);
        HistoricoAtualizacaoJpa jpa = mapeador.toJpaAtualizacao(atualizacao);
        validarHistoricoAtualizacaoJpa(jpa);
        return jpa;
    }

    private void validarProntuario(Prontuario prontuario) {
        if (prontuario == null) {
            throw new IllegalArgumentException("Prontuário não pode ser nulo");
        }
        if (prontuario.getId() == null || prontuario.getId().trim().isEmpty()) {
            logger.warn("Prontuário sem ID - será gerado pelo repositório");
        }
    }

    private void validarProntuarioJpa(ProntuarioJpa jpa) {
        if (jpa.getPacienteId() == null) {
            throw new IllegalArgumentException("ID do paciente é obrigatório");
        }
        if (jpa.getStatus() == null) {
            throw new IllegalArgumentException("Status do prontuário é obrigatório");
        }
    }

    private void validarHistoricoClinico(HistoricoClinico historico) {
        if (historico == null) {
            throw new IllegalArgumentException("Histórico clínico não pode ser nulo");
        }
        if (historico.getSintomas() == null || historico.getSintomas().trim().isEmpty()) {
            throw new IllegalArgumentException("Sintomas são obrigatórios");
        }
        if (historico.getDiagnostico() == null || historico.getDiagnostico().trim().isEmpty()) {
            throw new IllegalArgumentException("Diagnóstico é obrigatório");
        }
        if (historico.getConduta() == null || historico.getConduta().trim().isEmpty()) {
            throw new IllegalArgumentException("Conduta é obrigatória");
        }
    }

    private void validarHistoricoClinicoJpa(HistoricoClinicoJpa jpa) {
        if (jpa.getPacienteId() == null) {
            throw new IllegalArgumentException("ID do paciente no histórico clínico é obrigatório");
        }
    }

    private void validarHistoricoAtualizacao(HistoricoAtualizacao atualizacao) {
        if (atualizacao == null) {
            throw new IllegalArgumentException("Histórico de atualização não pode ser nulo");
        }
        if (atualizacao.getProntuarioId() == null || atualizacao.getProntuarioId().trim().isEmpty()) {
            throw new IllegalArgumentException("ID do prontuário é obrigatório");
        }
    }

    private void validarHistoricoAtualizacaoJpa(HistoricoAtualizacaoJpa jpa) {
        if (jpa.getProntuarioId() == null || jpa.getProntuarioId().trim().isEmpty()) {
            throw new IllegalArgumentException("ID do prontuário no histórico de atualização é obrigatório");
        }
    }
}
