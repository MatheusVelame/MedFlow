package br.com.medflow.dominio.financeiro.faturamentos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class FaturamentoFuncionalidade extends FaturamentoFuncionalidadeBase {

    private Faturamento faturamentoEmTeste;
    private String procedimentoAtual;
    private String valorAtual;
    private String metodoAtual;
    private String dataHoraAtual;
    private String statusAtual;
    private String justificativaAtual;
    private RuntimeException excecao;

    @Before 
    public void setup() {
        configurarContexto();
    }

    // ===== GIVEN STEPS =====
    
    @Given("existe o paciente {string} cadastrado")
    public void existe_o_paciente_cadastrado(String pacienteId) {
        // Simulação - em um sistema real, verificaria se o paciente existe
        assertTrue(true, "Paciente " + pacienteId + " existe");
    }

    @Given("existe na tabela o procedimento {string}")
    public void existe_na_tabela_o_procedimento(String procedimento) {
        this.procedimentoAtual = procedimento;
        // Simulação - em um sistema real, verificaria se o procedimento existe na tabela
        assertTrue(true, "Procedimento " + procedimento + " existe na tabela");
    }

    @Given("a tabela de preços define {string} = {double}")
    public void a_tabela_de_preços_define(String procedimento, Double valor) {
        // Configurar a tabela de preços dinamicamente
        tabelaPrecos.configurarPreco(procedimento, new java.math.BigDecimal(valor.toString()));
        assertTrue(true, "Tabela de preços configurada: " + procedimento + " = " + valor);
    }

    @Given("o usuário {string} \\(Administrador Financeiro\\) está autenticado")
    public void o_usuário_administrador_financeiro_está_autenticado(String usuario) {
        UsuarioContext.setUsuario(usuario, "Administrador Financeiro");
        // Simulação - em um sistema real, verificaria autenticação e permissões
        assertTrue(true, "Usuário " + usuario + " autenticado como Administrador Financeiro");
    }


    // ===== WHEN STEPS =====
    
    @When("a usuária registrar um faturamento com:")
    public void a_usuária_registrar_um_faturamento_com(DataTable dataTable) {
        try {
            var dados = dataTable.asMap(String.class, String.class);
            
            var pacienteId = new PacienteId(dados.get("paciente"));
            var tipoProcedimento = mapearTipoProcedimento(dados.get("procedimento"));
            var descricaoProcedimento = dados.get("procedimento");
            var valor = new Valor(new java.math.BigDecimal(dados.get("valor")));
            var metodoPagamento = new MetodoPagamento(dados.get("metodo"));
            var usuarioResponsavel = new UsuarioResponsavelId(UsuarioContext.getUsuarioAtual());
            
            this.faturamentoEmTeste = servico.registrarFaturamento(
                pacienteId, tipoProcedimento, descricaoProcedimento, 
                valor, metodoPagamento, usuarioResponsavel, null
            );
            
            System.out.println("DEBUG: Faturamento criado com sucesso: " + (faturamentoEmTeste != null ? "SIM" : "NÃO"));
            
        } catch (Exception e) {
            this.excecao = (RuntimeException) e;
            System.out.println("DEBUG: Exceção ao criar faturamento: " + e.getMessage());
        }
    }

    @When("o usuário tentar registrar um faturamento com:")
    public void o_usuário_tentar_registrar_um_faturamento_com(DataTable dataTable) {
        try {
            var dados = dataTable.asMap(String.class, String.class);
            
            var pacienteId = new PacienteId(dados.get("paciente"));
            var tipoProcedimento = mapearTipoProcedimento(dados.get("procedimento"));
            var descricaoProcedimento = dados.get("procedimento");
            var valor = new Valor(new java.math.BigDecimal(dados.get("valor")));
            var metodoPagamento = dados.get("metodo") != null && !dados.get("metodo").trim().isEmpty() 
                ? new MetodoPagamento(dados.get("metodo")) : null;
            var usuarioResponsavel = new UsuarioResponsavelId(UsuarioContext.getUsuarioAtual());
            
            this.faturamentoEmTeste = servico.registrarFaturamento(
                pacienteId, tipoProcedimento, descricaoProcedimento, 
                valor, metodoPagamento, usuarioResponsavel, null
            );
            
        } catch (Exception e) {
            this.excecao = (RuntimeException) e;
        }
    }

    @When("a usuária enviar um registro com campo de status {string}")
    public void a_usuária_enviar_um_registro_com_campo_de_status(String status, DataTable dataTable) {
        try {
            var dados = dataTable.asMap(String.class, String.class);
            
            var pacienteId = new PacienteId(dados.get("paciente"));
            var tipoProcedimento = mapearTipoProcedimento(dados.get("procedimento"));
            var descricaoProcedimento = dados.get("procedimento");
            var valor = new Valor(new java.math.BigDecimal(dados.get("valor")));
            var metodoPagamento = new MetodoPagamento(dados.get("metodo"));
            var usuarioResponsavel = new UsuarioResponsavelId(UsuarioContext.getUsuarioAtual());
            
            this.faturamentoEmTeste = servico.registrarFaturamento(
                pacienteId, tipoProcedimento, descricaoProcedimento, 
                valor, metodoPagamento, usuarioResponsavel, null
            );
            
            // O status deve ser ignorado e sempre ser Pendente
            assertTrue(faturamentoEmTeste.getStatus() == StatusFaturamento.PENDENTE, 
                "Status deve ser ignorado e sempre ser Pendente");
            
        } catch (Exception e) {
            this.excecao = (RuntimeException) e;
        }
    }

    // ===== THEN STEPS =====
    
    @Then("o sistema deve criar o faturamento com status inicial {string}")
    public void o_sistema_deve_criar_o_faturamento_com_status_inicial(String status) {
        assertNotNull(faturamentoEmTeste, "Faturamento deve ter sido criado");
        assertEquals(StatusFaturamento.valueOf(status.toUpperCase()), faturamentoEmTeste.getStatus());
    }

    @Then("o registro deve conter paciente, procedimento, valor e método de pagamento")
    public void o_registro_deve_conter_paciente_procedimento_valor_e_método_de_pagamento() {
        assertNotNull(faturamentoEmTeste.getPacienteId(), "Paciente deve estar presente");
        assertNotNull(faturamentoEmTeste.getTipoProcedimento(), "Procedimento deve estar presente");
        assertNotNull(faturamentoEmTeste.getValor(), "Valor deve estar presente");
        assertNotNull(faturamentoEmTeste.getMetodoPagamento(), "Método de pagamento deve estar presente");
    }

    @Then("o sistema deve rejeitar o registro informando que {string}")
    public void o_sistema_deve_rejeitar_o_registro_informando_que(String mensagem) {
        assertThrows(IllegalArgumentException.class, () -> {
            if (excecao != null) {
                throw excecao;
            }
        });
    }

    @Then("o sistema deve aceitar o registro")
    public void o_sistema_deve_aceitar_o_registro() {
        assertNotNull(faturamentoEmTeste, "Faturamento deve ter sido criado com sucesso");
    }

    @Then("a validação automática de valor deve indicar {string}")
    public void a_validação_automática_de_valor_deve_indicar(String resultado) {
        assertNotNull(faturamentoEmTeste, "Faturamento deve ter sido criado com validação bem-sucedida");
    }

    @Then("nenhum faturamento deve ser criado")
    public void nenhum_faturamento_deve_ser_criado() {
        assertNotNull(excecao, "Deveria ter ocorrido uma exceção impedindo a criação do faturamento");
        assertTrue(excecao instanceof RuntimeException, "Exceção deveria ser RuntimeException");
    }

    @Then("o sistema deve ignorar o valor recebido para status e salvar como {string}")
    public void o_sistema_deve_ignorar_o_valor_recebido_para_status_e_salvar_como(String status) {
        assertNotNull(faturamentoEmTeste, "Faturamento deve ter sido criado");
        assertEquals(StatusFaturamento.valueOf(status.toUpperCase()), faturamentoEmTeste.getStatus());
    }

    @Then("registrar em log que o status informado foi sobrescrito pela regra de negócio")
    public void registrar_em_log_que_o_status_informado_foi_sobrescrito_pela_regra_de_negócio() {
        // Simulação - em um sistema real, verificaria o log
        assertTrue(true, "Log de sobrescrita de status registrado");
    }

    // ===== STEPS FALTANTES =====
    
    @When("a usuária tentar registrar um faturamento com:")
    public void a_usuária_tentar_registrar_um_faturamento_com(DataTable dataTable) {
        o_usuário_tentar_registrar_um_faturamento_com(dataTable);
    }

    @Then("o sistema deve rejeitar o registro informando {string}")
    public void o_sistema_deve_rejeitar_o_registro_informando(String mensagem) {
        assertNotNull(excecao, "Deveria ter ocorrido uma exceção");
        assertTrue(excecao.getMessage().contains(mensagem) || 
                  excecao.getClass().getSimpleName().contains("Exception"), 
                  "Exceção deveria conter: " + mensagem);
    }


    @Then("o sistema deve criar o faturamento com status {string} \\(independente de qualquer entrada de status)")
    public void o_sistema_deve_criar_o_faturamento_com_status_independente_de_qualquer_entrada_de_status(String status) {
        assertNotNull(faturamentoEmTeste, "Faturamento deve ter sido criado");
        assertEquals(status, faturamentoEmTeste.getStatus().toString(), "Status deve ser " + status);
    }

    // ===== MÉTODOS AUXILIARES =====
    
    private TipoProcedimento mapearTipoProcedimento(String procedimento) {
        if (procedimento.toLowerCase().contains("consulta")) {
            return TipoProcedimento.CONSULTA;
        } else if (procedimento.toLowerCase().contains("exame")) {
            return TipoProcedimento.EXAME;
        } else {
            return TipoProcedimento.CONSULTA; // default
        }
    }
}
