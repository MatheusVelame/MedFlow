package br.com.medflow.aplicacao.prontuario.dto.request;

/**
 * DTO de request para criar um novo prontuário.
 */
public class CriarProntuarioRequest {
    
    private String pacienteId;
    private String atendimentoId;
    private String profissionalResponsavel;
    private String observacoesIniciais;

    // Construtor padrão
    public CriarProntuarioRequest() {}

    // Construtor completo
    public CriarProntuarioRequest(String pacienteId, String atendimentoId,
                                 String profissionalResponsavel, String observacoesIniciais) {
        this.pacienteId = pacienteId;
        this.atendimentoId = atendimentoId;
        this.profissionalResponsavel = profissionalResponsavel;
        this.observacoesIniciais = observacoesIniciais;
    }

    // Getters e Setters
    public String getPacienteId() { return pacienteId; }
    public void setPacienteId(String pacienteId) { this.pacienteId = pacienteId; }

    public String getAtendimentoId() { return atendimentoId; }
    public void setAtendimentoId(String atendimentoId) { this.atendimentoId = atendimentoId; }

    public String getProfissionalResponsavel() { return profissionalResponsavel; }
    public void setProfissionalResponsavel(String profissionalResponsavel) { 
        this.profissionalResponsavel = profissionalResponsavel; 
    }

    public String getObservacoesIniciais() { return observacoesIniciais; }
    public void setObservacoesIniciais(String observacoesIniciais) { 
        this.observacoesIniciais = observacoesIniciais; 
    }
}

