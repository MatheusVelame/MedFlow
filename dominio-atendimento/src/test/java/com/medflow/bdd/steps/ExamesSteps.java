package com.medflow.bdd.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.Set;

public class AgendamentoExameSteps {

    private boolean agendamentoCriado;
    private boolean atualizacaoSucesso;
    private boolean cancelamentoSucesso;
    private boolean conflitoHorario;
    private boolean dataInformada;
    private boolean exameCadastrado;
    private boolean exclusaoSucesso;
    private boolean historicoRegistrado;
    private boolean horarioInformado;
    private boolean medicoAtivo;
    private boolean medicoCadastrado;
    private boolean medicoDisponivel;
    private boolean pacienteCadastrado;
    private boolean possuiRegistroProntuario;
    private boolean vinculadoLaudo;
    private String data;
    private String dataCancelamento;
    private String horario;
    private String medicoAtual;
    private String mensagemErro;
    private String motivoCancelamento;
    private String novoHorario;
    private String novoMedico;
    private String paciente;
    private String status;
    private String statusAgendamento;
    private String tipoExame;
    private final Map<String, String> historico = new HashMap<>();
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


// public class ExamesAtualizarSteps {

    

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

// public class ExamesExcluirSteps {

    

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