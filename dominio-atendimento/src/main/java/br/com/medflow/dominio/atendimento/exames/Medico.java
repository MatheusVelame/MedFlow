package br.com.medflow.dominio.atendimento.exames;

public class Medico {
 private String nome;
 private boolean ativo;
 private Disponibilidade disponibilidade;

 public Medico(String nome, boolean ativo) {
     this.nome = nome;
     this.ativo = ativo;
     this.disponibilidade = new Disponibilidade();
 }

 public boolean isAtivo() {
     return ativo;
 }

 public Disponibilidade getDisponibilidade() {
     return disponibilidade;
 }

 public String getNome() {
     return nome;
 }
}
