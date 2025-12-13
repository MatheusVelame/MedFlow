package br.com.medflow.infraestrutura.persistencia.jpa.administracao;


import br.com.medflow.dominio.administracao.funcionarios.StatusFuncionario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FuncionarioJpaRepository extends JpaRepository<FuncionarioJpa, Integer> {

    // Suporte para o método 'pesquisar()' do Domínio
    List<FuncionarioJpa> findAll();
    
    // Suporte para o método 'obterPorNomeEContato(String, String)' do Domínio
    Optional<FuncionarioJpa> findByNomeIgnoreCaseAndContatoIgnoreCase(String nome, String contato);
    
    // Suporte para buscar pelo ID
    Optional<FuncionarioJpa> findById(Integer id);
    
    // Queries para filtros
    List<FuncionarioJpa> findByStatus(StatusFuncionario status);
    List<FuncionarioJpa> findByFuncaoIgnoreCase(String funcao);
    
    // Queries com JOIN FETCH para carregar o histórico (evita LazyInitializationException)
    @Query("SELECT DISTINCT f FROM FuncionarioJpa f LEFT JOIN FETCH f.historico")
    List<FuncionarioJpa> findAllWithHistorico();
    
    @Query("SELECT DISTINCT f FROM FuncionarioJpa f LEFT JOIN FETCH f.historico WHERE f.id = :id")
    Optional<FuncionarioJpa> findByIdWithHistorico(@Param("id") Integer id);
    
    @Query("SELECT DISTINCT f FROM FuncionarioJpa f LEFT JOIN FETCH f.historico WHERE f.status = :status")
    List<FuncionarioJpa> findByStatusWithHistorico(@Param("status") StatusFuncionario status);
    
    @Query("SELECT DISTINCT f FROM FuncionarioJpa f LEFT JOIN FETCH f.historico WHERE LOWER(f.funcao) = LOWER(:funcao)")
    List<FuncionarioJpa> findByFuncaoIgnoreCaseWithHistorico(@Param("funcao") String funcao);
}
