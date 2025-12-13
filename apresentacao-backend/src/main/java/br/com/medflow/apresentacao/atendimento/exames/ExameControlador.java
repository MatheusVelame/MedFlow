package br.com.medflow.apresentacao.atendimento.exames;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.client.RestTemplate;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import br.com.medflow.dominio.atendimento.exames.Exame;
import br.com.medflow.dominio.atendimento.exames.ExameId;
import br.com.medflow.dominio.atendimento.exames.IExameServico;
import br.com.medflow.dominio.atendimento.exames.ExameRepositorio;
import br.com.medflow.dominio.atendimento.exames.UsuarioResponsavelId;

@RestController
@RequestMapping("/exames")
@Tag(name = "Exames", description = "Operações para gerenciamento de agendamentos de exames")
public class ExameControlador {

    private final IExameServico exameServico;
    private final ExameRepositorio exameRepositorio;
    private final RestTemplate restTemplate = new RestTemplate();

    public ExameControlador(IExameServico exameServico, ExameRepositorio exameRepositorio) {
        this.exameServico = exameServico;
        this.exameRepositorio = exameRepositorio;
    }

    @GetMapping
    @Operation(summary = "Listar/executar busca de agendamentos", description = "Retorna agendamentos de exames. Suporta filtros por paciente (CPF), médico, tipo de exame, status e intervalo de datas. Por padrão, a listagem principal retorna exames futuros (exclui cancelados/realizados).")
    public ResponseEntity<List<ExameResponse>> listar(
        @Parameter(description = "CPF do paciente (11 dígitos)") @RequestParam(name = "pacienteCpf", required = false) String pacienteCpf,
        @Parameter(description = "ID do médico") @RequestParam(name = "medicoId", required = false) Long medicoId,
        @Parameter(description = "Tipo de exame (código)") @RequestParam(name = "tipoExame", required = false) String tipoExame,
        @Parameter(description = "Status do exame (AGENDADO, CANCELADO, REALIZADA)") @RequestParam(name = "status", required = false) String status,
        @Parameter(description = "Data inicial (ISO-8601) para filtro de intervalo") @RequestParam(name = "dataInicio", required = false) String dataInicio,
        @Parameter(description = "Data final (ISO-8601) para filtro de intervalo") @RequestParam(name = "dataFim", required = false) String dataFim,
        @Parameter(description = "Página (0-based)") @RequestParam(name = "page", required = false, defaultValue = "0") int page,
        @Parameter(description = "Tamanho da página") @RequestParam(name = "size", required = false, defaultValue = "50") int size,
        @Parameter(description = "Ordenação por data/hora (asc/desc)") @RequestParam(name = "order", required = false, defaultValue = "asc") String order
    ) {
        // Carrega todos e aplica filtros em memória (implementação simples; pode ser movida para repositório se necessário)
        List<Exame> todos = exameRepositorio.listarTodos();

        // Filtrar por paciente via CPF se fornecido: consulta ao endpoint de pacientes para obter o ID
        if (pacienteCpf != null && !pacienteCpf.isBlank()) {
            String cpf = pacienteCpf.trim();
            if (cpf.length() != 11 || !cpf.chars().allMatch(Character::isDigit)) {
                return ResponseEntity.badRequest().build();
            }
            try {
                String url = String.format("http://localhost:8080/api/pacientes/cpf/%s", cpf);
                PacienteResumo paciente = restTemplate.getForObject(url, PacienteResumo.class);
                if (paciente == null) {
                    // nenhum paciente encontrado -> retorna lista vazia
                    return ResponseEntity.ok(List.of());
                }
                Long pacienteId = Long.valueOf(paciente.id);
                todos = todos.stream().filter(e -> e.getPacienteId().equals(pacienteId)).collect(Collectors.toList());
            } catch (org.springframework.web.client.HttpClientErrorException.NotFound ex) {
                return ResponseEntity.ok(List.of());
            }
        }

        if (medicoId != null) {
            todos = todos.stream().filter(e -> e.getMedicoId().equals(medicoId)).collect(Collectors.toList());
        }

        if (tipoExame != null && !tipoExame.isBlank()) {
            todos = todos.stream().filter(e -> tipoExame.equals(e.getTipoExame())).collect(Collectors.toList());
        }

        if (status != null && !status.isBlank()) {
            todos = todos.stream().filter(e -> status.equalsIgnoreCase(e.getStatus().name())).collect(Collectors.toList());
        } else {
            // Por padrão, excluir realizados ou cancelados da listagem principal
            todos = todos.stream().filter(e -> e.getStatus() == br.com.medflow.dominio.atendimento.exames.StatusExame.AGENDADO).collect(Collectors.toList());
        }

        final LocalDateTime inicio;
        final LocalDateTime fim;
        try {
            if (dataInicio != null && !dataInicio.isBlank()) inicio = LocalDateTime.parse(dataInicio);
            else inicio = null;
            if (dataFim != null && !dataFim.isBlank()) fim = LocalDateTime.parse(dataFim);
            else fim = null;
        } catch (DateTimeParseException ex) {
            return ResponseEntity.badRequest().build();
        }

        if (inicio != null) {
            todos = todos.stream().filter(e -> !e.getDataHora().isBefore(inicio)).collect(Collectors.toList());
        }
        if (fim != null) {
            todos = todos.stream().filter(e -> !e.getDataHora().isAfter(fim)).collect(Collectors.toList());
        }

        // Ordenação por data/hora antes da paginação
        if ("desc".equalsIgnoreCase(order)) {
            todos = todos.stream().sorted((e1, e2) -> e2.getDataHora().compareTo(e1.getDataHora())).collect(Collectors.toList());
        } else {
            todos = todos.stream().sorted((e1, e2) -> e1.getDataHora().compareTo(e2.getDataHora())).collect(Collectors.toList());
        }

        // paginação simples
        int fromIndex = Math.max(0, page * size);
        int toIndex = Math.min(todos.size(), fromIndex + size);
        List<Exame> pageItems = (fromIndex < toIndex) ? todos.subList(fromIndex, toIndex) : List.of();

        List<ExameResponse> resp = pageItems.stream().map(ExameResponse::de).collect(Collectors.toList());
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExameDetalheResponse> buscar(@PathVariable Long id) {
        Exame exame = exameRepositorio.obterPorId(new ExameId(id))
            .orElseThrow(() -> new br.com.medflow.dominio.atendimento.exames.ExcecaoDominio("Agendamento de exame não encontrado"));
        return ResponseEntity.ok(ExameDetalheResponse.de(exame));
    }

    @PostMapping
    @Operation(summary = "Agendar exame", description = "Cria um novo agendamento de exame. Valida paciente, médico, tipo, disponibilidade e conflitos.")
    @ApiResponse(responseCode = "201", description = "Agendamento criado", content = @Content(schema = @Schema(implementation = ExameResponse.class)))
    @ApiResponse(responseCode = "400", description = "Dados inválidos (ex: data inválida)", content = @Content(schema = @Schema(implementation = ErroNegocioResponse.class)))
    @ApiResponse(responseCode = "404", description = "Paciente/Médico/Tipo não encontrado", content = @Content(schema = @Schema(implementation = ErroNegocioResponse.class)))
    @ApiResponse(responseCode = "409", description = "Conflito de agendamento (horário indisponível ou já ocupado)", content = @Content(schema = @Schema(implementation = ErroNegocioResponse.class)))
    public ResponseEntity<?> agendar(@Valid @RequestBody AgendamentoExameRequest request,
                                                 UriComponentsBuilder uriBuilder) {
        try {
            Exame exameCriado = exameServico.agendarExame(
                request.pacienteId(),
                request.medicoId(),
                request.tipoExame(),
                request.dataHora(),
                new UsuarioResponsavelId(request.responsavelId())
            );

            URI uri = uriBuilder.path("/exames/{id}").buildAndExpand(exameCriado.getId().getValor()).toUri();
            return ResponseEntity.created(uri).body(ExameResponse.de(exameCriado));
        } catch (br.com.medflow.dominio.atendimento.exames.EntidadeNaoEncontradaException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErroNegocioResponse(extractCode(ex.getMessage(), "ENTIDADE_NAO_ENCONTRADA"), extractUserMessage(ex.getMessage())));
        } catch (br.com.medflow.dominio.atendimento.exames.ConflitoNegocioException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErroNegocioResponse(extractCode(ex.getMessage(), "CONFLITO_NEGOCIO"), extractUserMessage(ex.getMessage())));
        } catch (br.com.medflow.dominio.atendimento.exames.ValidacaoNegocioException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErroNegocioResponse(extractCode(ex.getMessage(), "VALIDACAO_NEGOCIO"), extractUserMessage(ex.getMessage())));
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar agendamento de exame", description = "Permite alterar data/hora, médico (somente se ativo) e tipo de exame. Registra histórico de alterações.")
    @ApiResponse(responseCode = "200", description = "Agendamento atualizado com sucesso", content = @Content(schema = @Schema(implementation = ExameResponse.class)))
    @ApiResponse(responseCode = "400", description = "Dados inválidos (ex: data inválida)", content = @Content(schema = @Schema(implementation = ErroNegocioResponse.class)))
    @ApiResponse(responseCode = "404", description = "Agendamento/Médico/Tipo não encontrado", content = @Content(schema = @Schema(implementation = ErroNegocioResponse.class)))
    @ApiResponse(responseCode = "409", description = "Conflito de agendamento (horário indisponível ou já ocupado)", content = @Content(schema = @Schema(implementation = ErroNegocioResponse.class)))
    public ResponseEntity<?> atualizar(@PathVariable Long id,
                                                   @Valid @RequestBody AtualizacaoExameRequest request) {
        try {
            Exame exameAtualizado = exameServico.atualizarAgendamento(
                new ExameId(id),
                request.medicoId(),
                request.tipoExame(),
                request.dataHora(),
                new UsuarioResponsavelId(request.responsavelId()),
                request.observacoes()
            );

            return ResponseEntity.ok(ExameResponse.de(exameAtualizado));
        } catch (br.com.medflow.dominio.atendimento.exames.EntidadeNaoEncontradaException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErroNegocioResponse(extractCode(ex.getMessage(), "ENTIDADE_NAO_ENCONTRADA"), extractUserMessage(ex.getMessage())));
        } catch (br.com.medflow.dominio.atendimento.exames.ConflitoNegocioException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErroNegocioResponse(extractCode(ex.getMessage(), "CONFLITO_NEGOCIO"), extractUserMessage(ex.getMessage())));
        } catch (br.com.medflow.dominio.atendimento.exames.ValidacaoNegocioException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErroNegocioResponse(extractCode(ex.getMessage(), "VALIDACAO_NEGOCIO"), extractUserMessage(ex.getMessage())));
        }
    }

    /**
     * Endpoint para exclusão.
     * Conforme a regra de negócio (RN12), pode resultar em exclusão física ou cancelamento lógico.
     */
    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204", description = "Exclusão efetuada (ou cancelamento lógico)")
    @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content(schema = @Schema(implementation = ErroNegocioResponse.class)))
    @ApiResponse(responseCode = "404", description = "Agendamento não encontrado", content = @Content(schema = @Schema(implementation = ErroNegocioResponse.class)))
    @ApiResponse(responseCode = "409", description = "Operação não permitida (ex: já realizou ou vinculado a prontuário)", content = @Content(schema = @Schema(implementation = ErroNegocioResponse.class)))
    public ResponseEntity<?> tentarExcluir(@PathVariable Long id, 
                                              @RequestParam(name = "responsavelId", required = true) Long responsavelId) {
        try {
            exameServico.tentarExcluirAgendamento(
                new ExameId(id), 
                new UsuarioResponsavelId(responsavelId)
            );
            return ResponseEntity.noContent().build();
        } catch (br.com.medflow.dominio.atendimento.exames.EntidadeNaoEncontradaException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErroNegocioResponse(extractCode(ex.getMessage(), "ENTIDADE_NAO_ENCONTRADA"), extractUserMessage(ex.getMessage())));
        } catch (br.com.medflow.dominio.atendimento.exames.ConflitoNegocioException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErroNegocioResponse(extractCode(ex.getMessage(), "CONFLITO_NEGOCIO"), extractUserMessage(ex.getMessage())));
        } catch (br.com.medflow.dominio.atendimento.exames.ValidacaoNegocioException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErroNegocioResponse(extractCode(ex.getMessage(), "VALIDACAO_NEGOCIO"), extractUserMessage(ex.getMessage())));
        }
    }

    /**
     * Endpoint específico para cancelamento manual com motivo.
     */
    @PatchMapping("/{id}/cancelamento")
    @ApiResponse(responseCode = "200", description = "Agendamento cancelado com sucesso", content = @Content(schema = @Schema(implementation = ExameResponse.class)))
    @ApiResponse(responseCode = "400", description = "Dados inválidos (ex: motivo ausente)", content = @Content(schema = @Schema(implementation = ErroNegocioResponse.class)))
    @ApiResponse(responseCode = "404", description = "Agendamento não encontrado", content = @Content(schema = @Schema(implementation = ErroNegocioResponse.class)))
    @ApiResponse(responseCode = "409", description = "Operação não permitida (ex: já realizou)", content = @Content(schema = @Schema(implementation = ErroNegocioResponse.class)))
    public ResponseEntity<?> cancelar(@PathVariable Long id,
                                                  @Valid @RequestBody CancelamentoExameRequest request) {
        try {
            Exame exameCancelado = exameServico.cancelarAgendamento(
                new ExameId(id),
                request.motivo(),
                new UsuarioResponsavelId(request.responsavelId())
            );
            return ResponseEntity.ok(ExameResponse.de(exameCancelado));
        } catch (br.com.medflow.dominio.atendimento.exames.EntidadeNaoEncontradaException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErroNegocioResponse(extractCode(ex.getMessage(), "ENTIDADE_NAO_ENCONTRADA"), extractUserMessage(ex.getMessage())));
        } catch (br.com.medflow.dominio.atendimento.exames.ConflitoNegocioException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErroNegocioResponse(extractCode(ex.getMessage(), "CONFLITO_NEGOCIO"), extractUserMessage(ex.getMessage())));
        } catch (br.com.medflow.dominio.atendimento.exames.ValidacaoNegocioException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErroNegocioResponse(extractCode(ex.getMessage(), "VALIDACAO_NEGOCIO"), extractUserMessage(ex.getMessage())));
        }
    }

    // Classe interna para desserialização mínima do retorno do serviço de pacientes
    public static class PacienteResumo {
        public String id;
        public String nome;
    }

    // util de extração (duplicado do GlobalExceptionHandler para garantir formato consistente)
    private String extractCode(String message, String defaultCode) {
        if (message == null) return defaultCode;
        int idx = message.indexOf(":" );
        if (idx > 0) return message.substring(0, idx).trim();
        return defaultCode;
    }

    private String extractUserMessage(String message) {
        if (message == null) return "";
        int idx = message.indexOf(":" );
        if (idx > 0 && idx + 1 < message.length()) return message.substring(idx + 1).trim();
        return message;
    }
}