package br.com.medflow.infraestrutura.persistencia.jpa.atendimento;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import br.com.medflow.dominio.atendimento.exames.AcaoHistorico;

@Entity
@Table(name = "historico_exames")
public class HistoricoExameJpa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "exame_id", nullable = false)
    private ExameJpa exame;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private AcaoHistorico acao;

    @Column(nullable = false, length = 1000)
    private String descricao;

    @Column(name = "responsavel_id", nullable = false)
    private Long responsavelId;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    // Construtor padrão
    public HistoricoExameJpa() {}

    // Getters e Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public ExameJpa getExame() { return exame; }
    public void setExame(ExameJpa exame) { this.exame = exame; }
    public AcaoHistorico getAcao() { return acao; }
    public void setAcao(AcaoHistorico acao) { this.acao = acao; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public Long getResponsavelId() { return responsavelId; }
    public void setResponsavelId(Long responsavelId) { this.responsavelId = responsavelId; }
    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }
}
