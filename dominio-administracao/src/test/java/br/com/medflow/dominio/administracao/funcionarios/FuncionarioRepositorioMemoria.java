package br.com.medflow.dominio.administracao.funcionarios;

import static org.apache.commons.lang3.Validate.notNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class FuncionarioRepositorioMemoria implements FuncionarioRepositorio{
	private Map<FuncionarioId, Funcionario> funcionarios = new HashMap<>();
	private int sequenciaId = 0;
	
	@Override
	public void salvar(Funcionario funcionario) {
		notNull(funcionario, "O funcionário não pode ser nulo");
		
		if (funcionario.getId() == null) {
			sequenciaId++;
			FuncionarioId novoId = new FuncionarioId(sequenciaId);
			
			Funcionario novo = new Funcionario(
				novoId,
				funcionario.getNome(),
				funcionario.getFuncao(),
				funcionario.getContato(),
				funcionario.getStatus(),
				funcionario.getHistorico()
			);
			funcionarios.put(novoId, novo);
			

			funcionario.setId(novoId); 
			
		} else {
			funcionarios.put(funcionario.getId(), funcionario);
		}
	}
	
	@Override
	public Funcionario obter(FuncionarioId id) {
		notNull(id, "O id do funcionário não pode ser nulo");
		var funcionario = funcionarios.get(id);
		
		return Optional.ofNullable(funcionario)
				.orElseThrow(() -> new IllegalArgumentException("Funcionário não encontrado com ID: " + id.getId()));
	}


	@Override
	public Optional<Funcionario> obterPorNomeEContato(String nome, String contato) {
		return funcionarios.values().stream()
				.filter(f -> f.getNome().equalsIgnoreCase(nome) && 
				             f.getContato().equalsIgnoreCase(contato))
				.findFirst();
	}
	
	@Override
	public List<Funcionario> pesquisar() {
		return List.copyOf(funcionarios.values());
	}
	
	@Override
	public void remover(FuncionarioId id) {
	    notNull(id, "O ID para remoção não pode ser nulo.");
	    
	    if (!funcionarios.containsKey(id)) {
	        throw new IllegalArgumentException("Funcionário com ID " + id.toString() + " não está no repositório.");
	    }
	    funcionarios.remove(id);
	}
	

    public void clear() {
    	funcionarios.clear();
        sequenciaId = 0;
    }
}