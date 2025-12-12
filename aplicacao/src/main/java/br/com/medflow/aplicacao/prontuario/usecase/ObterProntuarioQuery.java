package br.com.medflow.aplicacao.prontuario.usecase;

import br.com.medflow.aplicacao.prontuario.ProntuarioServicoAplicacao;
import br.com.medflow.aplicacao.prontuario.dto.response.ProntuarioResponse;
import static org.apache.commons.lang3.Validate.notNull;

/**
 * Query para obter um prontuário completo.
 */
public class ObterProntuarioQuery {

    private final ProntuarioServicoAplicacao servicoAplicacao;

    public ObterProntuarioQuery(ProntuarioServicoAplicacao servicoAplicacao) {
        notNull(servicoAplicacao, "O serviço de aplicação não pode ser nulo");
        this.servicoAplicacao = servicoAplicacao;
    }

    public ProntuarioResponse executar(String id) {
        notNull(id, "O ID do prontuário não pode ser nulo");
        
        var detalhes = servicoAplicacao.obterDetalhes(id);
        
        return new ProntuarioResponse(
            detalhes.getId(),
            detalhes.getPacienteId(),
            detalhes.getAtendimentoId(),
            detalhes.getStatus(),
            detalhes.getDataHoraCriacao(),
            detalhes.getProfissionalResponsavel(),
            detalhes.getObservacoesIniciais()
        );
    }
}
