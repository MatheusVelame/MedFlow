package br.com.medflow.dominio.atendimento.exames;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import static org.apache.commons.lang3.Validate.notNull;

/**
 * Aggregate Root do domínio de Exames.
 */
public class Exame {

    private ExameId id;
    private final Long pacienteId;
    private Long medicoId;
    private String tipoExame;
    private LocalDateTime dataHora;
    private StatusExame status;
    private final List<HistoricoEntrada> historico;
    private boolean vinculadoALaudo = false;
    private boolean vinculadoAProntuario = false;
    private String motivoCancelamento;

    public Exame(Long pacienteId, Long medicoId, String tipoExame, LocalDateTime dataHora, UsuarioResponsavelId responsavel) {
        notNull(pacienteId, "O paciente é obrigatório.");
        
        this.pacienteId = pacienteId;
        this.medicoId = medicoId;
        this.tipoExame = tipoExame;
        this.dataHora = dataHora;
        
        // RN7
        this.status = StatusExame.AGENDADO;
        this.historico = new ArrayList<>();
        this.registrarHistorico(AcaoHistorico.CRIACAO, "Agendamento inicial criado.", responsavel);
    }
    
    protected Exame() {
        this.historico = new ArrayList<>();
        this.pacienteId = null; 
    }

    /*
     * Construtor protegido para reconstituição do estado a partir da persistência (uso interno pelos mapeadores).
     * Permite reconstruir um Exame sem acionar regras de domínio (ex.: registrar histórico de criação novamente).
     */
    public Exame(ExameId id,
                    Long pacienteId,
                    Long medicoId,
                    String tipoExame,
                    LocalDateTime dataHora,
                    StatusExame status,
                    List<HistoricoEntrada> historico,
                    boolean vinculadoALaudo,
                    boolean vinculadoAProntuario,
                    String motivoCancelamento) {
        this.id = id;
        this.pacienteId = pacienteId;
        this.medicoId = medicoId;
        this.tipoExame = tipoExame;
        this.dataHora = dataHora;
        this.status = status;
        this.historico = (historico != null) ? new ArrayList<>(historico) : new ArrayList<>();
        this.vinculadoALaudo = vinculadoALaudo;
        this.vinculadoAProntuario = vinculadoAProntuario;
        this.motivoCancelamento = motivoCancelamento;
    }

    // Atualiza assinatura para aceitar observacoes e gravar no histórico
    public void atualizar(Long novoMedicoId, String novoTipoExame, LocalDateTime novaDataHora, UsuarioResponsavelId responsavel, String observacoes) {
        LocalDateTime dataHoraAntiga = this.dataHora;
        Long medicoIdAntigo = this.medicoId;
        String tipoExameAntigo = this.tipoExame;

        this.medicoId = novoMedicoId;
        this.tipoExame = novoTipoExame;
        this.dataHora = novaDataHora;

        boolean dataHoraAlterada = !dataHoraAntiga.equals(novaDataHora);
        boolean medicoAlterado = !medicoIdAntigo.equals(novoMedicoId);
        boolean tipoExameAlterado = !tipoExameAntigo.equals(novoTipoExame);
        
        // RN4
        if (dataHoraAlterada || medicoAlterado || tipoExameAlterado) {
            StringBuilder descricao = new StringBuilder("Exame atualizado. Alterações: ");
            
            if (dataHoraAlterada) {
                descricao.append(String.format("Data/Hora de %s para %s; ", dataHoraAntiga, novaDataHora));
            }
            if (medicoAlterado) {
                descricao.append(String.format("Médico de %d para %d; ", medicoIdAntigo, novoMedicoId));
            }
            if (tipoExameAlterado) {
                descricao.append(String.format("Tipo de Exame de %s para %s; ", tipoExameAntigo, novoTipoExame));
            }

            if (observacoes != null && !observacoes.trim().isEmpty()) {
                descricao.append(" Observações: ").append(observacoes.trim());
            }
            
            this.registrarHistorico(AcaoHistorico.ATUALIZACAO, descricao.toString().trim(), responsavel);
        }
    }
    
    public void cancelar(String motivo, UsuarioResponsavelId responsavel) {
        
        // RN11
        if (this.status != StatusExame.AGENDADO) {
            throw new ExcecaoDominio("Ação não permitida para o status atual do exame");
        }
        
        // RN4
        if (motivo == null || motivo.trim().isEmpty()) {
            throw new ExcecaoDominio("É obrigatório informar o motivo do cancelamento"); 
        }
        
        this.status = StatusExame.CANCELADO;
        this.motivoCancelamento = motivo;
        this.registrarHistorico(AcaoHistorico.CANCELAMENTO, "Exame cancelado. Motivo: " + motivo, responsavel);
    }
    
    public void tentarExcluir(UsuarioResponsavelId responsavel) {
        
        // RN1
        if (this.status != StatusExame.AGENDADO) {
            throw new ExcecaoDominio("Exames realizados ou em andamento não podem ser excluídos");
        }
        
        // RN3
        if (this.vinculadoAProntuario) {
            throw new ExcecaoDominio("Não é permitido excluir exames associados a registros clínicos do paciente");
        }
        
        // RN2
        if (this.vinculadoALaudo) {
            throw new ExcecaoDominio("Exame com laudo não pode ser excluído, apenas cancelado.");
        }
        

        // Se passar em todas as RNs de bloqueio, pode-se prosseguir com a exclusão física/lógica final
        this.registrarHistorico(AcaoHistorico.EXCLUSAO, "Exame excluído com sucesso (físico/lógico).", responsavel);
        // O repositório irá remover/marcar como deletado.
    }
    
    // --- Getters e Setters de Repositório ---
    
    public ExameId getId() {
        return id;
    }

    public void setId(ExameId id) {
        this.id = id;
    }

    public Long getPacienteId() {
        return pacienteId;
    }
    
    public Long getMedicoId() {
        return medicoId; 
    }
    
    public String getTipoExame() {
        return tipoExame; 
    }

    public StatusExame getStatus() {
        return status;
    }
    
    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public List<HistoricoEntrada> getHistorico() {
        return historico;
    }

    public boolean isVinculadoALaudo() {
        return vinculadoALaudo;
    }

    public void setVinculadoALaudo(boolean vinculadoALaudo) {
        this.vinculadoALaudo = vinculadoALaudo;
    }

    public boolean isVinculadoAProntuario() {
        return vinculadoAProntuario;
    }

    public void setVinculadoAProntuario(boolean vinculadoAProntuario) {
        this.vinculadoAProntuario = vinculadoAProntuario;
    }
    
    public void setStatus(StatusExame status) {
        this.status = status;
    }
    
    public String getMotivoCancelamento() {
        return motivoCancelamento;
    }
    
    // --- Método de Suporte ---
    
    private void registrarHistorico(AcaoHistorico acao, String descricao, UsuarioResponsavelId responsavel) {
        this.historico.add(new HistoricoEntrada(acao, descricao, responsavel));
    }
}