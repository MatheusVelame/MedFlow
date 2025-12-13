package br.com.medflow.apresentacao.referencia.especialidades;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/referencia/especialidades")
public class EspecialidadeHistoricoController {

    private final EspecialidadeHistoricoService service;

    public EspecialidadeHistoricoController(EspecialidadeHistoricoService service) {
        this.service = service;
    }

    @GetMapping("/{id}/historico")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<EspecialidadeHistoricoDto>> listarHistorico(@PathVariable("id") Integer id) {
        List<EspecialidadeHistoricoDto> lista = service.listarPorEspecialidade(id);
        return ResponseEntity.ok(lista);
    }
}