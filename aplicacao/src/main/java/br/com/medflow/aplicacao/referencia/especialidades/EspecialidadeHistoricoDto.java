package br.com.medflow.aplicacao.referencia.especialidades;

import java.time.LocalDateTime;

public class EspecialidadeHistoricoDto {
    private Integer id;
    private Integer especialidadeId;
    private String campo;
    private String valorAnterior;
    private String novoValor;
    private LocalDateTime dataHora;
    private String tipo;

    public EspecialidadeHistoricoDto() {}

    public EspecialidadeHistoricoDto(Integer id, Integer especialidadeId, String campo, String valorAnterior, String novoValor, LocalDateTime dataHora, String tipo) {
        this.id = id;
        this.especialidadeId = especialidadeId;
        this.campo = campo;
        this.valorAnterior = valorAnterior;
        this.novoValor = novoValor;
        this.dataHora = dataHora;
        this.tipo = tipo;
    }

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
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
}
