package br.com.medflow.aplicacao.financeiro.convenios;

import java.util.List;
import java.util.Optional;

import br.com.medflow.dominio.financeiro.convenios.StatusConvenio;


public interface ConvenioRepositorioAplicacao {
	
	List<ConvenioResumo> pesquisarResumos();
	
	Optional<ConvenioDetalhes> obterDetalhesPorId(Integer id);
	
	List<ConvenioResumo> pesquisarPorCodigoIdentificacao(String nomecodigoIdentificacao);// validar ser vou manter esse mesmo
	
	List<ConvenioResumo> pesquisarPorStatus(StatusConvenio status);
}
