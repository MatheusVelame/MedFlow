package br.com.medflow.infraestrutura.persistencia.jpa.financeiro;

import br.com.medflow.dominio.financeiro.faturamentos.Faturamento;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Decorator para adicionar auditoria às operações do repositório de Faturamento.
 */
public class FaturamentoRepositorioAuditoriaDecorator extends FaturamentoRepositorioDecorator {

    private static final Logger logger = LoggerFactory.getLogger(FaturamentoRepositorioAuditoriaDecorator.class);

    public FaturamentoRepositorioAuditoriaDecorator(FaturamentoRepositorioBase repositorio) {
        super(repositorio);
    }

    @Override
    public void salvar(Faturamento faturamento) {
        logger.info("AUDITORIA: Salvando faturamento - id={}, pacienteId={}, status={}, valor={}", 
                faturamento.getId() != null ? faturamento.getId().getValor() : "NOVO",
                faturamento.getPacienteId().getValor(),
                faturamento.getStatus(),
                faturamento.getValor().getValor());
        try {
            repositorio.salvar(faturamento);
            logger.info("AUDITORIA: Faturamento salvo com sucesso - id={}", 
                    faturamento.getId() != null ? faturamento.getId().getValor() : "NOVO");
        } catch (Exception e) {
            logger.error("AUDITORIA: Erro ao salvar faturamento - pacienteId={}", 
                    faturamento.getPacienteId().getValor(), e);
            throw e;
        }
    }
}
