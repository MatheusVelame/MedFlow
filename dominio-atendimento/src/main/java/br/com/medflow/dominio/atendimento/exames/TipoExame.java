package br.com.medflow.dominio.atendimento.exames;

public class TipoExame {
 private String nome;
 private boolean cadastrado;

 public TipoExame(String nome, boolean cadastrado) {
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
