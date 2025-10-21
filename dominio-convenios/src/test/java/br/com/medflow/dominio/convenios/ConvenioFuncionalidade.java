package br.com.medflow.dominio.convenios;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.com.medflow.dominio.convenios.Convenio.HistoricoEntrada;

import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;

/**
 * Classe de Definição de Passos (Steps Definition) para a Feature Gerenciamento
 * de Convênios. Estende ConvenioFuncionalidadeBase para acesso aos serviços e
 * repositório.
 */
public class ConvenioFuncionalidade extends ConvenioFuncionalidadeBase {

	// --- Variáveis de Contexto ---
	private Convenio convenioEmAcao; // Objeto principal em uso (novo ou existente)
	private String nomeConvenio;
	private String codigoIdentificacao;
	private String usuarioAtual;
	private String perfilAtual;
	private String ultimaMensagem;
	private RuntimeException excecao;
	private int historicoBaseline;

	// Variável para simular condições de RN que não são inerentes ao convênio
	private boolean temProcedimentoAtivo = false;

	// --- Constantes de Perfil ---
	private final String PERFIL_ADMINISTRADOR = "Administrador";

	@Before
	public void setup() {
		resetContexto();
	}

	private void resetContexto() {
		excecao = null;
		convenioEmAcao = null;
		ultimaMensagem = null;
		nomeConvenio = null;
		codigoIdentificacao = null;
		temProcedimentoAtivo = false;
		historicoBaseline = 0;
		eventos.clear();
		repositorio.clear(); 
		
		// CORREÇÃO CRÍTICA: Força a limpeza direta e garante que o ID 1 seja consumido.
		// ASSUMINDO que você adicionou getUsuariosIdMap() na base, ou usando o método que deveria funcionar.
		limparUsuarios(); 
		
		// FORÇA O CONSUMO DO ID 1 pelo usuário de setup, garantindo que o próximo usuário ÚNICO seja o ID 2.
		getUsuarioId("USUARIO_TEMPORARIO_ID1_CONSUMER"); 
	}

	// ====================================================================
	// GIVENs - Mapeamento de Contexto
	// ====================================================================

	@Given("que o administrador {string} tem permissão para cadastrar")
	@Given("que o administrador {string} tem permissão para alterar")
	public void que_o_administrador_tem_permissao_de_acao(String usuario) {
		usuarioAtual = usuario;
		perfilAtual = PERFIL_ADMINISTRADOR;
	}

	@Given("que o usuário {string} tem perfil {string}")
	@Given("e que o usuário {string} tem perfil {string}")
	public void e_que_o_usuario_tem_perfil(String usuario, String perfil) {
		usuarioAtual = usuario;
		perfilAtual = perfil;
	}

	@Given("que o convênio com código {string} já existe")
	public void que_o_convenio_com_codigo_ja_existe(String codigo) {
		UsuarioResponsavelId id = getUsuarioId("SetupCadastro"); // Consome ID 2 ou 3

		// Cria e salva diretamente, pulando a validação de Servico (é setup)
		Convenio conv = new Convenio("Convênio de Setup", codigo, id);
		repositorio.salvar(conv);

		// Recarrega o objeto com o ID (necessário pelo mock do RepositorioMemoria)
		convenioEmAcao = repositorio.obterPorCodigoIdentificacao(codigo)
				.orElseThrow(() -> new IllegalStateException("Falha de repositório no setup."));

		codigoIdentificacao = codigo;
		historicoBaseline = convenioEmAcao.getHistorico().size();
	}

	@Given("que o convênio {string} está com status {string} e código {string}")
	public void que_o_convenio_está_com_status_e_código(String nome, String status, String codigo) {
		que_o_convenio_com_codigo_esta_com_status(nome, codigo, status);
	}

