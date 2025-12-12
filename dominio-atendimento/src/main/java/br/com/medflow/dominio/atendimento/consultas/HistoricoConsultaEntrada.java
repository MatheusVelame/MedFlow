package br.com.medflow.dominio.atendimento.consultas;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Value Object que representa uma única entrada no histórico de uma Consulta.
 */
public class HistoricoConsultaEntrada {
    private final AcaoHistorico acao;
    private final String descricao;
    private final UsuarioResponsavelId responsavelId;
    private final LocalDateTime dataHora;

    public HistoricoConsultaEntrada(AcaoHistorico acao, String descricao, UsuarioResponsavelId responsavelId, LocalDateTime dataHora) {
        // Validações de nulidade (critério de robustez)
        Objects.requireNonNull(acao, "Ação não pode ser nula.");
        Objects.requireNonNull(descricao, "Descrição não pode ser nula.");
        Objects.requireNonNull(responsavelId, "Responsável não pode ser nulo.");
        Objects.requireNonNull(dataHora, "Data/Hora não pode ser nula.");

        this.acao = acao;
        this.descricao = descricao;
        this.responsavelId = responsavelId;
        this.dataHora = dataHora;
    }

    // Getters
    public AcaoHistorico getAcao() { return acao; }
    public String getDescricao() { return descricao; }
    public UsuarioResponsavelId getResponsavelId() { return responsavelId; }
    public LocalDateTime getDataHora() { return dataHora; }
}