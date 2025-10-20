package br.com.medflow.dominio.atendimento.consultas;

public class Paciente {
    private final String id;
    private final String nome;
    private final String cpf;
    private final String dataNascimento;
    private final String prefNotificacao;
    private int cancelamentosRecentes = 0;

    public Paciente(String id, String nome, String cpf, String dataNascimento) {
        this.id = id;
        this.nome = nome;
        this.cpf = cpf;
        this.dataNascimento = dataNascimento;
        this.prefNotificacao = "email";
    }

    public Paciente(String nome, String prefNotificacao) {
        this.id = "PAC-" + System.currentTimeMillis();
        this.nome = nome;
        this.cpf = "00000000000";
        this.dataNascimento = "1990-01-01";
        this.prefNotificacao = prefNotificacao;
    }

    public String getId() { return id; }
    public String getNome() { return nome; }
    public String getCpf() { return cpf; }
    public String getDataNascimento() { return dataNascimento; }
    public String getPrefNotificacao() { return prefNotificacao; }
    public int getCancelamentosRecentes() { return cancelamentosRecentes; }
    public void setCancelamentosRecentes(int count) { this.cancelamentosRecentes = count; }
    
    @Override
    public int hashCode() { return id.hashCode(); }
    @Override
    public boolean equals(Object obj) { return (obj instanceof Paciente) && id.equals(((Paciente) obj).id); }
}