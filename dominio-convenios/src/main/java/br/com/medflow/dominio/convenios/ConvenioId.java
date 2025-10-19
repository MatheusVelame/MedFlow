package br.com.medflow.dominio.convenios;

import static org.apache.commons.lang3.Validate.isTrue;
import java.util.Objects;

public class ConvenioId {
	private final int id;

	public ConvenioId(int id) {
		isTrue(id > 0, "O id deve ser positivo");
		this.id = id;
	}

	public int getId() { return id; }

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
        
		if (obj == null || getClass() != obj.getClass()) return false;
        
		ConvenioId other = (ConvenioId) obj;
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