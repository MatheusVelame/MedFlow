// Localização: dominio-atendimento/src/main/java/br/com/medflow/dominio/atendimento/consultas/ConsultaRepositorio.java

package br.com.medflow.dominio.atendimento.consultas;

import java.util.Optional;

/**
 * Interface (Porta) para persistência do Aggregate Root Consulta.
 * É a dependência da camada de Domínio (para o Serviço de Domínio/Service).
 */
public interface ConsultaRepositorio {
    
    /**
     * Busca um Aggregate Root Consulta pelo seu ID.
     */
    Optional<Consulta> buscarPorId(ConsultaId id);

    /**
     * Salva ou atualiza um Aggregate Root Consulta.
     */
    void salvar(Consulta consulta);
}