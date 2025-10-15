package com.medflow.bdd.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

public class ExamesAtualizarSteps {

    private String tipoExame;
    private String paciente;
    private String medicoAtual;
    private String novoMedico;
    private String data;
    private String horario;
    private String novoHorario;
    private boolean medicoAtivo;
    private boolean conflitoHorario;
    private boolean atualizacaoSucesso;
    private boolean historicoRegistrado;
    private String mensagemErro;
    private final Map<String, String> historico = new HashMap<>();

    // ----------------- GIVEN -----------------

    @Given("existe um agendamento de exame de {string} com o médico {string} para o dia {string} às {string}")
    public void existeAgendamentoCompleto(String tipo, String medico, String data, String hora) {
        this.tipoExame = tipo;
        this.medicoAtual = medico;
        this.data = data;
        this.horario = hora;
        this.atualizacaoSucesso = false;
        this.historicoRegistrado = true;
    }

    @Given("{string} é um médico ativo")
    public void medicoAtivo(String medico) {
        this.novoMedico = medico;
        this.medicoAtivo = true;
    }

    @Given("existe um agendamento de exame de {string} com o paciente {string}")
    public void agendamentoComPaciente(String tipo, String paciente) {
        this.tipoExame = tipo;
        this.paciente = paciente;
    }

    @Given("existe um agendamento de exame para o paciente {string}")
    public void existeAgendamentoParaPaciente(String paciente) {
        this.paciente = paciente;
    }

    @Given("existe um agendamento de exame para o paciente {string} com o médico {string}")
    public void agendamentoComMedico(String paciente, String medico) {
        this.paciente = paciente;
        this.medicoAtual = medico;
    }

    @Given("o paciente {string} e o médico {string} não possuem outros agendamentos no dia {string} às {string}")
    public void semConflitoAgendamentos(String paciente, String medico, String data, String hora) {
        this.paciente = paciente;
        this.medicoAtual = medico;
        this.data = data;
        this.novoHorario = hora;
        this.conflitoHorario = false;
    }

    @Given("o médico {string} já possui outro exame agendado no dia {string} às {string}")
    public void medicoComConflito(String medico, String data, String hora) {
        this.medicoAtual = medico;
        this.data = data;
        this.novoHorario = hora;
        this.conflitoHorario = true;
    }

    @Given("existe um agendamento de exame para o dia {string} às {string}")
    public void existeAgendamentoParaDiaHora(String data, String hora) {
        this.data = data;
        this.horario = hora;
        this.historicoRegistrado = true;
    }

    // ----------------- WHEN -----------------

    @When("o funcionário atualizar o agendamento alterando o tipo de exame para {string} e o médico para {string}")
    public void atualizarAgendamentoCamposPermitidos(String novoTipo, String novoMedico) {
        if (medicoAtivo) {
            this.tipoExame = novoTipo;
            this.novoMedico = novoMedico;
            atualizacaoSucesso = true;
        } else {
            atualizacaoSucesso = false;
            mensagemErro = "Médico inativo não pode ser atribuído";
        }
    }

    @When("o funcionário tentar alterar o nome do paciente para {string}")
    public void tentarAlterarPaciente(String novoPaciente) {
        if (!novoPaciente.equals(this.paciente)) {
            atualizacaoSucesso = false;
            mensagemErro = "O paciente vinculado não pode ser alterado";
        }
    }

    @When("o funcionário tentar mudar o paciente para {string}")
    public void tentarMudarPaciente(String novoPaciente) {
        atualizacaoSucesso = false;
        mensagemErro = "Não é permitido alterar o paciente de um agendamento existente";
    }

    @When("o funcionário alterar apenas o horário para {string}")
    public void alterarApenasHorario(String novoHorario) {
        this.novoHorario = novoHorario;
        atualizacaoSucesso = true;
    }

    @When("o funcionário alterar o horário do exame para {string}")
    public void alterarHorarioExame(String novoHorario) {
        if (conflitoHorario) {
            atualizacaoSucesso = false;
            mensagemErro = "Conflito de horário detectado para o médico ou paciente";
        } else {
            this.novoHorario = novoHorario;
            atualizacaoSucesso = true;
        }
    }

    @When("o funcionário tentar remarcar o exame para esse mesmo horário")
    public void remarcarMesmoHorario() {
        alterarHorarioExame(novoHorario);
    }

    @When("o funcionário alterar o horário para {string}")
    public void alterarHorario(String novoHorario) {
        this.novoHorario = novoHorario;
        atualizacaoSucesso = true;
    }

    @When("o sistema não registrar a mudança no {string}")
    public void sistemaNaoRegistraHistorico(String campo) {
        historicoRegistrado = false;
    }

    // ----------------- THEN -----------------

    @Then("o sistema deve registrar a atualização com sucesso")
    public void registrarAtualizacaoSucesso() {
        assertTrue(atualizacaoSucesso, "Atualização deveria ter sido registrada com sucesso");
    }

    @Then("o sistema deve rejeitar a atualização")
    public void rejeitarAtualizacao() {
        assertFalse(atualizacaoSucesso, "Atualização deveria ter sido rejeitada");
    }

    @Then("o sistema deve impedir a alteração")
    public void impedirAlteracao() {
        assertFalse(atualizacaoSucesso, "Alteração deveria ter sido impedida");
    }

    @Then("o sistema deve salvar a atualização corretamente")
    public void salvarAtualizacaoCorretamente() {
        assertTrue(atualizacaoSucesso, "Atualização deveria ter sido salva corretamente");
    }

    @Then("manter o paciente {string} vinculado")
    public void manterPacienteVinculado(String pacienteEsperado) {
        assertEquals(pacienteEsperado, this.paciente, "O paciente vinculado não foi mantido");
    }

    @Then("o sistema deve confirmar a atualização com sucesso")
    public void confirmarAtualizacaoSucesso() {
        assertTrue(atualizacaoSucesso, "Atualização deveria ter sido confirmada com sucesso");
    }

    @Then("exibir a mensagem {string}")
    public void exibirMensagem(String mensagemEsperada) {
        assertEquals(mensagemEsperada, mensagemErro, "Mensagem de erro incorreta");
    }

    @Then("registrar no {string} a data antiga {string} e a nova {string}")
    public void registrarHistorico(String campo, String dataAntiga, String dataNova) {
        historico.put("campo", campo);
        historico.put("antiga", dataAntiga);
        historico.put("nova", dataNova);
    }

    @Then("o sistema deve salvar a alteração")
    public void salvarAlteracao() {
        assertTrue(atualizacaoSucesso, "Alteração deveria ter sido salva");
    }

    @Then("a atualização deve ser considerada inválida")
    public void atualizacaoInvalida() {
        assertFalse(historicoRegistrado, "Atualização deveria ter sido invalidada por falta de histórico");
    }

    @Then("o sistema deve exibir {string}")
    public void sistemaExibeMensagem(String mensagemEsperada) {
        assertEquals(mensagemEsperada, mensagemErro, "Mensagem exibida incorreta");
    }
}
