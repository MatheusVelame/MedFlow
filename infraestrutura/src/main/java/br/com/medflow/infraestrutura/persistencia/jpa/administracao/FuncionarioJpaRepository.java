package br.com.medflow.infraestrutura.persistencia.jpa.administracao;

import br.com.medflow.aplicacao.administracao.funcionarios.FuncionarioDetalhes;
import br.com.medflow.aplicacao.administracao.funcionarios.FuncionarioRepositorioAplicacao;
import br.com.medflow.aplicacao.administracao.funcionarios.FuncionarioResumo;
import br.com.medflow.dominio.administracao.funcionarios.StatusFuncionario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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
}
