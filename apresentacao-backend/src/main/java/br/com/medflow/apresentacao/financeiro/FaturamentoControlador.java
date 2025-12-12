package br.com.medflow.apresentacao.financeiro;

import br.com.medflow.aplicacao.financeiro.faturamentos.FaturamentoDetalhes;
import br.com.medflow.aplicacao.financeiro.faturamentos.FaturamentoResumo;
import br.com.medflow.aplicacao.financeiro.faturamentos.FaturamentoServicoAplicacao;
import br.com.medflow.aplicacao.financeiro.faturamentos.usecase.*;
import br.com.medflow.dominio.financeiro.faturamentos.MetodoPagamento;
import br.com.medflow.dominio.financeiro.faturamentos.StatusFaturamento;
import br.com.medflow.dominio.financeiro.faturamentos.TipoProcedimento;
import br.com.medflow.dominio.financeiro.faturamentos.Valor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Controller REST para gerenciamento de faturamentos.
 * Implementa a camada de apresentação da Clean Architecture.
 */
@Tag(name = "Faturamentos", description = "API para gerenciamento de faturamentos médicos")
@RestController
@RequestMapping("/backend/faturamentos")
public class FaturamentoControlador {
    
    private final FaturamentoServicoAplicacao servicoAplicacao;
    private final RegistrarFaturamentoUseCase registrarFaturamentoUseCase;
    private final MarcarComoPagoUseCase marcarComoPagoUseCase;
    private final CancelarFaturamentoUseCase cancelarFaturamentoUseCase;
    
    public FaturamentoControlador(
        FaturamentoServicoAplicacao servicoAplicacao,
        RegistrarFaturamentoUseCase registrarFaturamentoUseCase,
        MarcarComoPagoUseCase marcarComoPagoUseCase,
        CancelarFaturamentoUseCase cancelarFaturamentoUseCase
    ) {
        this.servicoAplicacao = servicoAplicacao;
        this.registrarFaturamentoUseCase = registrarFaturamentoUseCase;
        this.marcarComoPagoUseCase = marcarComoPagoUseCase;
        this.cancelarFaturamentoUseCase = cancelarFaturamentoUseCase;
    }
    
    @Operation(
        summary = "Listar todos os faturamentos",
        description = "Retorna uma lista com todos os faturamentos (excluindo os removidos)"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Lista de faturamentos retornada com sucesso",
        content = @Content(schema = @Schema(implementation = FaturamentoResumo.class))
    )
    @GetMapping
    public ResponseEntity<List<FaturamentoResumo>> listarFaturamentos() {
        List<FaturamentoResumo> faturamentos = servicoAplicacao.pesquisarResumos();
        return ResponseEntity.ok(faturamentos);
    }
    
