package br.com.medflow.dominio.atendimento.exames;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Interface de Repositório para a entidade Exame (Aggregate Root).
 * Define as operações de persistência necessárias para o domínio.
 */
public interface ExameRepositorio {

    /**
     * Busca um Exame pelo seu identificador único.
     * @param id O identificador do Exame.
     * @return Opcional contendo o Exame, se encontrado.
     */
    Optional<Exame> obterPorId(ExameId id);

    /**
     * Salva um novo Exame ou atualiza um Exame existente.
     * @param exame O Exame a ser persistido.
     * @return O Exame persistido (pode retornar o ID gerado).
     */
    Exame salvar(Exame exame);

    /**
     * Remove um Exame do repositório (exclusão física, se aplicável).
     * @param exame O Exame a ser excluído.
     */
    void excluir(Exame exame);

    /**
     * Verifica se existe um agendamento conflitante para o paciente em uma data/hora específica.
     * Necessário para a RN4 (Agendamento) e RN3 (Atualização).
     * @param pacienteId O ID do paciente.
     * @param dataHora A data e hora a ser verificada.
     * @param idExcluido O ID do exame a ser ignorado na busca (útil em atualizações). Pode ser null em novos agendamentos.
     * @return Opcional contendo o Exame conflitante, se encontrado.
     */
    Optional<Exame> obterAgendamentoConflitante(Long pacienteId, LocalDateTime dataHora, ExameId idExcluido);

}