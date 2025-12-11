// Localização: infraestrutura/src/main/java/br/com/medflow/infraestrutura/persistencia/jpa/atendimento/HistoricoConsultaJpa.java

package br.com.medflow.infraestrutura.persistencia.jpa.atendimento;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "historico_consultas")
public class HistoricoConsultaJpa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "consulta_id", nullable = false)
    private Integer consultaId;

    @Column(nullable = false)
    private String acao;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(name = "responsavel_id", nullable = false)
    private Integer responsavelId;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    // Construtor padrão exigido pelo JPA
    public HistoricoConsultaJpa() {}

    // Getters e Setters (necessários pelo JPA)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Integer getConsultaId() { return consultaId; }
    public void setConsultaId(Integer consultaId) { this.consultaId = consultaId; }
    public String getAcao() { return acao; }
    public void setAcao(String acao) { this.acao = acao; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public Integer getResponsavelId() { return responsavelId; }
    public void setResponsavelId(Integer responsavelId) { this.responsavelId = responsavelId; }
    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }
}