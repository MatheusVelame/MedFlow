package br.com.medflow.aplicacao.financeiro.convenios;

import static org.apache.commons.lang3.Validate.notNull;

import java.util.List;

import br.com.medflow.dominio.financeiro.convenios.StatusConvenio;


public class ConvenioServicoAplicacao {
	private final ConvenioRepositorioAplicacao repositorio;

	public ConvenioServicoAplicacao(ConvenioRepositorioAplicacao repositorio) {
		notNull(repositorio, "O repositório não pode ser nulo"); 

		this.repositorio = repositorio;
	}
	
	public List<ConvenioResumo> pesquisarResumos() {
		return repositorio.pesquisarResumos();
	}
	
	public ConvenioDetalhes obterDetalhes(Integer id) {
		return repositorio.obterDetalhesPorId(id)
			.orElseThrow(() -> new RuntimeException("Convênio não encontrado"));
	}
	
	public ConvenioDetalhes obterDetalhesPorCodigoIdentificacao(String codigoIdentificacao) {
		return repositorio.obterDetalhesPorCodigoIdentificacao(codigoIdentificacao)
			.orElseThrow(() -> new RuntimeException("Convênio não encontrado"));
	}
	
	public List<ConvenioResumo> pesquisarPorCodigoIdentificacao(String codigoIdentificacao) { // validar ser vou manter esse mesmo
		return repositorio.pesquisarPorCodigoIdentificacao(codigoIdentificacao);
	}
	
	public List<ConvenioResumo> pesquisarPorStatus(StatusConvenio status) {
		return repositorio.pesquisarPorStatus(status);
	}
}
