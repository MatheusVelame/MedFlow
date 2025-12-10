package br.com.medflow.infraestrutura.persistencia.jpa.atendimento;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConsultaJpaRepository extends JpaRepository<ConsultaJpa, Integer> {
    
    /**
     * Busca todas as consultas JPA por ID do paciente.
     * Spring Data JPA infere a consulta automaticamente.
     */
    List<ConsultaJpa> findByPacienteId(Integer pacienteId);
}