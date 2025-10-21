package br.com.medflow.dominio.atendimento.exames;

import java.time.LocalDateTime;
import static org.apache.commons.lang3.Validate.notNull;

// Importações dos pacotes de eventos compartilhados (conforme padrão dominio-catalogo)
import br.com.medflow.dominio.evento.EventoBarramento;

/**
 * Interface que simula a comunicação com o domínio Compartilhado/Administracao/Catalogo
 * Esta interface permanece aqui para desacoplamento do serviço.
 */
interface VerificadorExternoServico {
    boolean pacienteEstaCadastrado(Long pacienteId);
    boolean medicoEstaCadastrado(Long medicoId);
    boolean medicoEstaAtivo(Long medicoId);
    boolean tipoExameEstaCadastrado(String tipoExame);
    boolean medicoEstaDisponivel(Long medicoId, LocalDateTime dataHora);
}


/**
 * Serviço de Domínio para orquestrar as regras de negócio de Exames.
 */
public class ExameServico {

    private final ExameRepositorio repositorio;
    private final VerificadorExternoServico verificadorExterno;
    private final EventoBarramento eventoBarramento; // Agora importado do pacote genérico

    public ExameServico(ExameRepositorio repositorio, VerificadorExternoServico verificadorExterno, EventoBarramento eventoBarramento) {
        this.repositorio = notNull(repositorio, "O repositório é obrigatório.");
        this.verificadorExterno = notNull(verificadorExterno, "O verificador externo é obrigatório.");
        this.eventoBarramento = notNull(eventoBarramento, "O barramento de eventos é obrigatório.");
    }

    public Exame agendarExame(Long pacienteId, Long medicoId, String tipoExame, LocalDateTime dataHora, UsuarioResponsavelId responsavel) {
        
        // --- [VALIDAÇÕES DE RN1, RN2, RN3, RN4, RN5, RN6] ---
        // (O corpo da validação permanece o mesmo da resposta anterior, usando verificadorExterno)
        if (dataHora == null) {
            throw new ExcecaoDominio("Data e horário são obrigatórios");
        }
        notNull(tipoExame, "O tipo de exame é obrigatório.");

        if (!verificadorExterno.pacienteEstaCadastrado(pacienteId)) {
            throw new ExcecaoDominio("Paciente não cadastrado no sistema");
        }
        if (!verificadorExterno.medicoEstaAtivo(medicoId)) {
            throw new ExcecaoDominio("Médico inativo não pode ser vinculado ao exame");
        }
        if (!verificadorExterno.tipoExameEstaCadastrado(tipoExame)) {
            throw new ExcecaoDominio("Tipo de exame não cadastrado no sistema");
        }
        if (repositorio.obterAgendamentoConflitante(pacienteId, dataHora, null).isPresent()) {
            throw new ExcecaoDominio("Paciente já possui exame agendado neste horário");
        }
        if (!verificadorExterno.medicoEstaDisponivel(medicoId, dataHora)) {
            throw new ExcecaoDominio("Médico indisponível neste horário");
        }
        
        Exame novoExame = new Exame(pacienteId, medicoId, tipoExame, dataHora, responsavel);
        Exame exameSalvo = repositorio.salvar(novoExame);
        
        // Publica Evento de Domínio
        eventoBarramento.postar(new ExameAgendadoEvent(exameSalvo)); // Evento de Domínio local
        
        return exameSalvo;
    }

    // --- [MÉTODOS: atualizarAgendamento, tentarExcluirAgendamento, cancelarAgendamento] ---
    // (O corpo desses métodos permanece o mesmo da resposta anterior)
    
    public Exame atualizarAgendamento(ExameId exameId, Long novoMedicoId, String novoTipoExame, LocalDateTime novaDataHora, UsuarioResponsavelId responsavel) {
        
        Exame exame = repositorio.obterPorId(exameId)
            .orElseThrow(() -> new ExcecaoDominio("Agendamento de exame não encontrado"));
            
        if (!verificadorExterno.medicoEstaAtivo(novoMedicoId)) {
            throw new ExcecaoDominio("Médico inativo não pode ser vinculado ao exame");
        }
        
        if (repositorio.obterAgendamentoConflitante(exame.getPacienteId(), novaDataHora, exameId).isPresent()) {
            throw new ExcecaoDominio("Conflito de horário detectado para o médico ou paciente");
        }
        
        if (!verificadorExterno.medicoEstaDisponivel(novoMedicoId, novaDataHora)) {
            throw new ExcecaoDominio("Conflito de horário detectado para o médico ou paciente");
        }
        
        exame.atualizar(novoMedicoId, novoTipoExame, novaDataHora, responsavel);
        
        return repositorio.salvar(exame);
    }
    
    public void tentarExcluirAgendamento(ExameId exameId, UsuarioResponsavelId responsavel) {
         Exame exame = repositorio.obterPorId(exameId)
            .orElseThrow(() -> new ExcecaoDominio("Agendamento de exame não encontrado"));
            
        exame.tentarExcluir(responsavel);
        
        if (exame.getStatus() == StatusExame.CANCELADO) {
            repositorio.salvar(exame);
        } else {
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