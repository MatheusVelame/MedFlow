package br.com.medflow.aplicacao.catalogo.medicamentos;

import br.com.medflow.dominio.catalogo.medicamentos.Medicamento;
import br.com.medflow.dominio.catalogo.medicamentos.MedicamentoId;
import br.com.medflow.dominio.catalogo.medicamentos.MedicamentoRepositorio;
import br.com.medflow.dominio.catalogo.medicamentos.StatusMedicamento;
import br.com.medflow.dominio.catalogo.medicamentos.UsuarioResponsavelId;


public class MedicamentoServicoAplicacao {

	private final MedicamentoRepositorio repositorio;
	
	public MedicamentoServicoAplicacao(MedicamentoRepositorio repositorio) {
		this.repositorio = repositorio;
	}
	
	public void registrarMedicamento(String nome, String usoprincipal, String contraindicacoes, UsuarioResponsavelId responsavelId) {
		
	}
	
	
}
