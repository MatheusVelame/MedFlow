// Localização: aplicacao/src/main/java/br/com/medflow/aplicacao/atendimento/consultas/ConsultaDetalhes.java

package br.com.medflow.aplicacao.atendimento.consultas;

import br.com.medflow.dominio.atendimento.consultas.StatusConsulta;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO para exibir todos os detalhes de uma única consulta, incluindo seu histórico.
 */
public class ConsultaDetalhes {
    private final Integer id;
    private final LocalDateTime dataHora;
    private final String descricao;
    private final StatusConsulta status;
    private final String idPaciente;
    private final String nomeCompletoPaciente;
    private final String idMedico;
    private final String nomeCompletoMedico;
    
    private final List<HistoricoConsultaDetalhes> historico; 

    public ConsultaDetalhes(
        Integer id, 
        LocalDateTime dataHora, 
        String descricao, 
        StatusConsulta status, 
        String idPaciente, 
        String nomeCompletoPaciente, 
        String idMedico, 
        String nomeCompletoMedico, 
        List<HistoricoConsultaDetalhes> historico) {
        
        this.id = id;
        this.dataHora = dataHora;
        this.descricao = descricao;
        this.status = status;
        this.idPaciente = idPaciente;
        this.nomeCompletoPaciente = nomeCompletoPaciente;
        this.idMedico = idMedico;
        this.nomeCompletoMedico = nomeCompletoMedico;
        this.historico = historico;
    }

    // Classe interna para o histórico (simplificando)
    public record HistoricoConsultaDetalhes(LocalDateTime data, String acao, String responsavel) {}

    // Getters
    public Integer getId() { return id; }
    public LocalDateTime getDataHora() { return dataHora; }
    public String getDescricao() { return descricao; }
    public StatusConsulta getStatus() { return status; }
    public String getIdPaciente() { return idPaciente; }
    public String getNomeCompletoPaciente() { return nomeCompletoPaciente; }
    public String getIdMedico() { return idMedico; }
    public String getNomeCompletoMedico() { return nomeCompletoMedico; }
    public List<HistoricoConsultaDetalhes> getHistorico() { return historico; }
}