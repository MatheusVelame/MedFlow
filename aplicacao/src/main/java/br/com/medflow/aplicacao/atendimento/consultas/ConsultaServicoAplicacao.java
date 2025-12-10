// Localização: aplicacao/src/main/java/br/com/medflow/aplicacao/atendimento/consultas/ConsultaServicoAplicacao.java

package br.com.medflow.aplicacao.atendimento.consultas;

import static org.apache.commons.lang3.Validate.notNull;

import java.util.List;

import br.com.medflow.dominio.atendimento.consultas.StatusConsulta;

/**
 * Serviço de Aplicação (Use Case Handler) para Consultas,
 * focado em operações de LEITURA (Queries).
 */
public class ConsultaServicoAplicacao {

    // Dependência da Interface de Repositório de Aplicação (Porta)
    private final ConsultaRepositorioAplicacao repositorio;

    public ConsultaServicoAplicacao(ConsultaRepositorioAplicacao repositorio) {
        // Validação seguindo o padrão do projeto
        notNull(repositorio, "O repositório de consulta não pode ser nulo"); 
        this.repositorio = repositorio;
    }

    /**
     * Retorna a lista de todas as consultas para exibição em uma lista principal.
     */
    public List<ConsultaResumo> pesquisarResumos() {
        return repositorio.pesquisarResumos();
    }

    /**
     * Obtém os detalhes de uma consulta, lançando exceção se não for encontrada.
     * @param id O ID da consulta.
     * @return Os detalhes da consulta.
     * @throws RuntimeException se a consulta não for encontrada.
     */
    public ConsultaDetalhes obterDetalhes(Integer id) {
        return repositorio.obterDetalhesPorId(id)
            .orElseThrow(() -> new RuntimeException("Consulta não encontrada"));
    }

    /**
     * Retorna apenas as consultas que estão com status agendado.
     */
    public List<ConsultaResumo> pesquisarConsultasAgendadas() {
        // Usa o StatusConsulta do Domínio para filtrar
        return repositorio.pesquisarPorStatus(StatusConsulta.AGENDADA); 
    }
}