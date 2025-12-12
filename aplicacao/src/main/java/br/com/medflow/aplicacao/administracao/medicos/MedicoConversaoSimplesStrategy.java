// Localização: aplicacao/src/main/java/br/com/medflow/aplicacao/administracao/medicos/MedicoConversaoSimplesStrategy.java

package br.com.medflow.aplicacao.administracao.medicos;

import br.com.medflow.dominio.administracao.funcionarios.Medico;
import java.util.Collections;
import java.util.stream.Collectors;

/**
 * PADRÃO STRATEGY - Implementação Concreta Alternativa:
 * Estratégia de conversão simplificada que NÃO busca dados de consultas.
 *
 * Útil para:
 * - Listagens rápidas sem overhead de queries extras
 * - Contextos onde dados de consultas não são necessários
 * - Testes unitários
 */
public class MedicoConversaoSimplesStrategy implements MedicoConversaoStrategy {

    @Override
    public MedicoResumo converterParaResumo(Medico medico) {
        return MedicoResumo.builder()
                .id(medico.getId().getId())
                .nome(medico.getNome())
                .funcao(medico.getFuncao())
                .contato(medico.getContato())
                .status(medico.getStatus())
                .crm(medico.getCrm().toString())
                .especialidade(obterNomeEspecialidadeSimples(medico.getEspecialidade().getId()))
                .consultasHoje(null) // Não busca consultas
                .proximaConsulta(null) // Não busca consultas
                .build();
    }

    @Override
    public MedicoDetalhes converterParaDetalhes(Medico medico) {
        var historicoConvertido = medico.getHistorico().stream()
                .map(h -> new MedicoDetalhes.HistoricoDetalhes(
                        h.getAcao().name(),
                        h.getDescricao(),
                        h.getResponsavel().getCodigo(),
                        h.getDataHora()
                ))
                .collect(Collectors.toList());

        return new MedicoDetalhes(
                medico.getId().getId(),
                medico.getNome(),
                medico.getFuncao(),
                medico.getContato(),
                medico.getStatus(),
                historicoConvertido,
                medico.getCrm().toString(),
                obterNomeEspecialidadeSimples(medico.getEspecialidade().getId()),
                null, // dataNascimento
                Collections.emptyList() // horariosDisponiveis
        );
    }

    /**
     * Mapeamento simplificado de especialidades.
     * Na implementação real, isso viria de um repositório.
     */
    private String obterNomeEspecialidadeSimples(int especialidadeId) {
        return switch (especialidadeId) {
            case 1 -> "Cardiologia";
            case 2 -> "Pediatria";
            case 3 -> "Ortopedia";
            case 4 -> "Dermatologia";
            default -> "Especialidade " + especialidadeId;
        };
    }
}