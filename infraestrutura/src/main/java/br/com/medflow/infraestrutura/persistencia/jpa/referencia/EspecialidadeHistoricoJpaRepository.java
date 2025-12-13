package br.com.medflow.infraestrutura.persistencia.jpa.referencia;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EspecialidadeHistoricoJpaRepository extends JpaRepository<EspecialidadeHistoricoJpa, Integer> {
}
