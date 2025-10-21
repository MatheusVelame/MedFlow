package br.com.medflow.dominio.catalogo.medicamentos;

import static org.apache.commons.lang3.Validate.isTrue;
import java.util.Objects;

public class MedicamentoId {
	private final int id;

	public MedicamentoId(int id) {
		isTrue(id > 0, "O id deve ser positivo");
		this.id = id;
	}

	public int getId() { return id; }

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof MedicamentoId) {
			return id == ((MedicamentoId) obj).id;
		}
		return false;
	}

	@Override
	public int hashCode() { return Objects.hash(id); }
	@Override
	public String toString() { return Integer.toString(id); }
}