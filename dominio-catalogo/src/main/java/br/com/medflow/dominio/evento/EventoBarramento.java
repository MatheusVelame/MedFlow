package br.com.medflow.dominio.evento;

/**
 * Interface que representa o barramento de eventos de domínio, 
 * responsável por postar eventos e permitir a inscrição de observadores.
 */
public interface EventoBarramento {
	<E> void adicionar(EventoObservador<E> observador);

	<E> void postar(E evento);
}