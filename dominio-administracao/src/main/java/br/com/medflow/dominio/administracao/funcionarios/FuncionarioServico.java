package br.com.medflow.dominio.administracao.funcionarios;

import static org.apache.commons.lang3.Validate.notNull;

import java.util.List;
import java.util.Optional;

public class FuncionarioServico {
	private final FuncionarioRepositorio repositorio;
	
	public FuncionarioServico(FuncionarioRepositorio repositorio) {
		notNull(repositorio, "O repositório de funcionários não pode ser nulo");
		this.repositorio = repositorio;
	}
	
	public Funcionario cadastrar(String nome, String funcao, String contato, UsuarioResponsavelId responsavelId) {
		
		var existente = repositorio.obterPorNomeEContato(nome, contato);
		if (existente.isPresent()) {
			throw new IllegalArgumentException("Já existe um funcionário com este contato.");
		}
		
		var novo = new Funcionario(nome, funcao, contato, responsavelId);
		repositorio.salvar(novo);
		return novo;
	}

	public Funcionario atualizarDadosCadastrais(
			FuncionarioId id,
			String novoNome, 
			String novaFuncao, 
			String novoContato, 
			UsuarioResponsavelId responsavelId, 
			boolean temViculosAtivosFuncao) {
		
		var funcionario = obter(id);
		
		var existente = repositorio.obterPorNomeEContato(novoNome, novoContato);
		if (existente.isPresent() && !existente.get().getId().equals(id)) {
			throw new IllegalArgumentException("Já existe um funcionário com este contato.");
		}
		
		
		funcionario.atualizarDados(novoNome, novaFuncao, novoContato, responsavelId, temViculosAtivosFuncao);
		repositorio.salvar(funcionario);
		return funcionario;
	}

	public void mudarStatus(FuncionarioId id, StatusFuncionario novoStatus, UsuarioResponsavelId responsavelId, boolean temAtividadesFuturas) {
		var funcionario = obter(id);
		

		funcionario.mudarStatus(novoStatus, responsavelId, temAtividadesFuturas);
		repositorio.salvar(funcionario);
	}
	
	public Funcionario atualizarCompleto(
			FuncionarioId id,
			String nome, 
			String funcao, 
			String contato,
			StatusFuncionario status,
			UsuarioResponsavelId responsavelId, 
			boolean temViculosAtivosFuncao,
			boolean temAtividadesFuturas) {
		
		var funcionario = obter(id);
		
		// Valida se o nome e contato não estão duplicados
		var existente = repositorio.obterPorNomeEContato(nome, contato);
		if (existente.isPresent() && !existente.get().getId().equals(id)) {
			throw new IllegalArgumentException("Já existe um funcionário com este contato.");
		}
		
		// Atualiza os dados cadastrais
		funcionario.atualizarDados(nome, funcao, contato, responsavelId, temViculosAtivosFuncao);
		
		// Atualiza o status se for diferente
		if (funcionario.getStatus() != status) {
			funcionario.mudarStatus(status, responsavelId, temAtividadesFuturas);
		}
		
		repositorio.salvar(funcionario);
		return funcionario;
	}
	
	public void excluir(FuncionarioId id, UsuarioResponsavelId responsavelId, boolean possuiHistorico) {
	    var funcionario = obter(id);

	    if (funcionario.getStatus() != StatusFuncionario.INATIVO) {
	        throw new IllegalStateException("A exclusão definitiva só pode ser realizada em funcionários INATIVOS.");
	    }

	    if (possuiHistorico) {
	        throw new IllegalStateException("Não é possível excluir o funcionário, pois ele possui histórico de atuação (escalas/atendimentos) que deve ser preservado.");
	    }
	    
	    funcionario.adicionarEntradaHistorico(AcaoHistorico.EXCLUSAO, "Funcionário excluído permanentemente do sistema.", responsavelId);
	    
	    repositorio.remover(id); 
	}

	public void validarAtribuicaoParaNovaAtividade(FuncionarioId id) {
		var funcionario = obter(id);
		
		funcionario.validarAtribuicaoAtividade();
	}
	
	public Funcionario obter(FuncionarioId id) {
		return repositorio.obter(id);
	}
	
	public Optional<Funcionario> pesquisarPorNomeEContato(String nome, String contato) {
		return repositorio.obterPorNomeEContato(nome, contato);
	}
	
	public List<Funcionario> pesquisarTodos() {
		return repositorio.pesquisar();
	}
}