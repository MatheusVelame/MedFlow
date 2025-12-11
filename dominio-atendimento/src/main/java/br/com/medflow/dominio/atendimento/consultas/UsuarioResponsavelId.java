package br.com.medflow.dominio.atendimento.consultas;

import java.util.Objects;

/**
 * Value Object para o ID do usuário responsável pela ação. (Reutilizado/definido)
 */
public class UsuarioResponsavelId {
    private final Integer id;

    public UsuarioResponsavelId(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID do usuário responsável não pode ser nulo ou negativo.");
        }
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

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
}