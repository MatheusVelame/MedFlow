package br.com.medflow.dominio.atendimento.exames;

import java.time.LocalDateTime;
import static org.apache.commons.lang3.Validate.notNull;

// Importações dos pacotes de eventos compartilhados (conforme padrão dominio-catalogo)
import br.com.medflow.dominio.evento.EventoBarramento;

/**
 * Serviço de Domínio para orquestrar as regras de negócio de Exames.
 */
public class ExameServico {

    private final ExameRepositorio repositorio;
    private final VerificadorExternoServico verificadorExterno;
    private final EventoBarramento eventoBarramento;

    public ExameServico(ExameRepositorio repositorio, VerificadorExternoServico verificadorExterno, EventoBarramento eventoBarramento) {
        this.repositorio = notNull(repositorio, "O repositório é obrigatório.");
        this.verificadorExterno = notNull(verificadorExterno, "O verificador externo é obrigatório.");
        this.eventoBarramento = notNull(eventoBarramento, "O barramento de eventos é obrigatório.");
    }

    public Exame agendarExame(Long pacienteId, Long medicoId, String tipoExame, LocalDateTime dataHora, UsuarioResponsavelId responsavel) {
        
        // RN3
        if (dataHora == null) {
            throw new ExcecaoDominio("Data e horário do exame são obrigatórios.");
        }
        
        // RN7
        if (dataHora.isBefore(LocalDateTime.now().minusMinutes(1))) {
            throw new ExcecaoDominio("Não é permitido agendar exames para datas passadas.");
        }
        
        notNull(tipoExame, "O tipo de exame é obrigatório.");

        // RN1.2 - Paciente
        if (!verificadorExterno.pacienteEstaCadastrado(pacienteId)) {
            throw new ExcecaoDominio("Paciente não cadastrado no sistema.");
        }
        
        // ***** CORREÇÃO PARA RN 1.3 (Médico não cadastrado) *****
        if (!verificadorExterno.medicoEstaCadastrado(medicoId)) {
            throw new ExcecaoDominio("Médico não cadastrado no sistema.");
        }
        
        // RN5 - Médico ativo
        if (!verificadorExterno.medicoEstaAtivo(medicoId)) {
            throw new ExcecaoDominio("Médico vinculado ao exame deve estar ativo no sistema.");
        }
        // RN2.2
        if (!verificadorExterno.tipoExameEstaCadastrado(tipoExame)) {
            throw new ExcecaoDominio("Tipo de exame não cadastrado no sistema.");
        }
        // RN4
        if (repositorio.obterAgendamentoConflitante(pacienteId, dataHora, null).isPresent()) {
            throw new ExcecaoDominio("Paciente já possui um exame agendado neste horário.");
        }
        // RN6
        if (!verificadorExterno.medicoEstaDisponivel(medicoId, dataHora)) {
            throw new ExcecaoDominio("Não é permitido agendar exame em horário de indisponibilidade do médico.");
        }
        
        Exame novoExame = new Exame(pacienteId, medicoId, tipoExame, dataHora, responsavel);
        Exame exameSalvo = repositorio.salvar(novoExame);
        
        eventoBarramento.postar(new ExameAgendadoEvent(exameSalvo)); 
        
        return exameSalvo;
    }

    public Exame atualizarAgendamento(ExameId exameId, Long novoMedicoId, String novoTipoExame, LocalDateTime novaDataHora, UsuarioResponsavelId responsavel) {
        
        Exame exame = repositorio.obterPorId(exameId)
            .orElseThrow(() -> new ExcecaoDominio("Agendamento de exame não encontrado"));
            
        // RN9 (Inativo)
        if (!verificadorExterno.medicoEstaAtivo(novoMedicoId)) {
            throw new ExcecaoDominio("Médico inativo não pode ser vinculado ao exame.");
        }
        
        // RN10 (Conflito de Horário)
        if (repositorio.obterAgendamentoConflitante(exame.getPacienteId(), novaDataHora, exameId).isPresent()) {
            throw new ExcecaoDominio("A alteração não pode gerar conflito de horário para o paciente.");
        }
        
        // RN10 (Indisponibilidade Médica) - Corrigi a mensagem de erro no serviço para ser mais explícita
        if (!verificadorExterno.medicoEstaDisponivel(novoMedicoId, novaDataHora)) {
            throw new ExcecaoDominio("A alteração não pode gerar conflito de horário para o médico.");
        }
        
        // Simulação da RN9 (Paciente não pode ser alterado - validado no domínio por ser 'final')
        // O serviço aqui assume que o pacienteId não está sendo passado.
        // Se a chamada ao serviço do Step tentar alterar o paciente, a lógica abaixo irá barrar via assert.

        exame.atualizar(novoMedicoId, novoTipoExame, novaDataHora, responsavel);
        
        return repositorio.salvar(exame);
    }
    
    public void tentarExcluirAgendamento(ExameId exameId, UsuarioResponsavelId responsavel) {
         Exame exame = repositorio.obterPorId(exameId)
            .orElseThrow(() -> new ExcecaoDominio("Agendamento de exame não encontrado"));
            
        exame.tentarExcluir(responsavel);
        
        // Se a tentativa de excluir resultar em cancelamento (RN12.2), salva-se o estado de cancelado.
        if (exame.getStatus() == StatusExame.CANCELADO) {
            repositorio.salvar(exame);
        } else {
            // Se o status for diferente de cancelado (significa que passou na RN12), exclui fisicamente.
            repositorio.excluir(exame);
        }
    }
    
    public Exame cancelarAgendamento(ExameId exameId, String motivo, UsuarioResponsavelId responsavel) {
        Exame exame = repositorio.obterPorId(exameId)
            .orElseThrow(() -> new ExcecaoDominio("Agendamento de exame não encontrado"));
            
        if (motivo == null || motivo.trim().isEmpty()) {
            throw new ExcecaoDominio("É obrigatório informar o motivo do cancelamento");
        }
        
        exame.cancelar(motivo, responsavel);
        
        return repositorio.salvar(exame);
    }
}