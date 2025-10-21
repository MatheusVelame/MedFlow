package br.com.medflow.dominio.atendimento.exames;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import br.com.medflow.dominio.evento.EventoBarramento;

public class ExameServico {

    private final ExameRepositorio repositorio;
    private final VerificadorExternoServico verificadorExterno;
    private final EventoBarramento eventoBarramento;

    public ExameServico(ExameRepositorio repositorio, VerificadorExternoServico verificadorExterno, EventoBarramento eventoBarramento) {
        this.repositorio = repositorio;
        this.verificadorExterno = verificadorExterno;
        this.eventoBarramento = eventoBarramento;
    }

    public Exame agendarExame(Long pacienteId, Long medicoId, String tipoExame, LocalDateTime dataHora, UsuarioResponsavelId usuarioResponsavel) {
        if (dataHora == null) {
            throw new ExcecaoDominio("Data e horário do exame são obrigatórios.");
        }
        if (!verificadorExterno.pacienteEstaCadastrado(pacienteId)) {
            throw new ExcecaoDominio("Paciente não cadastrado no sistema.");
        }
        if (!verificadorExterno.medicoEstaCadastrado(medicoId)) {
            throw new ExcecaoDominio("Médico não cadastrado no sistema.");
        }
        if (!verificadorExterno.medicoEstaAtivo(medicoId)) {
            throw new ExcecaoDominio("Médico vinculado ao exame deve estar ativo no sistema.");
        }
        if (!verificadorExterno.tipoExameEstaCadastrado(tipoExame)) {
            throw new ExcecaoDominio("Tipo de exame não cadastrado no sistema.");
        }
        if (!verificadorExterno.medicoEstaDisponivel(medicoId, dataHora)) {
            throw new ExcecaoDominio("Não é permitido agendar exame em horário de indisponibilidade do médico.");
        }

        List<Exame> examesDoPaciente = repositorio.obterPorPaciente(pacienteId);
        boolean conflitoHorario = examesDoPaciente.stream()
                .anyMatch(exame -> exame.getDataHora().equals(dataHora));
        if (conflitoHorario) {
            throw new ExcecaoDominio("Paciente já possui um exame agendado neste horário.");
        }

        Exame novoExame = new Exame(pacienteId, medicoId, tipoExame, dataHora);
        repositorio.salvar(novoExame);
        
        eventoBarramento.postar(new ExameAgendadoEvent(novoExame.getId(), novoExame.getDataHora()));
        
        return novoExame;
    }

    public void alterarAgendamento(ExameId exameId, Long novoMedicoId, String novoTipoExame, LocalDateTime novaDataHora, UsuarioResponsavelId usuarioResponsavel) {
        Exame exame = repositorio.obterPorId(exameId)
                .orElseThrow(() -> new ExcecaoDominio("Agendamento de exame não encontrado."));

        // Aqui entrariam as lógicas de validação para a alteração
        exame.alterar(novoMedicoId, novoTipoExame, novaDataHora, usuarioResponsavel);
        repositorio.salvar(exame);
    }
    
    public void cancelarExame(ExameId exameId, String motivo, UsuarioResponsavelId usuarioResponsavel) {
         Exame exame = repositorio.obterPorId(exameId)
                .orElseThrow(() -> new ExcecaoDominio("Agendamento de exame não encontrado."));

        if(exame.getStatus() != StatusExame.AGENDADO) {
             throw new ExcecaoDominio("Ação não permitida para o status atual do exame");
        }
         
        exame.cancelar(motivo, usuarioResponsavel);
        repositorio.salvar(exame);
    }
    
    public void excluirExame(ExameId exameId, UsuarioResponsavelId usuarioResponsavel) {
        Exame exame = repositorio.obterPorId(exameId)
                .orElseThrow(() -> new ExcecaoDominio("Agendamento de exame não encontrado."));
        
        if (exame.possuiLaudo()) {
            throw new ExcecaoDominio("Exame com laudo não pode ser excluído, apenas cancelado.");
        }

        // Simulação de outras regras
        // if (verificadorExterno.possuiRegistroClinico(exameId)) {
        //    throw new ExcecaoDominio("Exame associado a um registro clínico não pode ser excluído.");
        // }
        
        repositorio.excluir(exameId); // Supondo que o repositório tenha um método de exclusão
    }
}