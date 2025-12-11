package br.com.medflow.infraestrutura.persistencia.jpa.referencia;

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

import br.com.medflow.dominio.referencia.tiposExames.AcaoHistorico;

@Entity
@Table(name = "historico_tipos_exames")
public class HistoricoTipoExameJpa {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne
    @JoinColumn(name = "tipo_exame_id", nullable = false)
    private TipoExameJpa tipoExame;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private AcaoHistorico acao;
    
    @Column(nullable = false, length = 500)
    private String descricao;
    
    @Column(name = "responsavel_id", nullable = false)
    private Integer responsavelId;
    
    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;
    
    // Construtor padrão (necessário para JPA)
    public HistoricoTipoExameJpa() {
    }
    
    // Getters e Setters
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public TipoExameJpa getTipoExame() {
        return tipoExame;
    }
    
    public void setTipoExame(TipoExameJpa tipoExame) {
        this.tipoExame = tipoExame;
    }
    
    public AcaoHistorico getAcao() {
        return acao;
    }
    
    public void setAcao(AcaoHistorico acao) {
        this.acao = acao;
    }
    
    public String getDescricao() {
        return descricao;
    }
    
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    
    public Integer getResponsavelId() {
        return responsavelId;
    }
    
    public void setResponsavelId(Integer responsavelId) {
        this.responsavelId = responsavelId;
    }
    
    public LocalDateTime getDataHora() {
        return dataHora;
    }
    
    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }
}