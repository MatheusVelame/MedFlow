// Localização: infraestrutura/src/main/java/br/com/medflow/infraestrutura/persistencia/jpa/catalogo/RevisaoPendenteJpaRepository.java

package br.com.medflow.infraestrutura.persistencia.jpa.catalogo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RevisaoPendenteJpaRepository extends JpaRepository<RevisaoPendenteJpa, Integer> {
}