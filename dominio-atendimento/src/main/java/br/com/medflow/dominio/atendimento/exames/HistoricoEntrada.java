package br.com.medflow.dominio.atendimento.exames;

import java.time.LocalDateTime;
import java.util.Objects;

public class HistoricoEntrada {
    private final LocalDateTime dataHora;
    private final AcaoHistorico acao;
    private final String descricao;
    private final UsuarioResponsavelId usuario;

    public HistoricoEntrada(AcaoHistorico acao, String descricao, UsuarioResponsavelId usuario) {
        this.dataHora = LocalDateTime.now();
        this.acao = Objects.requireNonNull(acao);
        this.descricao = Objects.requireNonNull(descricao);
        this.usuario = Objects.requireNonNull(usuario);
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public AcaoHistorico getAcao() {
        return acao;
    }

    public String getDescricao() {
        return descricao;
    }
    
    public UsuarioResponsavelId getUsuario() {
        return usuario;
    }
    
    // Métodos equals/hashCode/toString para Value Object
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HistoricoEntrada that = (HistoricoEntrada) o;
        return acao == that.acao &&
               Objects.equals(descricao, that.descricao) &&
               Objects.equals(usuario, that.usuario);
    }

    @Override
    public int hashCode() {
        return Objects.hash(acao, descricao, usuario);
    }
}