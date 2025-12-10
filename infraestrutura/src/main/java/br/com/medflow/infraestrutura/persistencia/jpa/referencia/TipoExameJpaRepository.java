package br.com.medflow.infraestrutura.persistencia.jpa.referencia;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.medflow.dominio.referencia.tiposExames.StatusTipoExame;

public interface TipoExameJpaRepository extends JpaRepository<TipoExameJpa, Integer> {
    
    Optional<TipoExameJpa> findByCodigo(String codigo);
    
    List<TipoExameJpa> findByStatusNot(StatusTipoExame status);
    
    @Query("SELECT te FROM TipoExameJpa te WHERE te.status = :status")
    List<TipoExameJpa> findByStatus(@Param("status") StatusTipoExame status);
}