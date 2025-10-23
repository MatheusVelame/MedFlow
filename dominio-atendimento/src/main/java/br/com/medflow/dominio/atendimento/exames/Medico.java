package br.com.medflow.dominio.atendimento.exames;

import static org.apache.commons.lang3.Validate.notNull;

public class Medico {
    
    private final Long id; // Identificador do Médico
    private String nome;
    private boolean ativo;
    private Disponibilidade disponibilidade;

    public Medico(Long id, String nome, boolean ativo) {
        notNull(id, "O ID do médico é obrigatório.");
        this.id = id;
        this.nome = nome;
        this.ativo = ativo;
        this.disponibilidade = Disponibilidade.DISPONIVEL;
    }

    public Long getId() {
        return id;
    }
    
    public String getNome() {
        return nome;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public Disponibilidade getDisponibilidade() {
        return disponibilidade;
    }

    // Se a classe Exame estivesse armazenando um objeto Medico, ela usaria este método.
    // Mas no DDD, geralmente se armazena apenas o ID (Long).
    public Long getMedicoId() {
        return id;
    }
}