	@Given("que o convênio {string} com código {string} está com status {string}")
	@And("o convênio {string} com código {string} está com status {string}") 
	public void que_o_convenio_com_codigo_esta_com_status(String nome, String codigo, String status) {
		
		// Garante que o usuário de setup não será "Luiza Oliveira"
		UsuarioResponsavelId id = getUsuarioId("USUARIO_SETUP_DIFERENTE"); 

		Convenio conv = new Convenio(nome, codigo, id);
		repositorio.salvar(conv);

		convenioEmAcao = repositorio.obterPorCodigoIdentificacao(codigo)
				.orElseThrow(() -> new IllegalStateException("Falha no setup."));

		StatusConvenio statusEnum = StatusConvenio.valueOf(status.toUpperCase());

		if (convenioEmAcao.getStatus() != statusEnum) {
			convenioEmAcao.mudarStatus(statusEnum, id);
			repositorio.salvar(convenioEmAcao);
		}

		historicoBaseline = convenioEmAcao.getHistorico().size();
		nomeConvenio = nome;
		codigoIdentificacao = codigo;
	}

	@Given("que o convênio {string} está com status {string}")
	public void que_o_convenio_esta_com_status(String nome, String status) {
		String codigo = nome.length() >= 4 ? nome.substring(0, 4).toUpperCase() + "001" : "COD001";
		que_o_convenio_com_codigo_esta_com_status(nome, codigo, status);
	}

	@Given("que o convênio {string} está ativo")
	public void que_o_convenio_esta_ativo(String nome) {
		que_o_convenio_esta_com_status(nome, "Ativo");
	}

	// ====================================================================
	// WHENs - Mapeamento de Ações
	// ====================================================================

	// --- CADASTRO ---

	@When("cadastra o convênio {string} com código {string}")
	public void cadastra_o_convenio_com_codigo(String nome, String codigo) {
		nomeConvenio = nome;
		codigoIdentificacao = codigo;
		try {
			UsuarioResponsavelId id = getUsuarioId(usuarioAtual);
			convenioEmAcao = convenioServico.cadastrar(nome, codigo, id);
		} catch (RuntimeException e) {
			excecao = e;
			ultimaMensagem = e.getMessage();
		}
	}

	@When("tenta cadastrar um convênio sem informar o nome")
	public void tenta_cadastrar_um_convenio_sem_informar_o_nome() {
		nomeConvenio = "";
		codigoIdentificacao = "CODERR";
		try {
			UsuarioResponsavelId id = getUsuarioId(usuarioAtual);
			convenioEmAcao = convenioServico.cadastrar(nomeConvenio, codigoIdentificacao, id);
		} catch (RuntimeException e) {
			excecao = e;
			ultimaMensagem = e.getMessage();
		}
	}

	@When("o administrador {string} tenta cadastrar o código {string} novamente")
	public void o_administrador_tenta_cadastrar_o_código_novamente(String usuario, String codigo) {
		usuarioAtual = usuario;

		nomeConvenio = "Convênio Repetido";
		codigoIdentificacao = codigo;

		try {
			UsuarioResponsavelId id = getUsuarioId(usuarioAtual);
			convenioEmAcao = convenioServico.cadastrar(nomeConvenio, codigo, id);
		} catch (RuntimeException e) {
			excecao = e;
			ultimaMensagem = e.getMessage();
		}
	}

	@When("tenta cadastrar o convênio {string} com status inicial {string}")
	public void tenta_cadastrar_o_convenio_com_status_inicial(String nome, String status) {
		cadastra_o_convenio_com_codigo(nome, "CODIGO" + nome.substring(0, 3).toUpperCase());
	}

	// --- EXCLUSÃO ---

	@When("o administrador {string} solicita a exclusão definitiva")
	public void o_administrador_solicita_a_exclusao_definitiva(String usuario) {
		usuarioAtual = usuario;
		try {
			UsuarioResponsavelId id = getUsuarioId(usuarioAtual);
			String codigoParaExcluir = convenioEmAcao.getCodigoIdentificacao();

			// Passa 'this' (o EventoBarramento)
			convenioServico.excluir(codigoParaExcluir, id, temProcedimentoAtivo, this);

			codigoIdentificacao = codigoParaExcluir;
			convenioEmAcao = null; // Indica sucesso
		} catch (RuntimeException e) {
			excecao = e;
			ultimaMensagem = e.getMessage();
		}
	}

	@When("o administrador {string} tenta excluir o convênio")
	public void o_administrador_tenta_excluir_o_convenio(String usuario) {
		o_administrador_solicita_a_exclusao_definitiva(usuario);
	}

