package br.com.medflow.infraestrutura.persistencia.jpa.referencia;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EspecialidadeJpaRepository extends JpaRepository<EspecialidadeJpa, Integer> {

    Optional<EspecialidadeJpa> findByNome(String nome);

    boolean existsByNome(String nome);
}