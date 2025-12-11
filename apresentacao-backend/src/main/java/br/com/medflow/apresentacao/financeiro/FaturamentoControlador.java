package br.com.medflow.apresentacao.financeiro;

import br.com.medflow.aplicacao.financeiro.faturamentos.FaturamentoDetalhes;
import br.com.medflow.aplicacao.financeiro.faturamentos.FaturamentoResumo;
import br.com.medflow.aplicacao.financeiro.faturamentos.FaturamentoServicoAplicacao;
import br.com.medflow.aplicacao.financeiro.faturamentos.usecase.*;
import br.com.medflow.dominio.financeiro.faturamentos.MetodoPagamento;
import br.com.medflow.dominio.financeiro.faturamentos.StatusFaturamento;
import br.com.medflow.dominio.financeiro.faturamentos.TipoProcedimento;
import br.com.medflow.dominio.financeiro.faturamentos.Valor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Controller REST para gerenciamento de faturamentos.
 * Implementa a camada de apresentação da Clean Architecture.
 */
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
    
    @GetMapping
    public ResponseEntity<List<FaturamentoResumo>> listarFaturamentos() {
        List<FaturamentoResumo> faturamentos = servicoAplicacao.pesquisarResumos();
        return ResponseEntity.ok(faturamentos);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<FaturamentoDetalhes> obterFaturamento(@PathVariable String id) {
        FaturamentoDetalhes faturamento = servicoAplicacao.obterDetalhes(id);
        return ResponseEntity.ok(faturamento);
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<List<FaturamentoResumo>> pesquisarPorStatus(@PathVariable String status) {
        StatusFaturamento statusEnum = StatusFaturamento.valueOf(status.toUpperCase());
        List<FaturamentoResumo> faturamentos = servicoAplicacao.pesquisarPorStatus(statusEnum);
        return ResponseEntity.ok(faturamentos);
    }
    
    @PostMapping
    public ResponseEntity<FaturamentoResumo> registrarFaturamento(
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
    
    @PutMapping("/{id}/pago")
    public ResponseEntity<Void> marcarComoPago(
        @PathVariable String id,
        @RequestBody MarcarComoPagoRequest request
    ) {
        marcarComoPagoUseCase.executar(id, request.usuarioResponsavel);
        return ResponseEntity.ok().build();
    }
    
    @PutMapping("/{id}/cancelar")
    public ResponseEntity<Void> cancelarFaturamento(
        @PathVariable String id,
        @RequestBody CancelarFaturamentoRequest request
    ) {
        cancelarFaturamentoUseCase.executar(id, request.motivo, request.usuarioResponsavel);
        return ResponseEntity.ok().build();
    }
    
    // DTOs de Request
    public static class RegistrarFaturamentoRequest {
        public String pacienteId;
        public String tipoProcedimento;
        public String descricaoProcedimento;
        public double valor;
        public String metodoPagamento;
        public String usuarioResponsavel;
        public String observacoes;
    }
    
    public static class MarcarComoPagoRequest {
        public String usuarioResponsavel;
    }
    
    public static class CancelarFaturamentoRequest {
        public String motivo;
        public String usuarioResponsavel;
    }
}
