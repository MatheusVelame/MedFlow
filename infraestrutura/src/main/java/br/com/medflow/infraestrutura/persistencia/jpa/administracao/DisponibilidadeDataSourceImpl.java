package br.com.medflow.infraestrutura.persistencia.jpa.administracao;

import br.com.medflow.aplicacao.administracao.medicos.MedicoConversaoComConsultasStrategy;
import br.com.medflow.dominio.administracao.funcionarios.FuncionarioId;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementação do DisponibilidadeDataSource para buscar horários de disponibilidade.
 *
 * Esta classe busca os horários em que o médico está disponível para atendimento.
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

        // Busca médico com disponibilidades carregadas (evita N+1)
        Optional<MedicoJpa> medicoOpt = medicoJpaRepository.findByIdComDisponibilidades(idInt);

        if (medicoOpt.isEmpty()) {
            return List.of();
        }

        MedicoJpa medico = medicoOpt.get();

        // Converte DisponibilidadeJpa para HorarioInfo
        return medico.getDisponibilidades().stream()
                .map(d -> new HorarioInfo(
                        d.getDiaSemana(),
                        d.getHoraInicio().toString(), // "08:00"
                        d.getHoraFim().toString()      // "12:00"
                ))
                .collect(Collectors.toList());
    }
}

