package br.com.medflow.apresentacao.financeiro.folhapagamento;

import br.com.medflow.aplicacao.financeiro.folhapagamento.*;
import br.com.medflow.dominio.financeiro.folhapagamento.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para Folha de Pagamento.
 */
@RestController
@RequestMapping("/api/folhas-pagamento")
@CrossOrigin(origins = "*")
public class FolhaPagamentoControlador {

    private final FolhaPagamentoServicoAplicacao servicoAplicacao;

    public FolhaPagamentoControlador(FolhaPagamentoServicoAplicacao servicoAplicacao) {
        this.servicoAplicacao = servicoAplicacao;
    }

    @PostMapping
    public ResponseEntity<FolhaPagamentoDetalhes> registrar(
            @RequestBody RegistrarFolhaFormulario formulario) {
        try {
            FolhaPagamentoDetalhes detalhes = servicoAplicacao.registrar(
                    formulario.getFuncionarioId(),
                    formulario.getPeriodoReferencia(),
                    formulario.getTipoRegistro(),
                    formulario.getSalarioBase(),
                    formulario.getBeneficios(),
                    formulario.getMetodoPagamento(),
                    formulario.getTipoVinculo(),
                    formulario.getUsuarioResponsavelId(),
                    formulario.isFuncionarioAtivo()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(detalhes);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<FolhaPagamentoDetalhes> obter(@PathVariable Integer id) {
        try {
            FolhaPagamentoDetalhes detalhes = servicoAplicacao.obterDetalhes(id);
            return ResponseEntity.ok(detalhes);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<FolhaPagamentoResumo>> listarTodas() {
        List<FolhaPagamentoResumo> resumos = servicoAplicacao.listarTodos();
        return ResponseEntity.ok(resumos);
    }

    @GetMapping("/funcionario/{funcionarioId}")
    public ResponseEntity<List<FolhaPagamentoResumo>> listarPorFuncionario(
            @PathVariable Integer funcionarioId) {
        List<FolhaPagamentoResumo> resumos = servicoAplicacao.listarPorFuncionario(funcionarioId);
        return ResponseEntity.ok(resumos);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<FolhaPagamentoResumo>> listarPorStatus(
            @PathVariable String status) {
        try {
            StatusFolha statusEnum = StatusFolha.valueOf(status.toUpperCase());
            List<FolhaPagamentoResumo> resumos = servicoAplicacao.listarPorStatus(statusEnum);
            return ResponseEntity.ok(resumos);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}/valores")
    public ResponseEntity<FolhaPagamentoDetalhes> atualizarValores(
            @PathVariable Integer id,
            @RequestBody AtualizarValoresFormulario formulario) {
        try {
            FolhaPagamentoDetalhes detalhes = servicoAplicacao.atualizarValores(
                    id,
                    formulario.getNovoSalarioBase(),
                    formulario.getNovosBeneficios(),
                    formulario.getUsuarioResponsavelId()
            );
            return ResponseEntity.ok(detalhes);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<FolhaPagamentoDetalhes> alterarStatus(
            @PathVariable Integer id,
            @RequestBody AlterarStatusFormulario formulario) {
        try {
            FolhaPagamentoDetalhes detalhes = servicoAplicacao.alterarStatus(
                    id,
                    formulario.getNovoStatus(),
                    formulario.getUsuarioResponsavelId()
            );
            return ResponseEntity.ok(detalhes);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(
            @PathVariable Integer id,
            @RequestParam Integer usuarioResponsavelId) {
        try {
            servicoAplicacao.remover(id, usuarioResponsavelId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }
}