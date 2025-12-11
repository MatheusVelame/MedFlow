package br.com.medflow.aplicacao.financeiro.convenios;

import br.com.medflow.dominio.financeiro.convenios.StatusConvenio;

public interface ConvenioResumo {
	Integer getId();

	String getNome();

	String getCodigoIdentificacao(); // validar ser vou manter esse mesmo

	public StatusConvenio getStatus();
}
