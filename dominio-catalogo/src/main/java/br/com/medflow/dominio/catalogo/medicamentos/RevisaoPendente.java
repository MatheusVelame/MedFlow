package br.com.medflow.dominio.catalogo.medicamentos;

// Value Object de Revisão Pendente
public class RevisaoPendente {
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