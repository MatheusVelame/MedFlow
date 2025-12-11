// Localização: infraestrutura/src/main/java/br/com/medflow/infraestrutura/persistencia/jpa/catalogo/RevisaoPendenteJpa.java

package br.com.medflow.infraestrutura.persistencia.jpa.catalogo;

import jakarta.persistence.*;
import br.com.medflow.dominio.catalogo.medicamentos.StatusRevisao;

@Entity
@Table(name = "revisoes_pendentes")
public class RevisaoPendenteJpa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "novo_valor", columnDefinition = "TEXT", nullable = false)
    private String novoValor;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusRevisao status; 
    
    @Column(name = "solicitante_id", nullable = false)
    private Integer solicitanteId;
    
    @Column(name = "revisor_id")
    private Integer revisorId;
    
    public RevisaoPendenteJpa() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getNovoValor() { return novoValor; }
    public void setNovoValor(String novoValor) { this.novoValor = novoValor; }
    public StatusRevisao getStatus() { return status; }
    public void setStatus(StatusRevisao status) { this.status = status; }
    public Integer getSolicitanteId() { return solicitanteId; }
    public void setSolicitanteId(Integer solicitanteId) { this.solicitanteId = solicitanteId; }
    public Integer getRevisorId() { return revisorId; }
    public void setRevisorId(Integer revisorId) { this.revisorId = revisorId; }
}