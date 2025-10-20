package br.com.medflow.dominio.evento;

import java.util.ArrayList;
import java.util.List;

public class EventoBarramento {
 private final List<EventoObservador> observadores = new ArrayList<>();

 public void registrar(EventoObservador observador) {
     observadores.add(observador);
 }

 public void publicar(Evento evento) {
     for (EventoObservador o : observadores) {
         o.notificar(evento);
     }
 }
}
