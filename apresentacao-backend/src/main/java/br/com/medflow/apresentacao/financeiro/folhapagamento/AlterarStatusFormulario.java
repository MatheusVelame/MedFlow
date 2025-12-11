package br.com.medflow.apresentacao.financeiro.folhapagamento;

import br.com.medflow.dominio.financeiro.folhapagamento.StatusFolha;

/**
 * Formulário para alterar status da folha.
 */
public class AlterarStatusFormulario {

    private StatusFolha novoStatus;
    private Integer usuarioResponsavelId;

    public StatusFolha getNovoStatus() {
        return novoStatus;
    }

    public void setNovoStatus(StatusFolha novoStatus) {
        this.novoStatus = novoStatus;
    }

    public Integer getUsuarioResponsavelId() {
        return usuarioResponsavelId;
    }

    public void setUsuarioResponsavelId(Integer usuarioResponsavelId) {
        this.usuarioResponsavelId = usuarioResponsavelId;
    }
}