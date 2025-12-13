package br.com.medflow.aplicacao.prontuario.usecase;

import com.medflow.dominio.prontuario.Prontuario;
import com.medflow.dominio.prontuario.ProntuarioRepositorio;
import static org.apache.commons.lang3.Validate.notNull;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * UseCase de comando para excluir logicamente um prontuário.
 */
public class ExcluirProntuarioUseCase {

    private final ProntuarioRepositorio repositorio;

    public ExcluirProntuarioUseCase(ProntuarioRepositorio repositorio) {
        notNull(repositorio, "O repositório não pode ser nulo");
        this.repositorio = repositorio;
    }

    public void executar(String prontuarioId, String profissionalResponsavel) {
        notNull(prontuarioId, "O ID do prontuário não pode ser nulo");
        notNull(profissionalResponsavel, "O profissional responsável é obrigatório");

        Prontuario prontuario = repositorio.obterPorId(prontuarioId)
            .orElseThrow(() -> new IllegalArgumentException("Prontuário não encontrado: " + prontuarioId));

        // Excluir logicamente o prontuário
        prontuario.excluirLogicamente();

        // Registrar a exclusão no histórico de atualizações
        com.medflow.dominio.prontuario.HistoricoAtualizacao atualizacaoExclusao = 
            new com.medflow.dominio.prontuario.HistoricoAtualizacao(
                UUID.randomUUID().toString(),
                prontuario.getId(),
                prontuario.getAtendimentoId(),
                LocalDateTime.now(),
                profissionalResponsavel,
                "Prontuário excluído logicamente",
                com.medflow.dominio.prontuario.StatusProntuario.EXCLUIDO
            );
        prontuario.adicionarAtualizacao(atualizacaoExclusao);

        repositorio.salvar(prontuario);
    }
}

