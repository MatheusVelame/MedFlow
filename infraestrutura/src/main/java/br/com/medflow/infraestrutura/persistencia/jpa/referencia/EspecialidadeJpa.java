package br.com.medflow.infraestrutura.persistencia.jpa.referencia;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import br.com.medflow.dominio.referencia.especialidades.StatusEspecialidade;

@Entity
@Table(name = "especialidades")
public class EspecialidadeJpa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 100)
    private String nome;

    @Column(length = 255)
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusEspecialidade status;

    @Column(name = "possui_vinculo_historico", nullable = false)
    private boolean possuiVinculoHistorico;

    public EspecialidadeJpa() {
        // Construtor padrão JPA
    }

    // Getters e Setters

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public StatusEspecialidade getStatus() {
        return status;
    }

    public void setStatus(StatusEspecialidade status) {
        this.status = status;
    }

    public boolean isPossuiVinculoHistorico() {
        return possuiVinculoHistorico;
    }

    public void setPossuiVinculoHistorico(boolean possuiVinculoHistorico) {
        this.possuiVinculoHistorico = possuiVinculoHistorico;
    }
}