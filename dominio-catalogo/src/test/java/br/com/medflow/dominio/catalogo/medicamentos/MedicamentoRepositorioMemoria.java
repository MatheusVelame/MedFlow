package br.com.medflow.dominio.catalogo.medicamentos;

import static org.apache.commons.lang3.Validate.notNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Implementação em memória do repositório para uso exclusivo em testes BDD.
 */
public class MedicamentoRepositorioMemoria implements MedicamentoRepositorio {
	private Map<MedicamentoId, Medicamento> medicamentos = new HashMap<>();
	private int sequenciaId = 0;

	@Override
	public void salvar(Medicamento medicamento) {
		notNull(medicamento, "O medicamento não pode ser nulo");
		
		// Simula a geração de ID (para novos agregados) ou atualização
		if (medicamento.getId() == null) {
			sequenciaId++;
			MedicamentoId novoId = new MedicamentoId(sequenciaId);
			
			// Reconstrução forçada para atribuir o ID, mantendo a imutabilidade do AGGREGATE ROOT
			Medicamento novo = new Medicamento(
				novoId, 
				medicamento.getNome(), 
				medicamento.getUsoPrincipal(), 
				medicamento.getContraindicacoes(), 
				medicamento.getStatus(),
				medicamento.getHistorico(),
				medicamento.getRevisaoPendente().orElse(null)
			);
			medicamentos.put(novoId, novo);
		} else {
			medicamentos.put(medicamento.getId(), medicamento);
		}
	}

	@Override
	public Medicamento obter(MedicamentoId id) {
		notNull(id, "O id do medicamento não pode ser nulo");
		var medicamento = medicamentos.get(id);
		return Optional.ofNullable(medicamento).get();
	}

	@Override
	public Optional<Medicamento> obterPorNome(String nome) {
		return medicamentos.values().stream()
				.filter(m -> m.getNome().equalsIgnoreCase(nome))
				.findFirst();
	}
	
	@Override
	public List<Medicamento> pesquisar() {
		// Retorna todos, exceto os ARQUIVADOS (Lista Padrão)
		return medicamentos.values().stream()
				.filter(m -> m.getStatus() != StatusMedicamento.ARQUIVADO)
				.toList();
	}

	@Override
	public List<Medicamento> pesquisarComFiltroArquivado() {
		// Retorna todos, incluindo os ARQUIVADOS
		return new ArrayList<>(medicamentos.values());
	}
    
    /**
     * Limpa o repositório em memória. Essencial para isolamento de testes BDD.
     */
    public void clear() {
        medicamentos.clear();
        sequenciaId = 0;
    }
}