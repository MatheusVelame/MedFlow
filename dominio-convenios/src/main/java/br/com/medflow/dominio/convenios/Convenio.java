package br.com.medflow.dominio.convenios;

import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Convenio {
	private ConvenioId id;
    private String nome;
    private String codigoIdentificacao;
    private StatusConvenio status;

    private List<HistoricoEntrada> historico = new ArrayList<>();
    
    
    public Convenio(String nome, String codigoIdentificacao, UsuarioResponsavelId responsavelId) {
    	this.id = null;
        setNome(nome);
        setCodigoIdentificacao(codigoIdentificacao);
        this.status = StatusConvenio.ATIVO;
        
        adicionarEntradaHistorico(AcaoHistorico.CRIACAO, "Convênio criado com status Ativo", responsavelId);
    }

    public Convenio(ConvenioId id, String nome, String codigoIdentificacao, StatusConvenio status, List<HistoricoEntrada> historico) {
    	notNull(id, "O ID do convênio não pode ser nulo na reconstrução.");
    	this.id = id;
        this.nome = nome;
        this.codigoIdentificacao = codigoIdentificacao;
        this.status = status;
        this.historico.addAll(historico);
    }

    public void mudarStatus(StatusConvenio novoStatus, UsuarioResponsavelId responsavelId) {
		if (this.status == StatusConvenio.ATIVO && novoStatus == StatusConvenio.ARQUIVADO) {
			throw new IllegalStateException("Convênio ativo não pode ser arquivado.");
		}
		if (this.status == novoStatus) {
			return;
		}
		
		this.status = novoStatus;
		adicionarEntradaHistorico(AcaoHistorico.ATUALIZACAO, "Status alterado para: " + novoStatus.name(), responsavelId);
	}
    
    public void arquivar(boolean temProcedimentoAtivo, UsuarioResponsavelId responsavelId) {
		if (this.status != StatusConvenio.ARQUIVADO) {
			this.status = StatusConvenio.ARQUIVADO;
			adicionarEntradaHistorico(AcaoHistorico.ARQUIVAMENTO, "Convênio arquivado.", responsavelId);
		}
	}
    
    
    public void setNome(String nome) {
        notBlank(nome, "O nome do convênio é obrigatório");
        this.nome = nome.trim();
    }

    public void setCodigoIdentificacao(String codigoIdentificacao) {
        notBlank(codigoIdentificacao, "O código de identificação do convênio é obrigatório");
        
        validarCodigoIdentificacao(codigoIdentificacao);
        this.codigoIdentificacao = codigoIdentificacao;
    }
    
    private void validarCodigoIdentificacao(String codigoIdentificacao) {
		String regex = "^[a-zA-Z0-9áéíóúÁÉÍÓÚãõñÃÕÑçÇàÀ.,\\s()\\-]+$";
		if (codigoIdentificacao != null && !codigoIdentificacao.matches(regex)) {
			throw new IllegalArgumentException("Código de identificação contém caracteres especiais inválidos.");
		}
	}

    public void setStatus(StatusConvenio status) {
        if (status == null) {
            throw new IllegalArgumentException("O status do convênio não pode ser nulo.");
        }
        this.status = status;
    }

    private void adicionarEntradaHistorico(AcaoHistorico acao, String descricao, UsuarioResponsavelId responsavelId) {
		var entrada = new HistoricoEntrada(acao, descricao, responsavelId, LocalDateTime.now());
		this.historico.add(entrada);
	}

    // Getters
    public ConvenioId getId() { return id; }
    public String getNome() { return nome; }
    public String getCodigoIdentificacao() { return codigoIdentificacao; }
    public StatusConvenio getStatus() { return status; }
    public List<HistoricoEntrada> getHistorico() { return List.copyOf(historico); }

}

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
