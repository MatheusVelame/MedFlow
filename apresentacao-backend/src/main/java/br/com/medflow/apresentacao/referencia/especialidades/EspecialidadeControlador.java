package br.com.medflow.apresentacao.referencia.especialidades;

import java.net.URI;
import java.util.List;

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

    @GetMapping("/{nome}")
    public ResponseEntity<EspecialidadeDetalhes> buscarPorId(@PathVariable String nome) {
        return servico.buscarPorNome(nome)
                .map(EspecialidadeDetalhes::new)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Transactional
    @PostMapping
    public ResponseEntity<EspecialidadeResumo> criar(
            @Valid @RequestBody EspecialidadeFormulario form,
            UriComponentsBuilder uriBuilder) {
        
        Especialidade nova = servico.criar(form.nome(), form.descricao());
        EspecialidadeResumo resumo = new EspecialidadeResumo(nova);
        
        URI uri = uriBuilder.path("/api/referencia/especialidades/{id}")
                .buildAndExpand(resumo.id()).toUri();
        
        return ResponseEntity.created(uri).body(resumo);
    }

    @Transactional
    @PatchMapping("/{nome}")
    public ResponseEntity<EspecialidadeResumo> atualizar(
            @PathVariable String nome,
            @Valid @RequestBody AtualizarEspecialidadeFormulario form) {
        try {
            Especialidade atualizada = servico.alterar(nome, form.getNovoNome(), form.getNovaDescricao());
            return ResponseEntity.ok(new EspecialidadeResumo(atualizada));
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Transactional
    @DeleteMapping("/{nome}")
    public ResponseEntity<Void> excluir(@PathVariable String nome) {
        try {
            servico.excluir(nome);
            return ResponseEntity.noContent().build();
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}