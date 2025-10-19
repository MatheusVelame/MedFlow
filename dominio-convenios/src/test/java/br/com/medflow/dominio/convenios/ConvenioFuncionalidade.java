package br.com.medflow.dominio.convenios;

import static br.com.medflow.dominio.convenios.AcaoHistorico.ARQUIVAMENTO;
import static br.com.medflow.dominio.convenios.AcaoHistorico.ATUALIZACAO;
import static br.com.medflow.dominio.convenios.AcaoHistorico.CRIACAO;
import static br.com.medflow.dominio.convenios.AcaoHistorico.REVISAO_APROVADA;
import static br.com.medflow.dominio.convenios.AcaoHistorico.REVISAO_SOLICITADA;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;

public class ConvenioFuncionalidade extends ConvenioFuncionalidadeBase {

	private Convenio convenioEmCadastro;
	private Convenio convenioExistente;
	private String nomeConvenio;
	private String codigoIdentificacao;
	private String usuarioAtual;
	private String perfilAtual;
	private String ultimaMensagem;
	private RuntimeException excecao;
	private boolean procedimentoAtivo = false; 

    @Before 
	public void setup() {
		resetContexto();
	}
    
    private void resetContexto() {
		excecao = null;
		convenioEmCadastro = null;
		convenioExistente = null;
		ultimaMensagem = null;
		nomeConvenio = null;
		codigoIdentificacao = null;
		procedimentoAtivo = false;
		eventos.clear();
        repositorio.clear(); 
	}
    
    // ===== GIVEN =====
    
    @Given("que o usuário {string} tem permissão de {string}")
    public void que_o_usuario_tem_permissao_de(String usuario, String perfil) {
    	usuarioAtual = usuario;
    	perfilAtual = perfil;
    }
    
    @Given("o perfil {string} tem permissão para cadastrar convênios")
    public void o_perfil_tem_permissao_para_cadastrar_convenios(String perfil) {
    	assertTrue(temPermissao(perfil, "cadastrar"));
    }
    
    @Given("o perfil {string} tem permissão para excluir convênios")
    public void o_perfil_tem_permissao_para_excluir_convenios(String perfil) {
    	assertTrue(temPermissao(perfil, "excluir"));
    }
    
    @Given("o perfil {string} tem permissão para alterar convênios")
    public void o_perfil_tem_permissao_para_alterar_convenios(String perfil) {
    	assertTrue(temPermissao(perfil, "alterar"));
    }
    
    @Given("o convênio {string} ainda não está registrado no sistema")
    public void o_convenio_ainda_nao_esta_registrado_no_sistema(String nome) {
    	nomeConvenio = nome;
    	assertFalse(obterConvenio(nome).isPresent());
    }
    
    @Given("já existe um convênio cadastrado com o código {string}")
    public void ja_existe_um_convenio_cadastrado_com_o_codigo(String codigo) {
    	codigoIdentificacao = codigo;
    	UsuarioResponsavelId id = getUsuarioId("SetupCadastro");
    	convenioServico.cadastrar("Setup Convênio", codigo, id);
    	convenioExistente = obterConvenioPorCodigo(codigo).get();
    	assertNotNull(convenioExistente);
    }
    
    @Given("existe um convênio cadastrado com o nome {string}")
    public void existe_um_convenio_cadastrado_com_o_nome(String nome) {
    	nomeConvenio = nome;
    	UsuarioResponsavelId id = getUsuarioId("SetupCadastro");
    	convenioServico.cadastrar(nome, "COD" + nome.substring(0,3).toUpperCase(), id);
    	convenioExistente = obterConvenio(nome).get();
    }
    
    @Given("o status do convênio está definido como {string}")
    public void o_status_do_convenio_esta_definido_como(String status) {
    	assertNotNull(convenioExistente);
    	UsuarioResponsavelId id = getUsuarioId(usuarioAtual);
    	
    	if (status.equalsIgnoreCase("Ativo")) {
    		convenioExistente.definirStatus(StatusConvenio.ATIVO, id);
    	} else if (status.equalsIgnoreCase("Inativo")) {
    		convenioExistente.definirStatus(StatusConvenio.INATIVO, id);
    	}
    	
    	repositorio.salvar(convenioExistente);
    }
    
    @Given("um novo convênio está sendo cadastrado")
    public void um_novo_convenio_esta_sendo_cadastrado() {
    	nomeConvenio = "Convenio Genérico";
    }

    // ===== WHEN =====
    
    @When("o usuário {string} tentar cadastrar um novo convênio com o nome {string}")
    public void o_usuario_tentar_cadastrar_um_novo_convenio_com_o_nome(String usuario, String nome) {
    	usuarioAtual = usuario;
    	nomeConvenio = nome;
    }

