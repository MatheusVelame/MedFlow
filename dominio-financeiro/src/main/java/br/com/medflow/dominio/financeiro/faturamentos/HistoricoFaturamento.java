package br.com.medflow.dominio.financeiro.faturamentos;

import java.time.LocalDateTime;
import java.util.Objects;

public class HistoricoFaturamento {
    private final AcaoHistoricoFaturamento acao;
    private final String descricao;
    private final UsuarioResponsavelId responsavel;
    private final LocalDateTime dataHora;

    public HistoricoFaturamento(AcaoHistoricoFaturamento acao, String descricao, 
                               UsuarioResponsavelId responsavel, LocalDateTime dataHora) {
        if (acao == null) throw new IllegalArgumentException("Ação do histórico é obrigatória");
        if (descricao == null || descricao.trim().isEmpty()) 
            throw new IllegalArgumentException("Descrição do histórico é obrigatória");
        if (responsavel == null) throw new IllegalArgumentException("Responsável é obrigatório");
        if (dataHora == null) throw new IllegalArgumentException("Data/hora é obrigatória");
        
        this.acao = acao;
        this.descricao = descricao;
        this.responsavel = responsavel;
        this.dataHora = dataHora;
    }

    public AcaoHistoricoFaturamento getAcao() {
        return acao;
    }

    public String getDescricao() {
        return descricao;
    }

    public UsuarioResponsavelId getResponsavel() {
        return responsavel;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HistoricoFaturamento that = (HistoricoFaturamento) o;
        return Objects.equals(acao, that.acao) &&
               Objects.equals(descricao, that.descricao) &&
               Objects.equals(responsavel, that.responsavel) &&
               Objects.equals(dataHora, that.dataHora);
    }

    @Override
    public int hashCode() {
        return Objects.hash(acao, descricao, responsavel, dataHora);
    }

    @Override
    public String toString() {
        return String.format("[%s] %s - %s (%s)", 
            dataHora, acao.getDescricao(), descricao, responsavel.getValor());
    }
}
