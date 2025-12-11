package br.com.medflow.aplicacao.financeiro.faturamentos.usecase;

import br.com.medflow.dominio.financeiro.faturamentos.FaturamentoId;
import br.com.medflow.dominio.financeiro.faturamentos.FaturamentoServico;
import br.com.medflow.dominio.financeiro.faturamentos.UsuarioResponsavelId;
import static org.apache.commons.lang3.Validate.notNull;

/**
 * UseCase de comando para marcar um faturamento como pago.
 */
public class MarcarComoPagoUseCase {

    private final FaturamentoServico faturamentoServico;

    public MarcarComoPagoUseCase(FaturamentoServico faturamentoServico) {
        notNull(faturamentoServico, "O serviço de faturamento não pode ser nulo");
        this.faturamentoServico = faturamentoServico;
    }

    public void executar(String faturamentoId, String usuarioResponsavelId) {
        notNull(faturamentoId, "O ID do faturamento não pode ser nulo");
        notNull(usuarioResponsavelId, "O ID do usuário responsável não pode ser nulo");

        FaturamentoId id = new FaturamentoId(faturamentoId);
        UsuarioResponsavelId usuarioId = new UsuarioResponsavelId(usuarioResponsavelId);

        faturamentoServico.marcarComoPago(id, usuarioId);
    }
}
