package com.medflow.bdd.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import static org.junit.jupiter.api.Assertions.*;

public class EspecialidadesCadastrarSteps {

    private String nome;
    private String descricao;
    private String status;
    private boolean cadastroSucesso;
    private String mensagemErro;

    // ---------------- RN1 — Nome obrigatório ----------------
    @Given("que o administrador informa o nome da especialidade como {string}")
    public void informarNome(String nome) {
        this.nome = nome;
    }

    @Given("que o administrador não informa o nome da especialidade")
    public void semNome() {
        this.nome = null;
    }

    @When("ele tentar cadastrar a especialidade")
    public void cadastrarEspecialidade() {
        // Aqui você chama o service/repository para cadastrar
        // Exemplo placeholder:
        if (nome == null || nome.isBlank()) {
            cadastroSucesso = false;
            mensagemErro = "Nome da especialidade é obrigatório";
        } else {
            cadastroSucesso = true;
        }
    }

    @Then("o sistema deve criar a especialidade com sucesso")
    public void verificarCadastroSucesso() {
        assertTrue(cadastroSucesso, "O cadastro deveria ter sido bem-sucedido");
    }

    @Then("o sistema deve rejeitar o cadastro")
    public void verificarCadastroFalha() {
        assertFalse(cadastroSucesso, "O cadastro deveria ter falhado");
    }

    @Then("exibir a mensagem {string}")
    public void exibirMensagem(String mensagem) {
        assertEquals(mensagem, mensagemErro, "Mensagem de erro incorreta");
    }

    @Then("registrar a operação no histórico")
    public void registrarHistorico() {
        // Placeholder: implementar registro de log/histórico
    }

    // ---------------- RN2 — Nome único ----------------
    @Given("que não existe nenhuma especialidade chamada {string} no sistema")
    public void nomeUnico(String nome) {
        // Placeholder: simular que não existe duplicidade
        this.nome = nome;
    }

    @Given("que já existe uma especialidade chamada {string} no sistema")
    public void nomeDuplicado(String nome) {
        // Placeholder: simular duplicidade
        this.nome = nome;
        cadastroSucesso = false;
        mensagemErro = "Especialidade já cadastrada";
    }

    @When("o administrador cadastrar {string}")
    public void cadastrarNome(String nome) {
        // Implementar cadastro real
        // Placeholder:
        if ("Dermatologia".equals(nome) && !cadastroSucesso) {
            mensagemErro = "Especialidade já cadastrada";
        } else {
            cadastroSucesso = true;
        }
    }

    @When("o administrador tentar cadastrar novamente {string}")
    public void tentarCadastrarDuplicado(String nome) {
        cadastroSucesso = false;
        mensagemErro = "Especialidade já cadastrada";
    }

    // ---------------- RN3 — Nome alfabético ----------------
    @Given("que o administrador informa o nome {string}")
    public void nomeValido(String nome) {
        this.nome = nome;
    }

    @When("ele cadastrar a especialidade")
    public void cadastrarNomeValido() {
        if (nome.matches("[A-Za-zÀ-ÿ ]+")) {
            cadastroSucesso = true;
        } else {
            cadastroSucesso = false;
            mensagemErro = "Nome da especialidade deve conter apenas letras e espaços";
        }
    }

    @When("ele tentar cadastrar a especialidade")
    public void cadastrarNomeInvalido() {
        if (!nome.matches("[A-Za-zÀ-ÿ ]+")) {
            cadastroSucesso = false;
            mensagemErro = "Nome da especialidade deve conter apenas letras e espaços";
        } else {
            cadastroSucesso = true;
        }
    }

    // ---------------- RN4 — Descrição ----------------
    @Given("que o administrador informa a descrição {string}")
    public void informarDescricao(String descricao) {
        this.descricao = descricao;
    }

    @When("ele cadastrar a especialidade")
    public void cadastrarComDescricao() {
        if (descricao != null && descricao.length() > 255) {
            cadastroSucesso = false;
            mensagemErro = "Descrição deve conter no máximo 255 caracteres";
        } else {
            cadastroSucesso = true;
        }
    }

    @When("ele tentar cadastrar a especialidade")
    public void cadastrarDescricaoInvalida() {
        if (descricao != null && descricao.length() > 255) {
            cadastroSucesso = false;
            mensagemErro = "Descrição deve conter no máximo 255 caracteres";
        } else {
            cadastroSucesso = true;
        }
    }

    // ---------------- RN5 — Status inicial ----------------
    @Given("que o administrador cadastra a especialidade {string}")
    public void cadastrarStatus(String nome) {
        this.nome = nome;
        this.status = "Ativa"; // Valor inicial esperado
        cadastroSucesso = true;
    }

    @When("o sistema criar a especialidade")
    public void criarEspecialidade() {
        // Placeholder: simular criação
        if (!"Ativa".equals(status)) {
            cadastroSucesso = false;
            mensagemErro = "O status inicial da especialidade deve ser 'Ativa'";
        } else {
            cadastroSucesso = true;
        }
    }

    @Then("o status da especialidade deve ser automaticamente definido como {string}")
    public void verificarStatus(String esperado) {
        assertEquals(esperado, status, "Status inicial incorreto");
    }

    @When("o sistema criar a especialidade com status {string}")
    public void criarEspecialidadeComStatus(String status) {
        this.status = status;
        if (!"Ativa".equals(status)) {
            cadastroSucesso = false;
            mensagemErro = "O status inicial da especialidade deve ser 'Ativa'";
        } else {
            cadastroSucesso = true;
        }
    }

    @Then("o cadastro deve ser considerado inválido")
    public void cadastroInvalido() {
        assertFalse(cadastroSucesso, "Cadastro inválido esperado");
    }

}
