package br.com.medflow.apresentacao.financeiro.convenios;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class NomeFormulario {

	@NotBlank(message = "O novo nome é obrigatório.")
	private String novoNome;

	@NotNull(message = "O ID do responsável é obrigatório.")
	private Integer responsavelId;

	public NomeFormulario() {}

	public String getNovoNome() {
		return novoNome;
	}

	public void setNovoNome(String novoNome) {
		this.novoNome = novoNome;
	}

	public Integer getResponsavelId() {
		return responsavelId;
	}

	public void setResponsavelId(Integer responsavelId) {
		this.responsavelId = responsavelId;
	}
}

