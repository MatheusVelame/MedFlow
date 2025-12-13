package br.com.medflow.apresentacao.referencia.especialidades;

import java.net.URI;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.medflow.dominio.referencia.especialidades.Especialidade;
import br.com.medflow.dominio.referencia.especialidades.IEspecialidadeServico;
import br.com.medflow.dominio.referencia.especialidades.RegraNegocioException;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/referencia/especialidades")
public class EspecialidadeControlador {

    private final IEspecialidadeServico servico;

    public EspecialidadeControlador(IEspecialidadeServico servico) {
        this.servico = servico;
    }

    @GetMapping
    public List<EspecialidadeResumo> listar() {
        return servico.listarTodas().stream()
                .map(EspecialidadeResumo::new)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<EspecialidadeDetalhes> buscarPorId(@PathVariable Integer id) {
        return servico.buscarPorId(id)
                .map(EspecialidadeDetalhes::new)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Transactional
    @PostMapping
    public ResponseEntity<?> criar(
            @Valid @RequestBody EspecialidadeFormulario form,
            UriComponentsBuilder uriBuilder) {
        try {
            Especialidade nova = servico.criar(form.nome(), form.descricao());
            EspecialidadeResumo resumo = new EspecialidadeResumo(nova);

            URI uri = uriBuilder.path("/api/referencia/especialidades/{id}")
                    .buildAndExpand(resumo.id()).toUri();

            return ResponseEntity.created(uri).body(resumo);
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage(), "errors", getErrorsSafe(e)));
        }
    }

    @Transactional
    @PatchMapping("/{id}")
    public ResponseEntity<?> atualizar(
            @PathVariable Integer id,
            @Valid @RequestBody AtualizarEspecialidadeFormulario form) {
        try {
            var atualOpt = servico.buscarPorId(id);
            if (atualOpt.isEmpty()) return ResponseEntity.notFound().build();
            Especialidade atual = atualOpt.get();

            Especialidade atualizada = servico.alterar(atual.getNome(), form.getNovoNome(), form.getNovaDescricao());
            return ResponseEntity.ok(new EspecialidadeResumo(atualizada));
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage(), "errors", getErrorsSafe(e)));
        }
    }

    @Transactional
    @DeleteMapping("/{id}")
    public ResponseEntity<?> excluir(@PathVariable Integer id) {
        var atualOpt = servico.buscarPorId(id);
        if (atualOpt.isEmpty()) return ResponseEntity.notFound().build();
        Especialidade atual = atualOpt.get();
        try {
            servico.excluir(atual.getNome());
            return ResponseEntity.noContent().build();
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage(), "errors", getErrorsSafe(e)));
        }
    }

    private Map<String, String> getErrorsSafe(RegraNegocioException e) {
        try {
            var m = e.getClass().getMethod("getErrors");
            Object o = m.invoke(e);
            if (o instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, String> map = (Map<String, String>) o;
                return map;
            }
        } catch (Exception ex) {
            // ignore
        }
        return Map.of();
    }
}