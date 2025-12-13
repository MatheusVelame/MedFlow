package br.com.medflow.apresentacao.prontuario;

import br.com.medflow.aplicacao.prontuario.ProntuarioServicoAplicacao;
import br.com.medflow.aplicacao.prontuario.ProntuarioResumo;
import br.com.medflow.aplicacao.prontuario.HistoricoItemResponse;
import br.com.medflow.aplicacao.prontuario.AtualizacaoItemResponse;
import br.com.medflow.aplicacao.prontuario.dto.request.AdicionarHistoricoRequest;
import br.com.medflow.aplicacao.prontuario.dto.request.CriarProntuarioRequest;
import br.com.medflow.aplicacao.prontuario.dto.response.ProntuarioResponse;
import br.com.medflow.aplicacao.prontuario.usecase.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST para gerenciamento de prontuários.
 * Implementa a camada de apresentação da Clean Architecture.
 */
@Tag(name = "Prontuários", description = "API para gerenciamento de prontuários médicos")
@RestController
@RequestMapping("/backend/prontuarios")
public class ProntuarioControlador {
    
    private final ProntuarioServicoAplicacao servicoAplicacao;
    private final CriarProntuarioUseCase criarProntuarioUseCase;
    private final AdicionarHistoricoClinicoUseCase adicionarHistoricoUseCase;
    private final ObterProntuarioQuery obterProntuarioQuery;
    private final ListarHistoricoQuery listarHistoricoQuery;
    private final ListarHistoricoAtualizacoesQuery listarHistoricoAtualizacoesQuery;
    private final ExcluirProntuarioUseCase excluirProntuarioUseCase;
    private final InativarProntuarioUseCase inativarProntuarioUseCase;
    
    public ProntuarioControlador(
        ProntuarioServicoAplicacao servicoAplicacao,
        CriarProntuarioUseCase criarProntuarioUseCase,
        AdicionarHistoricoClinicoUseCase adicionarHistoricoUseCase,
        ObterProntuarioQuery obterProntuarioQuery,
        ListarHistoricoQuery listarHistoricoQuery,
        ListarHistoricoAtualizacoesQuery listarHistoricoAtualizacoesQuery,
        ExcluirProntuarioUseCase excluirProntuarioUseCase,
        InativarProntuarioUseCase inativarProntuarioUseCase
    ) {
        this.servicoAplicacao = servicoAplicacao;
        this.criarProntuarioUseCase = criarProntuarioUseCase;
        this.adicionarHistoricoUseCase = adicionarHistoricoUseCase;
        this.obterProntuarioQuery = obterProntuarioQuery;
        this.listarHistoricoQuery = listarHistoricoQuery;
        this.listarHistoricoAtualizacoesQuery = listarHistoricoAtualizacoesQuery;
        this.excluirProntuarioUseCase = excluirProntuarioUseCase;
        this.inativarProntuarioUseCase = inativarProntuarioUseCase;
    }
    
    @Operation(
        summary = "Listar todos os prontuários",
        description = "Retorna uma lista com todos os prontuários cadastrados no sistema"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Lista de prontuários retornada com sucesso",
        content = @Content(schema = @Schema(implementation = ProntuarioResumo.class))
    )
    @GetMapping
    public ResponseEntity<List<ProntuarioResumo>> listarProntuarios() {
        List<ProntuarioResumo> prontuarios = servicoAplicacao.pesquisarResumos();
        return ResponseEntity.ok(prontuarios);
    }
    
