// Localização: aplicacao/src/main/java/br/com/medflow/aplicacao/administracao/medicos/DisponibilidadeGestor.java

package br.com.medflow.aplicacao.administracao.medicos;

import br.com.medflow.dominio.administracao.funcionarios.FuncionarioId;
import java.util.List;

/**
 * Interface (Porta) para gerenciar disponibilidades de médicos.
 *
 * Esta interface será implementada na camada de Infraestrutura,
 * permitindo que a camada de Aplicação salve/atualize disponibilidades
 * sem conhecer detalhes de implementação.
 */
public interface DisponibilidadeGestor {

    /**
     * Salva disponibilidades de um médico.
     * @param medicoId ID do médico
     * @param disponibilidades Lista de disponibilidades a salvar
     */
    void salvarDisponibilidades(FuncionarioId medicoId, List<DisponibilidadeRequest> disponibilidades);

    /**
     * Atualiza disponibilidades de um médico (remove antigas e adiciona novas).
     * @param medicoId ID do médico
     * @param disponibilidades Lista de novas disponibilidades
     */
    void atualizarDisponibilidades(FuncionarioId medicoId, List<DisponibilidadeRequest> disponibilidades);

    /**
     * DTO para representar uma disponibilidade.
     */
    record DisponibilidadeRequest(
            String diaSemana,
            String horaInicio,
            String horaFim
    ) {}
}