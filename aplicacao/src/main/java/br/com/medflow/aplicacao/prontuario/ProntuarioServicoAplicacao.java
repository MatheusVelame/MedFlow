package br.com.medflow.aplicacao.prontuario;

import static org.apache.commons.lang3.Validate.notNull;

import java.util.List;

/**
 * Serviço de Aplicação (Use Case Handler) para Prontuários,
 * focado em operações de LEITURA (Queries).
 */
public class ProntuarioServicoAplicacao {

    private final ProntuarioRepositorioAplicacao repositorio;

    public ProntuarioServicoAplicacao(ProntuarioRepositorioAplicacao repositorio) {
        notNull(repositorio, "O repositório não pode ser nulo"); 

        this.repositorio = repositorio;
    }

    public List<ProntuarioResumo> pesquisarResumos() {
        return repositorio.pesquisarResumos();
    }

    public ProntuarioDetalhes obterDetalhes(String id) {
        return repositorio.obterDetalhesPorId(id)
            .orElseThrow(() -> new RuntimeException("Prontuário não encontrado"));
    }

    public List<HistoricoItemResponse> listarHistoricoClinico(String prontuarioId) {
        return repositorio.listarHistoricoClinico(prontuarioId);
    }
}
