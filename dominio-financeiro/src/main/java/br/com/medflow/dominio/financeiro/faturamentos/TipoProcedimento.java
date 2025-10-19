package br.com.medflow.dominio.financeiro.faturamentos;

public enum TipoProcedimento {
    CONSULTA("Consulta"),
    EXAME("Exame");

    private final String descricao;

    TipoProcedimento(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public boolean ehConsulta() {
        return this == CONSULTA;
    }

    public boolean ehExame() {
        return this == EXAME;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
