package br.com.medflow.aplicacao.prontuario.usecase;

import com.medflow.dominio.prontuario.Prontuario;
import com.medflow.dominio.prontuario.ProntuarioRepositorio;
import static org.apache.commons.lang3.Validate.notNull;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * UseCase de comando para inativar um prontuário.
 */
public class InativarProntuarioUseCase {

    private final ProntuarioRepositorio repositorio;

    public InativarProntuarioUseCase(ProntuarioRepositorio repositorio) {
        notNull(repositorio, "O repositório não pode ser nulo");
        this.repositorio = repositorio;
    }

    public void executar(String prontuarioId, String profissionalResponsavel) {
        notNull(prontuarioId, "O ID do prontuário não pode ser nulo");
        notNull(profissionalResponsavel, "O profissional responsável é obrigatório");

        Prontuario prontuario = repositorio.obterPorId(prontuarioId)
            .orElseThrow(() -> new IllegalArgumentException("Prontuário não encontrado: " + prontuarioId));

        // Inativar o prontuário
        prontuario.inativar();

        // Registrar a inativação no histórico de atualizações
        com.medflow.dominio.prontuario.HistoricoAtualizacao atualizacaoInativacao = 
            new com.medflow.dominio.prontuario.HistoricoAtualizacao(
                UUID.randomUUID().toString(),
                prontuario.getId(),
                prontuario.getAtendimentoId(),
                LocalDateTime.now(),
                profissionalResponsavel,
                "Prontuário inativado",
                com.medflow.dominio.prontuario.StatusProntuario.INATIVADO
            );
        prontuario.adicionarAtualizacao(atualizacaoInativacao);

        repositorio.salvar(prontuario);
    }
}

