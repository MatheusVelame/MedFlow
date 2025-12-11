package br.com.medflow.apresentacao.prontuario;

import br.com.medflow.aplicacao.prontuario.ProntuarioServicoAplicacao;
import br.com.medflow.aplicacao.prontuario.HistoricoItemResponse;
import br.com.medflow.aplicacao.prontuario.dto.request.AdicionarHistoricoRequest;
import br.com.medflow.aplicacao.prontuario.dto.response.ProntuarioResponse;
import br.com.medflow.aplicacao.prontuario.usecase.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST para gerenciamento de prontuários.
 * Implementa a camada de apresentação da Clean Architecture.
 */
@RestController
@RequestMapping("/backend/prontuarios")
public class ProntuarioControlador {
    
    private final AdicionarHistoricoClinicoUseCase adicionarHistoricoUseCase;
    private final ObterProntuarioQuery obterProntuarioQuery;
    private final ListarHistoricoQuery listarHistoricoQuery;
    
    public ProntuarioControlador(
        AdicionarHistoricoClinicoUseCase adicionarHistoricoUseCase,
        ObterProntuarioQuery obterProntuarioQuery,
        ListarHistoricoQuery listarHistoricoQuery
    ) {
        this.adicionarHistoricoUseCase = adicionarHistoricoUseCase;
        this.obterProntuarioQuery = obterProntuarioQuery;
        this.listarHistoricoQuery = listarHistoricoQuery;
    }
    
    @PostMapping("/{id}/historico")
    public ResponseEntity<Void> adicionarHistorico(
        @PathVariable String id,
        @RequestBody @Valid AdicionarHistoricoRequest request
    ) {
        adicionarHistoricoUseCase.executar(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ProntuarioResponse> obterProntuario(@PathVariable String id) {
        ProntuarioResponse prontuario = obterProntuarioQuery.executar(id);
        return ResponseEntity.ok(prontuario);
    }
    
    @GetMapping("/{id}/historico")
    public ResponseEntity<List<HistoricoItemResponse>> listarHistorico(@PathVariable String id) {
        List<HistoricoItemResponse> historico = listarHistoricoQuery.executar(id);
        return ResponseEntity.ok(historico);
    }
}
