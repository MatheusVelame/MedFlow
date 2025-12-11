package br.com.medflow.infraestrutura.persistencia.jpa.financeiro.folhapagamento;

import br.com.medflow.dominio.financeiro.folhapagamento.*;
import jakarta.persistence.*;
import java.math.BigDecimal;

/**
 * Entidade JPA para Folha de Pagamento.
 */
@Entity
@Table(name = "folha_pagamento")
public class FolhaPagamentoJpa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "funcionario_id", nullable = false)
    private Integer funcionarioId;

    @Column(name = "periodo_referencia", nullable = false, length = 7)
    private String periodoReferencia;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_registro", nullable = false, length = 20)
    private TipoRegistro tipoRegistro;

    @Column(name = "salario_base", nullable = false, precision = 10, scale = 2)
    private BigDecimal salarioBase;

    @Column(name = "beneficios", nullable = false, precision = 10, scale = 2)
    private BigDecimal beneficios;

    @Column(name = "metodo_pagamento", nullable = false, length = 50)
    private String metodoPagamento;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private StatusFolha status;

    protected FolhaPagamentoJpa() {
    }

    public FolhaPagamentoJpa(FolhaPagamento dominio) {
        if (dominio.getId() != null) {
            this.id = dominio.getId().getId();
        }
        this.funcionarioId = dominio.getFuncionarioId();
        this.periodoReferencia = dominio.getPeriodoReferencia();
        this.tipoRegistro = dominio.getTipoRegistro();
        this.salarioBase = dominio.getSalarioBase();
        this.beneficios = dominio.getBeneficios();
        this.metodoPagamento = dominio.getMetodoPagamento();
        this.status = dominio.getStatus();
    }

    public FolhaPagamento paraDominio() {
        return new FolhaPagamento(
                new FolhaPagamentoId(this.id),
                this.funcionarioId,
                this.periodoReferencia,
                this.tipoRegistro,
                this.salarioBase,
                this.beneficios,
                this.metodoPagamento,
                this.status
        );
    }

    public void atualizarDe(FolhaPagamento dominio) {
        this.funcionarioId = dominio.getFuncionarioId();
        this.periodoReferencia = dominio.getPeriodoReferencia();
        this.tipoRegistro = dominio.getTipoRegistro();
        this.salarioBase = dominio.getSalarioBase();
        this.beneficios = dominio.getBeneficios();
        this.metodoPagamento = dominio.getMetodoPagamento();
        this.status = dominio.getStatus();
    }

    public Integer getId() { return id; }
    public Integer getFuncionarioId() { return funcionarioId; }
    public String getPeriodoReferencia() { return periodoReferencia; }
    public TipoRegistro getTipoRegistro() { return tipoRegistro; }
    public BigDecimal getSalarioBase() { return salarioBase; }
    public BigDecimal getBeneficios() { return beneficios; }
    public String getMetodoPagamento() { return metodoPagamento; }
    public StatusFolha getStatus() { return status; }
}