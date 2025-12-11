package br.com.medflow.infraestrutura.evento;

import br.com.medflow.dominio.financeiro.evento.EventoBarramento;
import br.com.medflow.dominio.financeiro.evento.EventoObservador;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Implementação do barramento de eventos para o módulo financeiro.
 * Gerencia a lista de observadores e distribui eventos para eles.
 */
@Component
public class EventoBarramentoImpl implements EventoBarramento {

	private final List<EventoObservador<Object>> observadores = new CopyOnWriteArrayList<>();

	@Override
	public <E> void adicionar(EventoObservador<E> observador) {
		// Como EventoObservador<Object> pode receber qualquer tipo de evento,
		// fazemos um cast seguro aqui
		@SuppressWarnings("unchecked")
		EventoObservador<Object> observadorObject = (EventoObservador<Object>) observador;
		observadores.add(observadorObject);
	}

	@Override
	public <E> void postar(E evento) {
		// Notifica todos os observadores sobre o evento
		for (EventoObservador<Object> observador : observadores) {
			try {
				observador.observarEvento(evento);
			} catch (Exception e) {
				// Log do erro mas não interrompe a execução para outros observadores
				System.err.println("Erro ao notificar observador: " + e.getMessage());
				e.printStackTrace();
			}
		}
	}
}

