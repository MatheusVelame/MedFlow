package br.com.medflow.aplicacao.administracao.pacientes;

import java.util.List;
import java.util.Optional;

/**
 * Interface do repositório na camada de aplicação.
 * Define operações com foco em projeções (DTOs) e não em entidades de domínio.
 */
public interface PacienteRepositorioAplicacao {
    
    /**
     * Busca um resumo do paciente por ID
     * @param id ID do paciente
     * @return Optional com resumo do paciente ou vazio se não encontrar
     */
    Optional<PacienteResumo> buscarResumoPorId(int id);
    
    /**
     * Busca detalhes completos do paciente por ID
     * @param id ID do paciente
     * @return Optional com detalhes do paciente ou vazio se não encontrar
     */
    Optional<PacienteDetalhes> buscarDetalhesPorId(int id);
    
    /**
     * Busca resumo do paciente por CPF
     * @param cpf CPF do paciente (11 dígitos)
     * @return Optional com resumo do paciente ou vazio se não encontrar
     */
    Optional<PacienteResumo> buscarResumoPorCpf(String cpf);
    
    /**
     * Lista todos os pacientes (resumo)
     * @return Lista com resumo de todos os pacientes
     */
    List<PacienteResumo> listarTodos();
    
    /**
     * Verifica se existe um paciente com o CPF informado
     * @param cpf CPF do paciente
     * @return true se existir, false caso contrário
     */
    boolean existePorCpf(String cpf);
}