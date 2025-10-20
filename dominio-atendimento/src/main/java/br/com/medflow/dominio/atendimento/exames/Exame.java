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
    private final Long pacienteId; // RN2: O paciente vinculado não pode ser alterado (final)
    private Long medicoId;
    private String tipoExame;
    private LocalDateTime dataHora;
    private StatusExame status;
    private final List<HistoricoEntrada> historico;
    
    // Propriedades de suporte à RN2 e RN3 do Cancelamento/Exclusão
    private boolean vinculadoALaudo = false;
    private boolean vinculadoAProntuario = false;
    private String motivoCancelamento;


    // Construtor para NOVO Agendamento
    public Exame(Long pacienteId, Long medicoId, String tipoExame, LocalDateTime dataHora, UsuarioResponsavelId responsavel) {
        // Validações básicas de criação (outras validações estão no ExameServico, como RN1, RN2, RN3, RN4, RN5, RN6)
        notNull(pacienteId, "O paciente é obrigatório.");
        
        this.pacienteId = pacienteId;
        this.medicoId = medicoId;
        this.tipoExame = tipoExame;
        this.dataHora = dataHora;
        
        // RN7: O exame deve receber um status inicial “Agendado”
        this.status = StatusExame.AGENDADO;
        this.historico = new ArrayList<>();
        this.registrarHistorico(AcaoHistorico.CRIACAO, "Agendamento inicial criado.", responsavel);
    }
    
    // Construtor usado pelo repositório (JPA, por exemplo)
    protected Exame() {
        this.historico = new ArrayList<>();
        this.pacienteId = null; 
    }

    /**
     * Atualiza os campos permitidos do exame.
     * RN1: Só podem ser alterados a data, o horário, o tipo de exame e o médico responsável.
     * RN4: O histórico de alterações de data/hora deve ser registrado.
     */
    public void atualizar(Long novoMedicoId, String novoTipoExame, LocalDateTime novaDataHora, UsuarioResponsavelId responsavel) {
        
        // RN4: Registro no Histórico de Alterações de Data/Hora
        if (!this.dataHora.equals(novaDataHora)) {
            String descricao = String.format("Data/Hora alterada de %s para %s.", this.dataHora, novaDataHora);
            this.registrarHistorico(AcaoHistorico.ATUALIZACAO, descricao, responsavel);
        }
        
        // Aplica as alterações (RN1)
        this.medicoId = novoMedicoId;
        this.tipoExame = novoTipoExame;
        this.dataHora = novaDataHora;
    }
    
    /**
     * Marca o exame como CANCELADO (soft delete de agendamento).
     * RN4 (Cancelamento): Deve registrar a data e o motivo.
     */
    public void cancelar(String motivo, UsuarioResponsavelId responsavel) {
        if (motivo == null || motivo.trim().isEmpty()) {
            throw new ExcecaoDominio("É obrigatório informar o motivo do cancelamento"); // RN4
        }
        
        this.status = StatusExame.CANCELADO;
        this.motivoCancelamento = motivo;
        this.registrarHistorico(AcaoHistorico.CANCELAMENTO, "Exame cancelado. Motivo: " + motivo, responsavel);
    }
    
    /**
     * Tenta excluir o exame (físico ou lógico).
     * RN1, RN2, RN3 da Feature Exclusão.
     */
    public void tentarExcluir(UsuarioResponsavelId responsavel) {
        
        // RN1: Não é permitido excluir exames já realizados ou em andamento.
        if (this.status != StatusExame.AGENDADO) {
            throw new ExcecaoDominio("Exames realizados ou em andamento não podem ser excluídos");
        }
        
        // RN3: A exclusão só é permitida se o exame ainda não estiver associado a nenhum registro clínico.
        if (this.vinculadoAProntuario) {
            throw new ExcecaoDominio("Não é permitido excluir exames associados a registros clínicos do paciente");
        }
        
        // RN2: Caso o exame já esteja vinculado a um laudo, não pode ser excluído (fisicamente), deve ser apenas "Cancelado"
        if (this.vinculadoALaudo) {
            // Se o status é AGENDADO e tem laudo (inconsistente, mas a RN prioriza o cancelamento)
            // Na prática, esta RN geralmente se aplica quando o status é REALIZADO, mas vamos seguir o teste.
            this.cancelar("Exclusão física bloqueada por vínculo com laudo.", responsavel);
            return; 
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

    public void setVinculadoALaudo(boolean vinculadoALaudo) { // Apenas para testes/população do GIVEN
        this.vinculadoALaudo = vinculadoALaudo;
    }

    public boolean isVinculadoAProntuario() {
        return vinculadoAProntuario;
    }

    public void setVinculadoAProntuario(boolean vinculadoAProntuario) { // Apenas para testes/população do GIVEN
        this.vinculadoAProntuario = vinculadoAProntuario;
    }
    
    public String getMotivoCancelamento() {
        return motivoCancelamento;
    }
    
    // --- Método de Suporte ---
    
    private void registrarHistorico(AcaoHistorico acao, String descricao, UsuarioResponsavelId responsavel) {
        this.historico.add(new HistoricoEntrada(acao, descricao, responsavel));
    }
}