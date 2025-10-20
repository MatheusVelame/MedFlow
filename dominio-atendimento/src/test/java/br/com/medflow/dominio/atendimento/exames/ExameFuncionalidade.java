package br.com.medflow.dominio.atendimento.exames;

import static org.junit.jupiter.api.Assertions.*;

import io.cucumber.java.pt.*;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Step Definitions para os cenários de BDD de Gerenciamento de Exames.
 */
public class ExameFuncionalidade extends ExameFuncionalidadeBase {

    private Exame exame;
    private Exception excecaoCapturada;

    @Given("existe um exame com status {string}")
    public void existe_um_exame_com_status(String status) {
        this.exame = new Exame(1L, pacientes.get("Lucas"), medicos.get("Dr. Carlos"),
                tiposExame.get("Raio-X"), LocalDate.now().plusDays(1), LocalTime.of(10, 0));
        this.exame.setStatus(StatusExame.valueOf(status.toUpperCase().replace(" ", "_")));
        repositorio.salvar(exame);
    }

    @When("o funcionário solicita a exclusão do exame")
    public void funcionario_solicita_exclusao() {
        try {
            exame.excluir();
            repositorio.remover(exame.getId());
        } catch (Exception e) {
            excecaoCapturada = e;
        }
    }

    @Then("o exame é excluído com sucesso")
    public void exame_excluido_com_sucesso() {
        assertFalse(repositorio.obterPorId(exame.getId()).isPresent());
    }

    @Then("o sistema exibe uma mensagem de erro informando que o exame não pode ser excluído")
    public void sistema_exibe_mensagem_erro_exclusao() {
        assertNotNull(excecaoCapturada);
        assertTrue(excecaoCapturada.getMessage().toLowerCase().contains("não pode ser excluído"));
    }

    @Given("existe um exame {string} com laudo vinculado")
    public void existe_exame_com_laudo(String status) {
        exame = new Exame(2L, pacientes.get("Ana"), medicos.get("Dr. Carlos"),
                tiposExame.get("Raio-X"), LocalDate.now().plusDays(1), LocalTime.of(10, 0));
        exame.setStatus(StatusExame.valueOf(status.toUpperCase()));
        exame.setPossuiLaudo(true);
        repositorio.salvar(exame);
    }

    @When("o funcionário cancela o exame informando {string}")
    public void cancela_exame_com_motivo(String motivo) {
        try {
            exame.cancelar(motivo);
            postar("EXAME_CANCELADO");
        } catch (Exception e) {
            excecaoCapturada = e;
        }
    }

    @Then("o sistema registra o status {string}, a data e o motivo {string}")
    public void sistema_registra_cancelamento(String status, String motivo) {
        assertEquals(StatusExame.valueOf(status.toUpperCase()), exame.getStatus());
        assertEquals(motivo, exame.getMotivoCancelamento());
        assertNotNull(exame.getDataCancelamento());
    }

    @Then("o sistema exibe uma mensagem de erro informando que o exame só pode ser cancelado")
    public void sistema_exibe_mensagem_somente_cancelar() {
        assertTrue(excecaoCapturada.getMessage().toLowerCase().contains("só pode ser cancelado"));
    }

    @Given("existe um exame agendado")
    public void existe_exame_agendado() {
        exame = new Exame(3L, pacientes.get("Lucas"), medicos.get("Dr. Ana"),
                tiposExame.get("Sangue"), LocalDate.now().plusDays(2), LocalTime.of(9, 0));
        repositorio.salvar(exame);
    }

    @When("o funcionário altera a data para {string}")
    public void funcionario_altera_data(String data) {
        LocalDate novaData = LocalDate.parse(data);
        exame.atualizar(novaData, exame.getHora(), exame.getMedico());
        postar("EXAME_ATUALIZADO");
    }

    @Then("o exame é atualizado com sucesso")
    public void exame_atualizado_com_sucesso() {
        assertTrue(eventos.contains("EXAME_ATUALIZADO"));
    }

    @Then("o sistema registra a alteração no histórico")
    public void sistema_registra_historico() {
        assertTrue(eventos.contains("EXAME_ATUALIZADO"));
    }
}
