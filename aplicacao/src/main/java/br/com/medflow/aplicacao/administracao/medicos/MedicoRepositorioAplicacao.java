// Localização: aplicacao/src/main/java/br/com/medflow/aplicacao/administracao/medicos/MedicoRepositorioAplicacao.java

package br.com.medflow.aplicacao.administracao.medicos;

import br.com.medflow.dominio.administracao.funcionarios.Medico;
import br.com.medflow.dominio.administracao.funcionarios.FuncionarioId;
import br.com.medflow.dominio.administracao.funcionarios.StatusFuncionario;
import br.com.medflow.dominio.administracao.funcionarios.CRM;
import java.util.List;
import java.util.Optional;

/**
 * Interface (Porta) para acesso de dados de LEITURA (Queries) de Médicos.
 *
 * Esta interface será implementada pela camada de Infraestrutura.
 * Retorna entidades de domínio (Medico), que serão convertidas em DTOs
 * pelo serviço de aplicação usando as Strategies.
 *
 * IMPORTANTE: Este repositório é específico para MÉDICOS.
 * Ele herda conceitualmente do FuncionarioRepositorioAplicacao mas é mais específico.
 */
public interface MedicoRepositorioAplicacao {

    /**
     * Pesquisa e retorna todos os médicos.
     * @return Lista de entidades Medico do domínio
     */
    List<Medico> pesquisarTodos();

    /**
     * Obtém um médico específico por ID.
     * @param id O ID do médico
     * @return Optional contendo a entidade ou vazio
     */
    Optional<Medico> obterPorId(FuncionarioId id);

    /**
     * Obtém um médico específico por CRM.
     * @param crm O CRM do médico
     * @return Optional contendo a entidade ou vazio
     */
    Optional<Medico> obterPorCrm(CRM crm);

    /**
     * Pesquisa médicos filtrando por status.
     * @param status O status pelo qual filtrar
     * @return Lista de médicos com o status especificado
     */
    List<Medico> pesquisarPorStatus(StatusFuncionario status);

    /**
     * Pesquisa médicos por especialidade.
     * @param especialidadeId ID da especialidade
     * @return Lista de médicos com a especialidade especificada
     */
    List<Medico> pesquisarPorEspecialidade(int especialidadeId);

    /**
     * Pesquisa médicos por nome (busca parcial).
     * @param nome O nome ou parte do nome a buscar
     * @return Lista de médicos cujo nome contém o termo buscado
     */
    List<Medico> pesquisarPorNome(String nome);

    /**
     * Pesquisa médicos por nome, CRM ou especialidade.
     * @param termoBusca Termo para buscar em múltiplos campos
     * @return Lista de médicos que correspondem à busca
     */
    List<Medico> buscarGeral(String termoBusca);

    /**
     * Pesquisa apenas médicos ativos (atalho).
     * @return Lista de médicos com status ATIVO
     */
    default List<Medico> pesquisarAtivos() {
        return pesquisarPorStatus(StatusFuncionario.ATIVO);
    }
}