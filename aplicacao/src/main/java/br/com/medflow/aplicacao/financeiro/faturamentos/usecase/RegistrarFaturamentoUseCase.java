package br.com.medflow.aplicacao.financeiro.faturamentos.usecase;

import br.com.medflow.dominio.financeiro.faturamentos.*;
import static org.apache.commons.lang3.Validate.notNull;

import java.util.UUID;

/**
 * UseCase de comando para registrar um novo faturamento.
 */
public class RegistrarFaturamentoUseCase {

    private final FaturamentoServico faturamentoServico;

    public RegistrarFaturamentoUseCase(FaturamentoServico faturamentoServico) {
        notNull(faturamentoServico, "O serviço de faturamento não pode ser nulo");
        this.faturamentoServico = faturamentoServico;
    }

    public Faturamento executar(
            String pacienteId,
            TipoProcedimento tipoProcedimento,
            String descricaoProcedimento,
            Valor valor,
            MetodoPagamento metodoPagamento,
            UsuarioResponsavelId usuarioResponsavel,
            String observacoes) {
        
        notNull(pacienteId, "O ID do paciente não pode ser nulo");
        notNull(tipoProcedimento, "O tipo de procedimento não pode ser nulo");
        notNull(descricaoProcedimento, "A descrição do procedimento não pode ser nula");
        notNull(valor, "O valor não pode ser nulo");
        notNull(metodoPagamento, "O método de pagamento não pode ser nulo");
        notNull(usuarioResponsavel, "O usuário responsável não pode ser nulo");

        PacienteId pacienteIdVO = new PacienteId(pacienteId);
        
        return faturamentoServico.registrarFaturamento(
                pacienteIdVO,
                tipoProcedimento,
                descricaoProcedimento,
                valor,
                metodoPagamento,
                usuarioResponsavel,
                observacoes
        );
    }
}
