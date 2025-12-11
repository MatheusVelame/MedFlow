package br.com.medflow.dominio.financeiro.convenios;

import static org.apache.commons.lang3.Validate.notNull;

import java.util.List;
import java.util.Optional;

import br.com.medflow.dominio.financeiro.evento.EventoBarramento;

public class ConvenioServico {
	private final ConvenioRepositorio repositorio;
	private final EventoBarramento barramento;

	public ConvenioServico(ConvenioRepositorio repositorio) {
		notNull(repositorio, "O repositório de convênios não pode ser nulo");
		this.repositorio = repositorio;
		this.barramento = null; // Compatibilidade com código existente
	}

	public ConvenioServico(ConvenioRepositorio repositorio, EventoBarramento barramento) {
		notNull(repositorio, "O repositório de convênios não pode ser nulo");
		notNull(barramento, "O barramento de eventos não pode ser nulo");
		this.repositorio = repositorio;
		this.barramento = barramento;
	}

	public Convenio cadastrar(String nome, String codigoIdentificacao, UsuarioResponsavelId responsavelId) {

		var existenteNome = repositorio.obterPorNome(nome);
		if (existenteNome.isPresent()) {
			throw new IllegalArgumentException("O Convênio '" + nome + "' já está registrado no sistema.");
		}

		var existenteCodigo = repositorio.obterPorCodigoIdentificacao(codigoIdentificacao);
		if (existenteCodigo.isPresent()) {
			throw new IllegalArgumentException(
					"O Convênio '" + codigoIdentificacao + "' já está registrado no sistema.");
		}

		var novo = new Convenio(nome, codigoIdentificacao, responsavelId);

		repositorio.salvar(novo);

		var convenioSalvo = repositorio.obterPorCodigoIdentificacao(codigoIdentificacao).orElseThrow(() -> new IllegalStateException(
				"Falha na persistência: Convênio não encontrado após cadastro inicial."));

		// Postar evento de domínio
		if (barramento != null) {
			barramento.postar(new ConvenioCriadoEvent(
				convenioSalvo.getId(),
				convenioSalvo.getNome(),
				convenioSalvo.getCodigoIdentificacao(),
				convenioSalvo.getStatus(),
				responsavelId
			));
		}

		return convenioSalvo;
	}

	public Convenio obter(ConvenioId id) {
		return repositorio.obter(id);
	}

	public void mudarStatus(ConvenioId id, StatusConvenio novoStatus, UsuarioResponsavelId responsavelId,
			boolean temProcedimentoAtivo) {
		var convenio = obter(id);
		StatusConvenio statusAnterior = convenio.getStatus();

		if (novoStatus == StatusConvenio.INATIVO && temProcedimentoAtivo) {
			throw new IllegalStateException(
					"Não é permitido alterar o status para INATIVO devido a prescrições ativas.");
		}

		convenio.mudarStatus(novoStatus, responsavelId);
		repositorio.salvar(convenio);

		// Postar evento de domínio se o status realmente mudou
		if (barramento != null && statusAnterior != novoStatus) {
			barramento.postar(new ConvenioStatusAlteradoEvent(
				id,
				statusAnterior,
				novoStatus,
				responsavelId
			));
		}
	}

	public void excluir(String codigoIdentificacao, UsuarioResponsavelId responsavelId, boolean temProcedimentoAtivo) {
		var convenio = repositorio.obterPorCodigoIdentificacao(codigoIdentificacao)
				.orElseThrow(() -> new IllegalArgumentException("Convênio não encontrado."));

		convenio.validarExclusao(temProcedimentoAtivo, responsavelId);

		// Capturar informações antes da exclusão
		ConvenioId convenioId = convenio.getId();
		String nome = convenio.getNome();
		String codigo = convenio.getCodigoIdentificacao();
		StatusConvenio statusAntesExclusao = convenio.getStatus();

		convenio.adicionarEntradaHistorico(AcaoHistorico.EXCLUSAO,
				"Convênio excluído permanentemente.", responsavelId);

		repositorio.remover(convenio.getId());

		// Postar evento de domínio após a exclusão
		if (barramento != null) {
			barramento.postar(new ConvenioExcluidoEvent(
				convenioId,
				nome,
				codigo,
				statusAntesExclusao,
				responsavelId
			));
		}
	}

	// Método sobrecarregado para compatibilidade com código existente
	public void excluir(String codigoIdentificacao, UsuarioResponsavelId responsavelId, boolean temProcedimentoAtivo,
			EventoBarramento barramentoExterno) {
		// Se um barramento externo foi passado, usar ele temporariamente
		// Mas preferir usar o barramento injetado se disponível
		EventoBarramento barramentoParaUsar = this.barramento != null ? this.barramento : barramentoExterno;
		
		var convenio = repositorio.obterPorCodigoIdentificacao(codigoIdentificacao)
				.orElseThrow(() -> new IllegalArgumentException("Convênio não encontrado."));

		convenio.validarExclusao(temProcedimentoAtivo, responsavelId);

		// Capturar informações antes da exclusão
		ConvenioId convenioId = convenio.getId();
		String nome = convenio.getNome();
		String codigo = convenio.getCodigoIdentificacao();
		StatusConvenio statusAntesExclusao = convenio.getStatus();

		convenio.adicionarEntradaHistorico(AcaoHistorico.EXCLUSAO,
				"Convênio excluído permanentemente.", responsavelId);

		repositorio.remover(convenio.getId());

		// Postar evento de domínio após a exclusão
		if (barramentoParaUsar != null) {
			barramentoParaUsar.postar(new ConvenioExcluidoEvent(
				convenioId,
				nome,
				codigo,
				statusAntesExclusao,
				responsavelId
			));
		}
	}

	public Optional<Convenio> pesquisarNome(String nome) {
		return repositorio.obterPorNome(nome);
	}

	public Optional<Convenio> pesquisarCodigoIdentificacao(String codigoIdentificacao) {
		return repositorio.obterPorCodigoIdentificacao(codigoIdentificacao);
	}

	public void alterarNome(ConvenioId id, String novoNome, UsuarioResponsavelId responsavelId) {
		var convenio = obter(id);
		String nomeAnterior = convenio.getNome();

		convenio.alterarNome(novoNome, responsavelId);
		repositorio.salvar(convenio);

		// Postar evento de domínio se o nome realmente mudou
		if (barramento != null && !nomeAnterior.trim().equalsIgnoreCase(novoNome.trim())) {
			barramento.postar(new ConvenioNomeAlteradoEvent(
				id,
				nomeAnterior,
				novoNome,
				responsavelId
			));
		}
	}

	public List<Convenio> pesquisarPadrao() {
		return repositorio.pesquisar();
	}
}