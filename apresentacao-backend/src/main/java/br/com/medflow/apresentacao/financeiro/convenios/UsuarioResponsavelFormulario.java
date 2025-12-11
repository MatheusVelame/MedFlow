package br.com.medflow.apresentacao.financeiro.convenios;

import jakarta.validation.constraints.NotNull;

// Usado em comandos de ação (mudar status, excluir)
public class UsuarioResponsavelFormulario {

	@NotNull(message = "O ID do responsável é obrigatório.")
	private Integer responsavelId;

	public UsuarioResponsavelFormulario() {}

	public Integer getResponsavelId() {
		return responsavelId;
	}

	public void setResponsavelId(Integer responsavelId) {
		this.responsavelId = responsavelId;
	}
}

