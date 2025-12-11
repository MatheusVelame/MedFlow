package br.com.medflow.infraestrutura.persistencia.jpa.prontuario;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoricoClinicoJpaRepository extends JpaRepository<HistoricoClinicoJpa, String> {
    
    List<HistoricoClinicoJpa> findByProntuarioId(String prontuarioId);
    
    List<HistoricoClinicoJpa> findByPacienteId(String pacienteId);
}
