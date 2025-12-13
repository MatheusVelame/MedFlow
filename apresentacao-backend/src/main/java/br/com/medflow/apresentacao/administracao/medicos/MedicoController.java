// Localização: apresentacao-backend/src/main/java/br/com/medflow/apresentacao/administracao/medicos/MedicoController.java

package br.com.medflow.apresentacao.administracao.medicos;

import br.com.medflow.aplicacao.administracao.medicos.*;
import br.com.medflow.dominio.administracao.funcionarios.StatusFuncionario;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST para gerenciamento de Médicos.
 *
 * CRUD COMPLETO:
 * - GET    /api/medicos              - Lista todos
 * - GET    /api/medicos/{id}         - Busca por ID
 * - GET    /api/medicos/crm/{crm}    - Busca por CRM
 * - GET    /api/medicos/buscar       - Busca geral
 * - GET    /api/medicos/especialidade/{id} - Por especialidade
 * - GET    /api/medicos/status/{status}    - Por status
 * - POST   /api/medicos              - Cadastra novo
 * - PUT    /api/medicos/{id}         - Atualiza
 * - DELETE /api/medicos/{id}         - Remove (inativa)
 */
@RestController
@RequestMapping("/api/medicos")
@CrossOrigin(origins = "*")
public class MedicoController {

    private final MedicoServicoAplicacao medicoServico;

    public MedicoController(MedicoServicoAplicacao medicoServico) {
        this.medicoServico = medicoServico;
    }

    // ========== QUERIES (GET) ==========

    /**
     * Health check.
     * GET /api/medicos/health
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Médicos API está funcionando! 🎉");
    }

    /**
     * Lista todos os médicos.
     * GET /api/medicos
     */
    @GetMapping
    public ResponseEntity<List<MedicoResumo>> listarTodos() {
        List<MedicoResumo> medicos = medicoServico.listarTodos();
        return ResponseEntity.ok(medicos);
    }

    /**
     * Busca médico por ID.
     * GET /api/medicos/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<MedicoDetalhes> buscarPorId(@PathVariable Integer id) {
        MedicoDetalhes medico = medicoServico.obterPorId(id);

        if (medico == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(medico);
    }

    /**
     * Busca médico por CRM.
     * GET /api/medicos/crm/{crm}
     */
    @GetMapping("/crm/{crm}")
    public ResponseEntity<MedicoDetalhes> buscarPorCrm(@PathVariable String crm) {
        MedicoDetalhes medico = medicoServico.obterPorCrm(crm);

        if (medico == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(medico);
    }

    /**
     * Busca geral.
     * GET /api/medicos/buscar?termo=X
     */
    @GetMapping("/buscar")
    public ResponseEntity<List<MedicoResumo>> buscarGeral(
            @RequestParam(name = "termo") String termoBusca) {

        List<MedicoResumo> medicos = medicoServico.buscarGeral(termoBusca);
        return ResponseEntity.ok(medicos);
    }

    /**
     * Lista por especialidade.
     * GET /api/medicos/especialidade/{especialidadeId}
     */
    @GetMapping("/especialidade/{especialidadeId}")
    public ResponseEntity<List<MedicoResumo>> listarPorEspecialidade(
            @PathVariable Integer especialidadeId) {

        List<MedicoResumo> medicos = medicoServico.listarPorEspecialidade(especialidadeId);
        return ResponseEntity.ok(medicos);
    }

    /**
     * Lista por status.
     * GET /api/medicos/status/{status}
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<MedicoResumo>> listarPorStatus(
            @PathVariable String status) {

        try {
            StatusFuncionario statusEnum = StatusFuncionario.valueOf(status.toUpperCase());
            List<MedicoResumo> medicos = medicoServico.listarPorStatus(statusEnum);
            return ResponseEntity.ok(medicos);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ========== COMMANDS (POST/PUT/DELETE) ==========

    /**
     * Cadastra novo médico.
     * POST /api/medicos
     */
    @PostMapping
    public ResponseEntity<MedicoDetalhes> cadastrar(
            @RequestBody MedicoCadastroRequest request) {

        try {
            MedicoDetalhes medico = medicoServico.cadastrar(request);

            if (medico == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }

            return ResponseEntity.status(HttpStatus.CREATED).body(medico);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Atualiza médico existente.
     * PUT /api/medicos/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<MedicoDetalhes> atualizar(
            @PathVariable Integer id,
            @RequestBody MedicoAtualizacaoRequest request) {

        try {
            MedicoDetalhes medico = medicoServico.atualizar(id, request);

            if (medico == null) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(medico);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Remove médico (inativa).
     * DELETE /api/medicos/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Integer id) {
        try {
            boolean removido = medicoServico.remover(id);

            if (!removido) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.noContent().build();

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}