// Localização: infraestrutura/src/main/java/br/com/medflow/infraestrutura/persistencia/jpa/administracao/medico/MedicoRepositorioImpl.java

package br.com.medflow.infraestrutura.persistencia.jpa.administracao;

import br.com.medflow.dominio.administracao.funcionarios.*;
import br.com.medflow.infraestrutura.persistencia.jpa.administracao.FuncionarioJpa;
import br.com.medflow.infraestrutura.persistencia.jpa.administracao.FuncionarioJpaRepository;
import br.com.medflow.infraestrutura.persistencia.jpa.administracao.HistoricoEntradaJpa;
import br.com.medflow.infraestrutura.persistencia.jpa.JpaMapeador;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementação do repositório de domínio para Médico (Commands/Escritas).
 *
 * Esta classe traduz entre entidades de domínio (Medico) e entidades JPA (MedicoJpa).
 * Usa FuncionarioRepositorio como base, mas adiciona métodos específicos de Médico.
 */
@Component("medicoRepositorioImpl")
public class MedicoRepositorioImpl implements FuncionarioRepositorio {

    private final MedicoJpaRepository medicoJpaRepository;
    private final FuncionarioJpaRepository funcionarioJpaRepository;
    private final JpaMapeador mapeador;

    public MedicoRepositorioImpl(
            MedicoJpaRepository medicoJpaRepository,
            FuncionarioJpaRepository funcionarioJpaRepository,
            JpaMapeador mapeador) {

        this.medicoJpaRepository = medicoJpaRepository;
        this.funcionarioJpaRepository = funcionarioJpaRepository;
        this.mapeador = mapeador;
    }

