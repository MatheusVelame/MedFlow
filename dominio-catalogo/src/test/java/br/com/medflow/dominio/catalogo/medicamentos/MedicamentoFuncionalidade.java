package br.com.medflow.dominio.catalogo.medicamentos;

import static br.com.medflow.dominio.catalogo.medicamentos.AcaoHistorico.ARQUIVAMENTO;
import static br.com.medflow.dominio.catalogo.medicamentos.AcaoHistorico.ATUALIZACAO;
import static br.com.medflow.dominio.catalogo.medicamentos.AcaoHistorico.CRIACAO;
import static br.com.medflow.dominio.catalogo.medicamentos.AcaoHistorico.REVISAO_APROVADA;
import static br.com.medflow.dominio.catalogo.medicamentos.AcaoHistorico.REVISAO_SOLICITADA;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class MedicamentoFuncionalidade extends MedicamentoFuncionalidadeBase {

	private Medicamento medicamentoEmCadastro;
	private Medicamento medicamentoExistente;
	private String nomeMedicamento;
	private String usoPrincipal;
	private String contraindicacoes;
	private String usuarioAtual;
	private String perfilAtual;
	private String ultimaMensagem;
	private RuntimeException excecao;
	private boolean prescricaoAtiva = false; 

    @Before 
	public void setup() {
		resetContexto();
	}

	private void resetContexto() {
		excecao = null;
		medicamentoEmCadastro = null;
		medicamentoExistente = null;
		ultimaMensagem = null;
		nomeMedicamento = null;
		usoPrincipal = null;
		contraindicacoes = null;
		prescricaoAtiva = false;
		eventos.clear();
        repositorio.clear(); 
	}

	@Given("que o usuário {string} tem permissão de {string}")
	public void que_o_usuario_tem_permissao_de(String usuario, String perfil) {
		usuarioAtual = usuario;
		perfilAtual = perfil;
	}
	
	@Given("que o usuário {string}, funcionária da recepção, não tem permissão para alterar dados de medicamentos")
	public void que_o_usuário_funcionária_da_recepção_não_tem_permissão_para_alterar_dados_de_medicamentos(String usuario) {
	    usuarioAtual = usuario;
	    perfilAtual = "Recepção"; 
	}
	
	@Given("que o usuário {string} tem permissão de revisor")
	public void que_o_usuario_tem_permissao_de_revisor(String usuario) {
		usuarioAtual = usuario;
		if (usuario.contains("Helena") || usuario.contains("Carlos")) {
			perfilAtual = "Administrador"; 
		} else {
			perfilAtual = "Recepção";
		}
	}

	@Given("que o usuário {string} tem o perfil de {string}")
	public void que_o_usuário_tem_o_perfil_de(String usuario, String perfil) {
	    usuarioAtual = usuario;
	    perfilAtual = perfil;
	}
	
    @Given("o usuário {string}, funcionário do TI, não tem permissão de revisor")
	public void o_usuário_funcionário_do_ti_não_tem_permissão_de_revisor(String usuario) {
	    usuarioAtual = usuario;
	    perfilAtual = "Enfermeiro"; 
	}

	@Given("o usuário {string}, funcionário da recepção, possui acesso a lista de medicamentos")
	public void o_usuário_funcionário_da_recepção_possui_acesso_a_lista_de_medicamentos(String usuario) {
	    usuarioAtual = usuario;
	    perfilAtual = "Recepção";
	}

	@Given("o usuário {string} possui acesso a lista de medicamentos")
	public void o_usuário_possui_acesso_a_lista_de_medicamentos(String usuario) {
	    usuarioAtual = usuario;
	}

	@Given("o perfil {string} tem permissão para arquivar medicamentos")
	public void o_perfil_tem_permissão_para_arquivar_medicamentos(String perfil) {
		assertTrue(temPermissao(perfil, "arquivar"));
	}
	
	@Given("o perfil {string} tem permissão para arquivar ou remover medicamentos")
	public void o_perfil_tem_permissão_para_arquivar_ou_remover_medicamentos(String perfil) {
	    assertTrue(temPermissao(perfil, "arquivar") || temPermissao(perfil, "excluir_permanente"));
	}

	@Given("o perfil {string} não tem permissão para arquivar ou remover medicamentos")
	public void o_perfil_não_tem_permissão_para_arquivar_ou_remover_medicamentos(String perfil) {
	    assertFalse(temPermissao(perfil, "arquivar") || temPermissao(perfil, "excluir_permanente"));
	}

	@Given("o sistema prioriza o arquivamento sobre a exclusão")
	public void o_sistema_prioriza_o_arquivamento_sobre_a_exclusão() {
	}
	
	@Given("o usuário {string} tem permissão de revisor")
	public void o_usuário_tem_permissão_de_revisor(String usuario) {
		que_o_usuario_tem_permissao_de_revisor(usuario);
	}

	@Given("o perfil {string} tem permissão para cadastrar medicamentos")
	public void o_perfil_tem_permissão_para_cadastrar_medicamentos(String perfil) {
		assertTrue(temPermissao(perfil, "cadastrar"));
	}
	
	@Given("o medicamento {string} ainda não está registrado no sistema")
	public void o_medicamento_ainda_não_está_registrado_no_sistema(String nome) {
		nomeMedicamento = nome;
		assertFalse(obterMedicamento(nome).isPresent());
	}
	
	@Given("o medicamento {string} já está registrado no sistema")
	public void o_medicamento_já_está_registrado_no_sistema(String nome) {
		nomeMedicamento = nome;
		UsuarioResponsavelId id = getUsuarioId("SetupCadastro");
		medicamentoServico.cadastrar(nome, "Setup Uso", null, id);
        medicamentoExistente = obterMedicamento(nome).get();
	}
	
	@Given("o medicamento {string} está cadastrado com o uso principal {string}")
	public void o_medicamento_está_cadastrado_com_o_uso_principal(String nome, String uso) {
		nomeMedicamento = nome;
		UsuarioResponsavelId id = getUsuarioId("SetupCadastro");
		medicamentoServico.cadastrar(nome, uso, null, id);
        medicamentoExistente = obterMedicamento(nome).get();
	}
	
	@Given("o medicamento {string} está cadastrado com o status {string}")
	public void o_medicamento_está_cadastrado_com_o_status(String nome, String status) {
		nomeMedicamento = nome;
		UsuarioResponsavelId id = getUsuarioId("SetupCadastro");
		medicamentoServico.cadastrar(nome, "Setup Uso", null, id);
        medicamentoExistente = obterMedicamento(nome).get();
		
		if (status.equalsIgnoreCase("Arquivado")) {
			medicamentoExistente.mudarStatus(StatusMedicamento.ARQUIVADO, id);
		} else if (status.equalsIgnoreCase("Inativo")) {
			medicamentoExistente.mudarStatus(StatusMedicamento.INATIVO, id);
		}
		repositorio.salvar(medicamentoExistente);
        medicamentoExistente = obterMedicamento(nome).get();
	}

	@Given("que o medicamento {string} está cadastrado com o status {string}")
	public void que_o_medicamento_está_cadastrado_com_o_status(String nome, String status) {
		o_medicamento_está_cadastrado_com_o_status(nome, status);
	}

	@Given("o medicamento {string} está cadastrado com as contraindicações {string}")
	public void o_medicamento_está_cadastrado_com_as_contraindicações(String nome, String contraindicacoes) {
		nomeMedicamento = nome;
		UsuarioResponsavelId id = getUsuarioId("SetupCadastro");
		medicamentoServico.cadastrar(nome, "Setup Uso", contraindicacoes, id);
        medicamentoExistente = obterMedicamento(nome).get();
	}
	
	@Given("um novo medicamento está sendo cadastrado")
	public void um_novo_medicamento_está_sendo_cadastrado() {
		nomeMedicamento = "Medicamento Genérico";
	}
	
	@Given("o medicamento {string} está cadastrado com uma alteração pendente de revisão em Contraindicações")
	public void o_medicamento_está_cadastrado_com_uma_alteração_pendente_de_revisao_em_contraindicações(String nome) {
		nomeMedicamento = nome;
		UsuarioResponsavelId id = getUsuarioId("SetupCadastro");
		medicamentoServico.cadastrar(nome, "Setup Uso", "Hipersensibilidade", id);
        medicamentoExistente = obterMedicamento(nome).get();
		
		try {
			medicamentoExistente.solicitarRevisaoContraindicacoes("Valor Pendente", getUsuarioId("Dr. Carlos"));
		} catch (Medicamento.RevisaoPendenteException e) {
			repositorio.salvar(medicamentoExistente);
            medicamentoExistente = obterMedicamento(nome).get();
		}
	}

	@Given("a alteração pendente é a adição {string}")
	public void a_alteração_pendente_é_a_adição(String novoValor) {
        assertNotNull(nomeMedicamento);
        medicamentoExistente = obterMedicamento(nomeMedicamento).orElseThrow(() -> new IllegalStateException("Medicamento não encontrado para checagem da revisão."));
        
		assertTrue(medicamentoExistente.getRevisaoPendente().isPresent());
		assertEquals(StatusRevisao.PENDENTE, medicamentoExistente.getRevisaoPendente().get().getStatus());
	}

	@Given("o medicamento {string} não está vinculado a nenhuma prescrição ativa")
	public void o_medicamento_não_está_vinculado_a_nenhuma_prescrição_ativa(String nome) {
		prescricaoAtiva = false;
	}

	@Given("o medicamento {string} está vinculado a uma prescrição ativa")
	public void o_medicamento_está_vinculado_a_uma_prescrição_ativa(String nome) {
		prescricaoAtiva = true;
	}

	@Given("o perfil {string} tem permissão para atualizar medicamentos")
	public void o_perfil_tem_permissão_para_atualizar_medicamentos(String perfil) {
		assertTrue(temPermissao(perfil, "atualizar"));
	}
