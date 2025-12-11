package br.com.medflow.aplicacao.prontuario.dto.request;

import java.util.List;

/**
 * DTO de request para adicionar histórico clínico a um prontuário.
 */
public class AdicionarHistoricoRequest {
    
    private String sintomas;
    
    private String diagnostico;
    
    private String conduta;
    
    private String profissionalResponsavel;
    
    private List<String> anexosReferenciados;

    // Construtor padrão
    public AdicionarHistoricoRequest() {}

    // Construtor completo
    public AdicionarHistoricoRequest(String sintomas, String diagnostico, String conduta,
                                   String profissionalResponsavel, List<String> anexosReferenciados) {
        this.sintomas = sintomas;
        this.diagnostico = diagnostico;
        this.conduta = conduta;
        this.profissionalResponsavel = profissionalResponsavel;
        this.anexosReferenciados = anexosReferenciados;
    }

    // Getters e Setters
    public String getSintomas() { return sintomas; }
    public void setSintomas(String sintomas) { this.sintomas = sintomas; }

    public String getDiagnostico() { return diagnostico; }
    public void setDiagnostico(String diagnostico) { this.diagnostico = diagnostico; }

    public String getConduta() { return conduta; }
    public void setConduta(String conduta) { this.conduta = conduta; }

    public String getProfissionalResponsavel() { return profissionalResponsavel; }
    public void setProfissionalResponsavel(String profissionalResponsavel) { 
        this.profissionalResponsavel = profissionalResponsavel; 
    }

    public List<String> getAnexosReferenciados() { return anexosReferenciados; }
    public void setAnexosReferenciados(List<String> anexosReferenciados) { 
        this.anexosReferenciados = anexosReferenciados; 
    }
}
