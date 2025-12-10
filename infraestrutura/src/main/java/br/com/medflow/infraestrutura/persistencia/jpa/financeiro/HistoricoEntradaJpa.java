package br.com.medflow.infraestrutura.persistencia.jpa.financeiro;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import br.com.medflow.dominio.financeiro.convenios.AcaoHistorico;

@Entity
@Table(name = "historico_convenio")
public class HistoricoEntradaJpa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AcaoHistorico acao;

    @Column(columnDefinition = "TEXT")
	private String descricao;

    @Column(name = "responsavel_id", nullable = false)
    private String responsavelId;

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


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public AcaoHistorico getAcao() { return acao; }
    public void setAcao(AcaoHistorico acao) { this.acao = acao; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public Integer getResponsavelId() { return responsavelId; }
    public void setResponsavelId(Integer responsavelId) { this.responsavelId = responsavelId; }
    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }
}
