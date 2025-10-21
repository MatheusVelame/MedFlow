package br.com.medflow.dominio.financeiro.faturamentos;

public enum PapelUsuario {
    ATENDENTE("Atendente"),
    ADMINISTRADOR_FINANCEIRO("Administrador Financeiro"),
    ADMINISTRADOR_SISTEMA("Administrador do Sistema");

    private final String descricao;

    PapelUsuario(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public boolean ehAdministrativo() {
        return this == ADMINISTRADOR_FINANCEIRO || this == ADMINISTRADOR_SISTEMA;
    }

    public boolean podeAlterarStatus() {
        return ehAdministrativo();
    }

    public boolean podeReverterPago() {
        return false; // Por padrão, nenhum papel pode reverter sem permissão especial
    }

    @Override
    public String toString() {
        return descricao;
    }
}
