// Localização: aplicacao/src/main/java/br/com/medflow/aplicacao/atendimento/consultas/ConsultaRepositorioAplicacao.java

package br.com.medflow.aplicacao.atendimento.consultas;

import java.util.List;
import java.util.Optional;

import br.com.medflow.dominio.atendimento.consultas.StatusConsulta;

/**
 * Interface (Porta) para acesso de dados de LEITURA (Queries) na camada de Aplicação.
 * Deve ser implementada pela camada de Infraestrutura (o Adapter).
 */
public interface ConsultaRepositorioAplicacao {

    /**
     * Pesquisa e retorna um resumo de todas as consultas.
     * @return Lista de ConsultaResumo.
     */
    List<ConsultaResumo> pesquisarResumos();

    /**
     * Obtém os detalhes completos de uma consulta específica.
     * @param id O ID numérico da consulta.
     * @return Optional contendo ConsultaDetalhes ou vazio se não encontrada.
     */
    Optional<ConsultaDetalhes> obterDetalhesPorId(Integer id);

    /**
     * Pesquisa consultas filtrando por um status específico.
     * @param status O status pelo qual filtrar.
     * @return Lista de ConsultaResumo.
     */
    List<ConsultaResumo> pesquisarPorStatus(StatusConsulta status);

    boolean existePorMedicoId(Integer medicoId);
}