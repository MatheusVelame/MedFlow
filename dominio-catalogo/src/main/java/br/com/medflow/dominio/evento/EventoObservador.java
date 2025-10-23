package br.com.medflow.dominio.evento;

/**
 * Interface de marcação para classes que observam e reagem a eventos de domínio.
 * @param <E> O tipo de evento de domínio que esta classe observa.
 */
public interface EventoObservador<E> {
	void observarEvento(E evento);
}