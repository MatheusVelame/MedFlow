// Localização: infraestrutura/src/main/java/br/com/medflow/infraestrutura/persistencia/jpa/administracao/DisponibilidadeDataSourceImpl.java

package br.com.medflow.infraestrutura.persistencia.jpa.administracao;

import br.com.medflow.aplicacao.administracao.medicos.MedicoConversaoComConsultasStrategy;
import br.com.medflow.dominio.administracao.funcionarios.FuncionarioId;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementação do DisponibilidadeDataSource.
 */
@Component
public class DisponibilidadeDataSourceImpl
        implements MedicoConversaoComConsultasStrategy.DisponibilidadeDataSource {

    private final MedicoJpaRepository medicoJpaRepository;

    public DisponibilidadeDataSourceImpl(MedicoJpaRepository medicoJpaRepository) {
        this.medicoJpaRepository = medicoJpaRepository;
    }

    @Override
    public List<HorarioInfo> obterHorariosDisponibilidade(FuncionarioId medicoId) {
        Integer idInt = Integer.parseInt(medicoId.getId());

        return medicoJpaRepository.findByIdComDisponibilidades(idInt)
                .map(medico -> medico.getDisponibilidades().stream()
                        .map(d -> new HorarioInfo(
                                d.getDiaSemana(),
                                d.getHoraInicio().toString(),
                                d.getHoraFim().toString()
                        ))
                        .collect(Collectors.toList()))
                .orElse(List.of());
    }
}