package br.com.medflow.infraestrutura.persistencia.jpa.financeiro;

import br.com.medflow.dominio.financeiro.faturamentos.StatusFaturamento;
import br.com.medflow.dominio.financeiro.faturamentos.TipoProcedimento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FaturamentoJpaRepository extends JpaRepository<FaturamentoJpa, String> {
    
    List<FaturamentoJpa> findByPacienteId(Integer pacienteId);
    
    List<FaturamentoJpa> findByStatus(StatusFaturamento status);
    
    List<FaturamentoJpa> findByTipoProcedimento(TipoProcedimento tipoProcedimento);
}
