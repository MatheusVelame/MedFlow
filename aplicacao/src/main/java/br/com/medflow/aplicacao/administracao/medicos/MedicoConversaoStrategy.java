// Localização: aplicacao/src/main/java/br/com/medflow/aplicacao/administracao/medicos/MedicoConversaoStrategy.java

package br.com.medflow.aplicacao.administracao.medicos;

import br.com.medflow.dominio.administracao.funcionarios.Medico;

/**
 * PADRÃO STRATEGY: Interface para estratégias de conversão de Médico.
 *
 * Define como converter a entidade de domínio Médico em diferentes DTOs
 * de aplicação (MedicoResumo e MedicoDetalhes).
 *
 * Diferentes implementações podem:
 * - Buscar dados de consultas de diferentes fontes
 * - Incluir/excluir informações baseado no contexto
 * - Aplicar transformações específicas nos dados
 */
public interface MedicoConversaoStrategy {

    /**
     * Converte um Médico de domínio em um DTO de resumo para listagem.
     * @param medico A entidade de domínio
     * @return DTO de resumo otimizado para cards/listagens
     */
    MedicoResumo converterParaResumo(Medico medico);

    /**
     * Converte um Médico de domínio em um DTO de detalhes completo.
     * @param medico A entidade de domínio
     * @return DTO com todos os detalhes incluindo histórico e disponibilidade
     */
    MedicoDetalhes converterParaDetalhes(Medico medico);
}