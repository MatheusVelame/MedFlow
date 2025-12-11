// Localização: dominio-catalogo/src/test/java/br/com/medflow/dominio/catalogo/medicamentos/MedicamentoRepositorioMemoria.java

package br.com.medflow.dominio.catalogo.medicamentos;

import static org.apache.commons.lang3.Validate.notNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class MedicamentoRepositorioMemoria implements MedicamentoRepositorio {
	private Map<MedicamentoId, Medicamento> medicamentos = new HashMap<>();
	private int sequenciaId = 0;

	@Override
	public void salvar(Medicamento medicamento) {
		notNull(medicamento, "O medicamento não pode ser nulo");
		
		// Lógica para gerar ID para medicamentos novos (simulando o banco)
		if (medicamento.getId() == null) {
			sequenciaId++;
			MedicamentoId novoId = new MedicamentoId(sequenciaId);
			
			// Usa o construtor de RECONSTRUÇÃO da AR (6 argumentos)
			// Nota: O AR de produção tem 6 argumentos, adicionamos para a reconstrução funcionar.
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
			// Apenas atualiza o mapa com a versão modificada do Aggregate Root
			medicamentos.put(medicamento.getId(), medicamento);
		}
	}

	// MÉTODOS DE LEITURA (Porta de Domínio)
	
    @Override
    public Optional<Medicamento> buscarPorId(MedicamentoId id) {
        return obter(id);
    }
    
	@Override
	public Optional<Medicamento> obter(MedicamentoId id) {
		notNull(id, "O id do medicamento não pode ser nulo");
		return Optional.ofNullable(medicamentos.get(id));
	}

	@Override
	public Optional<Medicamento> obterPorNome(String nome) {
		return medicamentos.values().stream()
				.filter(m -> m.getNome().equalsIgnoreCase(nome))
				.findFirst();
	}
	
	@Override
	public List<Medicamento> pesquisar() {
		// Retorna todos, exceto os ATIVOS (Lista Padrão)
		return medicamentos.values().stream()
				.filter(m -> m.getStatus() == StatusMedicamento.ATIVO)
				.collect(Collectors.toList());
	}

	@Override
	public List<Medicamento> pesquisarComFiltroArquivado() {
		// Retorna todos, incluindo os ARQUIVADOS
		return new ArrayList<>(medicamentos.values());
	}
    
    // MÉTODOS AUXILIARES PARA TESTE
    
    public void clear() {
        medicamentos.clear();
        sequenciaId = 0;
    }
}