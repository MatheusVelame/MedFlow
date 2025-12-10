package br.com.medflow.apresentacao.atendimento.consultas;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.medflow.aplicacao.atendimento.consultas.ConsultaDetalhes;
import br.com.medflow.aplicacao.atendimento.consultas.ConsultaResumo;
import br.com.medflow.aplicacao.atendimento.consultas.ConsultaServicoAplicacao;

// Este é o Controller REST (Adapter) que expõe as funcionalidades da API.
// Ele delega TODA a lógica ao Serviço de Aplicação (Queries).
@RestController
@RequestMapping("/v1/consultas") // Define o prefixo de URL para este recurso
public class ConsultaControlador {

    private final ConsultaServicoAplicacao servico;

    // Injeção de dependência do Serviço de Aplicação (Porta da camada Aplicação)
    public ConsultaControlador(ConsultaServicoAplicacao servico) {
        this.servico = servico;
    }

    /**
     * Endpoint para listar todos os resumos de consultas.
     * URI: GET /v1/consultas
     * @return Lista de ConsultaResumo (DTOs de leitura otimizados)
     */
    @GetMapping
    public ResponseEntity<List<ConsultaResumo>> pesquisarResumos() {
        // Chama o Serviço de Aplicação
        List<ConsultaResumo> resumos = servico.pesquisarResumos();
        
        // Retorna o resultado com status HTTP 200 OK
        return ResponseEntity.ok(resumos);
    }

    /**
     * Endpoint para obter os detalhes de uma consulta específica.
     * URI: GET /v1/consultas/{id}
     * @param id O ID da consulta.
     * @return ConsultaDetalhes (DTO de leitura completo)
     */
    @GetMapping("/{id}")
    public ResponseEntity<ConsultaDetalhes> obterDetalhes(@PathVariable Integer id) {
        // A busca é delegada ao Serviço de Aplicação.
        // Se a consulta não for encontrada, o serviço já lança uma exceção tratável (padrão do projeto).
        ConsultaDetalhes detalhes = servico.obterDetalhes(id);
        return ResponseEntity.ok(detalhes);
    }

    /**
     * Endpoint para listar consultas filtradas por ID do paciente.
     * URI: GET /v1/consultas/paciente/{pacienteId}
     * @param pacienteId O ID do paciente.
     * @return Lista de ConsultaResumo.
     */
    @GetMapping("/paciente/{pacienteId}")
    public ResponseEntity<List<ConsultaResumo>> pesquisarPorPaciente(@PathVariable Integer pacienteId) {
        List<ConsultaResumo> resumos = servico.pesquisarConsultasPorPaciente(pacienteId);
        return ResponseEntity.ok(resumos);
    }
}