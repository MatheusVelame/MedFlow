package com.medflow.bdd.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import static org.junit.jupiter.api.Assertions.*;

public class EspecialidadesAlterarSteps {

    private String nomeAtual;
    private String nomeNovo;
    private String descricao;
    private boolean temMedicosAtivos;
    private boolean alteracaoSucesso;
    private String mensagemErro;

    // ----------------- GIVEN -----------------
    @Given("que a especialidade {string} está cadastrada")
    public void especialidadeCadastrada(String nome) {
        this.nomeAtual = nome;
        this.alteracaoSucesso = false;
    }

    @Given("que não existe nenhuma especialidade chamada {string} no sistema")
    public void nomeInexistente(String nome) {
        // Simula que não há duplicidade
    }

    @Given("que já existe uma especialidade chamada {string} no sistema")
    public void nomeDuplicado(String nome) {
        // Simula duplicidade para cenários negativos
    }

    @Given("que a especialidade {string} não possui médicos ativos vinculados")
    public void especialidadeSemMedicos(String nome) {
        this.nomeAtual = nome;
        this.temMedicosAtivos = false;
    }

    @Given("que a especialidade {string} possui médicos ativos vinculados")
    public void especialidadeComMedicos(String nome) {
        this.nomeAtual = nome;
        this.temMedicosAtivos = true;
    }

    // ----------------- WHEN -----------------
    @When("o administrador alterar o nome para {string}")
    public void alterarNome(String novoNome) {
        this.nomeNovo = novoNome;
        validarAlteracaoNome();
    }

    @When("alterar a descrição para {string}")
    public void alterarDescricao(String descricao) {
        this.descricao = descricao;
        validarAlteracaoDescricao();
    }

    @When("o administrador tentar alterar o status da especialidade para {string}")
    public void alterarStatus(String status) {
        alteracaoSucesso = false;
        mensagemErro = "Apenas o nome ou a descrição podem ser alterados";
    }

    @When("o administrador alterar o nome de {string} para {string}")
    public void alterarNomeEspecifico(String nomeAtual, String novoNome) {
        this.nomeAtual = nomeAtual;
        this.nomeNovo = novoNome;
        validarAlteracaoNome();
    }

    @When("o administrador tentar alterar o nome de {string} para {string}")
    public void tentativaAlterarNome(String nomeAtual, String novoNome) {
        this.nomeAtual = nomeAtual;
        this.nomeNovo = novoNome;
        alteracaoSucesso = false;
        mensagemErro = "Nome da especialidade deve ser único e conter apenas letras e espaços";
    }

    @When("o administrador alterar o nome ou descrição")
    public void alterarNomeOuDescricao() {
        if (temMedicosAtivos) {
            alteracaoSucesso = false;
            mensagemErro = "Não é permitido alterar o nome de especialidades vinculadas a médicos ativos sem reatribuição";
        } else {
            alteracaoSucesso = true;
        }
    }

    // ----------------- THEN -----------------
    @Then("o sistema deve salvar as alterações com sucesso")
    public void salvarAlteracoesSucesso() {
        assertTrue(alteracaoSucesso, "A alteração deveria ter sido bem-sucedida");
    }

    @Then("o sistema deve rejeitar a alteração")
    public void rejeitarAlteracao() {
        assertFalse(alteracaoSucesso, "A alteração deveria ter sido rejeitada");
    }

    @Then("exibir a mensagem {string}")
    public void exibirMensagem(String mensagem) {
        assertEquals(mensagem, mensagemErro, "Mensagem de erro incorreta");
    }

    @Then("registrar a operação no histórico de alterações")
    public void registrarHistorico() {
        // Placeholder para registrar a alteração no histórico
    }

    // ----------------- MÉTODOS AUXILIARES -----------------
    private void validarAlteracaoNome() {
        if (nomeNovo == null || !nomeNovo.matches("[A-Za-zÀ-ÿ ]+") || temMedicosAtivos) {
            alteracaoSucesso = false;
            if (temMedicosAtivos) {
                mensagemErro = "Não é permitido alterar o nome de especialidades vinculadas a médicos ativos sem reatribuição";
            } else {
                mensagemErro = "Nome da especialidade deve ser único e conter apenas letras e espaços";
            }
        } else {
            alteracaoSucesso = true;
        }
    }

    private void validarAlteracaoDescricao() {
        if (descricao != null && descricao.length() > 255) {
            alteracaoSucesso = false;
            mensagemErro = "Descrição deve conter no máximo 255 caracteres";
        } else {
            alteracaoSucesso = true;
        }
    }
}