    @Operation(
        summary = "Criar novo prontuário",
        description = "Cria um novo prontuário independente para um paciente. Um paciente pode ter múltiplos prontuários para diferentes motivos/atendimentos."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Prontuário criado com sucesso",
            content = @Content(schema = @Schema(implementation = ProntuarioResponse.class))
        ),
        @ApiResponse(responseCode = "400", description = "Dados inválidos ou prontuário ativo já existe", content = @Content)
    })
    @PostMapping
    public ResponseEntity<ProntuarioResponse> criarProntuario(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Dados do prontuário", required = true)
        @RequestBody @Valid CriarProntuarioRequest request
    ) {
        var prontuario = criarProntuarioUseCase.executar(request);
        ProntuarioResponse response = obterProntuarioQuery.executar(prontuario.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @Operation(
        summary = "Adicionar histórico clínico ao prontuário",
        description = "Adiciona um novo registro de histórico clínico (sintomas, diagnóstico, conduta) ao prontuário especificado"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Histórico clínico adicionado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content),
        @ApiResponse(responseCode = "404", description = "Prontuário não encontrado", content = @Content)
    })
    @PostMapping("/{id}/historico")
    public ResponseEntity<Void> adicionarHistorico(
        @Parameter(description = "ID do prontuário", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
        @PathVariable String id,
        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Dados do histórico clínico", required = true)
        @RequestBody @Valid AdicionarHistoricoRequest request
    ) {
        adicionarHistoricoUseCase.executar(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    
    @Operation(
        summary = "Obter prontuário por ID",
        description = "Retorna os detalhes completos de um prontuário, incluindo histórico clínico e atualizações"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Prontuário encontrado",
            content = @Content(schema = @Schema(implementation = ProntuarioResponse.class))
        ),
        @ApiResponse(responseCode = "404", description = "Prontuário não encontrado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProntuarioResponse> obterProntuario(
        @Parameter(description = "ID do prontuário", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
        @PathVariable String id
    ) {
        ProntuarioResponse prontuario = obterProntuarioQuery.executar(id);
        return ResponseEntity.ok(prontuario);
    }
    
    @Operation(
        summary = "Listar histórico clínico do prontuário",
        description = "Retorna a lista completa de registros de histórico clínico de um prontuário específico"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Lista de histórico clínico retornada com sucesso",
            content = @Content(schema = @Schema(implementation = HistoricoItemResponse.class))
        ),
        @ApiResponse(responseCode = "404", description = "Prontuário não encontrado", content = @Content)
    })
    @GetMapping("/{id}/historico")
    public ResponseEntity<List<HistoricoItemResponse>> listarHistorico(
        @Parameter(description = "ID do prontuário", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
        @PathVariable String id
    ) {
        List<HistoricoItemResponse> historico = listarHistoricoQuery.executar(id);
        return ResponseEntity.ok(historico);
    }
    
    @Operation(
        summary = "Buscar prontuários por paciente",
        description = "Retorna uma lista de prontuários de um paciente específico"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Lista de prontuários retornada com sucesso",
            content = @Content(schema = @Schema(implementation = ProntuarioResumo.class))
        )
    })
    @GetMapping("/paciente/{pacienteId}")
    public ResponseEntity<List<ProntuarioResumo>> buscarPorPaciente(
        @Parameter(description = "ID do paciente", required = true, example = "1")
        @PathVariable String pacienteId
    ) {
        List<ProntuarioResumo> prontuarios = servicoAplicacao.buscarPorPaciente(pacienteId);
        return ResponseEntity.ok(prontuarios);
    }
    
    @Operation(
        summary = "Listar histórico de atualizações do prontuário",
        description = "Retorna a lista completa de registros de atualizações de um prontuário específico"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Lista de histórico de atualizações retornada com sucesso",
            content = @Content(schema = @Schema(implementation = AtualizacaoItemResponse.class))
        ),
        @ApiResponse(responseCode = "404", description = "Prontuário não encontrado", content = @Content)
    })
    @GetMapping("/{id}/atualizacoes")
    public ResponseEntity<List<AtualizacaoItemResponse>> listarHistoricoAtualizacoes(
        @Parameter(description = "ID do prontuário", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
        @PathVariable String id
    ) {
        List<AtualizacaoItemResponse> atualizacoes = listarHistoricoAtualizacoesQuery.executar(id);
        return ResponseEntity.ok(atualizacoes);
    }
    
    @Operation(
        summary = "Inativar prontuário",
        description = "Inativa um prontuário. Médicos e gestores podem realizar esta operação."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Prontuário inativado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Prontuário não encontrado", content = @Content),
        @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content)
    })
    @PatchMapping("/{id}/inativar")
    public ResponseEntity<Void> inativarProntuario(
        @Parameter(description = "ID do prontuário", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
        @PathVariable String id,
        @RequestParam(required = true) String profissionalResponsavel
    ) {
        inativarProntuarioUseCase.executar(id, profissionalResponsavel);
        return ResponseEntity.noContent().build();
    }
    
    @Operation(
        summary = "Excluir prontuário logicamente",
        description = "Exclui logicamente um prontuário. Médicos e gestores podem realizar esta operação."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Prontuário excluído com sucesso"),
        @ApiResponse(responseCode = "404", description = "Prontuário não encontrado", content = @Content),
        @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirProntuario(
        @Parameter(description = "ID do prontuário", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
        @PathVariable String id,
        @RequestParam(required = true) String profissionalResponsavel
    ) {
        excluirProntuarioUseCase.executar(id, profissionalResponsavel);
        return ResponseEntity.noContent().build();
    }
}
