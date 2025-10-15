package com.medflow.bdd.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

public class ExamesExcluirSteps {

    private String paciente;
    private String tipoExame;
    private String status;
    private boolean vinculadoLaudo;
    private boolean possuiRegistroProntuario;
    private boolean exclusaoSucesso;
    private boolean cancelamentoSucesso;
    private String mensagemErro;
    private String motivoCancelamento;
    private String dataCancelamento;

    private final Map<String, String> historico = new HashMap<>();

    // ----------------- GIVEN -----------------

    @Given("existe um exame com status {string} para o paciente {string}")
    public void exameComStatus(String status, String paciente) {
        this.paciente = paciente;
        this.status = status;
        this.vinculadoLaudo = false;
        this.possuiRegistroProntuario = false;
        this.exclusaoSucesso = false;
        this.cancelamentoSucesso = false;
    }

    @Given("existe um exame vinculado a um laudo para o paciente {string}")
    public void exameVinculadoLaudo(String paciente) {
        this.paciente = paciente;
        this.vinculadoLaudo = true;
        this.status = "Laudo Vinculado";
    }

    @Given("o exame de {string} do paciente {string} não possui registros no prontuário")
    public void exameSemRegistroProntuario(String tipoExame, String paciente) {
        this.tipoExame = tipoExame;
        this.paciente = paciente;
        this.possuiRegistroProntuario = false;
    }

    @Given("o exame de {string} do paciente {string} está vinculado a registros no prontuário")
    public void exameComRegistroProntuario(String tipoExame, String paciente) {
        this.tipoExame = tipoExame;
        this.paciente = paciente;
        this.possuiRegistroProntuario = true;
    }

    @Given("existe um exame agendado para o paciente {string}")
    public void exameAgendado(String paciente) {
        this.paciente = paciente;
        this.status = "Agendado";
    }

    // ----------------- WHEN -----------------

    @When("o funcionário solicitar a exclusão desse exame")
    public void solicitarExclusaoDesseExame() {
        if (status.equals("Agendado")) {
            exclusaoSucesso = true;
        } else if (status.equals("Realizado") || status.equals("Em andamento")) {
            exclusaoSucesso = false;
            mensagemErro = "Exames realizados ou em andamento não podem ser excluídos";
        }
    }

    @When("o funcionário tentar excluir o exame")
    public void tentarExcluirExame() {
        if (vinculadoLaudo) {
            exclusaoSucesso = false;
            cancelamentoSucesso = true;
            status = "Cancelado";
        } else {
            exclusaoSucesso = true;
        }
    }

    @When("o funcionário solicitar a exclusão permanente")
    public void solicitarExclusaoPermanente() {
        if (vinculadoLaudo) {
            exclusaoSucesso = false;
            mensagemErro = "Exames vinculados a laudos não podem ser excluídos, apenas cancelados";
        } else {
            exclusaoSucesso = true;
        }
    }

    @When("o funcionário solicitar a exclusão do exame")
    public void solicitarExclusaoDoExame() {
        if (possuiRegistroProntuario) {
            exclusaoSucesso = false;
            mensagemErro = "Não é permitido excluir exames associados a registros clínicos do paciente";
        } else {
            exclusaoSucesso = true;
        }
    }

    @When("o funcionário solicitar a exclusão")
    public void solicitarExclusao() {
        solicitarExclusaoDoExame();
    }

    @When("o funcionário cancelar o exame informando a data {string} e o motivo {string}")
    public void cancelarComDataMotivo(String data, String motivo) {
        if (motivo == null || motivo.isBlank()) {
            cancelamentoSucesso = false;
            mensagemErro = "É obrigatório informar o motivo do cancelamento";
        } else {
            this.dataCancelamento = data;
            this.motivoCancelamento = motivo;
            cancelamentoSucesso = true;
            status = "Cancelado";
        }
    }

    @When("o funcionário cancelar o exame sem informar o motivo")
    public void cancelarSemMotivo() {
        cancelamentoSucesso = false;
        mensagemErro = "É obrigatório informar o motivo do cancelamento";
    }

    // ----------------- THEN -----------------

    @Then("o sistema deve excluir o exame com sucesso")
    public void excluirComSucesso() {
        assertTrue(exclusaoSucesso, "Exclusão esperada com sucesso");
    }

    @Then("o sistema deve rejeitar a exclusão")
    public void rejeitarExclusao() {
        assertFalse(exclusaoSucesso, "Exclusão deveria ter sido rejeitada");
    }

    @Then("o sistema deve alterar o status do exame para {string}")
    public void alterarStatusExame(String statusEsperado) {
        assertEquals(statusEsperado, status, "Status do exame incorreto após operação");
    }

    @Then("o sistema deve impedir a exclusão")
    public void impedirExclusao() {
        assertFalse(exclusaoSucesso, "Exclusão deveria ter sido impedida");
    }

    @Then("o sistema deve rejeitar a operação")
    public void rejeitarOperacao() {
        assertFalse(exclusaoSucesso, "Operação deveria ter sido rejeitada");
    }

    @Then("o sistema deve registrar o cancelamento com o status {string}")
    public void registrarCancelamentoComStatus(String statusEsperado) {
        assertTrue(cancelamentoSucesso, "Cancelamento deveria ter sido realizado");
        assertEquals(statusEsperado, status, "Status incorreto após cancelamento");
    }

    @Then("armazenar a data e o motivo informados")
    public void armazenarDataMotivo() {
        assertNotNull(dataCancelamento, "Data do cancelamento não foi armazenada");
        assertNotNull(motivoCancelamento, "Motivo do cancelamento não foi armazenado");
    }

    @Then("o sistema deve rejeitar o cancelamento")
    public void rejeitarCancelamento() {
        assertFalse(cancelamentoSucesso, "Cancelamento deveria ter sido rejeitado");
    }

    @Then("exibir a mensagem {string}")
    public void exibirMensagem(String mensagemEsperada) {
        assertEquals(mensagemEsperada, mensagemErro, "Mensagem exibida incorreta");
    }

    @Then("registrar a operação no histórico de alterações")
    public void registrarHistoricoAlteracoes() {
        historico.put("últimaOperação", "Exclusão/Cancelamento");
    }

    @Then("registrar a exclusão no histórico de alterações")
    public void registrarExclusaoHistorico() {
        historico.put("últimaOperação", "Exclusão");
    }

    @Then("registrar o motivo e a data do cancelamento")
    public void registrarMotivoDataCancelamento() {
        assertTrue(cancelamentoSucesso, "Cancelamento deveria ter sido concluído");
        assertNotNull(motivoCancelamento, "Motivo do cancelamento não registrado");
        assertNotNull(dataCancelamento, "Data do cancelamento não registrada");
    }
}
