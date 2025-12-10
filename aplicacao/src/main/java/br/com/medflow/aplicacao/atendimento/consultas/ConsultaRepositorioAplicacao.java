package br.com.medflow.aplicacao.atendimento.consultas;

import java.util.List;
import java.util.Optional;

// Repositório de leitura (Queries) otimizado para a Camada de Aplicação
// (Retorna DTOs de leitura, não entidades de domínio)
public interface ConsultaRepositorioAplicacao {

    /**
     * Pesquisa todos os resumos de consultas ativas.
     * @return Uma lista de resumos de consulta.
     */
    List<ConsultaResumo> pesquisarResumos();

    /**
     * Obtém os detalhes completos de uma consulta pelo seu ID.
     * @param id O identificador da consulta.
     * @return Um Optional contendo os detalhes, se encontrado.
     */
    Optional<ConsultaDetalhes> obterDetalhesPorId(Integer id);

    /**
     * Pesquisa consultas resumidas filtrando por um paciente específico.
     * @param pacienteId O ID do paciente.
     * @return Uma lista de resumos de consulta para o paciente.
     */
    List<ConsultaResumo> pesquisarPorPacienteId(Integer pacienteId);
}