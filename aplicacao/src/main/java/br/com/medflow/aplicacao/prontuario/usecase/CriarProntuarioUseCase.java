package br.com.medflow.aplicacao.prontuario.usecase;

import br.com.medflow.aplicacao.prontuario.dto.request.CriarProntuarioRequest;
import com.medflow.dominio.prontuario.Prontuario;
import com.medflow.dominio.prontuario.ProntuarioRepositorio;
import com.medflow.dominio.prontuario.StatusProntuario;
import static org.apache.commons.lang3.Validate.notNull;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * UseCase de comando para criar um novo prontuário.
 */
public class CriarProntuarioUseCase {

    private final ProntuarioRepositorio repositorio;

    public CriarProntuarioUseCase(ProntuarioRepositorio repositorio) {
        notNull(repositorio, "O repositório não pode ser nulo");
        this.repositorio = repositorio;
    }

    public Prontuario executar(CriarProntuarioRequest request) {
        notNull(request, "A requisição não pode ser nula");
        notNull(request.getPacienteId(), "O ID do paciente é obrigatório");
        notNull(request.getProfissionalResponsavel(), "O profissional responsável é obrigatório");

        // Sempre criar um novo prontuário independente, mesmo que o paciente já tenha outros prontuários
        // Isso permite que um paciente tenha múltiplos prontuários para diferentes motivos/atendimentos
        String novoId = UUID.randomUUID().toString();
        LocalDateTime agora = LocalDateTime.now();
        Prontuario novoProntuario = new Prontuario(
            novoId,
            request.getPacienteId(),
            request.getAtendimentoId(),
            agora,
            request.getProfissionalResponsavel(),
            request.getObservacoesIniciais(),
            StatusProntuario.ATIVO
        );

        // Registrar criação no histórico de atualizações
        String atendimentoId = request.getAtendimentoId() != null ? request.getAtendimentoId() : "SISTEMA";
        com.medflow.dominio.prontuario.HistoricoAtualizacao atualizacaoInicial = 
            new com.medflow.dominio.prontuario.HistoricoAtualizacao(
                UUID.randomUUID().toString(),
                novoId,
                atendimentoId,
                agora,
                request.getProfissionalResponsavel(),
                "Prontuário criado" + (request.getObservacoesIniciais() != null && !request.getObservacoesIniciais().trim().isEmpty() 
                    ? ": " + request.getObservacoesIniciais() : ""),
                StatusProntuario.ATIVO
            );
        novoProntuario.adicionarAtualizacao(atualizacaoInicial);

        repositorio.salvar(novoProntuario);
        return novoProntuario;
    }
}

