package br.com.medflow.infraestrutura.persistencia.jpa.atendimento;

import br.com.medflow.aplicacao.atendimento.consultas.ConsultaResumo;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component // Deve ser um componente Spring para ser injetado no JpaMapeador
public class ConsultaResumoConverter extends AbstractConverter<ConsultaJpa, ConsultaResumo> {
    
    /**
     * Converte a entidade de persistência (Jpa) para o DTO de aplicação (Resumo).
     */
    @Override
    protected ConsultaResumo convert(ConsultaJpa source) {
        if (source == null) {
            return null;
        }
        
        // Chamada manual e explícita ao construtor com todos os argumentos do DTO.
        // ESSA é a única forma de garantir que os campos 'final' sejam preenchidos.
        return new ConsultaResumo(
            source.getId(),
            source.getPacienteNome(),
            source.getMedicoNome(),
            source.getDataHora(),
            source.getStatus()
        );
    }
}