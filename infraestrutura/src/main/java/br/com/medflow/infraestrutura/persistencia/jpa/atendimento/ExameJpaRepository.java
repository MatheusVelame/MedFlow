package br.com.medflow.infraestrutura.persistencia.jpa.atendimento;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ExameJpaRepository extends JpaRepository<ExameJpa, Long> {

    // Query customizada para verificar conflitos (RN4 e RN10)
    // Verifica se existe exame para o mesmo paciente, no mesmo horário, que NÃO esteja cancelado.
    // O parâmetro 'idIgnorado' é usado na atualização para não conflitar com o próprio exame.
    @Query("SELECT e FROM ExameJpa e " +
           "WHERE e.pacienteId = :pacienteId " +
           "AND e.dataHora = :dataHora " +
           "AND e.status <> 'CANCELADO' " +
           "AND (:idIgnorado IS NULL OR e.id <> :idIgnorado)")
    List<ExameJpa> encontrarConflitos(@Param("pacienteId") Long pacienteId, 
                                      @Param("dataHora") LocalDateTime dataHora, 
                                      @Param("idIgnorado") Long idIgnorado);

    // Query para verificar conflitos por médico (evita dupla reserva de mesmo médico no mesmo horário)
    @Query("SELECT e FROM ExameJpa e " +
           "WHERE e.medicoId = :medicoId " +
           "AND e.dataHora = :dataHora " +
           "AND e.status <> 'CANCELADO' " +
           "AND (:idIgnorado IS NULL OR e.id <> :idIgnorado)")
    List<ExameJpa> encontrarConflitosPorMedico(@Param("medicoId") Long medicoId,
                                               @Param("dataHora") LocalDateTime dataHora,
                                               @Param("idIgnorado") Long idIgnorado);

    boolean existsByMedicoId(Long medicoId);

    
    boolean existsByPacienteId(Long pacienteId);

}