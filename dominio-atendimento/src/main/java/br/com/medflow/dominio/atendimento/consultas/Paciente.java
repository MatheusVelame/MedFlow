package br.com.medflow.dominio.atendimento.consultas;

public class Paciente {
    private final String nome;
    private final String prefNotificacao;
    private int cancelamentosRecentes = 0;

    public Paciente(String nome, String prefNotificacao) {
        this.nome = nome;
        this.prefNotificacao = prefNotificacao;
    }

    public String getNome() { return nome; }
    public String getPrefNotificacao() { return prefNotificacao; }
    public int getCancelamentosRecentes() { return cancelamentosRecentes; }
    public void setCancelamentosRecentes(int count) { this.cancelamentosRecentes = count; }
    
    @Override
    public int hashCode() { return nome.hashCode(); }
    @Override
    public boolean equals(Object obj) { return (obj instanceof Paciente) && nome.equals(((Paciente) obj).nome); }
}