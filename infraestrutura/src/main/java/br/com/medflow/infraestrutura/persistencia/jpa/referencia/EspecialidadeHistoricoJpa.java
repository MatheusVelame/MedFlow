package br.com.medflow.infraestrutura.persistencia.jpa.referencia;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import br.com.medflow.dominio.referencia.especialidades.TipoOperacaoHistorico;

@Entity
@Table(name = "especialidade_historico")
public class EspecialidadeHistoricoJpa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "especialidade_id", nullable = false)
    private Integer especialidadeId;

    @Column(length = 100)
    private String campo;

    @Column(name = "valor_anterior", length = 255)
    private String valorAnterior;

    @Column(name = "novo_valor", length = 255)
    private String novoValor;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TipoOperacaoHistorico tipo;

    public EspecialidadeHistoricoJpa() {}

    // getters and setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getEspecialidadeId() { return especialidadeId; }
    public void setEspecialidadeId(Integer especialidadeId) { this.especialidadeId = especialidadeId; }
    public String getCampo() { return campo; }
    public void setCampo(String campo) { this.campo = campo; }
    public String getValorAnterior() { return valorAnterior; }
    public void setValorAnterior(String valorAnterior) { this.valorAnterior = valorAnterior; }
    public String getNovoValor() { return novoValor; }
    public void setNovoValor(String novoValor) { this.novoValor = novoValor; }
    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }
    public TipoOperacaoHistorico getTipo() { return tipo; }
    public void setTipo(TipoOperacaoHistorico tipo) { this.tipo = tipo; }
}
