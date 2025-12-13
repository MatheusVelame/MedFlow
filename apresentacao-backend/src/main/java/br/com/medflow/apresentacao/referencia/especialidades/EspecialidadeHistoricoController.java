package br.com.medflow.apresentacao.referencia.especialidades;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

import br.com.medflow.aplicacao.referencia.especialidades.IEspecialidadeHistoricoAppService;

@RestController
@RequestMapping("/api/referencia/especialidades")
public class EspecialidadeHistoricoController {

    private final IEspecialidadeHistoricoAppService service;

    public EspecialidadeHistoricoController(IEspecialidadeHistoricoAppService service) {
        this.service = service;
    }

    @GetMapping("/{id}/historico")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<EspecialidadeHistoricoDto>> listarHistorico(@PathVariable("id") Integer id) {
        List<br.com.medflow.aplicacao.referencia.especialidades.EspecialidadeHistoricoDto> appDtos = service.listarPorEspecialidade(id);
        List<EspecialidadeHistoricoDto> apresentacao = appDtos.stream().map(a -> {
            EspecialidadeHistoricoDto p = new EspecialidadeHistoricoDto();
            p.setId(a.getId());
            p.setEspecialidadeId(a.getEspecialidadeId());
            p.setCampo(a.getCampo());
            p.setValorAnterior(a.getValorAnterior());
            p.setNovoValor(a.getNovoValor());
            p.setDataHora(a.getDataHora());
            p.setTipo(a.getTipo());
            return p;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(apresentacao);
    }
}