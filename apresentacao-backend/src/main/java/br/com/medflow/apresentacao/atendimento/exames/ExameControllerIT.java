package br.com.medflow.apresentacao.atendimento.exames;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.context.ActiveProfiles;

import br.com.medflow.dominio.atendimento.exames.StatusExame;
import br.com.medflow.dominio.atendimento.exames.VerificadorExternoServico;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test") // Usa o application-test.properties (H2, etc)
@ExtendWith(OutputCaptureExtension.class) // Permite capturar logs do console (System.out)
public class ExameControllerIT {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    // Mockamos o serviço externo para não depender de dados de outros módulos (Pacientes, Médicos)
    // O foco aqui é testar o fluxo do Exame e o Proxy
    @MockBean
    private VerificadorExternoServico verificadorExterno;

    private String baseUrl() {
        return "http://localhost:" + port + "/exames";
    }

    @BeforeEach
    public void setup() {
        // Configura o RestTemplate para suportar PATCH
        restTemplate.getRestTemplate().setRequestFactory(new HttpComponentsClientHttpRequestFactory());

        // Configura o Mock para retornar TRUE para todas as validações externas
        when(verificadorExterno.pacienteEstaCadastrado(anyLong())).thenReturn(true);
        when(verificadorExterno.medicoEstaCadastrado(anyLong())).thenReturn(true);
        when(verificadorExterno.medicoEstaAtivo(anyLong())).thenReturn(true);
        when(verificadorExterno.tipoExameEstaCadastrado(anyString())).thenReturn(true);
        // Simula disponibilidade sempre positiva
        when(verificadorExterno.medicoEstaDisponivel(anyLong(), any(LocalDateTime.class))).thenReturn(true);
    }

    @Test
    @DisplayName("Deve agendar exame com sucesso e gerar log de auditoria no Proxy")
    public void agendarExame_fluxo_sucesso(CapturedOutput output) {
        // DADOS DO CENÁRIO
        Long pacienteId = 100L;
        Long medicoId = 200L;
        String tipoExame = "HEMOGRAMA";
        LocalDateTime dataHora = LocalDateTime.now().plusDays(1);
        Long responsavelId = 1L;

        AgendamentoExameRequest request = new AgendamentoExameRequest(
            pacienteId, medicoId, tipoExame, dataHora, responsavelId
        );

        // 1. POST - Agendar
        ResponseEntity<ExameResponse> response = restTemplate.postForEntity(baseUrl(), request, ExameResponse.class);

        // VALIDAÇÕES HTTP E CORPO
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo("AGENDADO");
        assertThat(response.getBody().tipoExame()).isEqualTo(tipoExame);

        // VALIDAÇÃO DO PROXY (LOG)
        assertThat(output.getOut())
            .as("O log de auditoria do Proxy deve estar presente no console")
            .contains("[AUDITORIA - PROXY] Operação: AGENDAR_EXAME")
            .contains("PacienteID: " + pacienteId);
    }

    @Test
    @DisplayName("Deve atualizar agendamento e gerar log de auditoria")
    public void atualizarExame_fluxo_sucesso(CapturedOutput output) {
        // 1. CRIAR UM EXAME INICIALMENTE
        ExameResponse exameCriado = criarExamePadrao();
        Long exameId = exameCriado.id();

        // 2. PREPARAR ATUALIZAÇÃO
        LocalDateTime novaData = LocalDateTime.now().plusDays(5);
        AtualizacaoExameRequest updateRequest = new AtualizacaoExameRequest(
            300L, // Novo médico
            "RAIO-X", // Novo tipo
            novaData,
            1L
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<AtualizacaoExameRequest> entity = new HttpEntity<>(updateRequest, headers);

        // 3. PUT - Atualizar
        ResponseEntity<ExameResponse> response = restTemplate.exchange(
            baseUrl() + "/" + exameId, 
            HttpMethod.PUT, 
            entity, 
            ExameResponse.class
        );

        // VALIDAÇÕES
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().tipoExame()).isEqualTo("RAIO-X");
        
        // VALIDAÇÃO DO PROXY
        assertThat(output.getOut())
            .contains("[AUDITORIA - PROXY] Operação: ATUALIZAR_AGENDAMENTO")
            .contains("ExameID: " + exameId);
    }

    @Test
    @DisplayName("Deve cancelar exame e gerar log de auditoria")
    public void cancelarExame_fluxo_sucesso(CapturedOutput output) {
        // 1. CRIAR EXAME
        ExameResponse exameCriado = criarExamePadrao();
        Long exameId = exameCriado.id();

        // 2. PATCH - Cancelar
        CancelamentoExameRequest cancelRequest = new CancelamentoExameRequest("Paciente desistiu", 1L);
        
        // Configurando suporte a PATCH via headers/entity se necessário, ou usando postForObject com method override se o RestTemplate padrão não suportar. 
        // Como configuramos HttpComponentsClientHttpRequestFactory no setup, o PATCH deve funcionar nativamente.
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CancelamentoExameRequest> entity = new HttpEntity<>(cancelRequest, headers);

        ResponseEntity<ExameResponse> response = restTemplate.exchange(
            baseUrl() + "/" + exameId + "/cancelamento",
            HttpMethod.PATCH,
            entity,
            ExameResponse.class
        );

        // VALIDAÇÕES
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().status()).isEqualTo(StatusExame.CANCELADO.name());

        // VALIDAÇÃO DO PROXY
        assertThat(output.getOut())
            .contains("[AUDITORIA - PROXY] Operação: CANCELAR_AGENDAMENTO")
            .contains("Motivo: Paciente desistiu");
    }

    @Test
    @DisplayName("Deve tentar excluir exame (DELETE) e gerar log")
    public void excluirExame_fluxo_sucesso(CapturedOutput output) {
        // 1. CRIAR EXAME
        ExameResponse exameCriado = criarExamePadrao();
        Long exameId = exameCriado.id();

        // 2. DELETE
        // Passando param responsavelId na URL
        ResponseEntity<Void> response = restTemplate.exchange(
            baseUrl() + "/" + exameId + "?responsavelId=1",
            HttpMethod.DELETE,
            null,
            Void.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        // VALIDAÇÃO DO PROXY
        assertThat(output.getOut())
            .contains("[AUDITORIA - PROXY] Operação: TENTATIVA_EXCLUSAO")
            .contains("Alvo ExameID: " + exameId);
    }

    // --- MÉTODOS AUXILIARES ---

    private ExameResponse criarExamePadrao() {
        AgendamentoExameRequest request = new AgendamentoExameRequest(
            100L, 200L, "HEMOGRAMA", LocalDateTime.now().plusDays(2), 1L
        );
        return restTemplate.postForObject(baseUrl(), request, ExameResponse.class);
    }
}