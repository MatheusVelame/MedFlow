package br.com.medflow.aplicacao.prontuario;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO para exibir um item do histórico clínico em respostas.
 */
public class HistoricoItemResponse {
    private final String id;
    private final String sintomas;
    private final String diagnostico;
    private final String conduta;
    private final LocalDateTime dataHoraRegistro;
    private final String profissionalResponsavel;
    private final List<String> anexosReferenciados;
    private final String prontuarioId; // Adicionado para identificar o prontuário
    private final String pacienteId; // Adicionado para identificar o paciente

    public HistoricoItemResponse(String id, String sintomas, String diagnostico, String conduta,
                                LocalDateTime dataHoraRegistro, String profissionalResponsavel,
                                List<String> anexosReferenciados) {
        this(id, sintomas, diagnostico, conduta, dataHoraRegistro, profissionalResponsavel, anexosReferenciados, null, null);
    }

    public HistoricoItemResponse(String id, String sintomas, String diagnostico, String conduta,
                                LocalDateTime dataHoraRegistro, String profissionalResponsavel,
                                List<String> anexosReferenciados, String prontuarioId, String pacienteId) {
        this.id = id;
        this.sintomas = sintomas;
        this.diagnostico = diagnostico;
        this.conduta = conduta;
        this.dataHoraRegistro = dataHoraRegistro;
        this.profissionalResponsavel = profissionalResponsavel;
        this.anexosReferenciados = anexosReferenciados;
        this.prontuarioId = prontuarioId;
        this.pacienteId = pacienteId;
    }

    // Getters
    public String getId() { return id; }
    public String getSintomas() { return sintomas; }
    public String getDiagnostico() { return diagnostico; }
    public String getConduta() { return conduta; }
    public LocalDateTime getDataHoraRegistro() { return dataHoraRegistro; }
    public String getProfissionalResponsavel() { return profissionalResponsavel; }
    public List<String> getAnexosReferenciados() { return anexosReferenciados; }
    public String getProntuarioId() { return prontuarioId; }
    public String getPacienteId() { return pacienteId; }
}
