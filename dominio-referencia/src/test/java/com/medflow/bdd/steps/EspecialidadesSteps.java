package com.medflow.bdd.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import static org.junit.jupiter.api.Assertions.*;

public class EspecialidadesSteps {

    private String descricao;
    private String mensagemErro;
    private String nome;
    private String nomeAtual;
    private String nomeNovo;
    private String nomeEspecialidade;
    private String status;
    private boolean alteracaoSucesso;
    private boolean cadastroSucesso;
    private boolean exclusaoSucesso;
    private boolean statusAtivo;
    private boolean teveHistoricoVinculo;
    private boolean temMedicosAtivos;

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

    // EspecialidadesCadastrarSteps {

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


    // EspecialidadesExcluirSteps {

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
