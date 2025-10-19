package br.com.medflow.dominio.financeiro.faturamentos;

import java.math.BigDecimal;
import java.util.Objects;

public class Valor {
    private final BigDecimal valor;

    public Valor(BigDecimal valor) {
        if (valor == null) {
            throw new IllegalArgumentException("Valor não pode ser nulo.");
        }
        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor deve ser positivo.");
        }
        this.valor = valor;
    }

    public Valor(double valor) {
        this(BigDecimal.valueOf(valor));
    }

    public BigDecimal getValor() {
        return valor;
    }

    public boolean ehMaiorQue(Valor outro) {
        return this.valor.compareTo(outro.valor) > 0;
    }

    public boolean ehMenorQue(Valor outro) {
        return this.valor.compareTo(outro.valor) < 0;
    }

    public boolean ehIgualA(Valor outro) {
        return this.valor.compareTo(outro.valor) == 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Valor valor1 = (Valor) o;
        return Objects.equals(valor, valor1.valor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(valor);
    }

    @Override
    public String toString() {
        return "R$ " + valor.toString();
    }
}
