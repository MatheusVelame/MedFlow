package br.com.medflow.aplicacao.atendimento.consultas;

import static org.apache.commons.lang3.Validate.notNull;

import java.util.List;

// Serviço de Aplicação para as consultas (Queries)
public class ConsultaServicoAplicacao {

    private final ConsultaRepositorioAplicacao repositorio;

    public ConsultaServicoAplicacao(ConsultaRepositorioAplicacao repositorio) {
        // Validação idêntica ao MedicamentoServicoAplicacao
        notNull(repositorio, "O repositório de consulta não pode ser nulo"); 

        this.repositorio = repositorio;
    }

    /**
     * Lista todas as consultas resumidas, delegando ao repositório de aplicação.
     * @return Lista de ConsultaResumo.
     */
    public List<ConsultaResumo> pesquisarResumos() {
        return repositorio.pesquisarResumos();
    }

    /**
     * Obtém os detalhes de uma consulta, lançando exceção se não for encontrada.
     * @param id O ID da consulta.
     * @return ConsultaDetalhes.
     */
    public ConsultaDetalhes obterDetalhes(Integer id) {
        return repositorio.obterDetalhesPorId(id)
            .orElseThrow(() -> new RuntimeException("Consulta não encontrada: " + id));
    }

    /**
     * Lista consultas por ID de paciente, delegando ao repositório de aplicação.
     * @param pacienteId O ID do paciente.
     * @return Lista de ConsultaResumo.
     */
    public List<ConsultaResumo> pesquisarConsultasPorPaciente(Integer pacienteId) {
        notNull(pacienteId, "O ID do paciente não pode ser nulo para a pesquisa.");
        return repositorio.pesquisarPorPacienteId(pacienteId);
    }
}