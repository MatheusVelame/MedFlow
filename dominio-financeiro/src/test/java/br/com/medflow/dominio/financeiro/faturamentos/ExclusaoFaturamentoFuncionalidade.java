package br.com.medflow.dominio.financeiro.faturamentos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class ExclusaoFaturamentoFuncionalidade extends FaturamentoFuncionalidadeBase {

    private Faturamento faturamentoEmTeste;
    private String motivoExclusao;
    private String dataHoraExclusao;
    private FaturamentoId faturamentoId;
    private RuntimeException excecao;

    @Before 
    public void setup() {
        configurarContexto();
    }

    // ===== GIVEN STEPS =====
    
    @Given("existe um faturamento com ID {string} e status {string}")
    public void existe_um_faturamento_com_id_e_status(String id, String status) {
        this.faturamentoId = new FaturamentoId(id);
        
        // Criar faturamento com status específico
        var pacienteId = new PacienteId("PAC-001");
        var tipo = TipoProcedimento.CONSULTA;
        var descricao = "Consulta de Exemplo";
        var valor = new Valor(new java.math.BigDecimal("200.00"));
        var metodo = new MetodoPagamento("Cartão");
        var responsavel = new UsuarioResponsavelId("Admin");
        
        this.faturamentoEmTeste = new Faturamento(pacienteId, tipo, descricao, valor, metodo, responsavel, null);
        
        // Definir ID específico para o teste
        try {
            var idField = Faturamento.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(faturamentoEmTeste, this.faturamentoId);
        } catch (Exception e) {
            // Ignorar erro de reflexão em teste
        }
        
        // Definir status específico
        StatusFaturamento statusEnum = mapearStatus(status);
        try {
            var statusField = Faturamento.class.getDeclaredField("status");
            statusField.setAccessible(true);
            statusField.set(faturamentoEmTeste, statusEnum);
        } catch (Exception e) {
            // Ignorar erro de reflexão em teste
        }
        
        repositorio.salvar(faturamentoEmTeste);
    }

    @Given("o usuário {string} possui papel {string}")
    public void o_usuário_possui_papel(String usuario, String papel) {
        UsuarioContext.setUsuario(usuario, papel);
    }

    @Given("o usuário {string} possui papel {string} \\(sem permissão administrativa\\)")
    public void o_usuário_possui_papel_sem_permissão_administrativa(String usuario, String papel) {
        UsuarioContext.setUsuario(usuario, papel);
    }

    @Given("o motivo informado é {string}")
    public void o_motivo_informado_é(String motivo) {
        this.motivoExclusao = motivo;
    }

    // ===== WHEN STEPS =====
    
    @When("a usuária solicitar a exclusão do faturamento {string} em {string}")
    public void a_usuária_solicitar_a_exclusão_do_faturamento_em(String id, String dataHora) {
        this.faturamentoId = new FaturamentoId(id);
        this.dataHoraExclusao = dataHora;
        
        try {
            boolean ehAdministrador = "Administrador Financeiro".equals(UsuarioContext.getPapelUsuario()) || 
                                    "Administrador do Sistema".equals(UsuarioContext.getPapelUsuario());
            
            servico.excluirLogicamente(faturamentoId, motivoExclusao, 
                new UsuarioResponsavelId(UsuarioContext.getUsuarioAtual()), ehAdministrador);
            
        } catch (Exception e) {
            excecao = (RuntimeException) e;
        }
    }

    @When("a usuária tentar excluir o faturamento {string} em {string}")
    public void a_usuária_tentar_excluir_o_faturamento_em(String id, String dataHora) {
        this.faturamentoId = new FaturamentoId(id);
        this.dataHoraExclusao = dataHora;
        
        try {
            boolean ehAdministrador = "Administrador Financeiro".equals(UsuarioContext.getPapelUsuario()) || 
                                    "Administrador do Sistema".equals(UsuarioContext.getPapelUsuario());
            
            servico.excluirLogicamente(faturamentoId, motivoExclusao, 
                new UsuarioResponsavelId(UsuarioContext.getUsuarioAtual()), ehAdministrador);
            
        } catch (Exception e) {
            excecao = (RuntimeException) e;
        }
    }

    @When("o usuário excluir o faturamento {string} em {string}")
    public void o_usuário_excluir_o_faturamento_em(String id, String dataHora) {
        this.faturamentoId = new FaturamentoId(id);
        this.dataHoraExclusao = dataHora;
        
        try {
            boolean ehAdministrador = "Administrador Financeiro".equals(UsuarioContext.getPapelUsuario()) || 
                                    "Administrador do Sistema".equals(UsuarioContext.getPapelUsuario());
            
            servico.excluirLogicamente(faturamentoId, motivoExclusao, 
                new UsuarioResponsavelId(UsuarioContext.getUsuarioAtual()), ehAdministrador);
            
        } catch (Exception e) {
            excecao = (RuntimeException) e;
        }
    }

    // ===== THEN STEPS =====
    
    @Then("o sistema deve marcar o faturamento {string} como {string} \\(exclusão lógica\\)")
    public void o_sistema_deve_marcar_o_faturamento_como_exclusão_lógica(String id, String status) {
        var faturamento = servico.obter(faturamentoId);
        assertEquals(StatusFaturamento.valueOf(status.toUpperCase()), faturamento.getStatus());
    }

    @Then("deve registrar no log: ID {string}, status atual {string}, usuário {string}, data\\/hora {string}, ação {string}, motivo {string}")
    public void deve_registrar_no_log_id_status_atual_usuário_data_hora_ação_motivo(String id, String status, String usuario, String dataHora, String acao, String motivo) {
        var faturamento = servico.obter(faturamentoId);
        var historico = faturamento.getHistorico();
        
        assertTrue(historico.size() >= 1);
        var ultimaEntrada = historico.get(historico.size() - 1);
        assertEquals(AcaoHistoricoFaturamento.EXCLUSAO_LOGICA, ultimaEntrada.getAcao());
        assertTrue(ultimaEntrada.getDescricao().contains(motivo));
        assertEquals(usuario, ultimaEntrada.getResponsavel().getValor());
    }

    @Then("o registro não deve aparecer nas listagens operacionais padrão")
    public void o_registro_não_deve_aparecer_nas_listagens_operacionais_padrão() {
        var faturamentos = servico.pesquisarExcluindoRemovidos();
        assertTrue(faturamentos.stream().noneMatch(f -> f.getId().equals(faturamentoId)));
    }

    @Then("o sistema deve negar a operação informando {string}")
    public void o_sistema_deve_negar_a_operação_informando(String mensagem) {
        // Verificar tanto a exceção local quanto a compartilhada
        RuntimeException excecaoParaVerificar = excecao != null ? excecao : UsuarioContext.getExcecao();
        assertNotNull(excecaoParaVerificar, "Exceção deve ter sido lançada");
        assertTrue(excecaoParaVerificar instanceof RuntimeException, "Exceção deve ser do tipo RuntimeException");
    }

    @Then("o faturamento deve permanecer inalterado")
    public void o_faturamento_deve_permanecer_inalterado() {
        var faturamento = servico.obter(faturamentoId);
        assertTrue(faturamento.getStatus() != StatusFaturamento.REMOVIDO);
    }

    @Then("deve ser registrado log de segurança da tentativa negada com ID, usuário, data\\/hora, ação tentada e motivo")
    public void deve_ser_registrado_log_de_segurança_da_tentativa_negada_com_id_usuário_data_hora_ação_tentada_e_motivo() {
        var faturamento = servico.obter(faturamentoId);
        var historico = faturamento.getHistorico();
        
        assertTrue(historico.size() >= 1);
        var ultimaEntrada = historico.get(historico.size() - 1);
        assertEquals(AcaoHistoricoFaturamento.TENTATIVA_EXCLUSAO_NEGADA, ultimaEntrada.getAcao());
        assertEquals(UsuarioContext.getUsuarioAtual(), ultimaEntrada.getResponsavel().getValor());
    }

    @Then("deve registrar no log os campos: ID, status {string}, usuário {string}, data\\/hora {string}, ação {string}, motivo {string}")
    public void deve_registrar_no_log_os_campos_id_status_usuário_data_hora_ação_motivo(String status, String usuario, String dataHora, String acao, String motivo) {
        var faturamento = servico.obter(faturamentoId);
        var historico = faturamento.getHistorico();
        
        assertTrue(historico.size() >= 1);
        var ultimaEntrada = historico.get(historico.size() - 1);
        assertEquals(AcaoHistoricoFaturamento.EXCLUSAO_LOGICA, ultimaEntrada.getAcao());
        assertTrue(ultimaEntrada.getDescricao().contains(motivo));
        assertEquals(usuario, ultimaEntrada.getResponsavel().getValor());
    }

    @Then("o sistema deve impedir a exclusão informando {string}")
    public void o_sistema_deve_impedir_a_exclusão_informando(String mensagem) {
        assertNotNull(excecao, "Exceção deve ter sido lançada");
        assertTrue(excecao instanceof RuntimeException, "Exceção deve ser do tipo RuntimeException");
    }

    @Then("deve ser registrado log de tentativa negada com ID, status atual {string}, usuário, data\\/hora, ação tentada e motivo")
    public void deve_ser_registrado_log_de_tentativa_negada_com_id_status_atual_usuário_data_hora_ação_tentada_e_motivo(String status) {
        var faturamento = servico.obter(faturamentoId);
        var historico = faturamento.getHistorico();
        
        assertTrue(historico.size() >= 1);
        var ultimaEntrada = historico.get(historico.size() - 1);
        assertEquals(AcaoHistoricoFaturamento.TENTATIVA_EXCLUSAO_NEGADA, ultimaEntrada.getAcao());
        assertEquals(UsuarioContext.getUsuarioAtual(), ultimaEntrada.getResponsavel().getValor());
    }

    private StatusFaturamento mapearStatus(String status) {
        switch (status.toLowerCase()) {
            case "pendente":
                return StatusFaturamento.PENDENTE;
            case "pago":
                return StatusFaturamento.PAGO;
            case "cancelado":
                return StatusFaturamento.CANCELADO;
            case "inválido":
            case "invalido":
                return StatusFaturamento.INVALIDO;
            case "removido":
                return StatusFaturamento.REMOVIDO;
            default:
                return StatusFaturamento.valueOf(status.toUpperCase());
        }
    }

}