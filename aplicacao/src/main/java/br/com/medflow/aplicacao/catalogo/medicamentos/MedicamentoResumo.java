package br.com.medflow.aplicacao.catalogo.medicamentos;

import br.com.medflow.dominio.catalogo.medicamentos.StatusMedicamento;
import java.util.Objects;

/**
 * DTO para exibir um resumo dos medicamentos em listas ou grids.
 */
public class MedicamentoResumo {
    private final Integer id;
    private final String nome;
    private final String usoPrincipal;
    private final String contraindicacoes; // <-- NOVO CAMPO ADICIONADO
    private final StatusMedicamento status;
    private final boolean possuiRevisaoPendente;

    public MedicamentoResumo(
        Integer id, 
        String nome, 
        String usoPrincipal, 
        String contraindicacoes, // <-- ADICIONADO AO CONSTRUTOR
        StatusMedicamento status, 
        boolean possuiRevisaoPendente) {
        
        this.id = id;
        this.nome = nome;
        this.usoPrincipal = usoPrincipal;
        this.contraindicacoes = contraindicacoes; // <-- INICIALIZAÇÃO ADICIONADA
        this.status = Objects.requireNonNull(status);
        this.possuiRevisaoPendente = possuiRevisaoPendente;
    }

    // Getters
    public Integer getId() { return id; }
    public String getNome() { return nome; }
    public String getUsoPrincipal() { return usoPrincipal; }
    public String getContraindicacoes() { return contraindicacoes; } // <-- NOVO GETTER
    public StatusMedicamento getStatus() { return status; }
    public boolean isPossuiRevisaoPendente() { return possuiRevisaoPendente; }
}