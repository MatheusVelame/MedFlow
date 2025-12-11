package br.com.medflow.aplicacao.administracao.pacientes;

/**
 * Interface de projeção para detalhes completos de Paciente.
 * Usada quando precisamos de todas as informações.
 */
public interface PacienteDetalhes {
    
    /**
     * @return ID do paciente
     */
    int getId();
    
    /**
     * @return Nome completo do paciente
     */
    String getNome();
    
    /**
     * @return CPF do paciente (11 dígitos)
     */
    String getCpf();
    
    /**
     * @return Data de nascimento no formato dd/mm/aaaa
     */
    String getDataNascimento();
    
    /**
     * @return Telefone do paciente
     */
    String getTelefone();
    
    /**
     * @return Endereço completo do paciente (pode ser null)
     */
    String getEndereco();
}