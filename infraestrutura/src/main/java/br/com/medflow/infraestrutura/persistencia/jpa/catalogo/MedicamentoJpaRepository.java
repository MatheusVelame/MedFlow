// Localização: infraestrutura/src/main/java/br/com/medflow/infraestrutura/persistencia/jpa/catalogo/MedicamentoJpaRepository.java

package br.com.medflow.infraestrutura.persistencia.jpa.catalogo;

import br.com.medflow.dominio.catalogo.medicamentos.StatusMedicamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedicamentoJpaRepository extends JpaRepository<MedicamentoJpa, Integer> {
    
    List<MedicamentoJpa> findByStatus(StatusMedicamento status);
    
    // Query customizada para buscar medicamentos com revisão pendente ativa (status = PENDENTE)
    @Query("SELECT m FROM MedicamentoJpa m JOIN m.revisaoPendente rp WHERE rp.status = 'PENDENTE'")
    List<MedicamentoJpa> findByRevisaoPendenteStatus(); 
}