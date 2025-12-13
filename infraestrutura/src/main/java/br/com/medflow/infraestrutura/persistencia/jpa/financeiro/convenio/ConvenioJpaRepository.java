package br.com.medflow.infraestrutura.persistencia.jpa.financeiro.convenio;

import br.com.medflow.dominio.financeiro.convenios.StatusConvenio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConvenioJpaRepository 
	extends JpaRepository<ConvenioJpa, Integer> {

	// Suporte para o método 'pesquisar()' do Domínio
	List<ConvenioJpa> findByStatusNot(StatusConvenio status);

	// Suporte para o método 'obterPorNome(String)' do Domínio
	Optional<ConvenioJpa> findByNomeIgnoreCase(String nome);

	// Suporte para o método 'obterPorCodigoIdentificacao(String)' do Domínio
	Optional<ConvenioJpa> findByCodigoIdentificacaoIgnoreCase(String codigoIdentificacao);

	// Suporte para buscar pelo ID
	Optional<ConvenioJpa> findById(Integer id);
}
