package br.com.medflow.infraestrutura.persistencia.jpa.administracao;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PacienteJpaRepositorio extends JpaRepository<PacienteJpa, Integer> {
    
    /**
     * Busca um paciente pelo CPF
     * @param cpf CPF do paciente (11 dígitos)
     * @return Optional com o paciente ou vazio se não encontrar
     */
    Optional<PacienteJpa> findByCpf(String cpf);
    
    /**
     * Verifica se existe algum paciente com o CPF informado
     * @param cpf CPF do paciente
     * @return true se existir, false caso contrário
     */
    boolean existsByCpf(String cpf);
}