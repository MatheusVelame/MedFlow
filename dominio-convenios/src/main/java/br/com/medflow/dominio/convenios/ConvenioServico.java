package br.com.medflow.dominio.convenios;

import static org.apache.commons.lang3.Validate.notNull;

import java.util.List;
import java.util.Optional;

import br.com.medflow.dominio.evento.EventoBarramento;
import br.com.medflow.dominio.convenios.Convenio.HistoricoEntrada;

public class ConvenioServico {
	private final ConvenioRepositorio repositorio;

	public ConvenioServico(ConvenioRepositorio repositorio) {
		notNull(repositorio, "O repositório de convênios não pode ser nulo");
		this.repositorio = repositorio;
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

		return repositorio.obterPorCodigoIdentificacao(codigoIdentificacao).orElseThrow(() -> new IllegalStateException(
				"Falha na persistência: Convênio não encontrado após cadastro inicial."));
	}

	public Convenio obter(ConvenioId id) {
		return repositorio.obter(id);
	}

	public void mudarStatus(ConvenioId id, StatusConvenio novoStatus, UsuarioResponsavelId responsavelId,
			boolean temProcedimentoAtivo) {
		var convenio = obter(id);

		if (novoStatus == StatusConvenio.INATIVO && temProcedimentoAtivo) {
			throw new IllegalStateException(
					"Não é permitido alterar o status para INATIVO devido a prescrições ativas.");
		}

		convenio.mudarStatus(novoStatus, responsavelId);
		repositorio.salvar(convenio);
	}

	public void excluir(String codigoIdentificacao, UsuarioResponsavelId responsavelId, boolean temProcedimentoAtivo,
			EventoBarramento barramento) {
		var convenio = repositorio.obterPorCodigoIdentificacao(codigoIdentificacao)
				.orElseThrow(() -> new IllegalArgumentException("Convênio não encontrado."));

		// 1. Validação (mantida a lógica de domínio)
		convenio.validarExclusao(temProcedimentoAtivo, responsavelId);

		// 2. Registra o Histórico de EXCLUSÃO e CAPTURA o objeto retornado
		HistoricoEntrada logRemocao = convenio.adicionarEntradaHistorico(AcaoHistorico.EXCLUSAO,
				"Convênio excluído permanentemente.", responsavelId);

		barramento.postar(logRemocao);

		// 4. REMOÇÃO FÍSICA NO REPOSITÓRIO
		repositorio.remover(convenio.getId());
	}

	public Optional<Convenio> pesquisarNome(String nome) {
		return repositorio.obterPorNome(nome);
	}

	public Optional<Convenio> pesquisarCodigoIdentificacao(String codigoIdentificacao) {
		return repositorio.obterPorCodigoIdentificacao(codigoIdentificacao);
	}

	// CORREÇÃO: Removido o filtro de arquivado
	public List<Convenio> pesquisarPadrao() {
		return repositorio.pesquisar();
	}
}