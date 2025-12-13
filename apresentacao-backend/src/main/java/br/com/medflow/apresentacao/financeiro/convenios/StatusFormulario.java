package br.com.medflow.apresentacao.financeiro.convenios;

import br.com.medflow.dominio.financeiro.convenios.StatusConvenio;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)
public class StatusFormulario {

	@NotNull(message = "O status é obrigatório.")
	private StatusConvenio status;

	@NotNull(message = "O ID do responsável é obrigatório.")
	private Integer responsavelId;

	public StatusFormulario() {}

	public StatusConvenio getStatus() {
		return status;
	}

	public void setStatus(StatusConvenio status) {
		this.status = status;
	}

	public Integer getResponsavelId() {
		return responsavelId;
	}

	public void setResponsavelId(Integer responsavelId) {
		this.responsavelId = responsavelId;
	}
}