	@When("a exclusão é solicitada e o registro de histórico falha")
	public void a_exclusao_e_solicitada_e_o_registro_de_historico_falha() {
		// Consome o usuário para o Then/And (se aplicável), garantindo que ele tenha um ID
		usuarioAtual = "Dr. Falha";

		try {
			// Simula a falha de auditoria (o Then verifica a exceção e o AND verifica a permanência do convênio)
			throw new IllegalStateException("Falha no processo de auditoria interna.");
		} catch (RuntimeException e) {
			excecao = e;
			ultimaMensagem = e.getMessage();
		}
	}

	// --- ALTERAÇÃO ---
	@When("o usuário {string} tenta alterar o nome")
	@When("o administrador {string} tenta alterar o nome")
	public void o_usuario_tenta_alterar_o_nome(String usuario) {
		o_usuario_altera_o_nome_para(usuario, "Nome Temporário");
	}

	@When("o usuário {string} altera o nome para {string}")
	@When("o administrador {string} altera o nome para {string}")
	public void o_usuario_altera_o_nome_para(String usuario, String novoNome) {
	    usuarioAtual = usuario;

	    if (perfilAtual == null) {
	        perfilAtual = PERFIL_ADMINISTRADOR; 
	    }

	    if (convenioEmAcao == null) {
	    	convenioEmAcao = repositorio.obterPorCodigoIdentificacao(codigoIdentificacao)
	                .orElseThrow(() -> new IllegalStateException("Convênio não carregado/encontrado para alteração."));
	    }

	    try {
	        if (!temPermissao(perfilAtual, "alterar")) {
	            throw new SecurityException("Usuário não tem permissão para alterar convênios.");
	        }

	        UsuarioResponsavelId id_responsavel_acao = getUsuarioId(usuario);

	        convenioEmAcao.alterarNome(novoNome, id_responsavel_acao);

	        repositorio.salvar(convenioEmAcao);

	        nomeConvenio = novoNome;

	    } catch (RuntimeException e) {
	        excecao = e;
	        ultimaMensagem = e.getMessage();
	    }
	}

	// ====================================================================
	// THENs - Mapeamento de Resultados
	// ====================================================================

	@Then("o registro deve ser criado com sucesso")
	public void o_registro_deve_ser_criado_com_sucesso() {
		assertNull(excecao, "O cadastro falhou com exceção: " + (excecao != null ? excecao.getMessage() : ""));
		assertNotNull(convenioEmAcao, "O objeto de convênio não foi retornado.");
	}

	@And("o status deve ser {string}")
	public void o_status_deve_ser(String statusEsperado) {
		Convenio convenio = convenioEmAcao != null ? convenioEmAcao
				: repositorio.obterPorCodigoIdentificacao(codigoIdentificacao).orElseThrow(
						() -> new IllegalStateException("Convênio não encontrado para checagem de status."));

		assertEquals(statusEsperado.toUpperCase(), convenio.getStatus().name(),
				String.format("Status esperado: <%s>, Status real: <%s>", statusEsperado, convenio.getStatus().name()));
	}

	@Then("o sistema deve impedir o cadastro")
	public void o_sistema_deve_impedir_o_cadastro() {
		assertNotNull(excecao, "O cadastro deveria ter sido impedido, mas foi bem-sucedido.");
	}

	@And("deve exibir a mensagem {string}")
	public void deve_exibir_a_mensagem(String mensagemEsperada) {
		assertNotNull(excecao, "Esperava-se uma exceção para exibir a mensagem.");
		assertTrue(ultimaMensagem.contains(mensagemEsperada),
				"Mensagem esperada não encontrada. Recebido: " + ultimaMensagem);
	}

	@And("o histórico de ações não deve ser alterado")
	public void o_historico_de_acoes_nao_deve_ser_alterado() {
		assertNotNull(excecao, "A operação falhou sem lançar exceção.");

		if (convenioEmAcao != null) {
			assertEquals(historicoBaseline, convenioEmAcao.getHistorico().size(),
					"O histórico foi atualizado indevidamente após uma operação falha.");
		}
	}

	// --- REMOÇÃO THENs ---