    @Operation(
        summary = "Obter faturamento por ID",
        description = "Retorna os detalhes completos de um faturamento, incluindo histórico de alterações"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Faturamento encontrado",
            content = @Content(schema = @Schema(implementation = FaturamentoDetalhes.class))
        ),
        @ApiResponse(responseCode = "404", description = "Faturamento não encontrado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<FaturamentoDetalhes> obterFaturamento(
        @Parameter(description = "ID do faturamento", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
        @PathVariable String id
    ) {
        FaturamentoDetalhes faturamento = servicoAplicacao.obterDetalhes(id);
        return ResponseEntity.ok(faturamento);
    }
    
    @Operation(
        summary = "Pesquisar faturamentos por status",
        description = "Retorna uma lista de faturamentos filtrados por status (PENDENTE, PAGO, CANCELADO, INVALIDO, REMOVIDO)"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Lista de faturamentos retornada com sucesso",
            content = @Content(schema = @Schema(implementation = FaturamentoResumo.class))
        ),
        @ApiResponse(responseCode = "400", description = "Status inválido", content = @Content)
    })
    @GetMapping("/status/{status}")
    public ResponseEntity<List<FaturamentoResumo>> pesquisarPorStatus(
        @Parameter(description = "Status do faturamento (PENDENTE, PAGO, CANCELADO, INVALIDO, REMOVIDO)", required = true, example = "PENDENTE")
        @PathVariable String status
    ) {
        StatusFaturamento statusEnum = StatusFaturamento.valueOf(status.toUpperCase());
        List<FaturamentoResumo> faturamentos = servicoAplicacao.pesquisarPorStatus(statusEnum);
        return ResponseEntity.ok(faturamentos);
    }
    
    @Operation(
        summary = "Registrar novo faturamento",
        description = "Cria um novo faturamento para um paciente. Tipos de procedimento: CONSULTA ou EXAME"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Faturamento criado com sucesso",
            content = @Content(schema = @Schema(implementation = FaturamentoResumo.class))
        ),
        @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content)
    })
    @PostMapping
    public ResponseEntity<FaturamentoResumo> registrarFaturamento(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Dados do faturamento", required = true)
        @RequestBody RegistrarFaturamentoRequest request
    ) {
        var faturamento = registrarFaturamentoUseCase.executar(
            request.pacienteId,
            TipoProcedimento.valueOf(request.tipoProcedimento.toUpperCase()),
            request.descricaoProcedimento,
            new Valor(BigDecimal.valueOf(request.valor)),
            new MetodoPagamento(request.metodoPagamento),
            new br.com.medflow.dominio.financeiro.faturamentos.UsuarioResponsavelId(request.usuarioResponsavel),
            request.observacoes
        );
        
        FaturamentoDetalhes detalhes = servicoAplicacao.obterDetalhes(faturamento.getId().getValor());
        FaturamentoResumo resumo = new FaturamentoResumo(
            detalhes.getId(),
            detalhes.getPacienteId(),
            detalhes.getTipoProcedimento(),
            detalhes.getDescricaoProcedimento(),
            detalhes.getValor(),
            detalhes.getMetodoPagamento(),
            detalhes.getStatus(),
            detalhes.getDataHoraFaturamento()
        );
        
        return ResponseEntity.status(HttpStatus.CREATED).body(resumo);
    }
    
    @Operation(
        summary = "Marcar faturamento como pago",
        description = "Atualiza o status de um faturamento para PAGO"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Faturamento marcado como pago com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content),
        @ApiResponse(responseCode = "404", description = "Faturamento não encontrado", content = @Content)
    })
    @PutMapping("/{id}/pago")
    public ResponseEntity<Void> marcarComoPago(
        @Parameter(description = "ID do faturamento", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
        @PathVariable String id,
        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Dados para marcar como pago", required = true)
        @RequestBody MarcarComoPagoRequest request
    ) {
        marcarComoPagoUseCase.executar(id, request.usuarioResponsavel);
        return ResponseEntity.ok().build();
    }
    
    @Operation(
        summary = "Cancelar faturamento",
        description = "Cancela um faturamento, atualizando seu status para CANCELADO e registrando o motivo"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Faturamento cancelado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content),
        @ApiResponse(responseCode = "404", description = "Faturamento não encontrado", content = @Content)
    })
    @PutMapping("/{id}/cancelar")
    public ResponseEntity<Void> cancelarFaturamento(
        @Parameter(description = "ID do faturamento", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
        @PathVariable String id,
        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Dados para cancelamento", required = true)
        @RequestBody CancelarFaturamentoRequest request
    ) {
        cancelarFaturamentoUseCase.executar(id, request.motivo, request.usuarioResponsavel);
        return ResponseEntity.ok().build();
    }
    
    // DTOs de Request
    @Schema(description = "Dados para registro de novo faturamento")
    public static class RegistrarFaturamentoRequest {
        @Schema(description = "ID do paciente (INTEGER)", example = "1", required = true)
        public String pacienteId;
        @Schema(description = "Tipo de procedimento (CONSULTA ou EXAME)", example = "CONSULTA", required = true)
        public String tipoProcedimento;
        @Schema(description = "Descrição do procedimento", example = "Consulta médica geral", required = true)
        public String descricaoProcedimento;
        @Schema(description = "Valor do faturamento", example = "150.00", required = true)
        public double valor;
        @Schema(description = "Método de pagamento", example = "CARTAO_CREDITO", required = true)
        public String metodoPagamento;
        @Schema(description = "ID do usuário responsável", example = "user-123", required = true)
        public String usuarioResponsavel;
        @Schema(description = "Observações adicionais", example = "Paciente com desconto especial")
        public String observacoes;
    }
    
    @Schema(description = "Dados para marcar faturamento como pago")
    public static class MarcarComoPagoRequest {
        @Schema(description = "ID do usuário responsável", example = "user-123", required = true)
        public String usuarioResponsavel;
    }
    
    @Schema(description = "Dados para cancelamento de faturamento")
    public static class CancelarFaturamentoRequest {
        @Schema(description = "Motivo do cancelamento", example = "Paciente desistiu do procedimento", required = true)
        public String motivo;
        @Schema(description = "ID do usuário responsável", example = "user-123", required = true)
        public String usuarioResponsavel;
    }
}
