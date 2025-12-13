// Localização: infraestrutura/src/main/java/br/com/medflow/infraestrutura/persistencia/jpa/atendimento/ConsultaJpaRepository.java

package br.com.medflow.infraestrutura.persistencia.jpa.atendimento;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConsultaJpaRepository extends JpaRepository<ConsultaJpa, Integer> {
    
    // Método customizado para suportar a busca por status no Query Service
    List<ConsultaJpa> findByStatus(String status);


    boolean existsByMedicoId(Integer medicoId);

    
    boolean existsByPacienteId(Integer pacienteId);

}