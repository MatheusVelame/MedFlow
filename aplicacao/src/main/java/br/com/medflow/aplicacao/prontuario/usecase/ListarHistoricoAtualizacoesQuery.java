package br.com.medflow.aplicacao.prontuario.usecase;

import br.com.medflow.aplicacao.prontuario.AtualizacaoItemResponse;
import br.com.medflow.aplicacao.prontuario.ProntuarioServicoAplicacao;
import static org.apache.commons.lang3.Validate.notNull;

import java.util.List;

/**
 * Query para listar o histórico de atualizações de um prontuário.
 */
public class ListarHistoricoAtualizacoesQuery {

    private final ProntuarioServicoAplicacao servicoAplicacao;

    public ListarHistoricoAtualizacoesQuery(ProntuarioServicoAplicacao servicoAplicacao) {
        notNull(servicoAplicacao, "O serviço de aplicação não pode ser nulo");
        this.servicoAplicacao = servicoAplicacao;
    }

    public List<AtualizacaoItemResponse> executar(String prontuarioId) {
        notNull(prontuarioId, "O ID do prontuário não pode ser nulo");
        return servicoAplicacao.listarHistoricoAtualizacoes(prontuarioId);
    }
}


