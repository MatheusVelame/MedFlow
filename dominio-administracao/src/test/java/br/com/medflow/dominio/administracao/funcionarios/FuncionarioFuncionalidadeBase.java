package br.com.medflow.dominio.administracao.funcionarios;

import static org.apache.commons.lang3.Validate.notNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

// Importações corretas para o Barramento de Eventos
import br.com.medflow.dominio.administracao.evento.EventoBarramento;
import br.com.medflow.dominio.administracao.evento.EventoObservador;



public class FuncionarioFuncionalidadeBase implements EventoBarramento {
	// Variáveis de domínio/ambiente
	protected FuncionarioServico funcionarioServico;
	protected FuncionarioRepositorioMemoria repositorio;
	

	private Map<String, UsuarioResponsavelId> usuariosId = new HashMap<>(); 
	private Map<String, List<String>> permissoes = new HashMap<>();
	
	protected List<Object> eventos;

	public FuncionarioFuncionalidadeBase() {
		this.repositorio = new FuncionarioRepositorioMemoria();
		this.funcionarioServico = new FuncionarioServico(repositorio);
		this.eventos = new ArrayList<>();
		
		permissoes.put("Administrador", List.of("cadastrar", "alterar", "mudar_status", "excluir", "consultar"));
		permissoes.put("Enfermeira Chefe", List.of("alterar", "consultar", "mudar_status"));
		permissoes.put("Recepcionista", List.of("consultar"));
		permissoes.put("Enfermeiro", List.of("consultar"));
	}
	
	protected Optional<Funcionario> obterFuncionarioPorNomeEContato(String nome, String contato) {
		return repositorio.obterPorNomeEContato(nome, contato);
	}

	protected Optional<Funcionario> obterFuncionarioPorNome(String nome) {
		return repositorio.pesquisar().stream()
				.filter(f -> f.getNome().equalsIgnoreCase(nome))
				.findFirst();
	}

	protected UsuarioResponsavelId getUsuarioId(String nome) {
		return usuariosId.computeIfAbsent(nome, k -> new UsuarioResponsavelId(usuariosId.size() + 1));
	}
	

	protected boolean temPermissao(String perfil, String acao) {
		return permissoes.getOrDefault(perfil, List.of()).contains(acao);
	}
	
	
	@Override
	public <E> void adicionar(EventoObservador<E> observador) { 
		throw new UnsupportedOperationException();
	}
	

	@Override
	public <E> void postar(E evento) {
		notNull(evento, "O evento não pode ser nulo");
		eventos.add(evento);
	}
}