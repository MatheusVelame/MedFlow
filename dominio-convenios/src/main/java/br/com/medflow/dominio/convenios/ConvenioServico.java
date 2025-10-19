package br.com.medflow.dominio.convenios;

import static org.apache.commons.lang3.Validate.notNull;

import java.util.List;
import java.util.Optional;

public class ConvenioServico {
	private final ConvenioRepositorio repositorio;
	
	public ConvenioServico(ConvenioRepositorio repositorio) {
		notNull(repositorio, "O repositório de convênios não pode ser nulo");
		this.repositorio = repositorio;
	}
	
	public Convenio cadastrar(String nome, String codigoIdentificacao, UsuarioResponsavelId responsavelId) {
		// Regra de Negócio: O nome do Convenio deve ser único no sistema
		
		var existenteNome = repositorio.obterPorNome(nome);
		if (existenteNome.isPresent()) {
			throw new IllegalArgumentException("O Convênio '" + nome + "' já está registrado no sistema.");
		}
		
		// Regra de Negócio: Não é permitido cadastrar identificações de convênios duplicados (código já existente)
		var existenteCodigo = repositorio.obterPorCodigoIdentificacao(codigoIdentificacao);
		if (existenteCodigo.isPresent()) {
			throw new IllegalArgumentException("O Convênio '" + codigoIdentificacao + "' já está registrado no sistema.");
		}
		
		var novo = new Convenio(nome, codigoIdentificacao, responsavelId);
		repositorio.salvar(novo);
		return novo;
	}
	
	public Convenio obter(ConvenioId id) {
		return repositorio.obter(id);
	}
	
	public void mudarStatus(ConvenioId id, StatusConvenio novoStatus, UsuarioResponsavelId responsavelId, boolean temProcedimentoAtivo) {
		var convenio = obter(id);
		
		if (temProcedimentoAtivo) {
			throw new IllegalStateException("Não é permitido alterar o status devido a prescrições ativas.");
		}
		
		convenio.mudarStatus(novoStatus, responsavelId);
		repositorio.salvar(convenio);
	}
	
	public void arquivar(ConvenioId id, UsuarioResponsavelId responsavelId, boolean temProcedimentoAtivo) {
		var convenio = obter(id);
		
		convenio.arquivar(temProcedimentoAtivo, responsavelId);
		repositorio.salvar(convenio);
	}
	
	public Optional<Convenio> pesquisarNome(String nome) {
		return repositorio.obterPorNome(nome);
	}
	
	public Optional<Convenio> pesquisarCodigoIdentificacao(String codigoIdentificacao) {
		return repositorio.obterPorCodigoIdentificacao(codigoIdentificacao);
	}
	
	public List<Convenio> pesquisarComFiltroArquivado() {
		return repositorio.pesquisarComFiltroArquivado();
	}

	public List<Convenio> pesquisarPadrao() {
		return repositorio.pesquisar();
	}
}
