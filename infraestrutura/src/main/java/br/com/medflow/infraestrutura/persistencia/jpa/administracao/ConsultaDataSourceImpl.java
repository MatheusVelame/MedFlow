// Localização: infraestrutura/src/main/java/br/com/medflow/infraestrutura/persistencia/jpa/administracao/ConsultaDataSourceImpl.java

package br.com.medflow.infraestrutura.persistencia.jpa.administracao;

import br.com.medflow.aplicacao.administracao.medicos.MedicoConversaoComConsultasStrategy;
import br.com.medflow.dominio.administracao.funcionarios.FuncionarioId;
import org.springframework.stereotype.Component;

/**
 * Implementação do ConsultaDataSource.
 * Por ora retorna dados mockados (0 consultas).
 */
@Component
public class ConsultaDataSourceImpl
        implements MedicoConversaoComConsultasStrategy.ConsultaDataSource {

    @Override
    public Integer contarConsultasHoje(FuncionarioId medicoId) {
        // TODO: Implementar query real quando tiver ConsultaJpaRepository
        return 0;
    }

    @Override
    public String obterProximaConsulta(FuncionarioId medicoId) {
        // TODO: Implementar query real quando tiver ConsultaJpaRepository
        return null;
    }
}