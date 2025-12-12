package br.com.medflow.apresentacao.financeiro.folhapagamento;

import br.com.medflow.dominio.financeiro.folhapagamento.TipoRegistro;
import java.math.BigDecimal;

/**
 * Formulário para registrar folha de pagamento.
 */
public class RegistrarFolhaFormulario {

    private Integer funcionarioId;
    private String periodoReferencia;
    private TipoRegistro tipoRegistro;
    private BigDecimal salarioBase;
    private BigDecimal beneficios;
    private String metodoPagamento;
    private Integer usuarioResponsavelId;
    private boolean funcionarioAtivo = true;

    public Integer getFuncionarioId() {
        return funcionarioId;
    }

    public void setFuncionarioId(Integer funcionarioId) {
        this.funcionarioId = funcionarioId;
    }

    public String getPeriodoReferencia() {
        return periodoReferencia;
    }

    public void setPeriodoReferencia(String periodoReferencia) {
        this.periodoReferencia = periodoReferencia;
    }

    public TipoRegistro getTipoRegistro() {
        return tipoRegistro;
    }

    public void setTipoRegistro(TipoRegistro tipoRegistro) {
        this.tipoRegistro = tipoRegistro;
    }

    public BigDecimal getSalarioBase() {
        return salarioBase;
    }

    public void setSalarioBase(BigDecimal salarioBase) {
        this.salarioBase = salarioBase;
    }

    public BigDecimal getBeneficios() {
        return beneficios;
    }

    public void setBeneficios(BigDecimal beneficios) {
        this.beneficios = beneficios;
    }

    public String getMetodoPagamento() {
        return metodoPagamento;
    }

    public void setMetodoPagamento(String metodoPagamento) {
        this.metodoPagamento = metodoPagamento;
    }

    public Integer getUsuarioResponsavelId() {
        return usuarioResponsavelId;
    }

    public void setUsuarioResponsavelId(Integer usuarioResponsavelId) {
        this.usuarioResponsavelId = usuarioResponsavelId;
    }

    public boolean isFuncionarioAtivo() {
        return funcionarioAtivo;
    }

    public void setFuncionarioAtivo(boolean funcionarioAtivo) {
        this.funcionarioAtivo = funcionarioAtivo;
    }
}