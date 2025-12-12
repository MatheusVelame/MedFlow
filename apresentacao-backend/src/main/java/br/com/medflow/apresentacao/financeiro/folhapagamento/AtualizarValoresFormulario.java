package br.com.medflow.apresentacao.financeiro.folhapagamento;

import java.math.BigDecimal;

/**
 * Formulário para atualizar valores da folha.
 */
public class AtualizarValoresFormulario {

    private BigDecimal novoSalarioBase;
    private BigDecimal novosBeneficios;
    private Integer usuarioResponsavelId;

    public BigDecimal getNovoSalarioBase() {
        return novoSalarioBase;
    }

    public void setNovoSalarioBase(BigDecimal novoSalarioBase) {
        this.novoSalarioBase = novoSalarioBase;
    }

    public BigDecimal getNovosBeneficios() {
        return novosBeneficios;
    }

    public void setNovosBeneficios(BigDecimal novosBeneficios) {
        this.novosBeneficios = novosBeneficios;
    }

    public Integer getUsuarioResponsavelId() {
        return usuarioResponsavelId;
    }

    public void setUsuarioResponsavelId(Integer usuarioResponsavelId) {
        this.usuarioResponsavelId = usuarioResponsavelId;
    }
}