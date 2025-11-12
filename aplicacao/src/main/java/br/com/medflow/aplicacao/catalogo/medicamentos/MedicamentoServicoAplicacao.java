package br.com.medflow.aplicacao.catalogo.medicamentos;

import static org.apache.commons.lang3.Validate.notNull;

import java.util.List;
import br.com.medflow.dominio.catalogo.medicamentos.StatusMedicamento;

public class MedicamentoServicoAplicacao {

	private final MedicamentoRepositorioAplicacao repositorio;

	public MedicamentoServicoAplicacao(MedicamentoRepositorioAplicacao repositorio) {
		notNull(repositorio, "O repositório não pode ser nulo"); 

		this.repositorio = repositorio;
	}

	public List<MedicamentoResumo> pesquisarResumos() {
		return repositorio.pesquisarResumos();
	}

	public MedicamentoDetalhes obterDetalhes(Integer id) {
		return repositorio.obterDetalhesPorId(id)
			.orElseThrow(() -> new RuntimeException("Medicamento não encontrado"));
	}

	public List<MedicamentoResumo> pesquisarMedicamentosComRevisaoPendente() {
		return repositorio.pesquisarMedicamentosComRevisaoPendente();
	}

    public List<MedicamentoResumo> pesquisarPorStatus(StatusMedicamento status) {
		return repositorio.pesquisarPorStatus(status);
	}
}