    // Helper para parsear inteiros de forma defensiva (retorna null em caso de formato inválido)
    private Integer tryParseInteger(String value) {
        if (value == null) return null;
        try {
            return Integer.valueOf(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    @Transactional
    public void salvar(Funcionario funcionario) {
        // Verifica se é um Médico
        if (!(funcionario instanceof Medico)) {
            throw new IllegalArgumentException("Este repositório é específico para Médicos");
        }

        Medico medico = (Medico) funcionario;
        FuncionarioId idMedico = medico.getId();

        // INSERT - Novo médico
        if (idMedico == null || idMedico.getId().isEmpty() || idMedico.getId().equals("0")) {
            MedicoJpa novoMedicoJpa = mapearDominioParaJpa(medico);

            // Configura bidirecional para disponibilidades
            if (novoMedicoJpa.getDisponibilidades() != null) {
                novoMedicoJpa.getDisponibilidades().forEach(d -> d.setMedico(novoMedicoJpa));
            }

            // Configura bidirecional para histórico
            if (novoMedicoJpa.getHistorico() != null) {
                novoMedicoJpa.getHistorico().forEach(h -> h.setFuncionario(novoMedicoJpa));
            }

            medicoJpaRepository.save(novoMedicoJpa);
            return;
        }

        // UPDATE - Médico existente
        Integer idAtualizacao = tryParseInteger(idMedico.getId());
        if (idAtualizacao == null) {
            throw new RuntimeException("ID do médico não é numérico: " + idMedico.getId());
        }

        MedicoJpa jpaExistente = medicoJpaRepository.findById(idAtualizacao)
                .orElseThrow(() -> new RuntimeException("Médico não encontrado para atualização (ID: " + idAtualizacao + ")"));

        // Atualiza campos básicos de funcionário
        jpaExistente.setNome(medico.getNome());
        jpaExistente.setFuncao(medico.getFuncao());
        jpaExistente.setContato(medico.getContato());
        jpaExistente.setStatus(medico.getStatus());

        // CRM é imutável - não atualiza
        // Mas valida se tentou mudar
        String crmAtual = jpaExistente.getCrmCompleto();
        String crmNovo = medico.getCrm().toString();
        if (!crmAtual.equals(crmNovo)) {
            throw new IllegalStateException("CRM não pode ser alterado após cadastro");
        }

        // Atualiza especialidade (pode mudar)
        jpaExistente.setEspecialidadeId(medico.getEspecialidade().getId());

        // Atualiza histórico
        // Remove histórico antigo e adiciona o novo
        jpaExistente.getHistorico().clear();
        medico.getHistorico().forEach(h -> {
            Integer responsavelId = tryParseInteger(h.getResponsavel().getCodigo());
            HistoricoEntradaJpa historicoJpa = new HistoricoEntradaJpa(
                    null,
                    h.getAcao(),
                    h.getDescricao(),
                    responsavelId,
                    h.getDataHora()
            );
            historicoJpa.setFuncionario(jpaExistente);
            jpaExistente.getHistorico().add(historicoJpa);
        });

        medicoJpaRepository.save(jpaExistente);
    }

    @Override
    public Funcionario obter(FuncionarioId id) {
        Integer idInt = tryParseInteger(id.getId());
        if (idInt == null) throw new RuntimeException("ID do médico não é numérico: " + id.getId());
        MedicoJpa jpa = medicoJpaRepository.findById(idInt)
                .orElseThrow(() -> new RuntimeException("Médico não encontrado: " + id.getId()));

        return mapearJpaParaDominio(jpa);
    }

    @Override
    public List<Funcionario> pesquisar() {
        List<MedicoJpa> jpas = medicoJpaRepository.findAll();

        return jpas.stream()
                .map(this::mapearJpaParaDominio)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Funcionario> obterPorNomeEContato(String nome, String contato) {
        // Usa repositório de funcionário base
        Optional<FuncionarioJpa> funcOpt = funcionarioJpaRepository
                .findByNomeIgnoreCaseAndContatoIgnoreCase(nome, contato);

        if (funcOpt.isEmpty()) {
            return Optional.empty();
        }

        // Verifica se é médico
        if (funcOpt.get() instanceof MedicoJpa) {
            return Optional.of(mapearJpaParaDominio((MedicoJpa) funcOpt.get()));
        }

        return Optional.empty();
    }

    @Override
    public Optional<Medico> obterPorCrm(CRM crm) {
        Optional<MedicoJpa> jpaOpt = medicoJpaRepository
                .findByCrmNumeroAndCrmUf(crm.getNumero(), crm.getUf());

        return jpaOpt.map(this::mapearJpaParaDominio);
    }

    @Override
    public void remover(FuncionarioId id) {
        Integer idInt = tryParseInteger(id.getId());
        if (idInt == null) throw new RuntimeException("ID do médico não é numérico: " + id.getId());
        medicoJpaRepository.deleteById(idInt);
    }

    /**
     * Mapeia entidade JPA para entidade de domínio.
     */
    private Medico mapearJpaParaDominio(MedicoJpa jpa) {
        FuncionarioId funcionarioId = new FuncionarioId(jpa.getId());
        CRM crm = new CRM(jpa.getCrmCompleto());
        Medico.EspecialidadeId especialidade = new Medico.EspecialidadeId(jpa.getEspecialidadeId());

        // Mapeia histórico
        List<Funcionario.HistoricoEntrada> historico = jpa.getHistorico().stream()
                .map(h -> new Funcionario.HistoricoEntrada(
                        h.getAcao(),
                        h.getDescricao(),
                        new UsuarioResponsavelId(h.getResponsavelId()),
                        h.getDataHora()
                ))
                .collect(Collectors.toList());

        // Usa construtor de reconstrução
        return new Medico(
                funcionarioId,
                jpa.getNome(),
                jpa.getFuncao(),
                jpa.getContato(),
                jpa.getStatus().name(),
                historico,
                crm,
                especialidade
        );
    }

    /**
     * Mapeia entidade de domínio para entidade JPA.
     */
    private MedicoJpa mapearDominioParaJpa(Medico medico) {
        Integer id = medico.getId() != null ? tryParseInteger(medico.getId().getId()) : null;

        // Mapeia histórico
        List<HistoricoEntradaJpa> historicoJpa = medico.getHistorico().stream()
                .map(h -> new HistoricoEntradaJpa(
                        null,
                        h.getAcao(),
                        h.getDescricao(),
                        tryParseInteger(h.getResponsavel().getCodigo()),
                        h.getDataHora()
                ))
                .collect(Collectors.toList());

        return new MedicoJpa(
                id,
                medico.getNome(),
                medico.getFuncao(),
                medico.getContato(),
                medico.getStatus(),
                historicoJpa,
                medico.getCrm().getNumero(),
                medico.getCrm().getUf(),
                medico.getEspecialidade().getId(),
                null, // dataNascimento - adicionar quando implementar no domínio
                null  // disponibilidades - gerenciar separadamente se necessário
        );
    }
}
