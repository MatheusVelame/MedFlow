package br.com.medflow.apresentacao.referencia.especialidades;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class EspecialidadeControllerAdditionalIT {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl() {
        return "http://localhost:" + port + "/api/referencia/especialidades";
    }

    @Test
    public void listar_deve_retornar_lista_vazia_ou_com_elementos() {
        // Garante que o endpoint de listagem responde com 200 e um array (possivelmente vazio)
        ResponseEntity<EspecialidadeResumo[]> resp = restTemplate.getForEntity(baseUrl(), EspecialidadeResumo[].class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        EspecialidadeResumo[] body = resp.getBody();
        assertThat(body).isNotNull();
    }

    @Test
    public void criar_com_nome_nulo_ou_branco_deve_retornar_bad_request_e_erro_de_validacao() {
        // Nome em branco deve provocar 400
        Map<String, Object> body = Map.of("nome", "", "descricao", "Sem nome");
        ResponseEntity<String> resp = restTemplate.postForEntity(baseUrl(), body, String.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        // Também testar ausência do campo nome
        Map<String, Object> body2 = Map.of("descricao", "Sem nome");
        ResponseEntity<String> resp2 = restTemplate.postForEntity(baseUrl(), body2, String.class);
        assertThat(resp2.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}
