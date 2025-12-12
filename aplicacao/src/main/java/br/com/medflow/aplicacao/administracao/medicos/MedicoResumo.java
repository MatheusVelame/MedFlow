// Localização: aplicacao/src/main/java/br/com/medflow/aplicacao/administracao/medicos/MedicoResumo.java

package br.com.medflow.aplicacao.administracao.medicos;

import br.com.medflow.dominio.administracao.funcionarios.StatusFuncionario;

/**
 * DTO para exibir um resumo dos médicos em listas ou cards.
 * Usado especificamente para a interface do usuário de médicos.
 */
public class MedicoResumo {
    private final String id;
    private final String nome;
    private final String funcao;
    private final String contato;
    private final StatusFuncionario status;

    // Atributos específicos de Médico
    private final String crm;
    private final String especialidade;
    private final Integer consultasHoje;
    private final String proximaConsulta;

    private MedicoResumo(Builder builder) {
        this.id = builder.id;
        this.nome = builder.nome;
        this.funcao = builder.funcao;
        this.contato = builder.contato;
        this.status = builder.status;
        this.crm = builder.crm;
        this.especialidade = builder.especialidade;
        this.consultasHoje = builder.consultasHoje;
        this.proximaConsulta = builder.proximaConsulta;
    }

    // Getters
    public String getId() { return id; }
    public String getNome() { return nome; }
    public String getFuncao() { return funcao; }
    public String getContato() { return contato; }
    public StatusFuncionario getStatus() { return status; }
    public String getCrm() { return crm; }
    public String getEspecialidade() { return especialidade; }
    public Integer getConsultasHoje() { return consultasHoje; }
    public String getProximaConsulta() { return proximaConsulta; }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private String nome;
        private String funcao;
        private String contato;
        private StatusFuncionario status;
        private String crm;
        private String especialidade;
        private Integer consultasHoje;
        private String proximaConsulta;

        public Builder id(String id) { this.id = id; return this; }
        public Builder nome(String nome) { this.nome = nome; return this; }
        public Builder funcao(String funcao) { this.funcao = funcao; return this; }
        public Builder contato(String contato) { this.contato = contato; return this; }
        public Builder status(StatusFuncionario status) { this.status = status; return this; }
        public Builder crm(String crm) { this.crm = crm; return this; }
        public Builder especialidade(String especialidade) { this.especialidade = especialidade; return this; }
        public Builder consultasHoje(Integer consultas) { this.consultasHoje = consultas; return this; }
        public Builder proximaConsulta(String proxima) { this.proximaConsulta = proxima; return this; }

        public MedicoResumo build() {
            return new MedicoResumo(this);
        }
    }
}