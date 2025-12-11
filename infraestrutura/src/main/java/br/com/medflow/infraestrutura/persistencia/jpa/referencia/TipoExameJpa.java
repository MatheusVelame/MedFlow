package br.com.medflow.infraestrutura.persistencia.jpa.referencia;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import br.com.medflow.dominio.referencia.tiposExames.StatusTipoExame;

@Entity
@Table(name = "tipos_exames")
public class TipoExameJpa {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(nullable = false, unique = true, length = 50)
    private String codigo;
    
    @Column(nullable = false, length = 255)
    private String descricao;
    
    @Column(nullable = false, length = 100)
    private String especialidade;
    
    @Column(nullable = false)
    private Double valor;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusTipoExame status;
    
    @OneToMany(mappedBy = "tipoExame", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HistoricoTipoExameJpa> historico = new ArrayList<>();
    
    public TipoExameJpa() {
    }
    
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public String getCodigo() {
        return codigo;
    }
    
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }
    
    public String getDescricao() {
        return descricao;
    }
    
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    
    public String getEspecialidade() {
        return especialidade;
    }
    
    public void setEspecialidade(String especialidade) {
        this.especialidade = especialidade;
    }
    
    public Double getValor() {
        return valor;
    }
    
    public void setValor(Double valor) {
        this.valor = valor;
    }
    
    public StatusTipoExame getStatus() {
        return status;
    }
    
    public void setStatus(StatusTipoExame status) {
        this.status = status;
    }
    
    public List<HistoricoTipoExameJpa> getHistorico() {
        return historico;
    }
    
    public void setHistorico(List<HistoricoTipoExameJpa> historico) {
        this.historico = historico;
    }
}