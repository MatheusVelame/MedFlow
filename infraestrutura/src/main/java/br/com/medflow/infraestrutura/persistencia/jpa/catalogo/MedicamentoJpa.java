package br.com.medflow.infraestrutura.persistencia.jpa.catalogo;

import br.com.medflow.dominio.catalogo.medicamentos.StatusMedicamento;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "medicamentos")
public class MedicamentoJpa {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(nullable = false)
	private String nome;

	@Column(name = "uso_principal", nullable = false)
	private String usoPrincipal;

	@Column(columnDefinition = "TEXT")
	private String contraindicacoes;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private StatusMedicamento status;

	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "revisao_pendente_id")
	private RevisaoPendenteJpa revisaoPendente;
    

	@OneToMany(mappedBy = "medicamento", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<HistoricoEntradaJpa> historico = new ArrayList<>();

    public MedicamentoJpa() {}
    
    public MedicamentoJpa(Integer id, String nome, String usoPrincipal, String contraindicacoes, 
                          StatusMedicamento status, List<HistoricoEntradaJpa> historico, 
                          RevisaoPendenteJpa revisaoPendente) {
        this.id = id;
        this.nome = nome;
        this.usoPrincipal = usoPrincipal;
        this.contraindicacoes = contraindicacoes;
        this.status = status;
        this.historico = historico != null ? historico : new ArrayList<>();
        this.revisaoPendente = revisaoPendente;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getUsoPrincipal() { return usoPrincipal; }
    public void setUsoPrincipal(String usoPrincipal) { this.usoPrincipal = usoPrincipal; }
    public String getContraindicacoes() { return contraindicacoes; }
    public void setContraindicacoes(String contraindicacoes) { this.contraindicacoes = contraindicacoes; }
    public StatusMedicamento getStatus() { return status; }
    public void setStatus(StatusMedicamento status) { this.status = status; }
    public List<HistoricoEntradaJpa> getHistorico() { return historico; }
    
    public void setHistorico(List<HistoricoEntradaJpa> historico) { 
        this.historico = historico;
        if (historico != null) {
            historico.forEach(h -> h.setMedicamento(this));
        }
    }
    
    public RevisaoPendenteJpa getRevisaoPendente() { return revisaoPendente; }
    public void setRevisaoPendente(RevisaoPendenteJpa revisaoPendente) { this.revisaoPendente = revisaoPendente; }
}