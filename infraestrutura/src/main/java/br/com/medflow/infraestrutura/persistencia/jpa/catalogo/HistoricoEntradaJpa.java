package br.com.medflow.infraestrutura.persistencia.jpa.catalogo;

import br.com.medflow.dominio.catalogo.medicamentos.AcaoHistorico;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "historico_medicamentos")
public class HistoricoEntradaJpa {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;


    @ManyToOne
    @JoinColumn(name = "medicamento_id", nullable = false)
    private MedicamentoJpa medicamento; 

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private AcaoHistorico acao;

	@Column(columnDefinition = "TEXT")
	private String descricao;

	@Column(name = "responsavel_id", nullable = false)
	private Integer responsavelId;

	@Column(name = "data_hora", nullable = false)
	private LocalDateTime dataHora;

    public HistoricoEntradaJpa() {}


    public HistoricoEntradaJpa(Long id, AcaoHistorico acao, String descricao, Integer responsavelId, LocalDateTime dataHora) {
        this.id = id;
        this.acao = acao;
        this.descricao = descricao;
        this.responsavelId = responsavelId;
        this.dataHora = dataHora;
    }


    public void setAcao(AcaoHistorico acao) { this.acao = acao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public void setResponsavelId(Integer responsavelId) { this.responsavelId = responsavelId; }
    

    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; } 
    

    public void setMedicamento(MedicamentoJpa medicamento) { this.medicamento = medicamento; } 

    // --- Getters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public AcaoHistorico getAcao() { return acao; }
    public String getDescricao() { return descricao; }
    public Integer getResponsavelId() { return responsavelId; }
    public LocalDateTime getDataHora() { return dataHora; }
    public MedicamentoJpa getMedicamento() { return medicamento; }
}