package br.com.medflow.dominio.convenios;

import java.time.LocalDateTime;

public class HistoricoRegistro {
    private final String responsavel;
    private final String acao;
    private final LocalDateTime dataHora;
    private final String detalhes;

    public HistoricoRegistro(String responsavel, String acao, String detalhes) {
        this.responsavel = responsavel;
        this.acao = acao;
        this.detalhes = detalhes;
        this.dataHora = LocalDateTime.now();
    }
    
    public String getAcao() { return acao; }
    public String getResponsavel() { return responsavel; }
    public String getDetalhes() { return detalhes; }
    public LocalDateTime getData() { return dataHora; }
    
    @Override
    public String toString() {
        return String.format("[%s] %s por %s em %s", 
            acao, detalhes != null ? detalhes : "", responsavel, dataHora);
    }
}