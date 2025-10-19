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
		// Assumindo que este método lança exceção se não encontrar.
		return repositorio.obter(id); 
	}
	
	public void mudarStatus(ConvenioId id, StatusConvenio novoStatus, UsuarioResponsavelId responsavelId, boolean temProcedimentoAtivo) {
		var convenio = obter(id);
		
		// Regra: Não é permitido mudar o status se houver procedimento ativo (para INATIVO)
		// NOTA: Esta validação deveria estar no método mudarStatus da entidade se fosse complexa. 
		if (novoStatus == StatusConvenio.INATIVO && temProcedimentoAtivo) {
			throw new IllegalStateException("Não é permitido alterar o status para INATIVO devido a prescrições ativas.");
		}
		
		convenio.mudarStatus(novoStatus, responsavelId);
		repositorio.salvar(convenio);
	}
	
	// Em ConvenioServico.java

	public void excluir(String codigoIdentificacao, UsuarioResponsavelId responsavelId, boolean temProcedimentoAtivo) {
	    // 1. Obtém o convênio, lança exceção se não encontrar.
	    var convenio = repositorio.obterPorCodigoIdentificacao(codigoIdentificacao)
	        .orElseThrow(() -> new IllegalArgumentException("Convênio não encontrado."));
	    
	    // 2. Valida (usando a Entidade)
	    convenio.validarExclusao(temProcedimentoAtivo, responsavelId);
	    
	    // 3. Registra o Histórico (Antes da remoção)
	    convenio.adicionarEntradaHistorico(AcaoHistorico.EXCLUSAO, "Convênio excluído permanentemente.", responsavelId);
	    
	    // 4. REMOÇÃO FÍSICA NO REPOSITÓRIO:
	    // Deve remover usando o ID do objeto encontrado
	    repositorio.remover(convenio.getId()); // <--- ESTE MÉTODO DEVE USAR O ID
	    
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