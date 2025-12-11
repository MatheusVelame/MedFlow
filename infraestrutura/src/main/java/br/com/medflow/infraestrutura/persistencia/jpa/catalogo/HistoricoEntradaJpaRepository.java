// Localização: infraestrutura/src/main/java/br/com/medflow/infraestrutura/persistencia/jpa/catalogo/HistoricoEntradaJpaRepository.java

package br.com.medflow.infraestrutura.persistencia.jpa.catalogo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface HistoricoEntradaJpaRepository extends JpaRepository<HistoricoEntradaJpa, Long> {
    List<HistoricoEntradaJpa> findByMedicamentoId(Integer medicamentoId);
}