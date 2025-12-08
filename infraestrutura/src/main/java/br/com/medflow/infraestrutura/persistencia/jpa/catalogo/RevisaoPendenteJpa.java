package br.com.medflow.infraestrutura.persistencia.jpa.catalogo;

import br.com.medflow.dominio.catalogo.medicamentos.StatusRevisao;
import jakarta.persistence.*;

@Entity
@Table(name = "revisoes_pendentes")
public class RevisaoPendenteJpa {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "novo_valor", columnDefinition = "TEXT")
	private String novoValor;

	@Column(name = "solicitante_id")
	private Integer solicitanteId;

	@Enumerated(EnumType.STRING)
	private StatusRevisao status;

	@Column(name = "revisor_id")
	private Integer revisorId;


    public RevisaoPendenteJpa() {}


    public RevisaoPendenteJpa(Integer id, String novoValor, Integer solicitanteId, StatusRevisao status, Integer revisorId) {
        this.id = id;
        this.novoValor = novoValor;
        this.solicitanteId = solicitanteId;
        this.status = status;
        this.revisorId = revisorId;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getNovoValor() { return novoValor; }
    public void setNovoValor(String novoValor) { this.novoValor = novoValor; }
    public Integer getSolicitanteId() { return solicitanteId; }
    public void setSolicitanteId(Integer solicitanteId) { this.solicitanteId = solicitanteId; }
    public StatusRevisao getStatus() { return status; }
    public void setStatus(StatusRevisao status) { this.status = status; }
    public Integer getRevisorId() { return revisorId; }
    public void setRevisorId(Integer revisorId) { this.revisorId = revisorId; }
}