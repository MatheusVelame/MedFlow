package br.com.medflow.infraestrutura.persistencia.jpa.financeiro.folhapagamento;

import br.com.medflow.dominio.financeiro.folhapagamento.StatusFolha;
import br.com.medflow.dominio.financeiro.folhapagamento.TipoRegistro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositório Spring Data JPA para Folha de Pagamento.
 */
@Repository
public interface FolhaPagamentoJpaRepository extends JpaRepository<FolhaPagamentoJpa, Integer> {

    Optional<FolhaPagamentoJpa> findByFuncionarioIdAndPeriodoReferenciaAndTipoRegistro(
            Integer funcionarioId,
            String periodoReferencia,
            TipoRegistro tipoRegistro
    );

    List<FolhaPagamentoJpa> findByFuncionarioId(Integer funcionarioId);

    List<FolhaPagamentoJpa> findByPeriodoReferencia(String periodoReferencia);

    List<FolhaPagamentoJpa> findByStatus(StatusFolha status);
}