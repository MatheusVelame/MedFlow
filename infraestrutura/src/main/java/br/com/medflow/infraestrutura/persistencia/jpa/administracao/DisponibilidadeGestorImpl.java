// Localização: infraestrutura/src/main/java/br/com/medflow/infraestrutura/persistencia/jpa/administracao/DisponibilidadeGestorImpl.java

package br.com.medflow.infraestrutura.persistencia.jpa.administracao;

import br.com.medflow.aplicacao.administracao.medicos.DisponibilidadeGestor;
import br.com.medflow.dominio.administracao.funcionarios.FuncionarioId;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementação do DisponibilidadeGestor usando JPA.
 */
@Component
public class DisponibilidadeGestorImpl implements DisponibilidadeGestor {

    private final MedicoJpaRepository medicoJpaRepository;

    public DisponibilidadeGestorImpl(MedicoJpaRepository medicoJpaRepository) {
        this.medicoJpaRepository = medicoJpaRepository;
    }

    @Override
    @Transactional
    public void salvarDisponibilidades(FuncionarioId medicoId, List<DisponibilidadeRequest> disponibilidades) {
        Integer idInt = Integer.parseInt(medicoId.getId());

        // Busca o MedicoJpa
        MedicoJpa medicoJpa = medicoJpaRepository.findById(idInt)
                .orElseThrow(() -> new RuntimeException("Médico não encontrado para salvar disponibilidades"));

        // Converte e adiciona disponibilidades
        List<DisponibilidadeJpa> disponibilidadesJpa = disponibilidades.stream()
                .map(d -> {
                    DisponibilidadeJpa disp = new DisponibilidadeJpa();
                    disp.setMedico(medicoJpa);
                    disp.setDiaSemana(d.diaSemana());
                    disp.setHoraInicio(LocalTime.parse(d.horaInicio()));
                    disp.setHoraFim(LocalTime.parse(d.horaFim()));
                    return disp;
                })
                .collect(Collectors.toList());

        medicoJpa.setDisponibilidades(disponibilidadesJpa);
        medicoJpaRepository.save(medicoJpa);
    }

    @Override
    @Transactional
    public void atualizarDisponibilidades(FuncionarioId medicoId, List<DisponibilidadeGestor.DisponibilidadeRequest> disponibilidades) {
        Integer idInt = Integer.parseInt(medicoId.getId());

        // Busca o MedicoJpa
        MedicoJpa medicoJpa = medicoJpaRepository.findById(idInt)
                .orElseThrow(() -> new RuntimeException("Médico não encontrado para atualizar disponibilidades"));

        // Remove disponibilidades antigas
        medicoJpa.getDisponibilidades().clear();

        // Adiciona novas disponibilidades
        List<DisponibilidadeJpa> disponibilidadesJpa = disponibilidades.stream()
                .map(d -> {
                    DisponibilidadeJpa disp = new DisponibilidadeJpa();
                    disp.setMedico(medicoJpa);
                    disp.setDiaSemana(d.diaSemana());
                    disp.setHoraInicio(LocalTime.parse(d.horaInicio()));
                    disp.setHoraFim(LocalTime.parse(d.horaFim()));
                    return disp;
                })
                .collect(Collectors.toList());

        medicoJpa.getDisponibilidades().addAll(disponibilidadesJpa);
        medicoJpaRepository.save(medicoJpa);
    }
}