package br.com.medflow.aplicacao.catalogo.medicamentos;

import java.util.List;
import java.util.Optional;
import br.com.medflow.dominio.catalogo.medicamentos.StatusMedicamento;

public interface MedicamentoRepositorioAplicacao {
	
	List<MedicamentoResumo> pesquisarResumos(); 

	Optional<MedicamentoDetalhes> obterDetalhesPorId(Integer id);
    
	List<MedicamentoResumo> pesquisarPorStatus(StatusMedicamento status);
	
	List<MedicamentoResumo> pesquisarMedicamentosComRevisaoPendente();
}