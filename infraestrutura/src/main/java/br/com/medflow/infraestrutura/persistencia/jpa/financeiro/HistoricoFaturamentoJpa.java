package br.com.medflow.infraestrutura.persistencia.jpa.financeiro;

import br.com.medflow.dominio.financeiro.faturamentos.AcaoHistoricoFaturamento;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "historico_faturamento")
public class HistoricoFaturamentoJpa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "faturamento_id", nullable = false, length = 36)
    private String faturamentoId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AcaoHistoricoFaturamento acao;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String descricao;

    @Column(name = "responsavel_id", nullable = false, length = 36)
    private String responsavelId;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    public HistoricoFaturamentoJpa() {}

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFaturamentoId() { return faturamentoId; }
    public void setFaturamentoId(String faturamentoId) { this.faturamentoId = faturamentoId; }

    public AcaoHistoricoFaturamento getAcao() { return acao; }
    public void setAcao(AcaoHistoricoFaturamento acao) { this.acao = acao; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getResponsavelId() { return responsavelId; }
    public void setResponsavelId(String responsavelId) { this.responsavelId = responsavelId; }

    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }
}
