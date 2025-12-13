package br.com.medflow.infraestrutura.persistencia.jpa.prontuario;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProntuarioJpaRepository extends JpaRepository<ProntuarioJpa, String> {
    
    List<ProntuarioJpa> findByPacienteId(Integer pacienteId);

    boolean existsByProfissionalResponsavel(String profissionalResponsavel);

    boolean existsByPacienteId(Integer pacienteId);

}
