// Localização: dominio-atendimento/src/main/java/br/com/medflow/dominio/atendimento/consultas/ConsultaId.java

package br.com.medflow.dominio.atendimento.consultas;

import static org.apache.commons.lang3.Validate.notNull;

/**
 * Value Object para identificar unicamente a Consulta (Aggregate Root ID).
 */
public class ConsultaId {
    private final Integer valor;

    public ConsultaId(Integer valor) {
        notNull(valor, "O ID da consulta não pode ser nulo.");
        this.valor = valor;
    }

    public Integer getValor() {
        return valor;
    }
    
    // Em uma implementação completa, equals() e hashCode() seriam implementados.
}