package br.com.medflow.infraestrutura.persistencia.jpa.financeiro.convenio;

import br.com.medflow.aplicacao.financeiro.convenios.ConvenioDetalhes;
import br.com.medflow.aplicacao.financeiro.convenios.ConvenioRepositorioAplicacao;
import br.com.medflow.aplicacao.financeiro.convenios.ConvenioResumo;
import br.com.medflow.dominio.financeiro.convenios.StatusConvenio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConvenioJpaRepository 
	extends JpaRepository<ConvenioJpa, Integer>,
		ConvenioRepositorioAplicacao {

	// QUERY para a Camada de Aplicação (Queries Service)
	@Override
	@Query("SELECT c FROM ConvenioJpa c WHERE c.status <> 'ARQUIVADO'")
	List<ConvenioResumo> pesquisarResumos();

	// QUERY para a Camada de Aplicação (Detalhes)
	@Override
	@Query("SELECT c FROM ConvenioJpa c LEFT JOIN FETCH c.historico h WHERE c.id = :id")
	Optional<ConvenioDetalhes> obterDetalhesPorId(Integer id);

	// QUERY para a Camada de Aplicação (Filtro por código de identificação)
	@Override
	@Query("SELECT c FROM ConvenioJpa c WHERE c.codigoIdentificacao = :codigoIdentificacao")
	List<ConvenioResumo> pesquisarPorCodigoIdentificacao(String codigoIdentificacao);

	// QUERY para a Camada de Aplicação (Filtro por status)
	@Override
	@Query("SELECT c FROM ConvenioJpa c WHERE c.status = :status")
	List<ConvenioResumo> pesquisarPorStatus(StatusConvenio status);

	// Suporte para o método 'pesquisar()' do Domínio
	List<ConvenioJpa> findByStatusNot(StatusConvenio status);

	// Suporte para o método 'obterPorNome(String)' do Domínio
	Optional<ConvenioJpa> findByNomeIgnoreCase(String nome);

	// Suporte para o método 'obterPorCodigoIdentificacao(String)' do Domínio
	Optional<ConvenioJpa> findByCodigoIdentificacaoIgnoreCase(String codigoIdentificacao);

	// Suporte para buscar pelo ID
	Optional<ConvenioJpa> findById(Integer id);
}
