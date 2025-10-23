package br.com.medflow.dominio.financeiro.faturamentos;

import java.util.Objects;

public class FaturamentoId {
    private final String id;

    public FaturamentoId(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("ID do faturamento não pode ser nulo ou vazio.");
        }
        this.id = id;
    }

    public String getValor() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FaturamentoId that = (FaturamentoId) o;
        return Objects.equals(id, that.id);
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
