package br.com.medflow.aplicacao.prontuario.usecase;

import br.com.medflow.aplicacao.prontuario.dto.request.AdicionarHistoricoRequest;
import com.medflow.dominio.prontuario.HistoricoClinico;
import com.medflow.dominio.prontuario.Prontuario;
import com.medflow.dominio.prontuario.ProntuarioRepositorio;
import static org.apache.commons.lang3.Validate.notNull;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * UseCase de comando para adicionar histórico clínico a um prontuário.
 */
public class AdicionarHistoricoClinicoUseCase {

    private final ProntuarioRepositorio repositorio;

    public AdicionarHistoricoClinicoUseCase(ProntuarioRepositorio repositorio) {
        notNull(repositorio, "O repositório não pode ser nulo");
        this.repositorio = repositorio;
    }

    public void executar(String prontuarioId, AdicionarHistoricoRequest request) {
        notNull(prontuarioId, "O ID do prontuário não pode ser nulo");
        notNull(request, "A requisição não pode ser nula");

        Prontuario prontuario = repositorio.obterPorId(prontuarioId)
            .orElseThrow(() -> new IllegalArgumentException("Prontuário não encontrado: " + prontuarioId));

        HistoricoClinico novoHistorico = new HistoricoClinico(
            UUID.randomUUID().toString(),
            prontuario.getPacienteId(),
            request.getSintomas(),
            request.getDiagnostico(),
            request.getConduta(),
            LocalDateTime.now(),
            request.getProfissionalResponsavel(),
            request.getAnexosReferenciados()
        );

        prontuario.adicionarHistoricoClinico(novoHistorico);
        
        // Registrar no histórico de atualizações
        // Como não temos atendimentoId na request, usamos null ou geramos um ID temporário
        // Na prática, isso deveria vir do contexto do atendimento
        String atendimentoId = prontuario.getAtendimentoId() != null ? prontuario.getAtendimentoId() : "SISTEMA";
        com.medflow.dominio.prontuario.HistoricoAtualizacao atualizacao = 
            new com.medflow.dominio.prontuario.HistoricoAtualizacao(
                UUID.randomUUID().toString(),
                prontuario.getId(),
                atendimentoId,
                LocalDateTime.now(),
                request.getProfissionalResponsavel(),
                "Nova evolução clínica registrada: " + request.getDiagnostico(),
                prontuario.getStatus()
            );
        prontuario.adicionarAtualizacao(atualizacao);
        
        repositorio.salvar(prontuario);
    }
}
