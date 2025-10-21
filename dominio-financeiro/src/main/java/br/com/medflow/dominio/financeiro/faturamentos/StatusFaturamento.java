package br.com.medflow.dominio.financeiro.faturamentos;

public enum StatusFaturamento {
    PENDENTE("Pendente"),
    PAGO("Pago"),
    CANCELADO("Cancelado"),
    INVALIDO("Inválido"),
    REMOVIDO("Removido");

    private final String descricao;

    StatusFaturamento(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public boolean ehPendente() {
        return this == PENDENTE;
    }

    public boolean ehPago() {
        return this == PAGO;
    }

    public boolean ehCancelado() {
        return this == CANCELADO;
    }

    public boolean ehInvalido() {
        return this == INVALIDO;
    }

    public boolean ehRemovido() {
        return this == REMOVIDO;
    }

    public boolean podeSerExcluido() {
        return this == PENDENTE || this == INVALIDO;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