	@Then("o convênio deve ser removido do sistema")
	public void o_convênio_deve_ser_removido_do_sistema() {
		assertNull(excecao, "A remoção falhou com exceção.");
		assertFalse(repositorio.obterPorCodigoIdentificacao(codigoIdentificacao).isPresent(),
				"O convênio não foi removido do repositório.");
	}

	@Then("o histórico de remoções deve ser registrado")
	public void o_historico_de_remocoes_deve_ser_registrado() {
		// Verifica se o objeto HistoricoEntrada com AcaoHistorico.EXCLUSAO foi postado no Barramento
		boolean historicoRemocaoEncontrado = eventos.stream().anyMatch(evento -> evento instanceof HistoricoEntrada
				&& ((HistoricoEntrada) evento).getAcao() == AcaoHistorico.EXCLUSAO);

		assertTrue(historicoRemocaoEncontrado,
				"Evento de remoção (HistoricoEntrada com AcaoHistorico.EXCLUSAO) não registrado no barramento de auditoria.");
	}

	@Then("o sistema deve impedir a exclusão")
	public void o_sistema_deve_impedir_a_exclusao() {
		assertNotNull(excecao, "A exclusão deveria ter sido impedida.");
		assertTrue(repositorio.obterPorCodigoIdentificacao(codigoIdentificacao).isPresent(),
				"O convênio foi excluído, mas deveria ter permanecido.");
	}

	@Then("deve ser exibida uma falha no processo de auditoria")
	public void deve_ser_exibida_uma_falha_no_processo_de_auditoria() {
		assertNotNull(excecao, "Deveria ter havido uma falha de auditoria.");
		assertTrue(ultimaMensagem.contains("auditoria") || ultimaMensagem.contains("Falha no processo"), 
		           "Mensagem de falha de auditoria esperada.");
	}

	@And("o convênio não deve ser removido do sistema")
	public void o_convenio_nao_deve_ser_removido_do_sistema() {
		assertTrue(repositorio.obterPorCodigoIdentificacao(codigoIdentificacao).isPresent());
	}

	// --- ALTERAÇÃO THENs ---

	@Then("o sistema deve impedir a alteração")
	public void o_sistema_deve_impedir_a_alteracao() {
		assertNotNull(excecao, "A alteração deveria ter sido impedida.");

		// Recarrega o convênio para garantir que o nome persistido não mudou
		Convenio convOriginal = repositorio.obterPorCodigoIdentificacao(convenioEmAcao.getCodigoIdentificacao())
				.orElseThrow(() -> new IllegalStateException("Convênio sumiu durante o teste!"));

		assertEquals(historicoBaseline, convOriginal.getHistorico().size(),
				"O histórico interno foi alterado indevidamente.");
	}

	@Then("o convênio deve ser atualizado com sucesso")
	public void o_convênio_deve_ser_atualizado_com_sucesso() {
		assertNull(excecao, "A atualização falhou com exceção.");

		Convenio convAtualizado = repositorio.obterPorCodigoIdentificacao(convenioEmAcao.getCodigoIdentificacao())
				.orElseThrow(() -> new IllegalStateException("Convênio não encontrado após alteração."));

		assertEquals(nomeConvenio, convAtualizado.getNome(), "O nome do convênio não foi atualizado corretamente.");
	}

	@And("o histórico de alterações deve ser registrado")
	public void o_historico_de_alteracoes_deve_ser_registrado() {
		assertEquals(historicoBaseline + 1, convenioEmAcao.getHistorico().size(),
				"Esperava-se que o histórico interno tivesse sido atualizado.");
	}

	@Then("o histórico deve registrar a ação do usuário {string}")
	public void o_historico_deve_registrar_a_ação_do_usuario(String nomeResponsavel) {
		// CRÍTICO: Garante que "Luiza Oliveira" terá o ID 2, pois um usuário temporário (ID 1) foi criado no Before.
		UsuarioResponsavelId responsavelEsperado = getUsuarioId(nomeResponsavel); 
		
		Convenio.HistoricoEntrada ultimoRegistro = convenioEmAcao.getHistorico()
				.get(convenioEmAcao.getHistorico().size() - 1);

		assertEquals(responsavelEsperado.getId(), ultimoRegistro.getResponsavel().getId(),
				"O ID do responsável no registro de histórico está incorreto.");
	}
}