package com.medflow.dominio.prontuario;

import java.util.List;
import java.util.Optional;

/**
 * Interface de repositório de domínio para Prontuário.
 */
public interface ProntuarioRepositorio {
    
    /**
     * Salva ou atualiza um prontuário.
     * @param prontuario O prontuário a ser salvo.
     */
    void salvar(Prontuario prontuario);
    
    /**
     * Busca um prontuário por ID.
     * @param id O ID do prontuário.
     * @return Optional contendo o prontuário ou vazio se não encontrado.
     */
    Optional<Prontuario> obterPorId(String id);
    
    /**
     * Busca prontuários por paciente.
     * @param pacienteId O ID do paciente.
     * @return Lista de prontuários do paciente.
     */
    List<Prontuario> buscarPorPaciente(String pacienteId);
    
    /**
     * Lista todos os prontuários.
     * @return Lista de todos os prontuários.
     */
    List<Prontuario> listarTodos();
    
    boolean existsByPacienteId(String pacienteId);
}
