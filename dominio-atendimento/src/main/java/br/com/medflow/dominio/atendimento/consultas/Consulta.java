// Localização: dominio-atendimento/src/main/java/br/com/medflow/dominio/atendimento/consultas/Consulta.java

package br.com.medflow.dominio.atendimento.consultas;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Entidade Raiz de Agregado (Aggregate Root) Consulta.
 */
public class Consulta {
    private ConsultaId id;
    private LocalDateTime dataHora;
    private String descricao;
    private StatusConsulta status;
    
    // CAMPOS ADICIONADOS PARA REFERÊNCIA EXTERNA
    private Integer pacienteId; 
    private Integer medicoId; 
    
    private List<Object> historico; 

    // Construtor usado pelo Repositório (Adapter) para reconstrução
    public Consulta(ConsultaId id, LocalDateTime dataHora, String descricao, StatusConsulta status, List<Object> historico) {
        this.id = id;
        this.dataHora = dataHora;
        this.descricao = descricao;
        this.status = status;
        this.historico = historico;
        // Na reconstrução, esses campos seriam populados a partir de campos de reconstrução (omitido para simplicidade).
        this.pacienteId = null; 
        this.medicoId = null;
    }

    // Construtor usado pelo Serviço de Domínio (Criação - Novo Agendamento)
    public Consulta(LocalDateTime dataHora, String descricao, Integer pacienteId, Integer medicoId) {
        this.id = null; 
        this.dataHora = dataHora;
        this.descricao = descricao;
        this.status = StatusConsulta.AGENDADA;
        this.pacienteId = pacienteId; // NOVO: Armazena o ID
        this.medicoId = medicoId;     // NOVO: Armazena o ID
        this.historico = List.of();
    }


    public void mudarStatus(StatusConsulta novoStatus) {
        this.status = novoStatus;
    }

    // GETTERS NOVOS PARA O REPOSITÓRIO (INFRAESTRUTURA)
    public Integer getPacienteId() { return pacienteId; }
    public Integer getMedicoId() { return medicoId; }
    
    // Getters existentes
    public ConsultaId getId() { return id; }
    public LocalDateTime getDataHora() { return dataHora; }
    public String getDescricao() { return descricao; }
    public StatusConsulta getStatus() { return status; }
}