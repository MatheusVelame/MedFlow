package br.com.medflow.dominio.administracao.funcionarios;

import static org.apache.commons.lang3.Validate.isTrue;
import java.util.Objects;

public class FuncionarioId {
	private final int id;

	public FuncionarioId(int id) {
		isTrue(id > 0, "O id deve ser positivo");
		this.id = id;
	}

	public int getId() { return id; }

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
        
		if (obj == null || getClass() != obj.getClass()) return false;
        
		FuncionarioId other = (FuncionarioId) obj;
		return id == other.id;
	}

	@Override
	public int hashCode() { 
        return Objects.hash(id); 
    }
    
	@Override
	public String toString() { 
        return Integer.toString(id); 
    }
}
