// Localização: aplicacao/src/main/java/br/com/medflow/aplicacao/administracao/medicos/MedicoServicoAplicacao.java

package br.com.medflow.aplicacao.administracao.medicos;

import static org.apache.commons.lang3.Validate.notEmpty;
import static org.apache.commons.lang3.Validate.notNull;

import br.com.medflow.dominio.administracao.funcionarios.Medico;
import br.com.medflow.dominio.administracao.funcionarios.FuncionarioId;
import br.com.medflow.dominio.administracao.funcionarios.StatusFuncionario;
import br.com.medflow.dominio.administracao.funcionarios.CRM;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Serviço de Aplicação para Médicos, focado em operações de LEITURA (Queries).
 *
 * PADRÃO STRATEGY APLICADO:
 * Este serviço usa uma estratégia de conversão injetada para transformar
 * entidades de domínio (Medico) em DTOs (MedicoResumo/MedicoDetalhes).
 *
 * A estratégia pode ser trocada em tempo de execução, permitindo:
 * - Versão completa com dados de consultas
 * - Versão simplificada sem consultas (mais rápida)
 * - Versões customizadas para diferentes contextos
 */
public class MedicoServicoAplicacao {

    private final MedicoRepositorioAplicacao repositorio;
    private final MedicoConversaoStrategy strategy;

    /**
     * Construtor que recebe o repositório e a estratégia de conversão.
     * @param repositorio Repositório para acesso aos dados de médicos
     * @param strategy Estratégia de conversão a ser aplicada
     */
    public MedicoServicoAplicacao(
            MedicoRepositorioAplicacao repositorio,
            MedicoConversaoStrategy strategy) {

        notNull(repositorio, "O repositório de médicos não pode ser nulo");
        notNull(strategy, "A estratégia de conversão não pode ser nula");

        this.repositorio = repositorio;
        this.strategy = strategy;
    }

    /**
     * Pesquisa e retorna resumos de todos os médicos.
     * Usa Strategy para converter cada entidade no DTO apropriado.
     */
    public List<MedicoResumo> pesquisarResumos() {
        List<Medico> medicos = repositorio.pesquisarTodos();

        return medicos.stream()
                .map(strategy::converterParaResumo)
                .collect(Collectors.toList());
    }

    /**
     * Obtém detalhes completos de um médico específico por ID.
     * @param id O ID do médico
     * @return Detalhes completos incluindo histórico e disponibilidade
     */
    public MedicoDetalhes obterDetalhes(String id) {
        notEmpty(id, "O ID não pode ser vazio");

        FuncionarioId medicoId = new FuncionarioId(id);
        Medico medico = repositorio.obterPorId(medicoId)
                .orElseThrow(() -> new RuntimeException("Médico não encontrado com ID: " + id));

        return strategy.converterParaDetalhes(medico);
    }

    /**
     * Obtém detalhes de um médico específico por CRM.
     * @param crmCompleto CRM no formato "número-UF" (ex: "12345-PE")
     * @return Detalhes completos do médico
     */
    public MedicoDetalhes obterDetalhesPorCrm(String crmCompleto) {
        notEmpty(crmCompleto, "O CRM não pode ser vazio");

        CRM crm = new CRM(crmCompleto);
        Medico medico = repositorio.obterPorCrm(crm)
                .orElseThrow(() -> new RuntimeException("Médico não encontrado com CRM: " + crmCompleto));

        return strategy.converterParaDetalhes(medico);
    }

    /**
     * Pesquisa médicos por status.
     * @param status Status a filtrar (ATIVO ou INATIVO)
     * @return Lista de resumos filtrada
     */
    public List<MedicoResumo> pesquisarPorStatus(StatusFuncionario status) {
        notNull(status, "O status não pode ser nulo");

        List<Medico> medicos = repositorio.pesquisarPorStatus(status);

        return medicos.stream()
                .map(strategy::converterParaResumo)
                .collect(Collectors.toList());
    }

    /**
     * Pesquisa médicos ativos.
     * Atalho conveniente para pesquisarPorStatus(ATIVO).
     * @return Lista de resumos de médicos ativos
     */
    public List<MedicoResumo> pesquisarAtivos() {
        return pesquisarPorStatus(StatusFuncionario.ATIVO);
    }

    /**
     * Pesquisa médicos por especialidade.
     * @param especialidadeId ID da especialidade
     * @return Lista de resumos filtrada
     */
    public List<MedicoResumo> pesquisarPorEspecialidade(int especialidadeId) {
        List<Medico> medicos = repositorio.pesquisarPorEspecialidade(especialidadeId);

        return medicos.stream()
                .map(strategy::converterParaResumo)
                .collect(Collectors.toList());
    }

    /**
     * Pesquisa médicos por nome (busca parcial).
     * @param nome Nome ou parte do nome a buscar
     * @return Lista de resumos filtrada
     */
    public List<MedicoResumo> pesquisarPorNome(String nome) {
        notEmpty(nome, "O nome não pode ser vazio");

        List<Medico> medicos = repositorio.pesquisarPorNome(nome);

        return medicos.stream()
                .map(strategy::converterParaResumo)
                .collect(Collectors.toList());
    }

    /**
     * Busca geral que procura em nome, CRM ou especialidade.
     * Útil para implementar campo de busca único no frontend.
     *
     * @param termoBusca Termo para buscar
     * @return Lista de resumos que correspondem à busca
     */
    public List<MedicoResumo> buscarGeral(String termoBusca) {
        notEmpty(termoBusca, "O termo de busca não pode ser vazio");

        List<Medico> medicos = repositorio.buscarGeral(termoBusca);

        return medicos.stream()
                .map(strategy::converterParaResumo)
                .collect(Collectors.toList());
    }

    /**
     * Pesquisa médicos ativos com especialidade específica.
     * Combinação útil para seleção em formulários.
     *
     * @param especialidadeId ID da especialidade
     * @return Lista de médicos ativos da especialidade
     */
    public List<MedicoResumo> pesquisarAtivosPorEspecialidade(int especialidadeId) {
        List<Medico> medicos = repositorio.pesquisarPorEspecialidade(especialidadeId);

        return medicos.stream()
                .filter(m -> m.getStatus() == StatusFuncionario.ATIVO)
                .map(strategy::converterParaResumo)
                .collect(Collectors.toList());
    }
}