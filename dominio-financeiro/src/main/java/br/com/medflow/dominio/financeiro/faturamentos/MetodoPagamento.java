package br.com.medflow.dominio.financeiro.faturamentos;

import java.util.Objects;

public class MetodoPagamento {
    private final String metodo;

    public MetodoPagamento(String metodo) {
        if (metodo == null || metodo.trim().isEmpty()) {
            throw new IllegalArgumentException("Método de pagamento não pode ser nulo ou vazio.");
        }
        this.metodo = metodo.trim();
    }

    public String getMetodo() {
        return metodo;
    }

    public boolean ehDinheiro() {
        return "DINHEIRO".equalsIgnoreCase(metodo);
    }

    public boolean ehCartao() {
        return "CARTAO".equalsIgnoreCase(metodo) || "CARTÃO".equalsIgnoreCase(metodo);
    }

    public boolean ehConvenio() {
        return "CONVENIO".equalsIgnoreCase(metodo) || "CONVÊNIO".equalsIgnoreCase(metodo);
    }

    public boolean ehPix() {
        return "PIX".equalsIgnoreCase(metodo);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MetodoPagamento that = (MetodoPagamento) o;
        return Objects.equals(metodo, that.metodo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(metodo);
    }

    @Override
    public String toString() {
        return metodo;
    }
}