    @When("o código de identificação {string}")
    public void o_codigo_de_identificacao(String codigo) {
    	codigoIdentificacao = codigo;
    	try {
    		UsuarioResponsavelId id = getUsuarioId(usuarioAtual);
    		convenioServico.cadastrar(nomeConvenio, codigoIdentificacao, id);
    		convenioEmCadastro = obterConvenio(nomeConvenio).get();
    		ultimaMensagem = "Convênio cadastrado com sucesso!";
    	} catch (RuntimeException e) {
    		excecao = e;
    		ultimaMensagem = e.getMessage();
    	}
    }
    
    @When("o usuário {string} tentar cadastrar o nome do convênio como {string}")
    public void o_usuario_tentar_cadastrar_o_nome_do_convenio_como(String usuario, String nome) {
    	usuarioAtual = usuario;
    	nomeConvenio = nome;
    }
    
    @When("o usuário {string} seleciona o convênio {string}")
    public void o_usuario_seleciona_o_convenio(String usuario, String nome) {
    	usuarioAtual = usuario;
    	nomeConvenio = nome;
    	convenioExistente = obterConvenio(nome).orElseThrow(() -> new IllegalStateException("Convênio não encontrado"));
    }

    @When("solicita a exclusão definitiva")
    public void solicita_a_exclusao_definitiva() {
    	try {
    		UsuarioResponsavelId id = getUsuarioId(usuarioAtual);
    		convenioServico.remover(convenioExistente, id);
    		ultimaMensagem = "Convênio removido com sucesso!";
    	} catch (RuntimeException e) {
    		excecao = e;
    		ultimaMensagem = e.getMessage();
    	}
    }

    @When("o usuário {string} altera o nome do convênio para {string}")
    public void o_usuario_altera_o_nome_do_convenio_para(String usuario, String novoNome) {
    	usuarioAtual = usuario;
    	try {
    		UsuarioResponsavelId id = getUsuarioId(usuario);
    		convenioServico.alterar(convenioExistente, novoNome, id);
    		convenioExistente = obterConvenio(novoNome).orElseThrow();
    		ultimaMensagem = "Convênio atualizado com sucesso!";
    	} catch (RuntimeException e) {
    		excecao = e;
    		ultimaMensagem = e.getMessage();
    	}
    }

    @When("confirma a alteração")
    public void confirma_a_alteracao() {
    	assertNull(excecao);
    }

    // ===== THEN =====
    
    @Then("o sistema deve registrar o convênio com sucesso")
    public void o_sistema_deve_registrar_o_convenio_com_sucesso() {
    	assertNotNull(convenioEmCadastro);
    	assertEquals(StatusConvenio.ATIVO, convenioEmCadastro.getStatus());
    }
    
    @Then("o Status do convênio recém-cadastrado deve ser automaticamente definido como {string}")
    public void o_status_do_convenio_recem_cadastrado_deve_ser_definido_como(String status) {
    	assertEquals(status, convenioEmCadastro.getStatus().name());
    }
    
    @Then("o sistema deve exibir a mensagem {string}")
    public void o_sistema_deve_exibir_a_mensagem(String mensagem) {
    	assertEquals(mensagem, ultimaMensagem);
    }
    
    @Then("o sistema deverá informar que o nome do convênio é obrigatório")
    public void o_sistema_devera_informar_que_o_nome_do_convenio_e_obrigatorio() {
    	assertNotNull(excecao);
    	assertEquals("O nome do convênio é obrigatório.", ultimaMensagem);
    }
    
    @Then("o convênio não deve ser cadastrado no sistema")
    public void o_convenio_nao_deve_ser_cadastrado_no_sistema() {
    	assertFalse(obterConvenio(nomeConvenio).isPresent());
    }

    @Then("o sistema deve impedir o cadastro do convênio")
    public void o_sistema_deve_impedir_o_cadastro_do_convenio() {
    	assertNotNull(excecao);
    }

    @Then("o sistema deve informar que o código de identificação já está em uso")
    public void o_sistema_deve_informar_que_o_codigo_de_identificacao_ja_esta_em_uso() {
    	assertEquals("Código de identificação já existente.", ultimaMensagem);
    }
    
    @Then("o sistema deve impedir a exclusão")
    public void o_sistema_deve_impedir_a_exclusao() {
    	assertNotNull(excecao);
    }
    
    @Then("o histórico não deve ser atualizado")
    public void o_historico_nao_deve_ser_atualizado() {
    	assertTrue(eventos.isEmpty());
    }
    
    @Then("o sistema deve remover o convênio com sucesso")
    public void o_sistema_deve_remover_o_convenio_com_sucesso() {
    	assertFalse(obterConvenio(nomeConvenio).isPresent());
    }
    
    @Then("o sistema deve registrar a ação no histórico com data, hora e usuário responsável")
    public void o_sistema_deve_registrar_a_acao_no_historico_com_data_hora_e_usuario_responsavel() {
    	assertTrue(eventos.stream().anyMatch(e -> e.getUsuario().equals(usuarioAtual)));
    }
}
