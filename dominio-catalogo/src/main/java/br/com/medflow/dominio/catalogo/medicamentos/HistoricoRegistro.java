package br.com.medflow.dominio.catalogo.medicamentos;

import java.time.LocalDateTime;

public class HistoricoRegistro {
    private final String medicamentoId;
    private final String responsavel;
    private final String acao;
    private final LocalDateTime data; // Campo que estava dando aviso
    private final String detalhe;

    public HistoricoRegistro(String medicamentoId, String responsavel, String acao, String detalhe) {
        this.medicamentoId = medicamentoId;
        this.responsavel = responsavel;
        this.acao = acao;
        this.detalhe = detalhe;
        this.data = LocalDateTime.now();
    }
    
    public String getAcao() { return acao; }
    public String getResponsavel() { return responsavel; }
    public String getMedicamentoId() { return medicamentoId; }
    public String getDetalhe() { return detalhe; }
    public LocalDateTime getData() { return data; }
}