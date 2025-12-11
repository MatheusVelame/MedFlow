package br.com.medflow.apresentacao.financeiro.convenios;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ConvenioFormulario {

	@NotBlank(message = "O nome é obrigatório.")
	private String nome;

	@NotBlank(message = "O código de identificação é obrigatório.")
	private String codigoIdentificacao;

	@NotNull(message = "O ID do responsável é obrigatório para a auditoria.")
	private Integer responsavelId;

	public ConvenioFormulario() {}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getCodigoIdentificacao() {
		return codigoIdentificacao;
	}

	public void setCodigoIdentificacao(String codigoIdentificacao) {
		this.codigoIdentificacao = codigoIdentificacao;
	}

	public Integer getResponsavelId() {
		return responsavelId;
	}

	public void setResponsavelId(Integer responsavelId) {
		this.responsavelId = responsavelId;
	}
}

