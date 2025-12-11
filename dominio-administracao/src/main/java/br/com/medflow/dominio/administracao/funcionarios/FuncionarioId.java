package br.com.medflow.dominio.administracao.funcionarios;

import static org.apache.commons.lang3.Validate.notEmpty;
import java.util.Objects;

public class FuncionarioId {
	private final String id;

	public FuncionarioId(String id) {
		notEmpty(id, "O id do funcionário não pode ser vazio");
		this.id = id;
	}

	// Construtor auxiliar para aceitar int (mantém compatibilidade)
	public FuncionarioId(int idNumerico) {
		this.id = String.valueOf(idNumerico);
	}

	public String getId() { return id; }

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null || getClass() != obj.getClass()) return false;
		// CORREÇÃO: Usar apenas a referência local FuncionarioId
		FuncionarioId other = (FuncionarioId) obj;
		return Objects.equals(id, other.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public String toString() {
		return id;
	}
}