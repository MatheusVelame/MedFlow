package br.com.medflow.aplicacao.financeiro.convenios;

import java.time.LocalDateTime;
import java.util.List;

import br.com.medflow.dominio.financeiro.convenios.StatusConvenio;

public interface ConvenioDetalhes {
	
	Integer getId();
	String getNome();
	String getCodigoIdentificacao();
	StatusConvenio getStatus();

	List<HistoricoEntradaResumo> getHistorico();
}

interface HistoricoEntradaResumo {
    String getAcao(); 
    String getDescricao();
    String getResponsavelNome();
    LocalDateTime getDataHora();
}
