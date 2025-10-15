package com.medflow.bdd.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.Set;

public class AgendamentoExameSteps {

    private boolean pacienteCadastrado;
    private boolean medicoCadastrado;
    private boolean medicoAtivo;
    private boolean medicoDisponivel;
    private boolean exameCadastrado;
    private boolean dataInformada;
    private boolean horarioInformado;
    private boolean conflitoHorario;
    private String statusAgendamento;
    private boolean agendamentoCriado;
    private String mensagemErro;

    private final Set<String> examesCadastrados = new HashSet<>();

    // ---------------- GIVEN ----------------

    @Given("que o paciente {string} e o médico {string} estão cadastrados no sistema")
    public void pacienteEMedicoCadastrados(String paciente, String medico) {
        pacienteCadastrado = true;
        medicoCadastrado = true;
        medicoAtivo = true;
    }

    @Given("que o paciente {string} não está cadastrado no sistema")
    public void pacienteNaoCadastrado(String paciente) {
        pacienteCadastrado = false;
    }

    @Given("o médico {string} está cadastrado")
    public void medicoCadastrado(String medico) {
        medicoCadastrado = true;
        medicoAtivo = true;
    }

    @Given("que o exame {string} está cadastrado no sistema")
    public void exameCadastrado(String exame) {
        exameCadastrado = true;
        examesCadastrados.add(exame);
    }

    @Given("que o exame {string} não está cadastrado no sistema")
    public void exameNaoCadastrado(String exame) {
        exameCadastrado = false;
    }

    @Given("que o paciente {string} não possui outro exame agendado às {string} do dia {string}")
    public void pacienteSemConflito(String paciente, String hora, String data) {
        conflitoHorario = false;
    }

    @Given("que o paciente {string} já possui um exame agendado às {string} do dia {string}")
    public void pacienteComConflito(String paciente, String hora, String data) {
        conflitoHorario = true;
    }

    @Given("que o médico {string} está ativo no sistema")
    public void medicoAtivo(String medico) {
        medicoAtivo = true;
    }

    @Given("que o médico {string} está inativo no sistema")
    public void medicoInativo(String medico) {
        medicoAtivo = false;
    }

    @Given("que o médico {string} está disponível às {string} do dia {string}")
    public void medicoDisponivel(String medico, String hora, String data) {
        medicoDisponivel = true;
    }

    @Given("que o médico {string} não está disponível às {string} do dia {string}")
    public void medicoIndisponivel(String medico, String hora, String data) {
        medicoDisponivel = false;
    }

    // ---------------- WHEN ----------------

    @When("o funcionário solicitar o agendamento de exame de {string}")
    public void solicitarAgendamento(String exame) {
        validarAgendamento(exame, true, true);
    }

    @When("o funcionário tentar agendar o exame de {string}")
    public void tentarAgendarExame(String exame) {
        validarAgendamento(exame, true, true);
    }

    @When("o funcionário agendar um exame do tipo {string} para o paciente {string}")
    public void agendarExameTipo(String exame, String paciente) {
        validarAgendamento(exame, true, true);
    }

    @When("o funcionário tentar agendar esse exame para o paciente {string}")
    public void tentarAgendarExameTipo(String paciente) {
        validarAgendamento(null, true, true);
    }

    @When("o funcionário agendar o exame para o dia {string} às {string}")
    public void agendarComDataHora(String data, String hora) {
        dataInformada = true;
        horarioInformado = true;
        validarCamposObrigatorios();
    }

    @When("o funcionário tentar agendar o exame sem informar data ou horário")
    public void agendarSemDataHora() {
        dataInformada = false;
        horarioInformado = false;
        validarCamposObrigatorios();
    }

    @When("o funcionário agendar o exame nesse horário")
    public void agendarSemConflito() {
        validarConflito();
    }

    @When("o funcionário tentar agendar outro exame no mesmo horário")
    public void agendarComConflito() {
        validarConflito();
    }

