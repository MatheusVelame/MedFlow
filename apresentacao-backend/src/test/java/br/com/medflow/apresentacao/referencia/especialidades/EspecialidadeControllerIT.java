package br.com.medflow.apresentacao.referencia.especialidades;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class EspecialidadeControllerIT {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl() {
        return "http://localhost:" + port + "/api/referencia/especialidades";
    }

    @BeforeEach
    public void setup() {
        // Nothing special: Flyway + H2 are configured via application-test.properties
    }

    @Test
    public void criar_obter_atualizar_excluir_flow() {
        // 1) Criar
        Map<String, Object> body = Map.of("nome", "Cardiologia IT", "descricao", "Especialidade de cardiologia");
        ResponseEntity<EspecialidadeResumo> postResp = restTemplate.postForEntity(baseUrl(), body, EspecialidadeResumo.class);
        assertThat(postResp.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        EspecialidadeResumo criado = postResp.getBody();
        assertThat(criado).isNotNull();
        assertThat(criado.id()).isNotNull();

        Integer id = criado.id();

        // 2) Obter
        ResponseEntity<EspecialidadeDetalhes> getResp = restTemplate.getForEntity(baseUrl() + "/{id}", EspecialidadeDetalhes.class, id);
        assertThat(getResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        EspecialidadeDetalhes detalhes = getResp.getBody();
        assertThat(detalhes).isNotNull();
        assertThat(detalhes.nome()).isEqualTo("Cardiologia IT");

        // 3) Atualizar (PATCH)
        Map<String, Object> patchBody = Map.of("novoNome", "Cardio IT", "novaDescricao", "Atualizada");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String,Object>> patchEntity = new HttpEntity<>(patchBody, headers);
        ResponseEntity<EspecialidadeResumo> patchResp = restTemplate.exchange(baseUrl() + "/{id}", HttpMethod.PATCH, patchEntity, EspecialidadeResumo.class, id);
        assertThat(patchResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        EspecialidadeResumo atualizado = patchResp.getBody();
        assertThat(atualizado).isNotNull();
        assertThat(atualizado.nome()).isEqualTo("Cardio IT");

        // 4) Excluir
        ResponseEntity<Void> deleteResp = restTemplate.exchange(baseUrl() + "/{id}", HttpMethod.DELETE, null, Void.class, id);
        assertThat(deleteResp.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        // 5) Verificar que não existe mais
        ResponseEntity<EspecialidadeDetalhes> getAfterDelete = restTemplate.getForEntity(baseUrl() + "/{id}", EspecialidadeDetalhes.class, id);
        assertThat(getAfterDelete.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void criar_com_nome_invalido_deve_retornar_bad_request() {
        Map<String, Object> body = Map.of("nome", "", "descricao", "Sem nome");
        ResponseEntity<String> resp = restTemplate.postForEntity(baseUrl(), body, String.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void criar_duplicado_deve_retornar_bad_request() {
        Map<String, Object> body = Map.of("nome", "Dermatologia IT", "descricao", "Desc");
        ResponseEntity<EspecialidadeResumo> first = restTemplate.postForEntity(baseUrl(), body, EspecialidadeResumo.class);
        assertThat(first.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        ResponseEntity<String> second = restTemplate.postForEntity(baseUrl(), body, String.class);
        assertThat(second.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}
