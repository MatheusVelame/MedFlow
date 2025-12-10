// Localização: apresentacao-backend/src/main/java/br/com/medflow/apresentacao/atendimento/consultas/ConsultaControlador.java

package br.com.medflow.apresentacao.atendimento.consultas;

import br.com.medflow.aplicacao.atendimento.consultas.ConsultaServicoAplicacao;
import br.com.medflow.aplicacao.atendimento.consultas.ConsultaResumo;
import br.com.medflow.dominio.atendimento.consultas.ConsultaServico;
import br.com.medflow.dominio.atendimento.consultas.ConsultaId;
import br.com.medflow.dominio.atendimento.consultas.StatusConsulta;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@RestController
@RequestMapping("/backend/consultas")
public class ConsultaControlador {

    private final ConsultaServicoAplicacao servicoConsulta; // Para Queries (Leitura)
    private final ConsultaServico servicoDominio;           // Para Commands (Escrita/CUD)

    // Injeção dos dois serviços
    public ConsultaControlador(
            ConsultaServicoAplicacao servicoConsulta, 
            ConsultaServico servicoDominio) {
        this.servicoConsulta = servicoConsulta;
        this.servicoDominio = servicoDominio;
    }

    // =====================================================================
    // QUERIES (Leitura - R do CRUD)
    // =====================================================================
    
    // GET /backend/consultas
    @GetMapping
    public List<ConsultaResumo> pesquisarConsultas() {
        return servicoConsulta.pesquisarResumos();
    }
    
    // GET /backend/consultas/{id}
    @GetMapping("/{id}")
    public ResponseEntity<?> obterDetalhes(@PathVariable Integer id) {
        try {
            var detalhes = servicoConsulta.obterDetalhes(id);
            return ResponseEntity.ok(detalhes);
        } catch (RuntimeException e) {
            // Se o serviço de aplicação não encontrar, retorna 404
            return ResponseEntity.notFound().build();
        }
    }

    // GET /backend/consultas/agendadas
    @GetMapping("/agendadas")
    public List<ConsultaResumo> listarAgendadas() {
        return servicoConsulta.pesquisarConsultasAgendadas();
    }
    
    // =====================================================================
    // COMMANDS (Escrita/Ação - CUD do CRUD)
    // =====================================================================
    
    // 1. Comando: agendar (C)
    // POST /backend/consultas
    @Transactional // Garante que a operação seja atômica
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void agendarConsulta(@Valid @RequestBody AgendamentoFormulario formulario) {
        
        servicoDominio.agendar( 
            formulario.getDataHora(),
            formulario.getDescricao(),
            formulario.getPacienteId(),
            formulario.getMedicoId()
        );
    }
    
    // 2. Comando: mudarStatus (U)
    // PUT /backend/consultas/{id}/status
    @Transactional
    @PutMapping("/{id}/status") 
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void mudarStatus(@PathVariable Integer id, @Valid @RequestBody StatusFormulario formulario) {
        var consultaId = new ConsultaId(id);
        
        servicoDominio.mudarStatus(
            consultaId, 
            formulario.getNovoStatus()
        );
    }
}