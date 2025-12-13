package br.com.medflow.apresentacao.atendimento.exames;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import br.com.medflow.dominio.atendimento.exames.Exame;
import br.com.medflow.dominio.atendimento.exames.ExameId;
import br.com.medflow.dominio.atendimento.exames.IExameServico;
import br.com.medflow.dominio.atendimento.exames.ExameRepositorio;
import br.com.medflow.dominio.atendimento.exames.UsuarioResponsavelId;

@RestController
@RequestMapping("/exames")
@Tag(name = "Exames", description = "Operações para gerenciamento de agendamentos de exames")
public class ExameControlador {

    private final IExameServico exameServico;
    private final ExameRepositorio exameRepositorio;

    public ExameControlador(IExameServico exameServico, ExameRepositorio exameRepositorio) {
        this.exameServico = exameServico;
        this.exameRepositorio = exameRepositorio;
    }

    @GetMapping
    public ResponseEntity<List<ExameResponse>> listar() {
        List<Exame> todos = exameRepositorio.listarTodos();
        List<ExameResponse> resp = todos.stream().map(ExameResponse::de).collect(Collectors.toList());
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExameDetalheResponse> buscar(@PathVariable Long id) {
        Exame exame = exameRepositorio.obterPorId(new ExameId(id))
            .orElseThrow(() -> new br.com.medflow.dominio.atendimento.exames.ExcecaoDominio("Agendamento de exame não encontrado"));
        return ResponseEntity.ok(ExameDetalheResponse.de(exame));
    }

    @PostMapping
    public ResponseEntity<ExameResponse> agendar(@Valid @RequestBody AgendamentoExameRequest request,
                                                 UriComponentsBuilder uriBuilder) {
        
        Exame exameCriado = exameServico.agendarExame(
            request.pacienteId(),
            request.medicoId(),
            request.tipoExame(),
            request.dataHora(),
            new UsuarioResponsavelId(request.responsavelId())
        );

        URI uri = uriBuilder.path("/exames/{id}").buildAndExpand(exameCriado.getId().getValor()).toUri();
        return ResponseEntity.created(uri).body(ExameResponse.de(exameCriado));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar agendamento de exame", description = "Permite alterar data/hora, médico (somente se ativo) e tipo de exame. Registra histórico de alterações.")
    @ApiResponse(responseCode = "200", description = "Agendamento atualizado com sucesso", content = @Content(schema = @Schema(implementation = ExameResponse.class)))
    public ResponseEntity<ExameResponse> atualizar(@PathVariable Long id,
                                                   @Valid @RequestBody AtualizacaoExameRequest request) {
        
        Exame exameAtualizado = exameServico.atualizarAgendamento(
            new ExameId(id),
            request.medicoId(),
            request.tipoExame(),
            request.dataHora(),
            new UsuarioResponsavelId(request.responsavelId()),
            request.observacoes()
        );

        return ResponseEntity.ok(ExameResponse.de(exameAtualizado));
    }

    /**
     * Endpoint para exclusão.
     * Conforme a regra de negócio (RN12), pode resultar em exclusão física ou cancelamento lógico.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> tentarExcluir(@PathVariable Long id, 
                                              @RequestParam(name = "responsavelId", required = true) Long responsavelId) {
        
        exameServico.tentarExcluirAgendamento(
            new ExameId(id), 
            new UsuarioResponsavelId(responsavelId)
        );
        
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Endpoint específico para cancelamento manual com motivo.
     */
    @PatchMapping("/{id}/cancelamento")
    public ResponseEntity<ExameResponse> cancelar(@PathVariable Long id,
                                                  @Valid @RequestBody CancelamentoExameRequest request) {
        
        Exame exameCancelado = exameServico.cancelarAgendamento(
            new ExameId(id),
            request.motivo(),
            new UsuarioResponsavelId(request.responsavelId())
        );
        
        return ResponseEntity.ok(ExameResponse.de(exameCancelado));
    }
}