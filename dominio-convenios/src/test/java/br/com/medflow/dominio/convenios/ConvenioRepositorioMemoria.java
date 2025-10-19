	package br.com.medflow.dominio.convenios;

import static org.apache.commons.lang3.Validate.notNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Implementação em memória do repositório para uso exclusivo em testes BDD.
 */
public class ConvenioRepositorioMemoria implements ConvenioRepositorio{
	private Map<ConvenioId, Convenio> convenios = new HashMap<>();
	private int sequenciaId = 0;
	
	@Override
	public void salvar(Convenio convenio) {
		notNull(convenio, "O convênio não pode ser nulo");
		
		// Simula a geração de ID (para novos agregados) ou atualização
		if (convenio.getId() == null) {
			sequenciaId++;
			ConvenioId novoId = new ConvenioId(sequenciaId);
			
			// Reconstrução forçada para atribuir o ID, mantendo a imutabilidade do AGGREGATE ROOT
			Convenio novo = new Convenio(
				novoId, 
				convenio.getNome(), 
				convenio.getCodigoIdentificacao(),
				convenio.getStatus(),
				convenio.getHistorico()
			);
			convenios.put(novoId, novo);
		} else {
			convenios.put(convenio.getId(), convenio);
		}
	}
	
	@Override
	public Convenio obter(ConvenioId id) {
		notNull(id, "O id do convênio não pode ser nulo");
		var convenio = convenios.get(id);
		return Optional.ofNullable(convenio).get();
	}

	@Override
	public Optional<Convenio> obterPorNome(String nome) {
		return convenios.values().stream()
				.filter(m -> m.getNome().equalsIgnoreCase(nome))
				.findFirst();
	}
	
	@Override
	public Optional<Convenio> obterPorCodigoIdentificacao(String codigoIdentificacao) {
		return convenios.values().stream()
				.filter(m -> m.getCodigoIdentificacao().equalsIgnoreCase(codigoIdentificacao))
				.findFirst();
	}
	
	@Override
	public List<Convenio> pesquisar() {
		// Retorna todos, exceto os ARQUIVADOS (Lista Padrão)
		return convenios.values().stream()
				.filter(m -> m.getStatus() != StatusConvenio.ARQUIVADO)
				.toList();
	}
	
	@Override
	public void remover(ConvenioId id) { 
	    notNull(id, "O ID para remoção não pode ser nulo.");
	    
	    // CORREÇÃO: Remover a entrada diretamente.
	    // O ID é a chave do Map. Se equals/hashCode estiverem certos, deve funcionar.
	    if (!convenios.containsKey(id)) {
	        throw new IllegalArgumentException("Convênio com ID " + id.toString() + " não está no repositório.");
	    }
	    convenios.remove(id); 
	}
	
	 /**
     * Limpa o repositório em memória. Essencial para isolamento de testes BDD.
     */
    public void clear() {
    	convenios.clear();
        sequenciaId = 0;
    }
}
