package br.com.medflow.dominio.evento;

public class Evento {
 private final String tipo;
 private final Object payload;

 public Evento(String tipo, Object payload) {
     this.tipo = tipo;
     this.payload = payload;
 }

 public String getTipo() {
     return tipo;
 }

 public Object getPayload() {
     return payload;
 }
}
