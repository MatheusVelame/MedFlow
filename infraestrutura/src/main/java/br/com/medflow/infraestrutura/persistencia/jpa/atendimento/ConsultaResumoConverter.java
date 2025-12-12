package br.com.medflow.infraestrutura.persistencia.jpa.atendimento;

import br.com.medflow.aplicacao.atendimento.consultas.ConsultaResumo;
import br.com.medflow.dominio.atendimento.consultas.StatusConsulta;
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
        
        // ConsultaJpa não possui métodos getPacienteNome() e getMedicoNome()
        // Usamos os IDs para criar nomes temporários (mesma lógica do ConsultaRepositorioImpl)
        String nomePaciente = "Paciente " + source.getPacienteId();
        String nomeMedico = "Médico " + source.getMedicoId();
        
        // Converte String status para StatusConsulta enum
        StatusConsulta status = StatusConsulta.valueOf(source.getStatus());
        
        // Chamada manual e explícita ao construtor com todos os argumentos do DTO.
        // ESSA é a única forma de garantir que os campos 'final' sejam preenchidos.
        return new ConsultaResumo(
            source.getId(),
            source.getDataHora(),
            nomePaciente,
            nomeMedico,
            status
        );
    }
}