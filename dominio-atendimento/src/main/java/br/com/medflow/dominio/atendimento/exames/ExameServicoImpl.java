package br.com.medflow.dominio.atendimento.exames;

import java.time.LocalDateTime;
import static org.apache.commons.lang3.Validate.notNull;

import br.com.medflow.dominio.evento.EventoBarramento;

/**
 * RealSubject: Implementação real do serviço de domínio de Exames.
 * Contém toda a lógica de negócio e validações (RNs).
 */
public class ExameServicoImpl implements IExameServico {

    private final ExameRepositorio repositorio;
    private final VerificadorExternoServico verificadorExterno;
    private final EventoBarramento eventoBarramento;

    public ExameServicoImpl(ExameRepositorio repositorio, VerificadorExternoServico verificadorExterno, EventoBarramento eventoBarramento) {
        this.repositorio = notNull(repositorio, "O repositório é obrigatório.");
        this.verificadorExterno = notNull(verificadorExterno, "O verificador externo é obrigatório.");
        this.eventoBarramento = notNull(eventoBarramento, "O barramento de eventos é obrigatório.");
    }

    @Override
    public Exame agendarExame(Long pacienteId, Long medicoId, String tipoExame, LocalDateTime dataHora, UsuarioResponsavelId responsavel) {
        
        // RN3 - Data obrigatória
        if (dataHora == null) {
            throw new ValidacaoNegocioException("DATA_HORA_OBRIGATORIA: Data e horário do exame são obrigatórios.");
        }
        
        // RN7 - Não agendar em passado
        if (dataHora.isBefore(LocalDateTime.now().minusMinutes(1))) {
            throw new ValidacaoNegocioException("DATA_PASSADA: Não é permitido agendar exames para datas passadas.");
        }
        
        notNull(tipoExame, "O tipo de exame é obrigatório.");

        // RN1.2 - Paciente
        if (!verificadorExterno.pacienteEstaCadastrado(pacienteId)) {
            throw new EntidadeNaoEncontradaException("PACIENTE_NAO_ENCONTRADO: Paciente não cadastrado no sistema.");
        }
        
        // RN1.3 - Medico existe
        if (!verificadorExterno.medicoEstaCadastrado(medicoId)) {
            throw new EntidadeNaoEncontradaException("MEDICO_NAO_ENCONTRADO: Médico não cadastrado no sistema.");
        }
        
        // RN5 - Médico ativo
        if (!verificadorExterno.medicoEstaAtivo(medicoId)) {
            throw new ConflitoNegocioException("MEDICO_INATIVO: Médico vinculado ao exame deve estar ativo no sistema.");
        }
        // RN2.2 - tipo exame
        if (!verificadorExterno.tipoExameEstaCadastrado(tipoExame)) {
            throw new EntidadeNaoEncontradaException("TIPO_EXAME_NAO_ENCONTRADO: Tipo de exame não cadastrado no sistema.");
        }
        // RN4 - conflito paciente
        if (repositorio.obterAgendamentoConflitante(pacienteId, dataHora, null).isPresent()) {
            throw new ConflitoNegocioException("HORARIO_CONFLITO: Paciente já possui um exame agendado neste horário.");
        }
        // RN6 - indisponibilidade medico
        if (!verificadorExterno.medicoEstaDisponivel(medicoId, dataHora)) {
            throw new ConflitoNegocioException("MEDICO_INDISPONIVEL: Não é permitido agendar exame em horário de indisponibilidade do médico.");
        }
        // RN6.1 - Não permitir dois exames com o mesmo médico no mesmo horário
        if (repositorio.obterAgendamentoConflitantePorMedico(medicoId, dataHora, null).isPresent()) {
            throw new ConflitoNegocioException("MEDICO_HORARIO_CONFLITO: Médico já possui outro exame agendado neste horário.");
        }
        
        Exame novoExame = new Exame(pacienteId, medicoId, tipoExame, dataHora, responsavel);
        // Garantir explicitamente que o status inicial seja AGENDADO (proteção extra contra inconsistências)
        novoExame.setStatus(StatusExame.AGENDADO);
        Exame exameSalvo = repositorio.salvar(novoExame);
        
        eventoBarramento.postar(new ExameAgendadoEvent(exameSalvo)); 
        
        return exameSalvo;
    }

