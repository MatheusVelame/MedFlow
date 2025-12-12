package br.com.medflow.infraestrutura.persistencia.jpa.administracao;

import br.com.medflow.aplicacao.administracao.medicos.MedicoConversaoComConsultasStrategy;
import br.com.medflow.dominio.administracao.funcionarios.FuncionarioId;
import org.springframework.stereotype.Component;

/**
 * Implementação do ConsultaDataSource para buscar dados de consultas.
 *
 * Esta classe busca informações de consultas para enriquecer os DTOs de Médico.
 * Por ora, retorna dados mockados. Você deve implementar queries reais quando
 * tiver a entidade Consulta no banco.
 */
@Component
public class ConsultaDataSourceImpl
        implements MedicoConversaoComConsultasStrategy.ConsultaDataSource {

    // TODO: Injetar ConsultaJpaRepository quando estiver implementado
    // private final ConsultaJpaRepository consultaRepository;
    //
    // public ConsultaDataSourceImpl(ConsultaJpaRepository consultaRepository) {
    //     this.consultaRepository = consultaRepository;
    // }

    @Override
    public Integer contarConsultasHoje(FuncionarioId medicoId) {
        // TODO: Implementar query real
        // LocalDate hoje = LocalDate.now();
        // return consultaRepository.countByMedicoIdAndData(
        //     Integer.parseInt(medicoId.getId()),
        //     hoje
        // );

        // Por ora, retorna mock
        return 0;
    }

    @Override
    public String obterProximaConsulta(FuncionarioId medicoId) {
        // TODO: Implementar query real
        // Optional<Consulta> proximaOpt = consultaRepository
        //     .findProximaConsultaDoMedico(
        //         Integer.parseInt(medicoId.getId()),
        //         LocalDateTime.now()
        //     );
        //
        // return proximaOpt
        //     .map(c -> c.getDataHora().format(DateTimeFormatter.ofPattern("HH:mm")))
        //     .orElse(null);

        // Por ora, retorna null
        return null;
    }

    /*
     * Queries SQL que você precisará implementar no ConsultaJpaRepository:
     *
     * @Query("""
     *     SELECT COUNT(c) FROM Consulta c
     *     WHERE c.medicoId = :medicoId
     *     AND DATE(c.dataHora) = :data
     *     AND c.status != 'CANCELADA'
     *     """)
     * Long countByMedicoIdAndData(
     *     @Param("medicoId") Integer medicoId,
     *     @Param("data") LocalDate data
     * );
     *
     * @Query("""
     *     SELECT c FROM Consulta c
     *     WHERE c.medicoId = :medicoId
     *     AND c.dataHora > :agora
     *     AND c.status = 'AGENDADA'
     *     ORDER BY c.dataHora ASC
     *     """)
     * Optional<Consulta> findProximaConsultaDoMedico(
     *     @Param("medicoId") Integer medicoId,
     *     @Param("agora") LocalDateTime agora
     * );
     */
}
