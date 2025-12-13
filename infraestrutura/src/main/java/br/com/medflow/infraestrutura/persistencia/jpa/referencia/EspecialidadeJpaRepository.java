package br.com.medflow.infraestrutura.persistencia.jpa.referencia;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface EspecialidadeJpaRepository extends JpaRepository<EspecialidadeJpa, Integer> {

    Optional<EspecialidadeJpa> findByNome(String nome);

    boolean existsByNome(String nome);

    // Consultas case-insensitive para evitar duplicidade por variações de caixa/espacos
    Optional<EspecialidadeJpa> findByNomeIgnoreCase(String nome);

    boolean existsByNomeIgnoreCase(String nome);

    // Retorna o maior id atualmente na tabela (pode ser usado para realinhar identity em bancos embutidos)
    @Query("select max(e.id) from EspecialidadeJpa e")
    Integer findMaxId();
}