    @Override
    public Exame atualizarAgendamento(ExameId exameId, Long novoMedicoId, String novoTipoExame, LocalDateTime novaDataHora, UsuarioResponsavelId responsavel, String observacoes) {
        
        Exame exame = repositorio.obterPorId(exameId)
            .orElseThrow(() -> new EntidadeNaoEncontradaException("AGENDAMENTO_NAO_ENCONTRADO: Agendamento de exame não encontrado"));
            
        // RN9 - Médico ativo
        if (!verificadorExterno.medicoEstaAtivo(novoMedicoId)) {
            throw new ConflitoNegocioException("MEDICO_INATIVO: Médico inativo não pode ser vinculado ao exame.");
        }
        
        // RN10 - Conflito de Horário para paciente
        if (repositorio.obterAgendamentoConflitante(exame.getPacienteId(), novaDataHora, exameId).isPresent()) {
            throw new ConflitoNegocioException("HORARIO_CONFLITO_PACIENTE: A alteração não pode gerar conflito de horário para o paciente.");
        }
        
        // RN10 - Indisponibilidade Médica
        if (!verificadorExterno.medicoEstaDisponivel(novoMedicoId, novaDataHora)) {
            throw new ConflitoNegocioException("MEDICO_INDISPONIVEL: A alteração não pode gerar conflito de horário para o médico.");
        }

        // RN10 - Não permitir dois exames com o mesmo médico no mesmo horário (excluindo o próprio exame)
        if (repositorio.obterAgendamentoConflitantePorMedico(novoMedicoId, novaDataHora, exameId).isPresent()) {
            throw new ConflitoNegocioException("MEDICO_HORARIO_CONFLITO: A alteração não pode gerar conflito de horário para o médico.");
        }

        exame.atualizar(novoMedicoId, novoTipoExame, novaDataHora, responsavel, observacoes);
        
        return repositorio.salvar(exame);
    }
    
    @Override
    public void tentarExcluirAgendamento(ExameId exameId, UsuarioResponsavelId responsavel) {
         Exame exame = repositorio.obterPorId(exameId)
            .orElseThrow(() -> new EntidadeNaoEncontradaException("AGENDAMENTO_NAO_ENCONTRADO: Agendamento de exame não encontrado"));
            
        exame.tentarExcluir(responsavel);
        
        // Se a tentativa de excluir resultar em cancelamento (RN12.2), salva-se o estado de cancelado.
        if (exame.getStatus() == StatusExame.CANCELADO) {
            repositorio.salvar(exame);
        } else {
            // Se o status for diferente de cancelado (significa que passou na RN12), exclui fisicamente.
            repositorio.excluir(exame);
        }
    }
    
    @Override
    public Exame cancelarAgendamento(ExameId exameId, String motivo, UsuarioResponsavelId responsavel) {
         Exame exame = repositorio.obterPorId(exameId)
             .orElseThrow(() -> new EntidadeNaoEncontradaException("AGENDAMENTO_NAO_ENCONTRADO: Agendamento de exame não encontrado"));
            
        if (motivo == null || motivo.trim().isEmpty()) {
            throw new ValidacaoNegocioException("MOTIVO_OBRIGATORIO: É obrigatório informar o motivo do cancelamento");
        }
        
        exame.cancelar(motivo, responsavel);
        
        return repositorio.salvar(exame);
    }

    @Override
    public Exame registrarResultado(ExameId exameId, String descricao, boolean vincularLaudo, boolean vincularProntuario, UsuarioResponsavelId responsavel) {
        Exame exame = repositorio.obterPorId(exameId)
            .orElseThrow(() -> new EntidadeNaoEncontradaException("AGENDAMENTO_NAO_ENCONTRADO: Agendamento de exame não encontrado"));

        // registrar resultado no domínio (validações internas lançam ExcecaoDominio quando aplicável)
        exame.registrarResultado(descricao, vincularLaudo, vincularProntuario, responsavel);

        return repositorio.salvar(exame);
    }
}