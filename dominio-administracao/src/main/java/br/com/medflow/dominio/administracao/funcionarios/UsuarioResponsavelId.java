package br.com.medflow.dominio.administracao.funcionarios;

import static org.apache.commons.lang3.Validate.notEmpty;
import java.util.Objects;

public final class UsuarioResponsavelId {
	private final String codigo;

	public UsuarioResponsavelId(String codigo) {
		notEmpty(codigo, "O código do usuário responsável não pode ser vazio.");
		this.codigo = codigo;
	}

	public UsuarioResponsavelId(int idNumerico) {
		this.codigo = String.valueOf(idNumerico);
	}

	public String getCodigo() { return codigo; }

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof UsuarioResponsavelId) {
			return Objects.equals(codigo, ((UsuarioResponsavelId) obj).codigo);
		}
		return false;
	}

	@Override
	public int hashCode() { return Objects.hash(codigo); }
	@Override
	public String toString() { return codigo; }
}