package br.com.medflow.aplicacao.administracao.medicos;

import static org.apache.commons.lang3.Validate.notEmpty;
import static org.apache.commons.lang3.Validate.notNull;

import br.com.medflow.dominio.administracao.funcionarios.Medico;
import br.com.medflow.dominio.administracao.funcionarios.FuncionarioId;
import br.com.medflow.dominio.administracao.funcionarios.StatusFuncionario;
import br.com.medflow.dominio.administracao.funcionarios.CRM;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Serviço de Aplicação para Médicos, focado em operações de LEITURA (Queries).
 *
 * PADRÃO STRATEGY APLICADO:
 * Este serviço usa uma estratégia de conversão injetada para transformar
 * entidades de domínio (Medico) em DTOs (MedicoResumo/MedicoDetalhes).
 */
@Service
public class MedicoServicoAplicacao {

    private final MedicoRepositorioAplicacao repositorio;
    private final MedicoConversaoStrategy strategy;

    @Autowired
    public MedicoServicoAplicacao(
            @Qualifier("medicoRepositorioAplicacaoImpl") MedicoRepositorioAplicacao repositorio,
            MedicoConversaoStrategy strategy) {

        notNull(repositorio, "O repositório de médicos não pode ser nulo");
        notNull(strategy, "A estratégia de conversão não pode ser nula");

        this.repositorio = repositorio;
        this.strategy = strategy;
    }

    /**
     * Lista todos os médicos (retorna resumos).
     * Usado pelo endpoint: GET /api/medicos
     */
    public List<MedicoResumo> listarTodos() {
        List<Medico> medicos = repositorio.pesquisarTodos();

        return medicos.stream()
                .map(strategy::converterParaResumo)
                .collect(Collectors.toList());
    }

    /**
     * Obtém detalhes de um médico por ID.
     * Usado pelo endpoint: GET /api/medicos/{id}
     */
    public MedicoDetalhes obterPorId(Integer id) {
        notNull(id, "O ID não pode ser nulo");

        FuncionarioId medicoId = new FuncionarioId(id.toString());
        Medico medico = repositorio.obterPorId(medicoId)
                .orElse(null);

        if (medico == null) {
            return null;
        }

        return strategy.converterParaDetalhes(medico);
    }

    /**
     * Obtém detalhes de um médico por CRM.
     * Usado pelo endpoint: GET /api/medicos/crm/{crm}
     */
    public MedicoDetalhes obterPorCrm(String crmCompleto) {
        notEmpty(crmCompleto, "O CRM não pode ser vazio");

        CRM crm = new CRM(crmCompleto);
        Medico medico = repositorio.obterPorCrm(crm)
                .orElse(null);

        if (medico == null) {
            return null;
        }

        return strategy.converterParaDetalhes(medico);
    }

    /**
     * Lista médicos por status.
     * Usado pelo endpoint: GET /api/medicos/status/{status}
     */
    public List<MedicoResumo> listarPorStatus(StatusFuncionario status) {
        notNull(status, "O status não pode ser nulo");

        List<Medico> medicos = repositorio.pesquisarPorStatus(status);

        return medicos.stream()
                .map(strategy::converterParaResumo)
                .collect(Collectors.toList());
    }

    /**
     * Lista médicos por especialidade.
     * Usado pelo endpoint: GET /api/medicos/especialidade/{id}
     */
    public List<MedicoResumo> listarPorEspecialidade(Integer especialidadeId) {
        notNull(especialidadeId, "O ID da especialidade não pode ser nulo");

        List<Medico> medicos = repositorio.pesquisarPorEspecialidade(especialidadeId);

        return medicos.stream()
                .map(strategy::converterParaResumo)
                .collect(Collectors.toList());
    }

    /**
     * Busca geral por nome, CRM ou especialidade.
     * Usado pelo endpoint: GET /api/medicos/buscar?termo=X
     */
    public List<MedicoResumo> buscarGeral(String termoBusca) {
        notEmpty(termoBusca, "O termo de busca não pode ser vazio");

        List<Medico> medicos = repositorio.buscarGeral(termoBusca);

        return medicos.stream()
                .map(strategy::converterParaResumo)
                .collect(Collectors.toList());
    }

    /**
     * Lista apenas médicos ativos.
     */
    public List<MedicoResumo> listarAtivos() {
        return listarPorStatus(StatusFuncionario.ATIVO);
    }

    /**
     * Lista médicos ativos por especialidade.
     */
    public List<MedicoResumo> listarAtivosPorEspecialidade(Integer especialidadeId) {
        notNull(especialidadeId, "O ID da especialidade não pode ser nulo");

        List<Medico> medicos = repositorio.pesquisarPorEspecialidade(especialidadeId);

        return medicos.stream()
                .filter(m -> m.getStatus() == StatusFuncionario.ATIVO)
                .map(strategy::converterParaResumo)
                .collect(Collectors.toList());
    }

    /**
     * Busca médicos por nome parcial.
     */
    public List<MedicoResumo> buscarPorNome(String nome) {
        notEmpty(nome, "O nome não pode ser vazio");

        List<Medico> medicos = repositorio.pesquisarPorNome(nome);

        return medicos.stream()
                .map(strategy::converterParaResumo)
                .collect(Collectors.toList());
    }
}