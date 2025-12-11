package br.com.medflow.infraestrutura.persistencia.jpa.financeiro;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoricoFaturamentoJpaRepository extends JpaRepository<HistoricoFaturamentoJpa, Long> {
    
    List<HistoricoFaturamentoJpa> findByFaturamentoId(String faturamentoId);
}
