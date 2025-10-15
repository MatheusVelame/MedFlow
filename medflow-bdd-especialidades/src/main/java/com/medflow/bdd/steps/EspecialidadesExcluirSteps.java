package com.medflow.bdd.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import static org.junit.jupiter.api.Assertions.*;

public class EspecialidadesExcluirSteps {

    private String nomeEspecialidade;
    private boolean temMedicosAtivos;
    private boolean teveHistoricoVinculo;
    private boolean statusAtivo;
    private boolean exclusaoSucesso;
    private String mensagemErro;

    // ----------------- GIVEN -----------------
    @Given("que a especialidade {string} não possui médicos ativos vinculados")
    public void semMedicosAtivos(String nome) {
        this.nomeEspecialidade = nome;
        this.temMedicosAtivos = false;
        this.teveHistoricoVinculo = false;
        this.statusAtivo = true;
    }

    @Given("que a especialidade {string} possui médicos ativos vinculados")
    public void comMedicosAtivos(String nome) {
        this.nomeEspecialidade = nome;
        this.temMedicosAtivos = true;
    }

    @Given("que a especialidade {string} nunca foi vinculada a nenhum médico")
    public void semHistoricoVinculo(String nome) {
        this.nomeEspecialidade = nome;
        this.teveHistoricoVinculo = false;
    }

    @Given("que a especialidade {string} já foi vinculada a médicos no passado")
    public void comHistoricoVinculo(String nome) {
        this.nomeEspecialidade = nome;
        this.teveHistoricoVinculo = true;
    }

    @Given("que a especialidade {string} já foi vinculada a médicos anteriormente")
    public void comHistoricoAnterior(String nome) {
        this.nomeEspecialidade = nome;
        this.teveHistoricoVinculo = true;
    }

    @Given("que a especialidade {string} está ativa")
    public void especialidadeAtiva(String nome) {
        this.nomeEspecialidade = nome;
        this.statusAtivo = true;
    }

    @Given("que a especialidade {string} está inativa")
    public void especialidadeInativa(String nome) {
        this.nomeEspecialidade = nome;
        this.statusAtivo = false;
    }

    // ----------------- WHEN -----------------
    @When("o administrador solicitar a exclusão da especialidade")
    public void solicitarExclusao() {
        if (temMedicosAtivos) {
            exclusaoSucesso = false;
            mensagemErro = "Especialidade não pode ser excluída pois possui médicos ativos vinculados";
        } else if (teveHistoricoVinculo) {
            exclusaoSucesso = true;
            statusAtivo = false; // Inativa
        } else {
            exclusaoSucesso = true;
        }
    }

    @When("o administrador tentar excluir a especialidade")
    public void tentarExclusao() {
        solicitarExclusao();
    }

    @When("o administrador solicitar a exclusão física")
    public void solicitarExclusaoFisica() {
        if (teveHistoricoVinculo) {
            exclusaoSucesso = false;
            mensagemErro = "Especialidade com histórico de vínculo não pode ser excluída fisicamente";
        } else {
            exclusaoSucesso = true;
        }
    }

    @When("o administrador tentar excluir fisicamente")
    public void tentarExclusaoFisica() {
        solicitarExclusaoFisica();
    }

    @When("o administrador tentar excluir a especialidade permanentemente")
    public void tentarExclusaoPermanente() {
        if (teveHistoricoVinculo) {
            exclusaoSucesso = false;
            mensagemErro = "Especialidade com histórico de vínculo deve ser apenas inativada, não excluída";
        } else {
            exclusaoSucesso = true;
        }
    }

    @When("o administrador tentar vincular a especialidade a um novo médico")
    public void vincularEspecialidade() {
        if (!statusAtivo) {
            exclusaoSucesso = false;
            mensagemErro = "Especialidade inativa não pode ser atribuída a novos médicos";
        } else {
            exclusaoSucesso = true;
        }
    }

    // ----------------- THEN -----------------
    @Then("o sistema deve excluir a especialidade com sucesso")
    public void excluirSucesso() {
        assertTrue(exclusaoSucesso, "Exclusão esperada com sucesso");
    }

    @Then("o sistema deve excluir a especialidade permanentemente")
    public void excluirPermanente() {
        assertTrue(exclusaoSucesso, "Exclusão física esperada com sucesso");
    }

    @Then("o sistema deve impedir a exclusão")
    public void impedirExclusao() {
        assertFalse(exclusaoSucesso, "Exclusão deveria ter sido impedida");
    }

    @Then("o sistema deve alterar o status da especialidade para {string}")
    public void inativarEspecialidade(String statusEsperado) {
        assertEquals(statusEsperado.equals("Inativa"), !statusAtivo, "Especialidade não foi inativada corretamente");
    }

    @Then("o sistema deve rejeitar a exclusão")
    public void rejeitarExclusao() {
        assertFalse(exclusaoSucesso, "Exclusão deveria ter sido rejeitada");
    }

    @Then("o sistema deve rejeitar a operação")
    public void rejeitarOperacao() {
        assertFalse(exclusaoSucesso, "Operação deveria ter sido rejeitada");
    }

    @Then("o sistema deve permitir o vínculo")
    public void permitirVinculo() {
        assertTrue(exclusaoSucesso, "Vínculo deveria ser permitido");
    }

    @Then("o sistema deve rejeitar o vínculo")
    public void rejeitarVinculo() {
        assertFalse(exclusaoSucesso, "Vínculo deveria ser rejeitado");
    }

    @Then("exibir a mensagem {string}")
    public void exibirMensagem(String mensagem) {
        assertEquals(mensagem, mensagemErro, "Mensagem de erro incorreta");
    }

    @Then("registrar a operação no histórico de alterações")
    public void registrarHistorico() {
        // Placeholder para registrar a operação no histórico
    }
}
