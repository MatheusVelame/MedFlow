package br.com.medflow.aplicacao.catalogo.medicamentos;

import br.com.medflow.dominio.catalogo.medicamentos.StatusMedicamento;

public interface MedicamentoResumo {
	Integer getId();

	String getNome();

	String getUsoPrincipal();

	StatusMedicamento getStatus();
}