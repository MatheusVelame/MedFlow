package br.com.medflow.dominio.catalogo.medicamentos;

import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Medicamento {
	private MedicamentoId id;

	private String nome;
	private String usoPrincipal;
	private String contraindicacoes; // Campo opcional
	private StatusMedicamento status;

	private List<HistoricoEntrada> historico = new ArrayList<>();
	private RevisaoPendente revisaoPendente; // Aggregate interno para alterações críticas

	// Construtor para NOVO cadastro
	public Medicamento(String nome, String usoPrincipal, String contraindicacoes, UsuarioResponsavelId responsavelId) {
		this.id = null; // O repositório irá gerar/atribuir o ID
		setNome(nome);
		setUsoPrincipal(usoPrincipal);
		setContraindicacoes(contraindicacoes);

		// Regra de Negócio: Todo medicamento cadastrado deve ter o status inicial definido automaticamente como “Ativo”
		this.status = StatusMedicamento.ATIVO;

		// Regra de Negócio: O sistema deve ignorar a tentativa de definir um status inicial diferente de "Ativo"
		// A requisição inicial não possui campo status, mas se tivesse, seria ignorado.
		
		adicionarEntradaHistorico(AcaoHistorico.CRIACAO, "Medicamento criado com status Ativo", responsavelId);
	}

	// Construtor para RECONSTRUÇÃO (do Repositório)
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

	// Métodos de negócio (Comandos)
	
	public void atualizarUsoPrincipal(String novoUsoPrincipal, UsuarioResponsavelId responsavelId) {
		notBlank(novoUsoPrincipal, "O uso principal não pode estar em branco."); // Regra de Negócio: Uso Principal é obrigatório

		if (this.usoPrincipal.equals(novoUsoPrincipal)) {
			return; // Nenhuma alteração, histórico não deve ser atualizado.
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
		throw new RevisaoPendenteException("Alteração crítica exige revisão."); // Notificação/Exception para o front-end
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
	
	// Métodos Auxiliares
	private void setNome(String nome) {
		notBlank(nome, "O nome do medicamento é obrigatório.");
		this.nome = nome;
	}
	
	private void setUsoPrincipal(String usoPrincipal) {
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
		if (contraindicacoes != null && !contraindicacoes.matches("^[a-zA-Z0-9áéíóúÁÉÍÓÚãõñÃÕÑçÇ.,\\s()\\-]+$")) {
			throw new IllegalArgumentException("Contraindicações contém caracteres especiais inválidos.");
		}
	}

	private void adicionarEntradaHistorico(AcaoHistorico acao, String descricao, UsuarioResponsavelId responsavelId) {
		var entrada = new HistoricoEntrada(acao, descricao, responsavelId, LocalDateTime.now());
		this.historico.add(entrada);
	}

	public Optional<RevisaoPendente> getRevisaoPendente() {
		return Optional.ofNullable(revisaoPendente);
	}

	// Getters (para verificação de estado nos testes)
	public String getNome() { return nome; }
	public String getUsoPrincipal() { return usoPrincipal; }
	public String getContraindicacoes() { return contraindicacoes; }
	public StatusMedicamento getStatus() { return status; }
	public List<HistoricoEntrada> getHistorico() { return List.copyOf(historico); }
	public MedicamentoId getId() { return id; }

	// Exceção de Negócio (Para cenários de alteração crítica)
	public static class RevisaoPendenteException extends IllegalStateException {
		private static final long serialVersionUID = 1L;
		public RevisaoPendenteException(String s) { super(s); }
	}
}

// Classe de suporte para o agregado
class RevisaoPendente {
    private final String novoValor;
    private final UsuarioResponsavelId solicitante;
    private StatusRevisao status;
	private UsuarioResponsavelId revisor;
	
    public RevisaoPendente(String novoValor, UsuarioResponsavelId solicitante) {
        this.novoValor = novoValor;
        this.solicitante = solicitante;
        this.status = StatusRevisao.PENDENTE;
    }

	public void aprovar(UsuarioResponsavelId revisorId) {
		this.status = StatusRevisao.APROVADA;
		this.revisor = revisorId;
	}

	public void rejeitar(UsuarioResponsavelId revisorId) {
		this.status = StatusRevisao.REPROVADA;
		this.revisor = revisorId;
	}

	public String getNovoValor() { return novoValor; }
    public StatusRevisao getStatus() { return status; }
	public UsuarioResponsavelId getSolicitante() { return solicitante; }
	public UsuarioResponsavelId getRevisor() { return revisor; }
}

// Value Object para a entrada no histórico
class HistoricoEntrada {
	private final AcaoHistorico acao;
	private final String descricao;
	private final UsuarioResponsavelId responsavel;
	private final LocalDateTime dataHora;

	public HistoricoEntrada(AcaoHistorico acao, String descricao, UsuarioResponsavelId responsavel, LocalDateTime dataHora) {
		this.acao = acao;
		this.descricao = descricao;
		this.responsavel = responsavel;
		this.dataHora = dataHora;
	}
	
	public AcaoHistorico getAcao() { return acao; }
	public UsuarioResponsavelId getResponsavel() { return responsavel; }
}