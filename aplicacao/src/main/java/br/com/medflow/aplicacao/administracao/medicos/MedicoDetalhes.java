// Localização: aplicacao/src/main/java/br/com/medflow/aplicacao/administracao/medicos/MedicoDetalhes.java

package br.com.medflow.aplicacao.administracao.medicos;

import br.com.medflow.dominio.administracao.funcionarios.StatusFuncionario;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO para exibir todos os detalhes de um médico, incluindo histórico completo.
 * Contém informações específicas de médicos além dos dados de funcionário.
 */
public class MedicoDetalhes {
    private final String id;
    private final String nome;
    private final String funcao;
    private final String contato;
    private final StatusFuncionario status;
    private final List<HistoricoDetalhes> historico;

    // Campos específicos de Médico
    private final String crm;
    private final String especialidade;
    private final LocalDate dataNascimento;
    private final List<HorarioDisponibilidade> horariosDisponiveis;

    // 🆕 Campos de vínculos clínicos
    private final Boolean temConsultas;
    private final Boolean temProntuarios;
    private final Boolean temExames;

    public MedicoDetalhes(
            String id,
            String nome,
            String funcao,
            String contato,
            StatusFuncionario status,
            List<HistoricoDetalhes> historico,
            String crm,
            String especialidade,
            LocalDate dataNascimento,
            List<HorarioDisponibilidade> horariosDisponiveis,
            Boolean temConsultas,
            Boolean temProntuarios,
            Boolean temExames) {

        this.id = id;
        this.nome = nome;
        this.funcao = funcao;
        this.contato = contato;
        this.status = status;
        this.historico = historico;
        this.crm = crm;
        this.especialidade = especialidade;
        this.dataNascimento = dataNascimento;
        this.horariosDisponiveis = horariosDisponiveis;
        this.temConsultas = temConsultas;
        this.temProntuarios = temProntuarios;
        this.temExames = temExames;
    }

    /**
     * Record para representar uma entrada do histórico.
     */
    public record HistoricoDetalhes(
            String acao,
            String descricao,
            String responsavelId,
            LocalDateTime dataHora
    ) {}

    /**
     * Record para representar horários de disponibilidade do médico.
     */
    public record HorarioDisponibilidade(
            String diaSemana,
            String horaInicio,
            String horaFim
    ) {}

    // Getters
    public String getId() { return id; }
    public String getNome() { return nome; }
    public String getFuncao() { return funcao; }
    public String getContato() { return contato; }
    public StatusFuncionario getStatus() { return status; }
    public List<HistoricoDetalhes> getHistorico() { return historico; }
    public String getCrm() { return crm; }
    public String getEspecialidade() { return especialidade; }
    public LocalDate getDataNascimento() { return dataNascimento; }
    public List<HorarioDisponibilidade> getHorariosDisponiveis() { return horariosDisponiveis; }

    // 🆕 Getters dos vínculos clínicos
    public Boolean getTemConsultas() { return temConsultas; }
    public Boolean getTemProntuarios() { return temProntuarios; }
    public Boolean getTemExames() { return temExames; }
}