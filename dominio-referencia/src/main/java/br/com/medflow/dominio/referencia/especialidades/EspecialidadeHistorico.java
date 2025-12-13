package br.com.medflow.dominio.referencia.especialidades;

import java.time.LocalDateTime;

public class EspecialidadeHistorico {

    private Integer id; // opcional, gerado pelo repositório
    private Integer especialidadeId;
    private String campo;
    private String valorAnterior;
    private String novoValor;
    private LocalDateTime dataHora;
    private TipoOperacaoHistorico tipo;

    public EspecialidadeHistorico() {}

    public EspecialidadeHistorico(Integer especialidadeId, String campo, String valorAnterior, String novoValor, TipoOperacaoHistorico tipo) {
        this.especialidadeId = especialidadeId;
        this.campo = campo;
        this.valorAnterior = valorAnterior;
        this.novoValor = novoValor;
        this.dataHora = LocalDateTime.now();
        this.tipo = tipo;
    }

    // Getters e setters
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
