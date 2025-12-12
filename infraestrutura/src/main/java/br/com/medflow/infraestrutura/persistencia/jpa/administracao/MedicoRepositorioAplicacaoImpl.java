// Localização: infraestrutura/src/main/java/br/com/medflow/infraestrutura/persistencia/jpa/administracao/MedicoRepositorioAplicacaoImpl.java

package br.com.medflow.infraestrutura.persistencia.jpa.administracao;

import br.com.medflow.aplicacao.administracao.medicos.MedicoRepositorioAplicacao;
import br.com.medflow.dominio.administracao.funcionarios.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementação do repositório de aplicação para Médico (Queries/Leituras).
 * Retorna entidades de DOMÍNIO, não DTOs.
 */
@Component("medicoRepositorioAplicacaoImpl")
public class MedicoRepositorioAplicacaoImpl implements MedicoRepositorioAplicacao {

    private final MedicoJpaRepository jpaRepository;

    public MedicoRepositorioAplicacaoImpl(MedicoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<Medico> pesquisarTodos() {
        List<MedicoJpa> jpas = jpaRepository.findAll();
        return jpas.stream()
                .map(this::mapearJpaParaDominio)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Medico> obterPorId(FuncionarioId id) {
        Integer idInt = Integer.parseInt(id.getId());
        Optional<MedicoJpa> jpaOpt = jpaRepository.findById(idInt);
        return jpaOpt.map(this::mapearJpaParaDominio);
    }

    @Override
    public Optional<Medico> obterPorCrm(CRM crm) {
        Optional<MedicoJpa> jpaOpt = jpaRepository
                .findByCrmNumeroAndCrmUf(crm.getNumero(), crm.getUf());
        return jpaOpt.map(this::mapearJpaParaDominio);
    }

    @Override
    public List<Medico> pesquisarPorStatus(StatusFuncionario status) {
        List<MedicoJpa> jpas = jpaRepository.findByStatus(status);
        return jpas.stream()
                .map(this::mapearJpaParaDominio)
                .collect(Collectors.toList());
    }

    @Override
    public List<Medico> pesquisarPorEspecialidade(int especialidadeId) {
        List<MedicoJpa> jpas = jpaRepository.findByEspecialidadeId(especialidadeId);
        return jpas.stream()
                .map(this::mapearJpaParaDominio)
                .collect(Collectors.toList());
    }

    @Override
    public List<Medico> pesquisarPorNome(String nome) {
        List<MedicoJpa> jpas = jpaRepository.findByNomeContaining(nome);
        return jpas.stream()
                .map(this::mapearJpaParaDominio)
                .collect(Collectors.toList());
    }

    @Override
    public List<Medico> buscarGeral(String termoBusca) {
        List<MedicoJpa> jpas = jpaRepository.buscarGeral(termoBusca);
        return jpas.stream()
                .map(this::mapearJpaParaDominio)
                .collect(Collectors.toList());
    }

    private Medico mapearJpaParaDominio(MedicoJpa jpa) {
        FuncionarioId funcionarioId = new FuncionarioId(jpa.getId());
        CRM crm = new CRM(jpa.getCrmCompleto());
        Medico.EspecialidadeId especialidade = new Medico.EspecialidadeId(jpa.getEspecialidadeId());

        List<Funcionario.HistoricoEntrada> historico = jpa.getHistorico().stream()
                .map(h -> new Funcionario.HistoricoEntrada(
                        h.getAcao(),
                        h.getDescricao(),
                        new UsuarioResponsavelId(h.getResponsavelId()),
                        h.getDataHora()
                ))
                .collect(Collectors.toList());

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
}