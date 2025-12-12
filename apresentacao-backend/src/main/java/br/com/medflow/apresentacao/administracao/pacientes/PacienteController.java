package br.com.medflow.apresentacao.administracao.pacientes;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.medflow.aplicacao.administracao.pacientes.PacienteDetalhes;
import br.com.medflow.aplicacao.administracao.pacientes.PacienteResumo;
import br.com.medflow.aplicacao.administracao.pacientes.PacienteServicoAplicacao;

@RestController
@RequestMapping("/api/pacientes")
public class PacienteController {
    
    private final PacienteServicoAplicacao servicoAplicacao;
    
    public PacienteController(PacienteServicoAplicacao servicoAplicacao) {
        this.servicoAplicacao = servicoAplicacao;
    }
    
    @PostMapping
    public ResponseEntity<PacienteDetalhes> cadastrar(@RequestBody CadastrarPacienteRequest request) {
        try {
            PacienteDetalhes paciente = servicoAplicacao.cadastrarPaciente(
                request.getNome(),
                request.getCpf(),
                request.getDataNascimento(),
                request.getTelefone(),
                request.getEndereco(),
                request.getResponsavelId()
            );
            
            return ResponseEntity.status(HttpStatus.CREATED).body(paciente);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<PacienteDetalhes> atualizar(
            @PathVariable int id,
            @RequestBody AtualizarPacienteRequest request) {
        try {
            PacienteDetalhes paciente = servicoAplicacao.atualizarDadosCadastrais(
                id,
                request.getNome(),
                request.getCpf(),
                request.getDataNascimento(),
                request.getTelefone(),
                request.getEndereco(),
                request.getResponsavelId()
            );
            
            return ResponseEntity.ok(paciente);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(
            @PathVariable int id,
            @RequestParam int responsavelId,
            @RequestParam(defaultValue = "false") boolean temProntuario,
            @RequestParam(defaultValue = "false") boolean temConsulta,
            @RequestParam(defaultValue = "false") boolean temExame) {
        try {
            servicoAplicacao.removerPaciente(id, responsavelId, temProntuario, temConsulta, temExame);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<PacienteDetalhes> buscarPorId(@PathVariable int id) {
        return servicoAplicacao.buscarPorId(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/cpf/{cpf}")
    public ResponseEntity<PacienteResumo> buscarPorCpf(@PathVariable String cpf) {
        return servicoAplicacao.buscarResumoPorCpf(cpf)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping
    public ResponseEntity<List<PacienteResumo>> listarTodos() {
        List<PacienteResumo> pacientes = servicoAplicacao.listarTodos();
        return ResponseEntity.ok(pacientes);
    }
}