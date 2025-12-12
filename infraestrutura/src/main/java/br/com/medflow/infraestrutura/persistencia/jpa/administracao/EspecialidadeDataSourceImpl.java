// Localização: infraestrutura/src/main/java/br/com/medflow/infraestrutura/persistencia/jpa/administracao/EspecialidadeDataSourceImpl.java

package br.com.medflow.infraestrutura.persistencia.jpa.administracao;

import br.com.medflow.aplicacao.administracao.medicos.MedicoConversaoComConsultasStrategy;
import br.com.medflow.dominio.referencia.especialidades.StatusEspecialidade;
import br.com.medflow.infraestrutura.persistencia.jpa.referencia.EspecialidadeJpa;
import br.com.medflow.infraestrutura.persistencia.jpa.referencia.EspecialidadeJpaRepository;
import org.springframework.stereotype.Component;

/**
 * Implementação do EspecialidadeDataSource.
 * Busca dados reais do banco.
 */
@Component
public class EspecialidadeDataSourceImpl
        implements MedicoConversaoComConsultasStrategy.EspecialidadeDataSource {

    private final EspecialidadeJpaRepository especialidadeRepository;

    public EspecialidadeDataSourceImpl(EspecialidadeJpaRepository especialidadeRepository) {
        this.especialidadeRepository = especialidadeRepository;
    }

    @Override
    public String obterNomeEspecialidade(int especialidadeId) {
        return especialidadeRepository.findById(especialidadeId)
                .map(EspecialidadeJpa::getNome)
                .orElse("Especialidade " + especialidadeId);
    }

    @Override
    public boolean especialidadeEstaAtiva(int especialidadeId) {
        return especialidadeRepository.findById(especialidadeId)
                .map(e -> e.getStatus() == StatusEspecialidade.ATIVA)
                .orElse(false);
    }
}