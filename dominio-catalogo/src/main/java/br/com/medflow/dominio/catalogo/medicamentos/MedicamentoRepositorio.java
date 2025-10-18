package br.com.medflow.dominio.catalogo.medicamentos;

import java.util.List;
import java.util.Optional;

public interface MedicamentoRepositorio {
	void salvar(Medicamento medicamento);
	Medicamento obter(MedicamentoId id);
	Optional<Medicamento> obterPorNome(String nome);
	List<Medicamento> pesquisar();
	List<Medicamento> pesquisarComFiltroArquivado();
}