    @When("o funcionário agendar um exame para {string} com esse médico")
    public void agendarComMedicoAtivo(String paciente) {
        validarMedicoAtivo();
    }

    @When("o funcionário tentar agendar o exame com esse médico")
    public void agendarComMedicoInativo() {
        validarMedicoAtivo();
    }

    @When("o funcionário agendar o exame para {string} nesse horário")
    public void agendarComDisponibilidade(String paciente) {
        validarDisponibilidade();
    }

    @When("o funcionário tentar agendar o exame nesse horário")
    public void agendarSemDisponibilidade() {
        validarDisponibilidade();
    }

    @When("o funcionário agendar o exame")
    public void agendarExame() {
        validarStatusInicial();
    }

    @When("o sistema criar o agendamento com status {string}")
    public void sistemaCriaComStatus(String status) {
        this.statusAgendamento = status;
    }

    // ---------------- THEN ----------------

    @Then("o sistema deve criar o agendamento com sucesso")
    public void criarAgendamentoSucesso() {
        assertTrue(agendamentoCriado, "O agendamento deveria ter sido criado com sucesso");
    }

    @Then("o sistema deve rejeitar o agendamento")
    public void rejeitarAgendamento() {
        assertFalse(agendamentoCriado, "O agendamento deveria ter sido rejeitado");
    }

    @Then("exibir a mensagem {string}")
    public void exibirMensagem(String mensagem) {
        assertEquals(mensagem, mensagemErro, "Mensagem de erro incorreta");
    }

    @Then("o sistema deve criar o agendamento com status {string}")
    public void criarAgendamentoComStatus(String status) {
        assertTrue(agendamentoCriado, "Agendamento não foi criado");
        assertEquals(status, statusAgendamento, "Status inicial incorreto");
    }

    @Then("a operação deve ser considerada inválida")
    public void operacaoInvalida() {
        assertFalse(agendamentoCriado, "A operação deveria ser inválida");
    }

    // ---------------- AUXILIARES ----------------

    private void validarAgendamento(String exame, boolean dataOk, boolean horaOk) {
        if (!pacienteCadastrado) {
            agendamentoCriado = false;
            mensagemErro = "Paciente não cadastrado no sistema";
        } else if (!medicoCadastrado) {
            agendamentoCriado = false;
            mensagemErro = "Médico não cadastrado no sistema";
        } else if (!exameCadastrado) {
            agendamentoCriado = false;
            mensagemErro = "Tipo de exame não cadastrado no sistema";
        } else {
            agendamentoCriado = true;
            statusAgendamento = "Agendado";
        }
    }

    private void validarCamposObrigatorios() {
        if (!dataInformada || !horarioInformado) {
            agendamentoCriado = false;
            mensagemErro = "Data e horário são obrigatórios";
        } else {
            agendamentoCriado = true;
            statusAgendamento = "Agendado";
        }
    }

    private void validarConflito() {
        if (conflitoHorario) {
            agendamentoCriado = false;
            mensagemErro = "Paciente já possui exame agendado neste horário";
        } else {
            agendamentoCriado = true;
            statusAgendamento = "Agendado";
        }
    }

    private void validarMedicoAtivo() {
        if (!medicoAtivo) {
            agendamentoCriado = false;
            mensagemErro = "Médico inativo não pode ser vinculado ao exame";
        } else {
            agendamentoCriado = true;
            statusAgendamento = "Agendado";
        }
    }

    private void validarDisponibilidade() {
        if (!medicoDisponivel) {
            agendamentoCriado = false;
            mensagemErro = "Médico indisponível neste horário";
        } else {
            agendamentoCriado = true;
            statusAgendamento = "Agendado";
        }
    }

    private void validarStatusInicial() {
        if (!"Agendado".equals(statusAgendamento)) {
            agendamentoCriado = false;
            mensagemErro = "O status inicial do exame deve ser ‘Agendado’";
        } else {
            agendamentoCriado = true;
        }
    }
}
