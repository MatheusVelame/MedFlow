package br.com.medflow.aplicacao.administracao.funcionarios;

import br.com.medflow.dominio.administracao.funcionarios.StatusFuncionario;
import java.util.List;

public interface FuncionarioDetalhes {
	
	Integer getId();
	String getNome();
	String getFuncao();
	String getContato();
	StatusFuncionario getStatus();
	
	List<HistoricoEntradaResumo> getHistorico();
}
