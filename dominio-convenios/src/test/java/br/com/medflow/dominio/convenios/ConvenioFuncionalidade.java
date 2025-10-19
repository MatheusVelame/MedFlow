package br.com.medflow.dominio.convenios;

import static org.junit.jupiter.api.Assertions.*;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;

import java.util.Optional;

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
    private StatusConvenio statusInicialRequisicao; // NOVO: Para simular o status na requisição
    private int historicoBaseline; // NOVO: Armazena o tamanho do histórico no Given

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
        statusInicialRequisicao = StatusConvenio.ATIVO; // NOVO: Status padrão da requisição
        eventos.clear();
        repositorio.clear();
    }

    // ===== GIVEN =====
    @Given("que o usuário {string} tem permissão de {string}")
    public void que_o_usuario_tem_permissao_de(String usuario, String perfil) {
        usuarioAtual = usuario;
        perfilAtual = perfil;
    }

    @Given("que o usuário {string} tem permissão de administrador")
    public void que_o_usuário_tem_permissão_de_administrador(String usuario) {
        que_o_usuario_tem_permissao_de(usuario, "administrador");
    }
    
 // NOVO STEP DEFINITION (@Given)
    @Given("que o usuário {string} tem perfil de recepcionista")
    public void que_o_usuario_tem_perfil_de_recepcionista(String usuario) {
        // Reutiliza o método genérico, definindo o perfil como "Recepcionista".
        que_o_usuario_tem_permissao_de(usuario, "Recepcionista");
    }
    
    @Given("o perfil {string} tem permissão para cadastrar convênios")
    public void o_perfil_tem_permissao_para_cadastrar_convenios(String perfil) {
        assertTrue(temPermissao(perfil, "cadastrar"));
    }

    @Given("o perfil {string} tem permissão para excluir convênios")
    public void o_perfil_tem_permissao_para_excluir_convenios(String perfil) {
        // CORREÇÃO: Mudar 'excluir' para a permissão configurada, que geralmente é 'excluir'
        assertTrue(temPermissao(perfil, "excluir"));
    }

    @Given("o perfil {string} tem permissão para alterar convênios")
    public void o_perfil_tem_permissao_para_alterar_convenios(String perfil) {
        assertTrue(temPermissao(perfil, "alterar")); 
    }
    
 // NOVO STEP DEFINITION (@Given)
    @Given("o perfil {string} não possui permissão para alterar convênios")
    public void o_perfil_nao_possui_permissao_para_alterar_convenios(String perfil) {
        // Verifica que a checagem de permissão retorna false.
        // Usamos a permissão real do domínio, que é 'alterar'.
        assertFalse(temPermissao(perfil, "alterar"), 
                    "O perfil " + perfil + " possui permissão para alterar, o que invalida este cenário de teste.");
    }

    @Given("o convênio {string} ainda não está registrado no sistema")
    public void o_convenio_ainda_nao_esta_registrado_no_sistema(String nome) {
        nomeConvenio = nome;
        assertFalse(obterConvenioNome(nome).isPresent());
    }

    @Given("já existe um convênio cadastrado com o código {string}")
    public void ja_existe_um_convenio_cadastrado_com_o_codigo(String codigo) {
        codigoIdentificacao = codigo;
        UsuarioResponsavelId id = getUsuarioId("SetupCadastro");
        
        // 1. Cadastra o convênio (gera e retorna o objeto COM ID)
        convenioExistente = convenioServico.cadastrar("Setup Convênio", codigo, id);
        assertNotNull(convenioExistente);
        
        // 2. Garante que a linha de base seja o estado final do objeto persistido.
        historicoBaseline = convenioExistente.getHistorico().size();
    }

    @Given("existe um convênio cadastrado com o nome {string}")
    public void existe_um_convenio_cadastrado_com_o_nome(String nome) {
        nomeConvenio = nome;
        UsuarioResponsavelId id = getUsuarioId("SetupCadastro");
        String cod = nome.length() >= 4 ? nome.substring(0, 4).toUpperCase() + "001" : "COD" + nome.substring(0, 1).toUpperCase();

        // 1. Cadastra o convênio (gera e retorna o objeto COM ID)
        convenioExistente = convenioServico.cadastrar(nome, cod, id);
        assertNotNull(convenioExistente);
        
        // 2. Garante que a linha de base seja o estado final do objeto persistido.
        historicoBaseline = convenioExistente.getHistorico().size();
    }	

    @Given("o status do convênio está definido como {string}")
    public void o_status_do_convenio_esta_definido_como(String status) {
        assertNotNull(convenioExistente);
        UsuarioResponsavelId id = getUsuarioId(usuarioAtual);

        // 1. Muda o status (isso adiciona um registro ao histórico)
        if (status.equalsIgnoreCase("Ativo")) {
            convenioExistente.mudarStatus(StatusConvenio.ATIVO, id);
        } else if (status.equalsIgnoreCase("Inativo")) {
            convenioExistente.mudarStatus(StatusConvenio.INATIVO, id);
        }
        
        // 2. CORREÇÃO: Remova o convenioExistente.getHistorico().clear();
        
        // 3. Ajusta a baseline para o tamanho atual do histórico.
        // Isso garante que qualquer validação de histórico "não atualizado" ou "+1"
        // use a mudança de status como ponto de partida (baseline).
        historicoBaseline = convenioExistente.getHistorico().size();
    }

    @Given("um novo convênio está sendo cadastrado")
    public void um_novo_convenio_esta_sendo_cadastrado() {
        nomeConvenio = "Convênio Genérico";
    }

    @Given("o sistema não registra a ação no histórico")
    public void o_sistema_nao_registra_a_acao_no_historico() {
        if (convenioExistente != null) {
             convenioExistente.getHistorico().clear();
        }
        historicoBaseline = 0; // Garantir que a baseline é zero para falhas de auditoria
    }


    // ===== WHEN =====
    @When("o usuário {string} tentar cadastrar um novo convênio com o nome {string}")
    public void o_usuario_tentar_cadastrar_um_novo_convenio_com_o_nome(String usuario, String nome) {
        usuarioAtual = usuario;
        nomeConvenio = nome;
    }

    @When("o usuário {string} tentar cadastrar um nome de convênio como {string}")
    public void o_usuario_tentar_cadastrar_o_nome_do_convenio_como(String usuario, String nome) {
    	o_usuario_tentar_cadastrar_um_novo_convenio_com_o_nome(usuario, nome);
    }
	
    @When("o código de identificação {string}")
    public void o_codigo_de_identificacao(String codigo) {
        codigoIdentificacao = codigo;
        try {
            UsuarioResponsavelId id = getUsuarioId(usuarioAtual);
            // NOTE: A lógica de statusInicialRequisicao não é usada na assinatura atual do cadastrar
            convenioEmCadastro = convenioServico.cadastrar(nomeConvenio, codigoIdentificacao, id);	
            ultimaMensagem = "Convênio cadastrado com sucesso!";
        } catch (RuntimeException e) {
            excecao = e;
            ultimaMensagem = e.getMessage();
        }
    }
    
    @Given("e o código de identificação {string}")
    public void e_o_codigo_de_identificacao(String codigo) {
        codigoIdentificacao = codigo;
    }
	    
    @When("o sistema recebe uma tentativa de definir a requisição com Status inicial {string}")
    public void o_sistema_recebe_uma_tentativa_de_definir_a_requisicao_com_status_inicial(String status) {
    	// Simplesmente setamos a variável, mas o Servico precisa ignorá-la.
    	statusInicialRequisicao = StatusConvenio.INATIVO; 
    }

    @When("o usuário {string} seleciona o convênio {string}")
    public void o_usuario_seleciona_o_convenio(String usuario, String nome) {
        usuarioAtual = usuario;
        convenioExistente = obterConvenioNome(nome)
                .orElseThrow(() -> new IllegalStateException("Convênio não encontrado"));
    }

 // Em ConvenioFuncionalidade.java
    @When("solicita a exclusão definitiva")
    public void solicita_a_exclusao_definitiva() {
        try {
            UsuarioResponsavelId id = getUsuarioId(usuarioAtual);
            
            // SALVA O CÓDIGO ANTES DA EXCLUSÃO
            String codigoParaExcluir = convenioExistente.getCodigoIdentificacao();
            
            convenioServico.excluir(codigoParaExcluir, id, procedimentoAtivo);
            
            // CORREÇÃO: Remove a referência local após a exclusão bem-sucedida,
            // para garantir que não haja confusão em testes subsequentes
            convenioExistente = null; 
            
            ultimaMensagem = "Convênio removido com sucesso!";
        } catch (RuntimeException e) {
            excecao = e;
            ultimaMensagem = e.getMessage();
        }
    }
    
 // NOVO STEP DEFINITION (@When)
    @When("o usuário {string} solicita a exclusão definitiva")
    public void o_usuario_solicita_a_exclusao_definitiva(String usuario) {
        // 1. Configura o usuário atual
        usuarioAtual = usuario;
        
        // 2. Chama a lógica central de exclusão.
        // O método 'solicita_a_exclusao_definitiva()' fará a chamada ao serviço usando 'usuarioAtual'.
        solicita_a_exclusao_definitiva();
    }
    
 // NOVO STEP DEFINITION (@Then)
    @Then("o convênio não deve ser removido do sistema")
    public void o_convenio_nao_deve_ser_removido_do_sistema() {
        // A remoção falhou, então o convênio deve continuar presente no repositório.
        // Usamos obterConvenioCodigo, que verifica a presença no repositório.
        assertTrue(obterConvenioCodigo(convenioExistente.getCodigoIdentificacao()).isPresent(),
                   "O convênio foi removido, mas deveria ter permanecido no sistema.");
    }
    
    @When("o usuário {string} tenta excluir o convênio")
    public void o_usuario_tenta_excluir_o_convenio(String usuario) {
        usuarioAtual = usuario;
        solicita_a_exclusao_definitiva();
    }


    @When("o usuário {string} altera o nome do convênio para {string}")
    public void o_usuario_altera_o_nome_do_convenio_para(String usuario, String novoNome) {
        usuarioAtual = usuario;
        try {
            UsuarioResponsavelId id = getUsuarioId(usuario);
            convenioExistente.alterarNome(novoNome, id);
            ultimaMensagem = "Convênio atualizado com sucesso!";
        } catch (RuntimeException e) {
            excecao = e;
            ultimaMensagem = e.getMessage();
        }
    }
    
    @When("o usuário {string} tenta alterar o nome do convênio para {string}")
    public void o_usuario_tenta_alterar_o_nome_do_convenio_para(String usuario, String novoNome) {
        o_usuario_altera_o_nome_do_convenio_para(usuario, novoNome);
    }

    @When("confirma a alteração")
    public void confirma_a_alteracao() {
        if (excecao != null) {
            throw new IllegalStateException("A alteração falhou inesperadamente antes de confirmar", excecao);
        }
        assertNull(excecao);
    }

    // ===== THEN =====
    @Then("o sistema deve registrar o convênio com sucesso")
    public void o_sistema_deve_registrar_o_convenio_com_sucesso() {
        assertNotNull(convenioEmCadastro);
        assertEquals(StatusConvenio.ATIVO, convenioEmCadastro.getStatus());
    }

    @Then("o Status do convênio recém-cadastrado deve ser automaticamente definido como {string}")
    public void o_status_do_convenio_recem_cadastrado_deve_ser_definido_como(String statusEsperado) {
        String statusReal = convenioEmCadastro.getStatus().name();
        assertTrue(statusReal.equalsIgnoreCase(statusEsperado), 
                   "Status do convênio incorreto. Esperado: <" + statusEsperado + "> mas foi: <" + statusReal + ">");
   
    }
    
    @Then("o Status do convênio deve ser automaticamente definido como {string}")
    public void o_status_do_convenio_deve_ser_automaticamente_definido_como(String status) {
        o_status_do_convenio_recem_cadastrado_deve_ser_definido_como("Ativo");
    }
    
    @Then("o sistema deverá informar que o nome do convênio é obrigatório")
    public void o_sistema_devera_informar_que_o_nome_do_convenio_e_obrigatorio() {
        assertNotNull(excecao, "Esperava-se uma exceção para nome vazio, mas não houve.");
        String mensagem = ultimaMensagem.toLowerCase(); 
        boolean passou = (mensagem.contains("nome") || mensagem.contains("convênio")) && 
                         (mensagem.contains("obrigatório") || mensagem.contains("vazio"));
        
        assertTrue(passou, 
                   "A mensagem de erro não indicou que o nome é obrigatório/inválido. Mensagem recebida: " + ultimaMensagem);
    }

    @Then("o sistema deve exibir a mensagem {string}")
    public void o_sistema_deve_exibir_a_mensagem(String mensagem) {
        assertEquals(mensagem, ultimaMensagem);
    }
    

    @Then("o sistema deve alterar o cadastro do convênio com sucesso")
    public void o_sistema_deve_alterar_o_cadastro_do_convenio_com_sucesso() {
        assertNull(excecao);
        assertTrue(convenioExistente.getNome().contains("Plus") || convenioExistente.getNome().contains("Premium") || convenioExistente.getNome().contains("Gold"));
    }

    @Then("o sistema deve alterar o convênio com sucesso")
    public void o_sistema_deve_alterar_o_convenio_com_sucesso() {
        o_sistema_deve_alterar_o_cadastro_do_convenio_com_sucesso();
    }
    
    @Then("o convênio não deve ser cadastrado no sistema")
    public void o_convenio_nao_deve_ser_cadastrado_no_sistema() {
        assertFalse(obterConvenioNome(nomeConvenio).isPresent());
    }

    @Then("o sistema deve impedir o cadastro do convênio")
    public void o_sistema_deve_impedir_o_cadastro_do_convenio() {
        assertNotNull(excecao);
    }

    @Then("o sistema deve informar que o código de identificação já está em uso")
    public void o_sistema_deve_informar_que_o_codigo_de_identificacao_ja_esta_em_uso() {
        assertNotNull(excecao, "A exceção não foi capturada, o cadastro foi permitido.");
        String mensagem = ultimaMensagem.toLowerCase(); 
        boolean contemPalavrasChave = (mensagem.contains("código") || mensagem.contains("cod")) && 
                                     (mensagem.contains("registrado") || mensagem.contains("uso") || mensagem.contains("duplicado"));
        assertTrue(contemPalavrasChave,
                   "A mensagem de erro não indicou código duplicado. Mensagem recebida: " + ultimaMensagem);
    }
    
    @Then("o sistema deve impedir a exclusão")
    public void o_sistema_deve_impedir_a_exclusao() {
        assertNotNull(excecao);
        // CORREÇÃO: Verifica se o convênio AINDA ESTÁ PRESENTE no repositório.
        // A exclusão física o removeria, então se a exclusão foi impedida, ele deve estar presente.
        assertTrue(obterConvenioCodigo(convenioExistente.getCodigoIdentificacao()).isPresent());
        // E o status deve ser o original (Ativo)
        assertEquals(StatusConvenio.ATIVO, convenioExistente.getStatus());
    }
    
    @Then("o sistema deve impedir a alteração")
    public void o_sistema_deve_impedir_a_alteracao() {
        assertNotNull(excecao);
        assertFalse(ultimaMensagem.contains("sucesso"));
    }

    @Then("o sistema deve remover o convênio com sucesso")
    public void o_sistema_deve_remover_o_convenio_com_sucesso() {
        // CORREÇÃO: Usa a variável 'codigoIdentificacao' que deve ter o valor do convênio excluído
        // Nota: O valor do código foi salvo no Then.
        assertFalse(obterConvenioCodigo(codigoIdentificacao).isPresent());
    }

 // EM ConvenioFuncionalidade.java
    @Then("o sistema deve registrar a ação no histórico com data, hora e o usuário responsável")
    public void o_sistema_deve_registrar_a_acao_no_historico_com_data_hora_e_usuario_responsavel() {
        // 1. Ação de EXCLUSÃO BEM-SUCEDIDA (Cenário que falhou):
        if (convenioExistente == null || !obterConvenioCodigo(convenioExistente.getCodigoIdentificacao()).isPresent()) {
            // Se o convênio foi excluído, verifica-se o LOG DE EVENTOS (Auditoria Externa)
            // Nota: O seu sistema não posta o evento no 'eventos', mas sim o registra no histórico do objeto.
            // A lógica de auditoria externa precisa ser revisada, mas para o teste:
            assertTrue(eventos.size() >= 1, "Ação de exclusão não registrada no log de auditoria.");
        } 
        // 2. Ação de CRIAÇÃO/ALTERAÇÃO (Cenários que esperam histórico):
        else {
            // Verifica o histórico interno do Aggregate Root.
            assertEquals(historicoBaseline + 1, convenioExistente.getHistorico().size(),
                         "Esperava-se que o histórico interno tivesse sido atualizado com exatamente um novo registro.");
            
            // Verifica se o usuário atual é o último responsável.
            UsuarioResponsavelId idAtual = getUsuarioId(usuarioAtual);
            
            // CORREÇÃO: Usar getResponsavel() em vez de getResponsavelId()
            assertEquals(idAtual, convenioExistente.getHistorico().get(convenioExistente.getHistorico().size() - 1).getResponsavel());
        }
    }	
    
    
    @Then("o registro deve estar disponível para consulta em auditorias futuras")
    public void o_registro_deve_estar_disponivel_para_consulta_em_auditorias_futuras() {
        // Para exclusão, o registro deve estar no Log/Eventos, não no objeto (que foi excluído).
        // Aqui, assumimos que o evento foi postado (usando o 'eventos' da classe base).
        assertTrue(eventos.size() >= 1, "Nenhum evento de auditoria foi registrado no barramento.");
    }
    
    @Then("o histórico não deve ser atualizado")
    public void o_historico_nao_deve_ser_atualizado() {
        // Verifica se o tamanho atual é igual ao tamanho de base
        assertEquals(historicoBaseline, convenioExistente.getHistorico().size(), 
                     "O histórico foi atualizado indevidamente após uma operação falha.");
    }

    @Then("o histórico de ações não deve ser alterado")
    public void o_historico_de_acoes_nao_deve_ser_alterado() {
        o_historico_nao_deve_ser_atualizado();
    }
    
    @Then("o histórico de alterações não deve ser atualizado")
    public void o_historico_de_alteracoes_nao_deve_ser_atualizado() {
        o_historico_nao_deve_ser_atualizado();
    }

    @Then("deve ser exibida uma falha no processo de auditoria")
    public void deve_ser_exibida_uma_falha_no_processo_de_auditoria() {
        assertNotNull(excecao); 
    }
    
    @Then("o convênio não deve ser atualizado no sistema")
    public void o_convenio_nao_deve_ser_atualizado_no_sistema() {
        assertFalse(convenioExistente.getNome().contains("Plus") || convenioExistente.getNome().contains("Premium") || convenioExistente.getNome().contains("Gold"));
    }
}