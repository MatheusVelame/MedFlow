package br.com.medflow.dominio.catalogo.medicamentos;

import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Objects; 

/**
 * CLASSE MODIFICADA PARA IMPLEMENTAR O PADRÃO ITERATOR.
 * Implementa ColecaoHistorico para ser o Aggregate/Collection.
 */
public class Medicamento implements ColecaoHistorico { // <-- MODIFICAÇÃO AQUI
	private MedicamentoId id;

	private String nome;
	private String usoPrincipal;
	private String contraindicacoes;
	private StatusMedicamento status;

	private List<HistoricoEntrada> historico = new ArrayList<>();
	private RevisaoPendente revisaoPendente;

    /**
     * Construtor padrão exigido pelo ModelMapper para mapeamento JPA -> Domínio.
     */
    public Medicamento() {
        this.historico = new ArrayList<>();
    }

	public Medicamento(String nome, String usoPrincipal, String contraindicacoes, UsuarioResponsavelId responsavelId) {
		this.id = null;
		setNome(nome);
		setUsoPrincipal(usoPrincipal);
		setContraindicacoes(contraindicacoes);

		this.status = StatusMedicamento.ATIVO;

		adicionarEntradaHistorico(AcaoHistorico.CRIACAO, "Medicamento criado com status Ativo", responsavelId);
	}

	public Medicamento(MedicamentoId id, String nome, String usoPrincipal, String contraindicacoes, StatusMedicamento status, List<HistoricoEntrada> historico, RevisaoPendente revisaoPendente) {
		notNull(id, "O ID do medicamento não pode ser nulo na reconstrução.");
		this.id = id;
		this.nome = nome;
		this.usoPrincipal = usoPrincipal;
		this.contraindicacoes = contraindicacoes;
		this.status = status;
		this.historico.addAll(historico);
		this.revisaoPendente = revisaoPendente;
	}

	public void atualizarUsoPrincipal(String novoUsoPrincipal, UsuarioResponsavelId responsavelId) {
		notBlank(novoUsoPrincipal, "O uso principal não pode estar em branco.");

		// CORREÇÃO: Usa Objects.equals() para comparação segura contra null
		if (Objects.equals(this.usoPrincipal, novoUsoPrincipal)) {
			return;
		}

		this.usoPrincipal = novoUsoPrincipal;
		adicionarEntradaHistorico(AcaoHistorico.ATUALIZACAO, "Uso principal alterado para: " + novoUsoPrincipal, responsavelId);
	}
	
	public void mudarStatus(StatusMedicamento novoStatus, UsuarioResponsavelId responsavelId) {
		if (this.status == StatusMedicamento.ARQUIVADO && novoStatus == StatusMedicamento.INATIVO) {
			throw new IllegalStateException("Medicamento arquivado não pode ser alterado para Inativo.");
		}
		if (this.status == novoStatus) {
			return;
		}
		
		this.status = novoStatus;
		adicionarEntradaHistorico(AcaoHistorico.ATUALIZACAO, "Status alterado para: " + novoStatus.name(), responsavelId);
	}
	
	public void arquivar(boolean temPrescricaoAtiva, UsuarioResponsavelId responsavelId) {
		if (temPrescricaoAtiva) {
			throw new IllegalStateException("Não é permitido arquivar medicamentos vinculados a prescrições ativas.");
		}
		
		if (this.status != StatusMedicamento.ARQUIVADO) {
			this.status = StatusMedicamento.ARQUIVADO;
			adicionarEntradaHistorico(AcaoHistorico.ARQUIVAMENTO, "Medicamento arquivado.", responsavelId);
		}
	}
	
	public void solicitarRevisaoContraindicacoes(String novaContraindicacao, UsuarioResponsavelId responsavelId) {
		validarContraindicacoes(novaContraindicacao);
		
		this.revisaoPendente = new RevisaoPendente(novaContraindicacao, responsavelId);
		adicionarEntradaHistorico(AcaoHistorico.REVISAO_SOLICITADA, "Alteração crítica de Contraindicações solicitada: " + novaContraindicacao, responsavelId);
		throw new RevisaoPendenteException("Alteração crítica exige revisão.");
	}

	public void aprovarRevisao(UsuarioResponsavelId revisorId) {
		if (revisaoPendente == null || revisaoPendente.getStatus() != StatusRevisao.PENDENTE) {
			throw new IllegalStateException("Não há revisão pendente para aprovação.");
		}

		this.contraindicacoes = revisaoPendente.getNovoValor();
		this.revisaoPendente.aprovar(revisorId);
		
		adicionarEntradaHistorico(AcaoHistorico.REVISAO_APROVADA, "Revisão de Contraindicações APROVADA.", revisorId);
	}
	
	public void rejeitarRevisao(UsuarioResponsavelId revisorId) {
		if (revisaoPendente == null || revisaoPendente.getStatus() != StatusRevisao.PENDENTE) {
			throw new IllegalStateException("Não há revisão pendente para rejeição.");
		}

		this.revisaoPendente.rejeitar(revisorId);
		adicionarEntradaHistorico(AcaoHistorico.REVISAO_REPROVADA, "Revisão de Contraindicações REPROVADA.", revisorId);
	}
	
	private void setNome(String nome) {
		notBlank(nome, "O nome do medicamento é obrigatório.");
		this.nome = nome;
	}
	
	// Adicione este setter para auxiliar o ModelMapper na reconstrução (Recomendado)
	public void setUsoPrincipal(String usoPrincipal) {
		notBlank(usoPrincipal, "O uso principal do medicamento é obrigatório.");
		this.usoPrincipal = usoPrincipal;
	}

	private void setContraindicacoes(String contraindicacoes) {
		if (contraindicacoes != null) {
			validarContraindicacoes(contraindicacoes);
		}
		this.contraindicacoes = contraindicacoes;
	}

	private void validarContraindicacoes(String contraindicacoes) {
		String regex = "^[a-zA-Z0-9áéíóúÁÉÍÓÚãõñÃÕÑçÇàÀ.,\\s()\\-]+$";
		if (contraindicacoes != null && !contraindicacoes.matches(regex)) {
			throw new IllegalArgumentException("Contraindicações contém caracteres especiais inválidos.");
		}
	}

	private void adicionarEntradaHistorico(AcaoHistorico acao, String descricao, UsuarioResponsavelId responsavelId) {
		var entrada = new HistoricoEntrada(acao, descricao, responsavelId, LocalDateTime.now());
		this.historico.add(entrada);
	}

    // MÉTODO NOVO: Implementação do padrão Iterator (ColecaoHistorico)
    @Override
    public IteradorHistorico<HistoricoEntrada> criarIterador() {
        // Retorna uma cópia defensiva da lista para o Iterator, garantindo encapsulamento.
        return new MedicamentoHistoricoIterator(List.copyOf(this.historico));
    }
	
	public Optional<RevisaoPendente> getRevisaoPendente() {
		return Optional.ofNullable(revisaoPendente);
	}

	public String getNome() { return nome; }
	public String getUsoPrincipal() { return usoPrincipal; }
	public String getContraindicacoes() { return contraindicacoes; }
	public StatusMedicamento getStatus() { return status; }
    
    // MÉTODO MODIFICADO (Opcional, mas boa prática para desencorajar o acesso direto)
    @Deprecated // Indicando que o método preferencial é via Iterator
	public List<HistoricoEntrada> getHistorico() { return List.copyOf(historico); }
	public MedicamentoId getId() { return id; }

}