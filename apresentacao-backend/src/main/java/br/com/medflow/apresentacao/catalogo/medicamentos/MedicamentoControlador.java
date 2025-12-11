// Localização: apresentacao-backend/src/main/java/br/com/medflow/apresentacao/catalogo/medicamentos/MedicamentoControlador.java

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
import org.springframework.transaction.annotation.Transactional; 
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
    // QUERIES (Leitura - R do CRUD)
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
    
    // GET /backend/medicamentos/revisao-pendente
    @GetMapping("/revisao-pendente")
    public List<MedicamentoResumo> pesquisarMedicamentosComRevisaoPendente() {
        return servicoConsulta.pesquisarMedicamentosComRevisaoPendente();
    }


    // =====================================================================
    // COMMANDS (Escrita/Ação - CUD do CRUD)
    // =====================================================================
    
    // 1. Comando: cadastrar
    // POST /backend/medicamentos
    @Transactional 
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
    @Transactional 
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
    // PUT /backend/medicamentos/{id}/status/{novoStatus}?temPrescricaoAtiva=false
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
    
    // 4. Comando: arquivar (Simplificação)
    // PUT /backend/medicamentos/{id}/arquivar?temPrescricaoAtiva=false
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
    // PUT /backend/medicamentos/{id}/revisao/solicitar
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
    // PUT /backend/medicamentos/{id}/revisao/aprovar
    @Transactional
    @PutMapping("/{id}/revisao/aprovar")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void aprovarRevisao(@PathVariable Integer id, @Valid @RequestBody UsuarioResponsavelFormulario revisor) {
        var medicamentoId = new MedicamentoId(id);
        var revisorId = new UsuarioResponsavelId(revisor.getResponsavelId());

        servicoDominio.aprovarRevisao(medicamentoId, revisorId);
    }

    // 7. Comando: rejeitarRevisao
    // PUT /backend/medicamentos/{id}/revisao/rejeitar
    @Transactional
    @PutMapping("/{id}/revisao/rejeitar")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void rejeitarRevisao(@PathVariable Integer id, @Valid @RequestBody UsuarioResponsavelFormulario revisor) {
        var medicamentoId = new MedicamentoId(id);
        var revisorId = new UsuarioResponsavelId(revisor.getResponsavelId());

        servicoDominio.rejeitarRevisao(medicamentoId, revisorId);
    }
}