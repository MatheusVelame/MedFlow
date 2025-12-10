// Localização: dominio-atendimento/src/main/java/br/com/medflow/dominio/atendimento/consultas/Consulta.java

package br.com.medflow.dominio.atendimento.consultas;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Entidade Raiz de Agregado (Aggregate Root) Consulta.
 * Contém o estado e os métodos de comportamento (CUD).
 */
public class Consulta {
    private ConsultaId id;
    private LocalDateTime dataHora;
    private String descricao;
    private StatusConsulta status;
    // Em um projeto real, precisaria de PacienteId e MedicoId
    // private PacienteId pacienteId; 
    // private MedicoId medicoId; 
    private List<Object> historico; // Simplificado

    // Construtor usado pelo Repositório (Adapter) para reconstrução
    public Consulta(ConsultaId id, LocalDateTime dataHora, String descricao, StatusConsulta status, List<Object> historico) {
        this.id = id;
        this.dataHora = dataHora;
        this.descricao = descricao;
        this.status = status;
        this.historico = historico;
    }

    // Construtor usado pelo Serviço de Domínio (Criação - Novo Agendamento)
    public Consulta(LocalDateTime dataHora, String descricao, Object pacienteId, Object medicoId) {
        this.id = null; // ID será gerado na Infraestrutura
        this.dataHora = dataHora;
        this.descricao = descricao;
        this.status = StatusConsulta.AGENDADA;
        this.historico = List.of();
    }


    // Método para o comando de escrita (U - Update)
    public void mudarStatus(StatusConsulta novoStatus) {
        // Lógica de domínio: Adicionar entrada no histórico, validar transições, etc.
        this.status = novoStatus;
    }

    // Getters para uso do Repositório (Adapter)
    public ConsultaId getId() { return id; }
    public LocalDateTime getDataHora() { return dataHora; }
    public String getDescricao() { return descricao; }
    public StatusConsulta getStatus() { return status; }
    // ...
}