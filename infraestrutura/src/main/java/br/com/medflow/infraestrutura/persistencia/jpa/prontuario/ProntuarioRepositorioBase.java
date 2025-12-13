package br.com.medflow.infraestrutura.persistencia.jpa.prontuario;

import com.medflow.dominio.prontuario.HistoricoAtualizacao;
import com.medflow.dominio.prontuario.HistoricoClinico;
import com.medflow.dominio.prontuario.Prontuario;
import com.medflow.dominio.prontuario.ProntuarioRepositorio;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementação base do repositório de Prontuário.
 * Contém a lógica de persistência sem decorators.
 */
public class ProntuarioRepositorioBase implements ProntuarioRepositorio {

    protected final ProntuarioJpaRepository jpaRepository;
    protected final HistoricoClinicoJpaRepository historicoClinicoJpaRepository;
    protected final HistoricoAtualizacaoJpaRepository historicoAtualizacaoJpaRepository;

    public ProntuarioRepositorioBase(
            ProntuarioJpaRepository jpaRepository,
            HistoricoClinicoJpaRepository historicoClinicoJpaRepository,
            HistoricoAtualizacaoJpaRepository historicoAtualizacaoJpaRepository) {
        this.jpaRepository = jpaRepository;
        this.historicoClinicoJpaRepository = historicoClinicoJpaRepository;
        this.historicoAtualizacaoJpaRepository = historicoAtualizacaoJpaRepository;
    }

    @Override
    public void salvar(Prontuario prontuario) {
        // Gerar ID se não existir (embora Prontuario sempre deve ter ID)
        if (prontuario.getId() == null || prontuario.getId().trim().isEmpty()) {
            // Isso não deveria acontecer, mas por segurança
            throw new IllegalArgumentException("Prontuário deve ter um ID válido");
        }
        
        ProntuarioJpa jpa = toJpa(prontuario);
        jpaRepository.save(jpa);
        
        // Salvar histórico clínico
        for (HistoricoClinico historico : prontuario.getHistoricoClinico()) {
            HistoricoClinicoJpa historicoJpa = toJpaHistorico(historico, prontuario.getId());
            historicoClinicoJpaRepository.save(historicoJpa);
        }
        
        // Salvar histórico de atualizações
        for (HistoricoAtualizacao atualizacao : prontuario.getHistoricoAtualizacoes()) {
            HistoricoAtualizacaoJpa atualizacaoJpa = toJpaAtualizacao(atualizacao);
            historicoAtualizacaoJpaRepository.save(atualizacaoJpa);
        }
    }

    @Override
    public Optional<Prontuario> obterPorId(String id) {
        return jpaRepository.findById(id)
                .map(this::toDomain);
    }

    @Override
    public List<Prontuario> buscarPorPaciente(String pacienteId) {
        Integer pacienteIdInt = Integer.parseInt(pacienteId);
        return jpaRepository.findByPacienteId(pacienteIdInt).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Prontuario> listarTodos() {
        return jpaRepository.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    // Mapeamento Domain -> JPA
    protected ProntuarioJpa toJpa(Prontuario prontuario) {
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

    protected HistoricoClinicoJpa toJpaHistorico(HistoricoClinico historico, String prontuarioId) {
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

    protected HistoricoAtualizacaoJpa toJpaAtualizacao(HistoricoAtualizacao atualizacao) {
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

    // Mapeamento JPA -> Domain
    protected Prontuario toDomain(ProntuarioJpa jpa) {
        List<HistoricoClinico> historicoClinico = historicoClinicoJpaRepository
                .findByProntuarioId(jpa.getId()).stream()
                .map(this::toDomainHistorico)
                .collect(Collectors.toList());

        List<HistoricoAtualizacao> historicoAtualizacoes = historicoAtualizacaoJpaRepository
                .findByProntuarioId(jpa.getId()).stream()
                .map(this::toDomainAtualizacao)
                .collect(Collectors.toList());

        Prontuario prontuario = new Prontuario(
                jpa.getId(),
                String.valueOf(jpa.getPacienteId()), // Converter Integer (JPA) para String (domínio)
                jpa.getAtendimentoId(),
                jpa.getDataHoraCriacao(),
                jpa.getProfissionalResponsavel(),
                jpa.getObservacoesIniciais(),
                jpa.getStatus()
        );

        // Adicionar histórico usando reflexão ou método protegido
        for (HistoricoClinico hc : historicoClinico) {
            prontuario.adicionarHistoricoClinico(hc);
        }
        for (HistoricoAtualizacao ha : historicoAtualizacoes) {
            prontuario.adicionarAtualizacao(ha);
        }

        return prontuario;
    }

    protected HistoricoClinico toDomainHistorico(HistoricoClinicoJpa jpa) {
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

    protected HistoricoAtualizacao toDomainAtualizacao(HistoricoAtualizacaoJpa jpa) {
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
