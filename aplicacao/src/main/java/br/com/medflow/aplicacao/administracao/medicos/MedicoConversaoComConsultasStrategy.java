package br.com.medflow.aplicacao.administracao.medicos;

import br.com.medflow.dominio.administracao.funcionarios.Medico;
import br.com.medflow.dominio.administracao.funcionarios.FuncionarioId;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * PADRÃO STRATEGY - Implementação Concreta:
 * Estratégia de conversão que inclui dados de consultas do médico.
 */
@Component
public class MedicoConversaoComConsultasStrategy implements MedicoConversaoStrategy {

    private final ConsultaDataSource consultaDataSource;
    private final DisponibilidadeDataSource disponibilidadeDataSource;
    private final EspecialidadeDataSource especialidadeDataSource;

    public MedicoConversaoComConsultasStrategy(
            ConsultaDataSource consultaDataSource,
            DisponibilidadeDataSource disponibilidadeDataSource,
            EspecialidadeDataSource especialidadeDataSource) {

        this.consultaDataSource = consultaDataSource;
        this.disponibilidadeDataSource = disponibilidadeDataSource;
        this.especialidadeDataSource = especialidadeDataSource;
    }

    @Override
    public MedicoResumo converterParaResumo(Medico medico) {
        Integer consultasHoje = consultaDataSource.contarConsultasHoje(medico.getId());
        String proximaConsulta = consultaDataSource.obterProximaConsulta(medico.getId());
        String nomeEspecialidade = especialidadeDataSource.obterNomeEspecialidade(
                medico.getEspecialidade().getId()
        );

        return MedicoResumo.builder()
                .id(medico.getId().getId())
                .nome(medico.getNome())
                .funcao(medico.getFuncao())
                .contato(medico.getContato())
                .status(medico.getStatus())
                .crm(medico.getCrm().toString())
                .especialidade(nomeEspecialidade)
                .consultasHoje(consultasHoje)
                .proximaConsulta(proximaConsulta)
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

        var horariosDisponiveis = disponibilidadeDataSource
                .obterHorariosDisponibilidade(medico.getId())
                .stream()
                .map(h -> new MedicoDetalhes.HorarioDisponibilidade(
                        h.diaSemana(),
                        h.horaInicio(),
                        h.horaFim()
                ))
                .collect(Collectors.toList());

        String nomeEspecialidade = especialidadeDataSource.obterNomeEspecialidade(
                medico.getEspecialidade().getId()
        );

        return new MedicoDetalhes(
                medico.getId().getId(),
                medico.getNome(),
                medico.getFuncao(),
                medico.getContato(),
                medico.getStatus(),
                historicoConvertido,
                medico.getCrm().toString(),
                nomeEspecialidade,
                null, // dataNascimento
                horariosDisponiveis
        );
    }

    /**
     * Interface para acesso a dados de consultas.
     */
    public interface ConsultaDataSource {
        Integer contarConsultasHoje(FuncionarioId medicoId);
        String obterProximaConsulta(FuncionarioId medicoId);
    }

    /**
     * Interface para acesso a dados de disponibilidade.
     */
    public interface DisponibilidadeDataSource {
        java.util.List<HorarioInfo> obterHorariosDisponibilidade(FuncionarioId medicoId);
        record HorarioInfo(String diaSemana, String horaInicio, String horaFim) {}
    }

    /**
     * Interface para acesso a dados de especialidades.
     */
    public interface EspecialidadeDataSource {
        String obterNomeEspecialidade(int especialidadeId);
        boolean especialidadeEstaAtiva(int especialidadeId);
    }
}