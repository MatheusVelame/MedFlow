package br.com.medflow.dominio.atendimento.exames;

public class Paciente {
 private String nome;
 private boolean cadastrado;

 public Paciente(String nome, boolean cadastrado) {
     this.nome = nome;
     this.cadastrado = cadastrado;
 }

 public boolean isCadastrado() {
     return cadastrado;
 }

 public String getNome() {
     return nome;
 }
}
