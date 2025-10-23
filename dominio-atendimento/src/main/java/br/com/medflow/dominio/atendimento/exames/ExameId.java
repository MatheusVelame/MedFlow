package br.com.medflow.dominio.atendimento.exames;

import java.util.Objects;
import static org.apache.commons.lang3.Validate.notNull;

public class ExameId {
    private final Long id;

    public ExameId(Long id) {
        notNull(id, "O ID do exame não pode ser nulo.");
        this.id = id;
    }

    public Long getValor() {
        return id;
    }
    
    // Sobrescritas para o DDD (Value Object)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExameId exameId = (ExameId) o;
        return id.equals(exameId.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}