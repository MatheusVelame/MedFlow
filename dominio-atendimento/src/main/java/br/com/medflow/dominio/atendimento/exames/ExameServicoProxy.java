package br.com.medflow.dominio.atendimento.exames;

import java.time.LocalDateTime;

/**
 * Proxy: Intercepta as chamadas do serviço de exames para adicionar comportamento (Log/Auditoria)
 * antes de repassar para a implementação real (ExameServicoImpl).
 */
public class ExameServicoProxy implements IExameServico {

    private final IExameServico servicoReal;

    public ExameServicoProxy(IExameServico servicoReal) {
        this.servicoReal = servicoReal;
    }

    private void log(String operacao, String detalhe) {
        System.out.println("[AUDITORIA - PROXY] Operação: " + operacao + " | Detalhes: " + detalhe);
    }

    @Override
    public Exame agendarExame(Long pacienteId, Long medicoId, String tipoExame, LocalDateTime dataHora, UsuarioResponsavelId responsavel) {
        log("AGENDAR_EXAME", "PacienteID: " + pacienteId + ", MédicoID: " + medicoId + ", Tipo: " + tipoExame + ", Data: " + dataHora);
        return servicoReal.agendarExame(pacienteId, medicoId, tipoExame, dataHora, responsavel);
    }

    @Override
    public Exame atualizarAgendamento(ExameId exameId, Long novoMedicoId, String novoTipoExame, LocalDateTime novaDataHora, UsuarioResponsavelId responsavel, String observacoes) {
        log("ATUALIZAR_AGENDAMENTO", "ExameID: " + exameId + " -> Novos dados: MédicoID=" + novoMedicoId + ", Data=" + novaDataHora + ", Observações=" + observacoes);
        return servicoReal.atualizarAgendamento(exameId, novoMedicoId, novoTipoExame, novaDataHora, responsavel, observacoes);
    }

    @Override
    public void tentarExcluirAgendamento(ExameId exameId, UsuarioResponsavelId responsavel) {
        log("TENTATIVA_EXCLUSAO", "Alvo ExameID: " + exameId + " | Usuário: " + responsavel);
        servicoReal.tentarExcluirAgendamento(exameId, responsavel);
    }

    @Override
    public Exame cancelarAgendamento(ExameId exameId, String motivo, UsuarioResponsavelId responsavel) {
        log("CANCELAR_AGENDAMENTO", "Alvo ExameID: " + exameId + " | Motivo: " + motivo);
        return servicoReal.cancelarAgendamento(exameId, motivo, responsavel);
    }
}