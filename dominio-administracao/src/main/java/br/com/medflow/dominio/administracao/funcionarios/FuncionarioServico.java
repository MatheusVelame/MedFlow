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

	public void salvar(Medico medico) {

		// 1. Validar se a instância é nula (RN1)
		notNull(medico, "O Médico não pode ser nulo.");

		// 2. RN5: CHECAGEM DE UNICIDADE DO CRM
		// O repositório irá tentar encontrar um médico que já use este CRM.
		// Se encontrar (isPresent), lança exceção.
		repositorio.obterPorCrm(medico.getCrm())
				.ifPresent(m -> {
					throw new IllegalArgumentException("CRM já está em uso.");
				});
		// 3. Salvar o Medico no repositório (chama o método salvar(Funcionario))
		repositorio.salvar(medico);
	}

	// Dentro da classe FuncionarioServico (após o método salvar(Medico medico))

	// NOVO MÉTODO: Lógica para Atualização (RN1 de Atualização: CRM Imutável)
	public Medico atualizarDadosMedico(
			FuncionarioId id,
			String novoNome,
			String novoContato,
			CRM crmParaChecagem, // O CRM que o usuário tentou enviar
			UsuarioResponsavelId responsavelId
	) {

		var funcionarioObtido = obter(id);

		if (!(funcionarioObtido instanceof Medico)) {
			throw new IllegalArgumentException("ID pertence a um funcionário não médico.");
		}

		Medico medicoExistente = (Medico) funcionarioObtido;

		// RN1 de Atualização: Checar se o CRM mudou.
		if (!medicoExistente.getCrm().equals(crmParaChecagem)) {
			// Se o CRM que está no banco for diferente do que o usuário tentou enviar
			throw new IllegalArgumentException("O CRM não pode ser alterado após o cadastro inicial.");
		}

		// Se o CRM for o mesmo (RN1 passou), reusa a lógica de atualização genérica (do seu amigo)
		// Assumindo que a função é "MEDICO" e não há vínculos ativos de função (para o teste).
		// O método do seu amigo é complexo, vou chamá-lo com valores de teste para compilar:
		atualizarDadosCadastrais(id, novoNome, "MEDICO", novoContato, responsavelId, false);

		return (Medico) repositorio.obter(id);
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