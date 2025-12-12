package br.com.medflow.infraestrutura.persistencia.jpa.administracao;

import br.com.medflow.dominio.administracao.funcionarios.StatusFuncionario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositório Spring Data JPA para a entidade MedicoJpa.
 *
 * Fornece queries customizadas específicas para médicos.
 */
@Repository
public interface MedicoJpaRepository extends JpaRepository<MedicoJpa, Integer> {

    /**
     * Busca médico por CRM completo (número + UF).
     */
    Optional<MedicoJpa> findByCrmNumeroAndCrmUf(String crmNumero, String crmUf);

    /**
     * Busca médicos por status.
     */
    List<MedicoJpa> findByStatus(StatusFuncionario status);

    /**
     * Busca médicos por especialidade.
     */
    List<MedicoJpa> findByEspecialidadeId(Integer especialidadeId);

    /**
     * Busca médicos por nome (case insensitive, partial match).
     */
    @Query("SELECT m FROM MedicoJpa m WHERE LOWER(m.nome) LIKE LOWER(CONCAT('%', :nome, '%'))")
    List<MedicoJpa> findByNomeContaining(@Param("nome") String nome);

    /**
     * Busca geral: procura em nome, CRM ou especialidade.
     * Útil para campo de busca único no frontend.
     */
    @Query("""
        SELECT m FROM MedicoJpa m 
        WHERE LOWER(m.nome) LIKE LOWER(CONCAT('%', :termo, '%'))
           OR CONCAT(m.crmNumero, '-', m.crmUf) LIKE CONCAT('%', :termo, '%')
           OR CAST(m.especialidadeId AS string) LIKE CONCAT('%', :termo, '%')
        """)
    List<MedicoJpa> buscarGeral(@Param("termo") String termo);

    /**
     * Busca médicos ativos por especialidade.
     * Query customizada combinando dois filtros.
     */
    List<MedicoJpa> findByEspecialidadeIdAndStatus(Integer especialidadeId, StatusFuncionario status);

    /**
     * Busca médico com disponibilidades carregadas (evita N+1).
     */
    @Query("SELECT DISTINCT m FROM MedicoJpa m LEFT JOIN FETCH m.disponibilidades WHERE m.id = :id")
    Optional<MedicoJpa> findByIdComDisponibilidades(@Param("id") Integer id);

    /**
     * Busca todos os médicos com disponibilidades (evita N+1).
     */
    @Query("SELECT DISTINCT m FROM MedicoJpa m LEFT JOIN FETCH m.disponibilidades")
    List<MedicoJpa> findAllComDisponibilidades();

    /**
     * Conta médicos por especialidade.
     * Útil para estatísticas.
     */
    @Query("SELECT COUNT(m) FROM MedicoJpa m WHERE m.especialidadeId = :especialidadeId AND m.status = 'ATIVO'")
    Long contarAtivosPorEspecialidade(@Param("especialidadeId") Integer especialidadeId);
}