package br.com.medflow.apresentacao.referencia.tiposExames;

import br.com.medflow.aplicacao.referencia.tiposExames.TipoExameServicoAplicacao;
import br.com.medflow.aplicacao.referencia.tiposExames.TipoExameResumo;
import br.com.medflow.dominio.referencia.tiposExames.TipoExameServico;
import br.com.medflow.dominio.referencia.tiposExames.TipoExameId;
import br.com.medflow.dominio.referencia.tiposExames.UsuarioResponsavelId;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@RestController
@RequestMapping("/backend/tipos-exames")
public class TipoExameControlador {

    private final TipoExameServicoAplicacao servicoConsulta;
    private final TipoExameServico servicoDominio;

    public TipoExameControlador(
            TipoExameServicoAplicacao servicoConsulta,
            TipoExameServico servicoDominio) {
        this.servicoConsulta = servicoConsulta;
        this.servicoDominio = servicoDominio;
    }

    // =====================================================================
    // QUERIES (Leitura)
    // =====================================================================
    
    // GET /backend/tipos-exames
    @GetMapping
    public List<TipoExameResumo> pesquisarTiposExames() {
        return servicoConsulta.pesquisarResumos();
    }
    
    // GET /backend/tipos-exames/{id}
    @GetMapping("/{id}")
    public ResponseEntity<?> obterDetalhes(@PathVariable Integer id) {
        try {
            var detalhes = servicoConsulta.obterDetalhes(id);
            return ResponseEntity.ok(detalhes);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // GET /backend/tipos-exames/inativos
    @GetMapping("/inativos")
    public List<TipoExameResumo> pesquisarTiposExamesInativos() {
        return servicoConsulta.pesquisarTiposExamesInativos();
    }

    // =====================================================================
    // COMMANDS (Escrita/Ação)
    // =====================================================================
    
    // 1. Comando: cadastrar
    // POST /backend/tipos-exames
    @Transactional
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void cadastrarTipoExame(@Valid @RequestBody TipoExameFormulario formulario) {
        var responsavelId = new UsuarioResponsavelId(formulario.getResponsavelId());
        
        servicoDominio.cadastrar(
            formulario.getCodigo(),
            formulario.getDescricao(),
            formulario.getEspecialidade(),
            formulario.getValor(),
            responsavelId
        );
    }
    
    // 2. Comando: atualizarDescricao
    // PATCH /backend/tipos-exames/{id}/descricao
    @Transactional
    @PatchMapping("/{id}/descricao")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void atualizarDescricao(
            @PathVariable Integer id,
            @Valid @RequestBody AtualizarDescricaoFormulario formulario,
            @RequestParam(defaultValue = "false") boolean temAgendamentos) {
        
        var tipoExameId = new TipoExameId(id);
        var responsavelId = new UsuarioResponsavelId(formulario.getResponsavelId());
        
        servicoDominio.atualizarDescricao(
            tipoExameId,
            formulario.getNovaDescricao(),
            responsavelId,
            temAgendamentos
        );
    }
    
    // 3. Comando: atualizarEspecialidade
    // PATCH /backend/tipos-exames/{id}/especialidade
    @Transactional
    @PatchMapping("/{id}/especialidade")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void atualizarEspecialidade(
            @PathVariable Integer id,
            @Valid @RequestBody AtualizarEspecialidadeFormulario formulario,
            @RequestParam(defaultValue = "false") boolean temAgendamentos) {
        
        var tipoExameId = new TipoExameId(id);
        var responsavelId = new UsuarioResponsavelId(formulario.getResponsavelId());
        
        servicoDominio.atualizarEspecialidade(
            tipoExameId,
            formulario.getNovaEspecialidade(),
            responsavelId,
            temAgendamentos
        );
    }
    
    // 4. Comando: atualizarValor
    // PATCH /backend/tipos-exames/{id}/valor
    @Transactional
    @PatchMapping("/{id}/valor")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void atualizarValor(
            @PathVariable Integer id,
            @Valid @RequestBody AtualizarValorFormulario formulario,
            @RequestParam(defaultValue = "false") boolean temAgendamentos) {
        
        var tipoExameId = new TipoExameId(id);
        var responsavelId = new UsuarioResponsavelId(formulario.getResponsavelId());
        
        servicoDominio.atualizarValor(
            tipoExameId,
            formulario.getNovoValor(),
            responsavelId,
            temAgendamentos
        );
    }
    
    // 5. Comando: inativar
    // PUT /backend/tipos-exames/{id}/inativar
    @Transactional
    @PutMapping("/{id}/inativar")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void inativarTipoExame(
            @PathVariable Integer id,
            @Valid @RequestBody UsuarioResponsavelFormulario responsavel,
            @RequestParam(defaultValue = "false") boolean temAgendamentosFuturos) {
        
        var tipoExameId = new TipoExameId(id);
        var responsavelId = new UsuarioResponsavelId(responsavel.getResponsavelId());
        
        servicoDominio.inativar(tipoExameId, responsavelId, temAgendamentosFuturos);
    }
    
    // 6. Comando: excluir
    // DELETE /backend/tipos-exames/{id}
    @Transactional
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluirTipoExame(
            @PathVariable Integer id,
            @Valid @RequestBody UsuarioResponsavelFormulario responsavel,
            @RequestParam(defaultValue = "false") boolean temAgendamentos) {
        
        var tipoExameId = new TipoExameId(id);
        var responsavelId = new UsuarioResponsavelId(responsavel.getResponsavelId());
        
        servicoDominio.excluir(tipoExameId, responsavelId, temAgendamentos);
    }
}