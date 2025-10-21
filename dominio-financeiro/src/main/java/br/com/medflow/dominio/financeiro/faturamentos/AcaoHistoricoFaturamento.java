package br.com.medflow.dominio.financeiro.faturamentos;

public enum AcaoHistoricoFaturamento {
    CRIACAO("Criação"),
    ATUALIZACAO("Atualização"),
    CANCELAMENTO("Cancelamento"),
    PAGAMENTO("Pagamento"),
    EXCLUSAO_LOGICA("Exclusão Lógica"),
    TENTATIVA_EXCLUSAO_NEGADA("Tentativa de Exclusão Negada"),
    ALTERACAO_STATUS("Alteração de Status"),
    TENTATIVA_ALTERACAO_NEGADA("Tentativa de Alteração Negada");

    private final String descricao;

    AcaoHistoricoFaturamento(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
