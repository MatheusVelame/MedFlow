package br.com.medflow.dominio.atendimento.exames;

import java.time.LocalDateTime;

/**
 * Interface que define o contrato para as operações de Agendamento de Exames.
 */
public interface IExameServico {

    Exame agendarExame(Long pacienteId, Long medicoId, String tipoExame, LocalDateTime dataHora, UsuarioResponsavelId responsavel);

    Exame atualizarAgendamento(ExameId exameId, Long novoMedicoId, String novoTipoExame, LocalDateTime novaDataHora, UsuarioResponsavelId responsavel, String observacoes);

    void tentarExcluirAgendamento(ExameId exameId, UsuarioResponsavelId responsavel);

    Exame cancelarAgendamento(ExameId exameId, String motivo, UsuarioResponsavelId responsavel);

}