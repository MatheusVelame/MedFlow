package br.com.medflow.apresentacao.catalogo.medicamentos;

import br.com.medflow.aplicacao.catalogo.medicamentos.MedicamentoServicoAplicacao;
import br.com.medflow.aplicacao.catalogo.medicamentos.MedicamentoResumo;
import br.com.medflow.dominio.catalogo.medicamentos.MedicamentoServico;
import br.com.medflow.dominio.catalogo.medicamentos.StatusMedicamento;
import br.com.medflow.dominio.catalogo.medicamentos.MedicamentoId;
import br.com.medflow.dominio.catalogo.medicamentos.UsuarioResponsavelId;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional; // NOVO IMPORT
import java.util.List;

@RestController
@RequestMapping("/backend/medicamentos")
public class MedicamentoControlador {

    private final MedicamentoServicoAplicacao servicoConsulta; 
    private final MedicamentoServico servicoDominio;

    public MedicamentoControlador(
            MedicamentoServicoAplicacao servicoConsulta, 
            MedicamentoServico servicoDominio) {
        this.servicoConsulta = servicoConsulta;
        this.servicoDominio = servicoDominio;
    }

    // =====================================================================
    // QUERIES (Leitura)
    // =====================================================================
    
    // GET /backend/medicamentos
    @GetMapping
    public List<MedicamentoResumo> pesquisarMedicamentos() {
        return servicoConsulta.pesquisarResumos();
    }
    
    // GET /backend/medicamentos/{id}
    @GetMapping("/{id}")
    public ResponseEntity<?> obterDetalhes(@PathVariable Integer id) {
        try {
            var detalhes = servicoConsulta.obterDetalhes(id);
            return ResponseEntity.ok(detalhes);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // =====================================================================
    // COMMANDS (Escrita/Ação) - MÉTODOS DO MEDICAMENTOSERVICO
    // =====================================================================
    
    // 1. Comando: cadastrar
    // POST /backend/medicamentos
    @Transactional // Garante que o cadastro seja atômico
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void cadastrarMedicamento(@Valid @RequestBody MedicamentoFormulario formulario) {
        var responsavelId = new UsuarioResponsavelId(formulario.getResponsavelId());
        
        servicoDominio.cadastrar( 
            formulario.getNome(),
            formulario.getUsoPrincipal(),
            formulario.getContraindicacoes(),
            responsavelId
        );
    }
    
    // 2. Comando: atualizarUsoPrincipal
    @Transactional // <--- CORREÇÃO CRÍTICA APLICADA AQUI
    @PatchMapping("/{id}/uso-principal") 
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void atualizarUsoPrincipal(@PathVariable Integer id, @Valid @RequestBody UsoPrincipalFormulario formulario) {
        var medicamentoId = new MedicamentoId(id);
        var responsavelId = new UsuarioResponsavelId(formulario.getResponsavelId());
        
        servicoDominio.atualizarUsoPrincipal(
            medicamentoId, 
            formulario.getNovoUsoPrincipal(),
            responsavelId
        );
    }

    // 3. Comando: mudarStatus (Geral)
    @Transactional
    @PutMapping("/{id}/status/{novoStatus}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void mudarStatus(
            @PathVariable Integer id, 
            @PathVariable StatusMedicamento novoStatus, 
            @Valid @RequestBody UsuarioResponsavelFormulario responsavel,
            @RequestParam(defaultValue = "false") boolean temPrescricaoAtiva) { 
        
        var medicamentoId = new MedicamentoId(id);
        var responsavelId = new UsuarioResponsavelId(responsavel.getResponsavelId());

        servicoDominio.mudarStatus(
            medicamentoId, 
            novoStatus, 
            responsavelId, 
            temPrescricaoAtiva
        );
    }
    
    // 4. Comando: arquivar
    @Transactional
    @PutMapping("/{id}/arquivar")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void arquivarMedicamento(
            @PathVariable Integer id, 
            @Valid @RequestBody UsuarioResponsavelFormulario responsavel,
            @RequestParam(defaultValue = "false") boolean temPrescricaoAtiva) { 
        
        var medicamentoId = new MedicamentoId(id);
        var responsavelId = new UsuarioResponsavelId(responsavel.getResponsavelId());

        servicoDominio.arquivar(medicamentoId, responsavelId, temPrescricaoAtiva);
    }
    
    // 5. Comando: solicitarRevisaoContraindicacoes
    @Transactional
    @PutMapping("/{id}/revisao/solicitar")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void solicitarRevisao(@PathVariable Integer id, @Valid @RequestBody RevisaoFormulario formulario) {
        var medicamentoId = new MedicamentoId(id);
        var responsavelId = new UsuarioResponsavelId(formulario.getResponsavelId());

        servicoDominio.solicitarRevisaoContraindicacoes(
            medicamentoId, 
            formulario.getNovaContraindicacao(), 
            responsavelId
        );
    }

    // 6. Comando: aprovarRevisao
    @Transactional
    @PutMapping("/{id}/revisao/aprovar")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void aprovarRevisao(@PathVariable Integer id, @Valid @RequestBody UsuarioResponsavelFormulario revisor) {
        var medicamentoId = new MedicamentoId(id);
        var revisorId = new UsuarioResponsavelId(revisor.getResponsavelId());

        servicoDominio.aprovarRevisao(medicamentoId, revisorId);
    }

    // 7. Comando: rejeitarRevisao
    @Transactional
    @PutMapping("/{id}/revisao/rejeitar")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void rejeitarRevisao(@PathVariable Integer id, @Valid @RequestBody UsuarioResponsavelFormulario revisor) {
        var medicamentoId = new MedicamentoId(id);
        var revisorId = new UsuarioResponsavelId(revisor.getResponsavelId());

        servicoDominio.rejeitarRevisao(medicamentoId, revisorId);
    }
}