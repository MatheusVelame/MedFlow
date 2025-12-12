package br.com.medflow.infraestrutura.persistencia.jpa.prontuario;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoricoAtualizacaoJpaRepository extends JpaRepository<HistoricoAtualizacaoJpa, String> {
    
    List<HistoricoAtualizacaoJpa> findByProntuarioId(String prontuarioId);
}
