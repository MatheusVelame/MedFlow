package br.com.medflow.aplicacao.prontuario;

import java.util.List;
import java.util.Optional;

/**
 * Interface (Porta) para acesso de dados de LEITURA (Queries) na camada de Aplicação.
 * Será implementada pela camada de Infraestrutura (o Adapter).
 */
public interface ProntuarioRepositorioAplicacao {

    /**
     * Pesquisa e retorna um resumo de todos os prontuários.
     * @return Lista de ProntuarioResumo.
     */
    List<ProntuarioResumo> pesquisarResumos();

    /**
     * Obtém os detalhes completos de um prontuário específico.
     * @param id O ID do prontuário.
     * @return Optional contendo ProntuarioDetalhes ou vazio se não encontrado.
     */
    Optional<ProntuarioDetalhes> obterDetalhesPorId(String id);

    /**
     * Lista o histórico clínico de um prontuário.
     * @param prontuarioId O ID do prontuário.
     * @return Lista de HistoricoItemResponse.
     */
    List<HistoricoItemResponse> listarHistoricoClinico(String prontuarioId);

    /**
     * Verifica se existe prontuário por ID do médico.
     * @param medicoId O ID do médico.
     * @return true se existe, false caso contrário.
     */
    boolean existePorMedicoId(Integer medicoId);

    /**
     * Busca prontuários por paciente.
     * @param pacienteId O ID do paciente.
     * @return Lista de ProntuarioResumo do paciente.
     */
    List<ProntuarioResumo> buscarPorPacienteAplicacao(String pacienteId);

    /**
     * Lista o histórico de atualizações de um prontuário.
     * @param prontuarioId O ID do prontuário.
     * @return Lista de AtualizacaoItemResponse.
     */
    List<AtualizacaoItemResponse> listarHistoricoAtualizacoes(String prontuarioId);
}
