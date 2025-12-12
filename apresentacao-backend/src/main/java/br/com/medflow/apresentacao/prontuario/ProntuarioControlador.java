package br.com.medflow.apresentacao.prontuario;

import br.com.medflow.aplicacao.prontuario.ProntuarioServicoAplicacao;
import br.com.medflow.aplicacao.prontuario.ProntuarioResumo;
import br.com.medflow.aplicacao.prontuario.HistoricoItemResponse;
import br.com.medflow.aplicacao.prontuario.dto.request.AdicionarHistoricoRequest;
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
    private final AdicionarHistoricoClinicoUseCase adicionarHistoricoUseCase;
    private final ObterProntuarioQuery obterProntuarioQuery;
    private final ListarHistoricoQuery listarHistoricoQuery;
    
    public ProntuarioControlador(
        ProntuarioServicoAplicacao servicoAplicacao,
        AdicionarHistoricoClinicoUseCase adicionarHistoricoUseCase,
        ObterProntuarioQuery obterProntuarioQuery,
        ListarHistoricoQuery listarHistoricoQuery
    ) {
        this.servicoAplicacao = servicoAplicacao;
        this.adicionarHistoricoUseCase = adicionarHistoricoUseCase;
        this.obterProntuarioQuery = obterProntuarioQuery;
        this.listarHistoricoQuery = listarHistoricoQuery;
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
}
