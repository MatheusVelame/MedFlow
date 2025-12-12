package br.com.medflow.aplicacao.administracao.pacientes;

/**
 * Interface de projeção para resumo de Paciente.
 * Usada em listagens e consultas que não precisam de todos os detalhes.
 */
public interface PacienteResumo {
    
    /**
     * @return ID do paciente
     */
    int getId();
    
    /**
     * @return Nome do paciente
     */
    String getNome();
    
    /**
     * @return CPF do paciente (11 dígitos)
     */
    String getCpf();
    
    /**
     * @return Telefone do paciente
     */
    String getTelefone();
}