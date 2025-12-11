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
    
    private Integer pacienteId; 
    private Integer medicoId; 
    
    private List<Object> historico; 

    // NOVO CONSTRUTOR DE RECONSTRUÇÃO (USADO PELO REPOSITÓRIO NA LEITURA)
    public Consulta(ConsultaId id, LocalDateTime dataHora, String descricao, StatusConsulta status, Integer pacienteId, Integer medicoId, List<Object> historico) {
        this.id = id;
        this.dataHora = dataHora;
        this.descricao = descricao;
        this.status = status;
        this.pacienteId = pacienteId; // CORRIGIDO: Preserva o ID
        this.medicoId = medicoId;     // CORRIGIDO: Preserva o ID
        this.historico = historico;
    }
    
    // Construtor de criação (usado pelo Serviço de Domínio para novos agendamentos)
    public Consulta(LocalDateTime dataHora, String descricao, Integer pacienteId, Integer medicoId) {
        this.id = null; 
        this.dataHora = dataHora;
        this.descricao = descricao;
        this.status = StatusConsulta.AGENDADA;
        this.pacienteId = pacienteId; 
        this.medicoId = medicoId;     
        this.historico = List.of();
    }


    public void mudarStatus(StatusConsulta novoStatus) {
        this.status = novoStatus;
    }

    // Getters para o Repositório (Infraestrutura)
    public Integer getPacienteId() { return pacienteId; }
    public Integer getMedicoId() { return medicoId; }
    
    // Getters existentes
    public ConsultaId getId() { return id; }
    public LocalDateTime getDataHora() { return dataHora; }
    public String getDescricao() { return descricao; }
    public StatusConsulta getStatus() { return status; }
}