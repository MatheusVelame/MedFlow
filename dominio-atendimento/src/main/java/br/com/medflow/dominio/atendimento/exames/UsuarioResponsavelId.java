package br.com.medflow.dominio.atendimento.exames;

import java.util.Objects;
import static org.apache.commons.lang3.Validate.notNull;

/**
 * Value Object para representar o identificador do usuário que realizou a operação.
 * Usado em Entradas de Histórico, seguindo o padrão DDD para rastreamento de responsabilidade.
 */
public class UsuarioResponsavelId {
    private final Long id;

    public UsuarioResponsavelId(Long id) {
        notNull(id, "O ID do usuário responsável não pode ser nulo.");
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
        UsuarioResponsavelId that = (UsuarioResponsavelId) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return id.toString();
    }
}