package br.com.medflow.apresentacao.administracao.funcionarios;

import br.com.medflow.aplicacao.administracao.funcionarios.FuncionarioServicoAplicacao;
import br.com.medflow.aplicacao.administracao.funcionarios.FuncionarioResumo;
import br.com.medflow.aplicacao.administracao.funcionarios.FuncionarioDetalhes;
import br.com.medflow.dominio.administracao.funcionarios.FuncionarioServico;
import br.com.medflow.dominio.administracao.funcionarios.StatusFuncionario;
import br.com.medflow.dominio.administracao.funcionarios.FuncionarioId;
import br.com.medflow.dominio.administracao.funcionarios.UsuarioResponsavelId;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@RestController
@RequestMapping("/backend/funcionarios")
public class FuncionarioControlador {

    private final FuncionarioServicoAplicacao servicoConsulta; 
    private final FuncionarioServico servicoDominio;

    public FuncionarioControlador(
            FuncionarioServicoAplicacao servicoConsulta, 
            FuncionarioServico servicoDominio) {
        this.servicoConsulta = servicoConsulta;
        this.servicoDominio = servicoDominio;
    }

    // =====================================================================
    // QUERIES (Leitura)
    // =====================================================================
    
    // GET /backend/funcionarios
    @GetMapping
    public List<FuncionarioResumo> pesquisarFuncionarios() {
        return servicoConsulta.pesquisarResumos();
    }
    
    // GET /backend/funcionarios/{id}
    @GetMapping("/{id}")
    public ResponseEntity<?> obterDetalhes(@PathVariable Integer id) {
        try {
            FuncionarioDetalhes detalhes = servicoConsulta.obterDetalhes(id);
            return ResponseEntity.ok(detalhes);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // GET /backend/funcionarios/status/{status}
    @GetMapping("/status/{status}")
    public List<FuncionarioResumo> pesquisarPorStatus(@PathVariable StatusFuncionario status) {
        return servicoConsulta.pesquisarPorStatus(status);
    }
    
    // GET /backend/funcionarios/funcao/{funcao}
    @GetMapping("/funcao/{funcao}")
    public List<FuncionarioResumo> pesquisarPorFuncao(@PathVariable String funcao) {
        return servicoConsulta.pesquisarPorFuncao(funcao);
    }

    // =====================================================================
    // COMMANDS (Escrita/Ação) - MÉTODOS DO FUNCIONARIOSERVICO
    // =====================================================================
    
    // 1. Comando: cadastrar
    // POST /backend/funcionarios
    @Transactional
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void cadastrarFuncionario(@Valid @RequestBody FuncionarioFormulario formulario) {
        var responsavelId = new UsuarioResponsavelId(formulario.getResponsavelId());
        
        servicoDominio.cadastrar( 
            formulario.getNome(),
            formulario.getFuncao(),
            formulario.getContato(),
            responsavelId
        );
    }
    
    // 2. Comando: atualizarDadosCadastrais
    @Transactional
    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void atualizarDadosCadastrais(
            @PathVariable Integer id, 
            @Valid @RequestBody FuncionarioAtualizacaoFormulario formulario,
            @RequestParam(defaultValue = "false") boolean temVinculosAtivosFuncao) {
        
        var funcionarioId = new FuncionarioId(id);
        var responsavelId = new UsuarioResponsavelId(formulario.getResponsavelId());
        
        servicoDominio.atualizarDadosCadastrais(
            funcionarioId, 
            formulario.getNovoNome(),
            formulario.getNovaFuncao(),
            formulario.getNovoContato(),
            responsavelId,
            temVinculosAtivosFuncao
        );
    }
    
    // 2b. Comando: atualizarCompleto (PUT - atualiza tudo incluindo status)
    @Transactional
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void atualizarCompleto(
            @PathVariable Integer id, 
            @Valid @RequestBody FuncionarioAtualizacaoCompletaFormulario formulario,
            @RequestParam(defaultValue = "false") boolean temVinculosAtivosFuncao,
            @RequestParam(defaultValue = "false") boolean temAtividadesFuturas) {
        
        var funcionarioId = new FuncionarioId(id);
        var responsavelId = new UsuarioResponsavelId(formulario.getResponsavelId());
        
        servicoDominio.atualizarCompleto(
            funcionarioId, 
            formulario.getNome(),
            formulario.getFuncao(),
            formulario.getContato(),
            formulario.getStatus(),
            responsavelId,
            temVinculosAtivosFuncao,
            temAtividadesFuturas
        );
    }

    // 3. Comando: mudarStatus
    @Transactional
    @PutMapping("/{id}/status/{novoStatus}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void mudarStatus(
            @PathVariable Integer id, 
            @PathVariable StatusFuncionario novoStatus, 
            @Valid @RequestBody UsuarioResponsavelFormulario responsavel,
            @RequestParam(defaultValue = "false") boolean temAtividadesFuturas) { 
        
        var funcionarioId = new FuncionarioId(id);
        var responsavelId = new UsuarioResponsavelId(responsavel.getResponsavelId());

        servicoDominio.mudarStatus(
            funcionarioId, 
            novoStatus, 
            responsavelId, 
            temAtividadesFuturas
        );
    }
    
    // 4. Comando: excluir
    @Transactional
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluirFuncionario(
            @PathVariable Integer id,
            @Valid @RequestBody UsuarioResponsavelFormulario responsavel,
            @RequestParam(defaultValue = "false") boolean possuiHistorico) {
        
        var funcionarioId = new FuncionarioId(id);
        var responsavelId = new UsuarioResponsavelId(responsavel.getResponsavelId());
        
        servicoDominio.excluir(funcionarioId, responsavelId, possuiHistorico);
    }
}
