package br.com.medflow.infraestrutura.persistencia.jpa.financeiro;

import br.com.medflow.dominio.financeiro.convenios.StatusConvenio;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "convenios")
public class ConvenioJpa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String nome;

    @Column(name = "codigo_identificacao", nullable = false)
    private String codigoIdentificacao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusConvenio status;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "convenio_id", nullable = false)
    private List<HistoricoEntradaJpa> historico = new ArrayList<>();

    public ConvenioJpa() {}

    public ConvenioJpa(
            Integer id,
            String nome,
            String codigoIdentificacao,
            StatusConvenio status,
            List<HistoricoEntradaJpa> historico
    ) {
        this.id = id;
        this.nome = nome;
        this.codigoIdentificacao = codigoIdentificacao;
        this.status = status;
        if (historico != null) {
            this.historico = historico;
        }
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCodigoIdentificacao() { return codigoIdentificacao; }
    public void setCodigoIdentificacao(String codigoIdentificacao) {
        this.codigoIdentificacao = codigoIdentificacao;
    }

    public StatusConvenio getStatus() { return status; }
    public void setStatus(StatusConvenio status) { this.status = status; }

    public List<HistoricoEntradaJpa> getHistorico() { return historico; }
    public void setHistorico(List<HistoricoEntradaJpa> historico) { this.historico = historico; }
}
