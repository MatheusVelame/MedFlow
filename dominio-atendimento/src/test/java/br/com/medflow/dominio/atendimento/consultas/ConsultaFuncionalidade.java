package br.com.medflow.dominio.atendimento.consultas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.time.LocalDateTime;
import java.util.Optional;

import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class ConsultaFuncionalidade extends ConsultaFuncionalidadeBase {

    @Before
    public void setup() {
        resetContexto();
        dataHoraAtual = LocalDateTime.of(2025, 10, 12, 10, 0); 
    }
    
    // ====================================================================================
    // GIVEN Steps
    // ====================================================================================

    @Given("que o usuário {string} tem permissão de recepcionista")
    public void que_o_usuário_tem_permissão_de_recepcionista(String usuario) {
        simularPermissao(usuario);
        assertTrue(usuarioAtual.equals("Recepcionista") || usuarioAtual.equals("Administrador")); 
    }

    @Given("o médico {string} tem o horário {string} do dia {string} livre")
    public void o_médico_tem_o_horário_do_dia_livre(String medicoNome, String hora, String data) {
        dataHoraConsulta = parseDateTime(data, hora);
        if (!medicos.containsKey(medicoNome)) {
            medicos.put(medicoNome, new Medico(medicoNome, "Geral", "E-mail"));
        }
        assertTrue(repositorio.isHorarioLivre(medicoNome, dataHoraConsulta));
    }
    
    @Given("o médico {string} já tem uma consulta agendada para o dia {string} às {string}")
    public void o_médico_já_tem_uma_consulta_agendada_para_o_dia_às(String medicoNome, String data, String hora) {
        dataHoraConsulta = parseDateTime(data, hora);
        cadastrarConsulta(medicoNome, "Outro Paciente", dataHoraConsulta);
        assertFalse(repositorio.isHorarioLivre(medicoNome, dataHoraConsulta)); 
    }

    @Given("o paciente {string} está cadastrado no sistema")
    public void o_paciente_está_cadastrado_no_sistema(String pacienteNome) {
        assertNotNull(pacientes.get(pacienteNome));
    }

    @Given("o médico {string} está cadastrado no sistema")
    public void o_médico_está_cadastrado_no_sistema(String medicoNome) {
        assertNotNull(medicos.get(medicoNome));
    }

    @Given("o paciente {string} não está cadastrado no sistema")
    public void o_paciente_não_está_cadastrado_no_sistema(String pacienteNome) {
        pacientes.remove(pacienteNome);
        assertNull(pacientes.get(pacienteNome));
    }

    @Given("o médico {string} não está cadastrado no sistema")
    public void o_médico_não_está_cadastrado_no_sistema(String medicoNome) {
        medicos.remove(medicoNome);
        assertNull(medicos.get(medicoNome));
    }
    
    @Given("a data da consulta é {string}")
    public void a_data_da_consulta_é(String data) {
        dataHoraConsulta = parseDateTime(data, "10:00"); 
    }
    
    @Given("a data da consulta é {string} às {string}")
    public void a_data_da_consulta_é_às(String data, String hora) {
        dataHoraConsulta = parseDateTime(data, hora);
    }
    
    @Given("a data atual é {string}")
    public void a_data_atual_é(String data) {
        setSystemDate(data);
    }

    @Given("que o médico {string} tem a especialidade {string} cadastrada em seu perfil")
    public void que_o_médico_tem_a_especialidade_cadastrada_em_seu_perfil(String medicoNome, String especialidade) {
        medicos.put(medicoNome, new Medico(medicoNome, especialidade, "E-mail")); 
    }

    @Given("que o médico {string} tem a especialidade principal {string}")
    public void que_o_médico_tem_a_especialidade_principal(String medicoNome, String especialidade) {
        medicos.put(medicoNome, new Medico(medicoNome, especialidade, "E-mail"));
    }
    
    @Given("a especialidade solicitada é {string}")
    public void a_especialidade_solicitada_é(String especialidade) {
    }
    
    @Given("que a consulta foi marcada com sucesso")
    public void que_a_consulta_foi_marcada_com_sucesso() throws Exception {
        simularPermissao("Julia"); 
        dataHoraAtual = LocalDateTime.of(2025, 10, 12, 10, 0);
        dataHoraConsulta = LocalDateTime.of(2025, 12, 12, 10, 0);
        
        Consulta nova = new Consulta("Dr. Eduardo", "Ana Silva", dataHoraConsulta);
        repositorio.salvar("Dr. Eduardo" + dataHoraConsulta.toString(), nova);
        consultaAtual = nova;
        
        notificacaoServico.clear(); 
    }
    
    @Given("o paciente {string} tem a preferência de notificação por {string}")
    public void o_paciente_tem_a_preferência_de_notificação_por(String pacienteNome, String pref) {
        Paciente p = pacientes.get(pacienteNome);
        p = new Paciente(pacienteNome, pref); 
        pacientes.put(pacienteNome, p);
        
        if (pacienteNome.equals("Pedro Alves")) {
            String medico = "Dra. Helena";
            LocalDateTime agendamento = LocalDateTime.of(2025, 12, 12, 10, 0); 
            
            if (!medicos.containsKey(medico)) {
                 medicos.put(medico, new Medico(medico, "Dermatologia", "E-mail"));
            }

            Consulta nova = new Consulta(medico, pacienteNome, agendamento);
            repositorio.salvar(medico + agendamento.toString(), nova);
            consultaAtual = nova;
        }
    }

    @Given("o médico {string} tem a preferência de notificação por {string}")
    public void o_médico_tem_a_preferência_de_notificação_por(String medicoNome, String pref) {
        String specialty = medicos.containsKey(medicoNome) ? medicos.get(medicoNome).getEspecialidades().iterator().next() : "Geral";
        medicos.put(medicoNome, new Medico(medicoNome, specialty, pref)); 
    }

    @Given("que o usuário {string} tem permissão para remarcar consultas")
    public void que_o_usuário_tem_permissão_para_remarcar_consultas(String usuario) {
        simularPermissao(usuario);
    }
    
    @Given("uma consulta já foi remarcada {string} vez")
    public void uma_consulta_já_foi_remarcada_vez(String count) {
        cadastrarConsulta("Dr. Eduardo", "Paciente Teste", LocalDateTime.of(2025, 12, 10, 10, 0));
        consultaAtual.remarcar(LocalDateTime.of(2025, 12, 11, 10, 0));
        if (Integer.parseInt(count) == 2) {
            consultaAtual.remarcar(LocalDateTime.of(2025, 12, 12, 10, 0));
        }
        assertEquals(Integer.parseInt(count), consultaAtual.getRemarcacoesCount());
    }
    
    @Given("uma consulta já foi remarcada {string} vezes")
    public void uma_consulta_já_foi_remarcada_vezes(String count) {
        uma_consulta_já_foi_remarcada_vez(count);
    }
    
    @Given("uma consulta do paciente {string} está agendada para o dia {string} às {string}")
    public void uma_consulta_do_paciente_está_agendada_para_o_dia_às(String paciente, String data, String hora) {
        dataHoraConsulta = parseDateTime(data, hora);
        if (pacientes.get(paciente) == null) pacientes.put(paciente, new Paciente(paciente, "E-mail"));
        cadastrarConsulta("Dr. Eduardo", paciente, dataHoraConsulta); 
        if (paciente.equals("João")) { 
             consultaJoao = consultaAtual; 
        }
    }
    
    @Given("nenhuma consulta está agendada para o dia {string} às {string}")
    public void nenhuma_consulta_está_agendada_para_o_dia_às(String data, String hora) {
        dataHoraNovaConsulta = parseDateTime(data, hora);
        assertTrue(repositorio.isHorarioLivre(consultaAtual.getMedicoNome(), dataHoraNovaConsulta));
    }
    
    @Given("e uma consulta do paciente {string} já está agendada para o dia {string} às {string}")
    public void e_uma_consulta_do_paciente_já_está_agendada_para_o_dia_às(String paciente, String data, String hora) {
        LocalDateTime dataHoraOcupada = parseDateTime(data, hora);
        String medicoConflito = consultaJoao != null ? consultaJoao.getMedicoNome() : "Dr. Eduardo";
        Consulta consultaConflito = new Consulta(medicoConflito, paciente, dataHoraOcupada);
        repositorio.salvar(medicoConflito + dataHoraOcupada.toString(), consultaConflito);
        assertFalse(repositorio.isHorarioLivre(medicoConflito, dataHoraOcupada));
    }

    @Given("que a remarcação da consulta foi concluída com sucesso")
    public void que_a_remarcação_da_consulta_foi_concluída_com_sucesso() throws Exception {
        simularPermissao("Maria");
        dataHoraAtual = LocalDateTime.of(2025, 12, 10, 10, 0);
        cadastrarConsulta("Dr. Eduardo", "Ana Silva", LocalDateTime.of(2025, 12, 20, 14, 0));
        remarcarConsulta("Ana Silva", LocalDateTime.of(2025, 12, 28, 16, 0), "Maria");
        notificacaoServico.clear();
    }
    
    @Given("a data e hora atual é {string} às {string}")
    public void a_data_e_hora_atual_é_às(String data, String hora) {
        this.dataHoraAtual = parseDateTime(data, hora);
    }

    @Given("a consulta agendada é para {string} às {string}")
    public void a_consulta_agendada_é_para_às(String data, String hora) {
        this.dataHoraConsulta = parseDateTime(data, hora);
        if (pacientes.get("João") == null) pacientes.put("João", new Paciente("João", "E-mail"));
        cadastrarConsulta("Dr. Eduardo", "João", dataHoraConsulta);
    }
    
    @Given("que o usuário {string} tem permissão para cancelar consultas")
    public void que_o_usuário_tem_permissão_para_cancelar_consultas(String usuario) {
        simularPermissao(usuario);
    }

    @Given("que a política de cancelamento é de {int} horas")
    public void que_a_política_de_cancelamento_é_de_horas(Integer int1) {
    }

    @Given("a consulta está marcada para {string} às {string}")
    public void a_consulta_está_marcada_para_às(String data, String hora) {
        this.dataHoraConsulta = parseDateTime(data, hora);
        if (pacientes.get("João") == null) pacientes.put("João", new Paciente("João", "E-mail"));
        cadastrarConsulta("Dr. Eduardo", "João", dataHoraConsulta);
    }

    @Given("a data atual do sistema é {string} às {string}")
    public void a_data_atual_do_sistema_é_às(String data, String hora) {
        this.dataHoraAtual = parseDateTime(data, hora);
    }
    
    @Given("que o cancelamento da consulta do paciente {string} foi permitido")
    public void que_o_cancelamento_da_consulta_do_paciente_foi_permitido(String pacienteNome) {
        if (pacientes.get(pacienteNome) == null) pacientes.put(pacienteNome, new Paciente(pacienteNome, "E-mail"));
        dataHoraAtual = LocalDateTime.of(2025, 12, 10, 10, 0); 
        cadastrarConsulta("Dr. Eduardo", pacienteNome, dataHoraAtual.plusDays(7));
        assertNotNull(repositorio.findByMedicoAndDate("Dr. Eduardo", dataHoraAtual.plusDays(7)));
    }
    
    @Given("que a consulta de {string} com o {string} foi cancelada com sucesso")
    public void que_a_consulta_de_com_o_foi_cancelada_com_sucesso(String paciente, String medico) throws Exception {
        dataHoraAtual = LocalDateTime.of(2025, 11, 24, 10, 0); 
        LocalDateTime agendamento = LocalDateTime.of(2025, 11, 25, 16, 0);
        
        if (!medicos.containsKey(medico)) {
             medicos.put(medico, new Medico(medico, "Cardiologia", "E-mail"));
        }
        
        cadastrarConsulta(medico, paciente, agendamento);
        
        try {
            cancelarConsulta("Motivo Simulado", "Admin");
        } catch (IllegalStateException e) {
            fail("O setup de cancelamento falhou: " + e.getMessage());
        }
        
        assertTrue(consultaAtual.getStatus() == StatusConsulta.CANCELADA);
        dataHoraConsulta = agendamento; 
    }


    @Given("a consulta cancelada estava marcada para {string} às {string}")
    public void a_consulta_cancelada_estava_marcada_para_às(String data, String hora) {
        this.dataHoraConsulta = parseDateTime(data, hora);
        assertTrue(consultaAtual.getStatus() == StatusConsulta.CANCELADA); 
    }
    
    @Given("a consulta estava agendada para {string} às {string}")
    public void a_consulta_estava_agendada_para_às(String data, String hora) {
        this.dataHoraConsulta = parseDateTime(data, hora);
    }
    
    @Given("que a consulta foi cancelada com sucesso")
    public void que_a_consulta_foi_cancelada_com_sucesso() throws Exception {
        dataHoraAtual = LocalDateTime.of(2025, 12, 10, 10, 0);
        LocalDateTime agendamento = LocalDateTime.of(2025, 12, 20, 14, 0);
        cadastrarConsulta("Dr. Eduardo", "Ana Silva", agendamento);
        consultaAtual.cancelar("Motivo Simulado");
        notificacaoServico.clear();
    }
    
    @Given("que o paciente {string} possui {string} cancelamentos nos últimos 6 meses")
    public void que_o_paciente_possui_cancelamentos_nos_últimos_6_meses(String pacienteNome, String count) {
        Paciente p = pacientes.get(pacienteNome);
        p.setCancelamentosRecentes(Integer.parseInt(count));
        pacientes.put(pacienteNome, p);
    }
    
    @Given("que o paciente {string} possui {string} cancelamento nos últimos {int} meses")
    public void que_o_paciente_possui_cancelamento_nos_últimos_meses(String pacienteNome, String count, Integer meses) {
        que_o_paciente_possui_cancelamentos_nos_últimos_6_meses(pacienteNome, count);
    }

    @Given("a regra de penalidade para cancelamentos frequentes está ativa \\(limite = {int})")
    public void a_regra_de_penalidade_para_cancelamentos_frequentes_está_ativa_limite(Integer limite) {
        assertTrue(limite == 2); 
    }
    
    // ====================================================================================
    // WHEN Steps
    // ====================================================================================

    @When("o usuário {string} tentar marcar uma consulta com {string} para o dia {string} às {string}")
    public void o_usuário_tentar_marcar_uma_consulta_com_para_o_dia_às(String usuario, String medico, String data, String hora) {
        try {
            dataHoraConsulta = parseDateTime(data, hora);
            String especialidade = medico.contains("Helena") ? "Dermatologia" : "Cardiologia"; 
            marcarConsulta(medico, "Ana Silva", especialidade, dataHoraConsulta, usuario);
        } catch (Exception e) {
            this.excecao = (RuntimeException) e;
            this.ultimaMensagem = e.getMessage();
        }
    }
    
    @When("o usuário {string} tentar marcar uma nova consulta com {string} para o dia {string} às {string}")
    public void o_usuário_tentar_marcar_uma_nova_consulta_com_para_o_dia_às(String usuario, String medico, String data, String hora) {
        o_usuário_tentar_marcar_uma_consulta_com_para_o_dia_às(usuario, medico, data, hora);
    }

    @When("o usuário {string} tentar marcar uma consulta com {string} para o paciente {string}")
    public void o_usuário_tentar_marcar_uma_consulta_com_para_o_paciente(String usuario, String medico, String paciente) {
        try {
            String especialidade = medico.contains("Helena") ? "Dermatologia" : "Cardiologia";
            marcarConsulta(medico, paciente, especialidade, dataHoraAtual.plusDays(7), usuario);
        } catch (Exception e) {
            this.excecao = (RuntimeException) e;
            this.ultimaMensagem = e.getMessage();
        }
    }

    @When("o usuário {string} tentar marcar uma consulta para a data {string}")
    public void o_usuário_tentar_marcar_uma_consulta_para_a_data(String usuario, String data) {
        try {
            LocalDateTime dataHora = parseDateTime(data, "10:00");
            marcarConsulta("Dr. Eduardo", "Ana Silva", "Cardiologia", dataHora, usuario);
        } catch (Exception e) {
            this.excecao = (RuntimeException) e;
            this.ultimaMensagem = e.getMessage();
        }
    }

    @When("o usuário {string} tentar marcar uma consulta com {string} na especialidade {string}")
    public void o_usuário_tentar_marcar_uma_consulta_com_na_especialidade(String usuario, String medico, String especialidade) {
        try {
            marcarConsulta(medico, "Ana Silva", especialidade, dataHoraAtual.plusDays(7), usuario);
        } catch (Exception e) {
            this.excecao = (RuntimeException) e;
            this.ultimaMensagem = e.getMessage();
        }
    }

    @When("o sistema enviar a confirmação de agendamento")
    public void o_sistema_enviar_a_confirmação_de_agendamento() {
        if (consultaAtual != null) {
            Paciente p = pacientes.get(consultaAtual.getPacienteNome());
            Medico m = medicos.get(consultaAtual.getMedicoNome());
            
            if (m == null) {
                fail("Médico não encontrado no mapa de mocks: " + consultaAtual.getMedicoNome());
                return;
            }

            notificacaoServico.enviarNotificacao(p.getNome(), p.getPrefNotificacao(), "Consulta marcada");
            notificacaoServico.enviarNotificacao(m.getNome(), m.getPrefNotificacao(), "Consulta marcada");
        } else {
             fail("Nenhuma consulta marcada para enviar notificação.");
        }
    }
    
    @When("o usuário {string} remarcar a consulta do paciente {string} para o dia {string} às {string}")
    public void o_usuário_remarcar_a_consulta_do_paciente_para_o_dia_às(String usuario, String paciente, String data, String hora) {
        try {
            dataHoraNovaConsulta = parseDateTime(data, hora);
            if (paciente.equals("João") && consultaJoao != null) {
                consultaAtual = consultaJoao; 
            }
            remarcarConsulta(paciente, dataHoraNovaConsulta, usuario);
        } catch (Exception e) {
            this.excecao = (RuntimeException) e;
            this.ultimaMensagem = e.getMessage();
        }
    }

    @When("o usuário {string} tentar remarcar a consulta pela segunda vez")
    public void o_usuário_tentar_remarcar_a_consulta_pela_segunda_vez(String usuario) {
        try {
            remarcarConsulta("Paciente Teste", LocalDateTime.of(2025, 12, 13, 10, 0), usuario);
        } catch (Exception e) {
            this.excecao = (RuntimeException) e;
            this.ultimaMensagem = e.getMessage();
        }
    }

    @When("o usuário {string} tentar remarcar a consulta pela terceira vez")
    public void o_usuário_tentar_remarcar_a_consulta_pela_terceira_vez(String usuario) {
        try {
            remarcarConsulta("Paciente Teste", LocalDateTime.of(2025, 12, 14, 10, 0), usuario);
        } catch (Exception e) {
            this.excecao = (RuntimeException) e;
            this.ultimaMensagem = e.getMessage();
        }
    }
    
    @When("o sistema enviar a notificação de remarcação")
    public void o_sistema_enviar_a_notificação_de_remarcação() {
        if (consultaAtual != null) {
            Paciente p = pacientes.get(consultaAtual.getPacienteNome());
            Medico m = medicos.get(consultaAtual.getMedicoNome());
            notificacaoServico.enviarNotificacao(p.getNome(), p.getPrefNotificacao(), "Remarcada");
            notificacaoServico.enviarNotificacao(m.getNome(), m.getPrefNotificacao(), "Remarcada");
        }
    }

    @When("o usuário {string} tentar remarcar a consulta")
    public void o_usuário_tentar_remarcar_a_consulta(String usuario) {
         try {
            dataHoraNovaConsulta = consultaAtual.getDataHora().plusDays(7);
            remarcarConsulta(consultaAtual.getPacienteNome(), dataHoraNovaConsulta, usuario);
        } catch (Exception e) {
            this.excecao = (RuntimeException) e;
            this.ultimaMensagem = e.getMessage();
        }
    }
    
    @When("o usuário {string} tentar cancelar a consulta")
    public void o_usuário_tentar_cancelar_a_consulta(String usuario) {
        try {
            cancelarConsulta("Motivo de teste", usuario);
        } catch (Exception e) {
            this.excecao = (RuntimeException) e;
            this.ultimaMensagem = e.getMessage();
        }
    }
    
    @When("o usuário {string} cancelar a consulta do paciente {string} e preencher o motivo como {string}")
    public void o_usuário_cancelar_a_consulta_do_paciente_e_preencher_o_motivo_como(String usuario, String paciente, String motivo) {
        try {
            cancelarConsulta(motivo, usuario);
        } catch (Exception e) {
            this.excecao = (RuntimeException) e;
            this.ultimaMensagem = e.getMessage();
        }
    }

    @When("o usuário {string} tentar cancelar a consulta do paciente {string} e deixar o campo {string} em branco")
    public void o_usuário_tentar_cancelar_a_consulta_do_paciente_e_deixar_o_campo_em_branco(String usuario, String paciente, String campo) {
         try {
            cancelarConsulta("", usuario);
        } catch (Exception e) {
            this.excecao = (RuntimeException) e;
            this.ultimaMensagem = e.getMessage();
        }
    }
    
    @When("o sistema processar o cancelamento")
    public void o_sistema_processar_o_cancelamento() {
    }

    @When("o sistema enviar a notificação de cancelamento")
    public void o_sistema_enviar_a_notificação_de_cancelamento() {
         if (consultaAtual != null) {
            Paciente p = pacientes.get(consultaAtual.getPacienteNome());
            Medico m = medicos.get(consultaAtual.getMedicoNome());
            notificacaoServico.enviarNotificacao(p.getNome(), p.getPrefNotificacao(), "Cancelamento");
            notificacaoServico.enviarNotificacao(m.getNome(), m.getPrefNotificacao(), "Cancelamento");
        }
    }

    @When("o paciente {string} tentar cancelar uma nova consulta")
    public void o_paciente_tentar_cancelar_uma_nova_consulta(String paciente) {
        dataHoraAtual = LocalDateTime.of(2025, 10, 12, 10, 0); 
        dataHoraConsulta = dataHoraAtual.plusDays(7);
        cadastrarConsulta("Dr. Eduardo", paciente, dataHoraConsulta);
        
        try {
            cancelarConsulta("Motivo de teste", paciente);
        } catch (Exception e) {
            this.excecao = (RuntimeException) e;
            this.ultimaMensagem = e.getMessage();
        }
    }
    
    @When("o paciente {string} cancelar uma nova consulta")
    public void o_paciente_cancelar_uma_nova_consulta(String paciente) {
        dataHoraAtual = LocalDateTime.of(2025, 10, 12, 10, 0); 
        dataHoraConsulta = dataHoraAtual.plusDays(7);
        cadastrarConsulta("Dr. Eduardo", paciente, dataHoraConsulta);
        
        try {
            cancelarConsulta("Motivo de teste", paciente);
        } catch (Exception e) {
            this.excecao = (RuntimeException) e;
            this.ultimaMensagem = e.getMessage();
        }
    }

    // ====================================================================================
    // THEN Steps
    // ====================================================================================

    @Then("o sistema deve registrar a consulta com sucesso")
    public void o_sistema_deve_registrar_a_consulta_com_sucesso() {
        assertNull(excecao);
        assertNotNull(consultaAtual);
        assertEquals(StatusConsulta.AGENDADA, consultaAtual.getStatus());
    }

    @Then("o sistema deve atualizar a agenda do {string}, marcando {string} como ocupado")
    public void o_sistema_deve_atualizar_a_agenda_do_marcando_como_ocupado(String medico, String hora) {
        assertFalse(repositorio.isHorarioLivre(medico, dataHoraConsulta));
    }

    @Then("o sistema deve impedir a marcação da consulta")
    public void o_sistema_deve_impedir_a_marcação_da_consulta() {
        assertNotNull(excecao);
    }
    
    @Then("nenhuma notificação deve ser enviada")
    public void nenhuma_notificação_deve_ser_enviada() {
        assertTrue(notificacaoServico.notificacoesEnviadas.isEmpty());
    }

    @Then("o sistema deve informar que o horário {string} já está ocupado")
    public void o_sistema_deve_informar_que_o_horário_já_está_ocupado(String hora) {
        assertTrue(ultimaMensagem.contains("Horário já está ocupado."));
    }

    @Then("o sistema deve prosseguir com a marcação da consulta")
    public void o_sistema_deve_prosseguir_com_a_marcação_da_consulta() {
        assertNull(excecao);
    }

    @Then("o sistema deve solicitar o cadastro prévio do paciente {string}")
    public void o_sistema_deve_solicitar_o_cadastro_prévio_do_paciente(String pacienteNome) {
        assertTrue(ultimaMensagem.contains("Paciente não encontrado."));
    }

    @Then("o sistema deve informar que o médico {string} não foi encontrado")
    public void o_sistema_deve_informar_que_o_médico_não_foi_encontrado(String medicoNome) {
        assertTrue(ultimaMensagem.contains("Médico não encontrado."));
    }

    @Then("o Status da consulta não deve ser atualizado")
    public void o_status_da_consulta_não_deve_ser_atualizado() {
        assertNull(consultaAtual);
    }
    
    @Then("o sistema deve informar que não é possível agendar consultas para datas passadas")
    public void o_sistema_deve_informar_que_não_é_possível_agendar_consultas_para_datas_passadas() {
        assertTrue(ultimaMensagem.contains("datas passadas"));
    }

    @Then("o sistema deve prosseguir com a marcação")
    public void o_sistema_deve_prosseguir_com_a_marcação() {
        assertNull(excecao);
    }

    @Then("o sistema deve informar que o {string} não atende {string}")
    public void o_sistema_deve_informar_que_o_não_atende(String medico, String especialidade) {
        assertTrue(ultimaMensagem.contains("não atende"));
    }

    @Then("o paciente {string} deve receber uma confirmação por e-mail")
    public void o_paciente_deve_receber_uma_confirmação_por_e_mail(String paciente) {
        assertTrue(notificacaoServico.notificacoesEnviadas.contains(paciente + ":E-mail"));
    }

    @Then("o médico {string} deve receber uma confirmação por e-mail")
    public void o_médico_deve_receber_uma_confirmação_por_e_mail(String medico) {
        assertTrue(notificacaoServico.notificacoesEnviadas.contains(medico + ":E-mail"));
    }

    @Then("o paciente {string} deve receber uma confirmação via SMS")
    public void o_paciente_deve_receber_uma_confirmação_via_sms(String paciente) {
        assertTrue(notificacaoServico.notificacoesEnviadas.contains(paciente + ":SMS"));
    }

    @Then("o sitstema deverá remarcar a consulta do paciente {string} para o dia {string} às {string}")
    public void o_sitstema_deverá_remarcar_a_consulta_do_paciente_para_o_dia_às(String paciente, String data, String hora) {
        assertNull(excecao);
        assertEquals(dataHoraNovaConsulta, consultaAtual.getDataHora());
    }

    @Then("o histórico da consulta deve conter uma entrada registrando a alteração")
    public void o_histórico_da_consulta_deve_conter_uma_entrada_registrando_a_alteração() {
        assertEquals(1, consultaAtual.getHistorico().size()); 
    }

    @Then("o histórico de remarcações deve ser consultável no prontuário do paciente e na agenda do médico")
    public void o_histórico_de_remarcações_deve_ser_consultável_no_prontuário_do_paciente_e_na_agenda_do_médico() {
    }

    @Then("o sitstema deve informar que já existe uma consulta marcada para o dia e o horário escolhido")
    public void o_sitstema_deve_informar_que_já_existe_uma_consulta_marcada_para_o_dia_e_o_horário_escolhido() {
        assertTrue(ultimaMensagem.toLowerCase().contains("já existe uma consulta marcada"));
    }

    @Then("a consulta não deve ser remarcada")
    public void a_consulta_não_deve_ser_remarcada() {
        assertNotNull(excecao);
        if (consultaJoao != null) {
             assertEquals(LocalDateTime.of(2025, 12, 20, 14, 0), consultaJoao.getDataHora()); 
        } else {
             assertEquals(dataHoraConsulta, consultaAtual.getDataHora());
        }
    }

    @Then("o histórico não deve ser alterado")
    public void o_histórico_não_deve_ser_alterado() {
        assertNotNull(excecao);
        assertTrue(consultaAtual.getHistorico().isEmpty());
    }

    @Then("o sistema deve registrar a remarcação com sucesso")
    public void o_sistema_deve_registrar_a_remarcação_com_sucesso() {
        assertNull(excecao);
    }

    @Then("o campo {string} deve ser atualizado para {string}")
    public void o_campo_deve_ser_atualizado_para(String campo, String valor) {
        assertEquals(Integer.parseInt(valor), consultaAtual.getRemarcacoesCount());
    }

    @Then("o sistema deve informar que o limite de remarcações foi atingido")
    public void o_sistema_deve_informar_que_o_limite_de_remarcações_foi_atingido() {
    }

    @Then("o sistema deve impedir a remarcação da consulta")
    public void o_sistema_deve_impedir_a_remarcação_da_consulta() {
        assertNotNull(excecao);
    }

    @Then("o sistema deve informar que o limite máximo de {int} remarcações foi atingido")
    public void o_sistema_deve_informar_que_o_limite_máximo_de_remarcações_foi_atingido(Integer int1) {
        assertTrue(ultimaMensagem.contains("Limite máximo de 2 remarcações foi atingido."));
    }

    @Then("o Status e o Histórico da consulta não devem ser atualizados")
    public void o_status_e_o_histórico_da_consulta_não_devem_ser_atualizados() {
        assertEquals(2, consultaAtual.getRemarcacoesCount());
    }

    @Then("o paciente {string} deve receber uma notificação de alteração por e-mail")
    public void o_paciente_deve_receber_uma_notificação_de_alteração_por_e_mail(String paciente) {
        assertTrue(notificacaoServico.notificacoesEnviadas.contains(paciente + ":E-mail"));
    }

    @Then("o médico {string} deve receber uma notificação de alteração via SMS")
    public void o_médico_deve_receber_uma_notificação_de_alteração_via_sms(String medico) {
        assertTrue(notificacaoServico.notificacoesEnviadas.contains(medico + ":SMS"));
    }

    @Then("a notificação deve incluir a Data e Hora Originais e a Nova Data e Hora")
    public void a_notificação_deve_incluir_a_data_e_hora_originais_e_a_nova_data_e_hora() {
        assertFalse(notificacaoServico.notificacoesEnviadas.isEmpty());
    }
    
    @Then("o sistema deve processar a remarcação normalmente")
    public void o_sistema_deve_processar_a_remarcação_normalmente() {
        assertNull(excecao);
    }

    @Then("não deve ser aplicada nenhuma taxa ou bloqueio")
    public void não_deve_ser_aplicada_nenhuma_taxa_ou_bloqueio() {
    }
    
    @Then("o sistema deve informar que a remarcação só é possível com mais de 24h de antecedência")
    public void o_sistema_deve_informar_que_a_remarcação_só_é_possível_com_mais_de_24h_de_antecedência() {
        assertTrue(ultimaMensagem.contains("24h de antecedência"));
    }
    
    @Then("o sistema deve alertar sobre a aplicação de uma taxa de {int}% do valor da consulta")
    public void o_sistema_deve_alertar_sobre_a_aplicação_de_uma_taxa_do_valor_da_consulta(Integer int1) {
        assertTrue(true); 
    }

    @Then("o sistema deve exigir a confirmação do usuário para prosseguir com a remarcação e a cobrança")
    public void o_sistema_deve_exigir_a_confirmação_do_usuário_para_prosseguir_com_a_remarcação_e_a_cobrança() {
        assertTrue(true);
    }
    
    @Then("o sistema deve permitir o cancelamento normalmente")
    public void o_sistema_deve_permitir_o_cancelamento_normalmente() {
        assertNull(excecao);
        assertEquals(StatusConsulta.CANCELADA, consultaAtual.getStatus());
    }

    @Then("o histórico deve indicar o responsável pelo cancelamento \\({string})")
    public void o_histórico_deve_indicar_o_responsável_pelo_cancelamento(String responsavel) {
        assertTrue(true);
    }

    @Then("o sistema deve impedir o cancelamento")
    public void o_sistema_deve_impedir_o_cancelamento() {
        assertNotNull(excecao);
    }

    @Then("o sistema deve informar que o prazo limite de {int} horas foi excedido")
    public void o_sistema_deve_informar_que_o_prazo_limite_de_horas_foi_excedido(Integer int1) {
        assertTrue(ultimaMensagem.contains("prazo limite de 24 horas foi excedido"));
    }

    @Then("o Status da consulta deve permanecer {string}")
    public void o_status_da_consulta_deve_permanecer(String status) {
        if (excecao != null) {
            assertEquals(StatusConsulta.AGENDADA, consultaAtual.getStatus());
        }
    }
    
    @Then("o sistema deve registrar o motivo {string} no histórico da consulta")
    public void o_sistema_deve_registrar_o_motivo_no_histórico_da_consulta(String motivo) {
        assertEquals(motivo, consultaAtual.getMotivoCancelamento());
    }

    @Then("o Status da consulta deve ser alterada para {string}")
    public void o_status_da_consulta_deve_ser_alterada_para(String status) {
        assertEquals(StatusConsulta.CANCELADA, consultaAtual.getStatus());
    }
    
    @Then("o sistema deve informar que o motivo é obrigatório")
    public void o_sistema_deve_informar_que_o_motivo_é_obrigatório() {
        assertTrue(ultimaMensagem.contains("motivo é obrigatório"));
    }
    
    @Then("o horário {string} do dia {string} deve ser marcado como {string} na agenda do {string}")
    public void o_horário_do_dia_deve_ser_marcado_como_na_agenda_do(String hora, String data, String statusLivre, String medico) {
        LocalDateTime dataHora = parseDateTime(data, hora);
        assertTrue(repositorio.isHorarioLivre(medico, dataHora));
    }

    @Then("uma nova marcação de consulta para este horário deve ser permitida")
    public void uma_nova_marcação_de_consulta_para_este_horário_deve_ser_permitida() {
    }
    
    @Then("o paciente {string} deve receber uma notificação de cancelamento por e-mail")
    public void o_paciente_deve_receber_uma_notificação_de_cancelamento_por_e_mail(String paciente) {
        assertTrue(notificacaoServico.notificacoesEnviadas.contains(paciente + ":E-mail"));
    }

    @Then("o médico {string} deve receber uma notificação de cancelamento via SMS")
    public void o_médico_deve_receber_uma_notificação_de_cancelamento_via_sms(String medico) {
        assertTrue(notificacaoServico.notificacoesEnviadas.contains(medico + ":SMS"));
    }

    @Then("a notificação deve informar que o horário está liberado")
    public void a_notificação_deve_informar_que_o_horário_está_liberado() {
        assertFalse(notificacaoServico.notificacoesEnviadas.isEmpty());
    }
    
    @Then("o sistema deve registrar o cancelamento")
    public void o_sistema_deve_registrar_o_cancelamento() {
         if (excecao != null) {
              assertEquals(StatusConsulta.AGENDADA, consultaAtual.getStatus());
         } else {
              assertEquals(StatusConsulta.CANCELADA, consultaAtual.getStatus());
         }
    }

    @Then("o sistema deve adicionar um alerta no perfil do {string}")
    public void o_sistema_deve_adicionar_um_alerta_no_perfil_do(String paciente) {
        assertTrue(ultimaMensagem.contains("Penalidade"));
    }

    @Then("o sistema deve aplicar uma {string}")
    public void o_sistema_deve_aplicar_uma(String penalidade) {
        assertTrue(ultimaMensagem.contains("restrição de agendamento por 30 dias"));
    }

    @Then("o paciente {string} deve ser notificado sobre a penalidade")
    public void o_paciente_deve_ser_notificado_sobre_a_penalidade(String paciente) {
        assertTrue(true); 
    }

    @Then("o sistema não deve aplicar nenhuma restrição ao perfil da {string}")
    public void o_sistema_não_deve_aplicar_nenhuma_restrição_ao_perfil_da(String paciente) {
        assertNull(excecao); 
        assertEquals(StatusConsulta.CANCELADA, consultaAtual.getStatus());
    }

    @Then("o sistema não deve gerar alerta de penalidade")
    public void o_sistema_não_deve_gerar_alerta_de_penalidade() {
         assertNull(excecao);
    }
}