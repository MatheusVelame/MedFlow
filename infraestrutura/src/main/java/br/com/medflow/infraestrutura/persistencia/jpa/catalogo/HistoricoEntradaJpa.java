// Localização: infraestrutura/src/main/java/br/com/medflow/infraestrutura/persistencia/jpa/catalogo/HistoricoEntradaJpa.java

package br.com.medflow.infraestrutura.persistencia.jpa.catalogo;

import jakarta.persistence.*;
import br.com.medflow.dominio.catalogo.medicamentos.AcaoHistorico;
import java.time.LocalDateTime;

@Entity
@Table(name = "historico_medicamentos")
public class HistoricoEntradaJpa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "medicamento_id", nullable = false)
    private Integer medicamentoId;
    
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
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Integer getMedicamentoId() { return medicamentoId; }
    public void setMedicamentoId(Integer medicamentoId) { this.medicamentoId = medicamentoId; }
    public AcaoHistorico getAcao() { return acao; }
    public void setAcao(AcaoHistorico acao) { this.acao = acao; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public Integer getResponsavelId() { return responsavelId; }
    public void setResponsavelId(Integer responsavelId) { this.responsavelId = responsavelId; }
    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }
}