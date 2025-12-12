package br.com.medflow.aplicacao.prontuario.usecase;

import br.com.medflow.aplicacao.prontuario.HistoricoItemResponse;
import br.com.medflow.aplicacao.prontuario.ProntuarioServicoAplicacao;
import static org.apache.commons.lang3.Validate.notNull;

import java.util.List;

/**
 * Query para listar o histórico clínico de um prontuário.
 */
public class ListarHistoricoQuery {

    private final ProntuarioServicoAplicacao servicoAplicacao;

    public ListarHistoricoQuery(ProntuarioServicoAplicacao servicoAplicacao) {
        notNull(servicoAplicacao, "O serviço de aplicação não pode ser nulo");
        this.servicoAplicacao = servicoAplicacao;
    }

    public List<HistoricoItemResponse> executar(String prontuarioId) {
        notNull(prontuarioId, "O ID do prontuário não pode ser nulo");
        return servicoAplicacao.listarHistoricoClinico(prontuarioId);
    }
}
