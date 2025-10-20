package br.com.medflow.dominio.administracao.funcionarios;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class FuncionarioFuncionalidade extends FuncionarioFuncionalidadeBase {

    private Funcionario funcionarioEmAcao;
    private String nomeFuncionario;
    private String funcaoFuncionario;
    private String contatoFuncionario;
    private String ultimaMensagem;
    private RuntimeException excecao;
    private boolean temViculosAtivosFuncao = false;
    private boolean temAtividadesFuturas = false;
    private int historicoBaseline; 

    @Before
    public void setup() {
        resetContexto();
    }

    private void resetContexto() {
        excecao = null;
        funcionarioEmAcao = null;
        ultimaMensagem = null;
        nomeFuncionario = null;
        funcaoFuncionario = null;
        contatoFuncionario = null;
        temViculosAtivosFuncao = false;
        temAtividadesFuturas = false;
        historicoBaseline = 0;
        eventos.clear();
        repositorio.clear();
    }
    
    // GIVENs - Contexto e Pré-condições

    @Given("que o administrador tem permissão de administrador")
    public void que_o_administrador_tem_permissao_de_administrador() {
    }

    @Given("o perfil {string} tem permissão para cadastrar funcionários")
    public void o_perfil_tem_permissao_para_cadastrar_funcionarios(String perfil) {
        assertTrue(temPermissao(perfil, "cadastrar"), "O perfil deve ter permissão para cadastrar.");
    }
    
    @Given("o perfil {string} tem permissão para alterar funcionários")
    public void o_perfil_tem_permissao_para_alterar_funcionarios(String perfil) {
        assertTrue(temPermissao(perfil, "alterar"), "O perfil deve ter permissão para alterar.");
    }

    @Given("o administrador preenche o campo {string} com {string}")
    public void o_administrador_preenche_o_campo_com(String campo, String valor) {
        switch (campo) {
            case "Nome": this.nomeFuncionario = valor; break;
            case "Função": this.funcaoFuncionario = valor; break;
            case "Contato": this.contatoFuncionario = valor; break;
        }
    }
    
    @Given("o administrador deixa o campo {string} em branco") 
    public void o_administrador_deixa_o_campo_em_branco(String campo) {
        switch (campo) {
            case "Nome": this.nomeFuncionario = ""; break;
            case "Função": this.funcaoFuncionario = ""; break;
            case "Contato": this.contatoFuncionario = ""; break;
        }
    }
    
    @Given("deixa o campo {string} em branco")
    public void deixa_o_campo_em_branco(String campo) {
        o_administrador_deixa_o_campo_em_branco(campo);
    }
    
    @Given("já existe um funcionário com nome {string} e contato {string}")
    public void ja_existe_um_funcionario_com_nome_e_contato(String nome, String contato) {
        // Prepara o repositório com um funcionário existente para a RN 6
        UsuarioResponsavelId responsavel = getUsuarioId("SetupCadastro");
        
        try {
            Funcionario existente = new Funcionario(nome, "Funcao Setup", contato, responsavel);
            repositorio.salvar(existente);
        } catch (RuntimeException e) {
        }
    }

    @Given("o administrador acessa o cadastro do funcionário {string}")
    public void o_administrador_acessa_o_cadastro_do_funcionario(String nome) {
        
        Optional<Funcionario> funcOpt = obterFuncionarioPorNome(nome);
        
        if (funcOpt.isEmpty()) {
            UsuarioResponsavelId responsavel = getUsuarioId("Setup Implícito");
            Funcionario novoFunc = new Funcionario(nome, "Funcao Padrão", nome.toLowerCase().replaceAll("\\s", "") + "@email.com", responsavel);
            repositorio.salvar(novoFunc);
            this.funcionarioEmAcao = novoFunc;
            
        } else {
            this.funcionarioEmAcao = funcOpt.get();
        }
        
        this.nomeFuncionario = funcionarioEmAcao.getNome();
        this.funcaoFuncionario = funcionarioEmAcao.getFuncao();
        this.contatoFuncionario = funcionarioEmAcao.getContato();
        this.historicoBaseline = funcionarioEmAcao.getHistorico().size();
    }    
    @Given("o funcionário {string} possui status {string}")
    public void o_funcionario_possui_status(String nome, String status) {
        UsuarioResponsavelId responsavel = getUsuarioId("Setup Status");
        StatusFuncionario statusEnum = StatusFuncionario.valueOf(status.toUpperCase());
        
        Optional<Funcionario> funcOpt = obterFuncionarioPorNome(nome);
        Funcionario func;
        
        if (funcOpt.isEmpty()) {
            func = new Funcionario(nome, "Funcao Base", nome.toLowerCase().replaceAll("\\s", "") + "@email.com", responsavel);
            repositorio.salvar(func);
        } else {
            func = funcOpt.get();
        }
        
        try {
             if (func.getStatus() != statusEnum) {
                 func.mudarStatus(statusEnum, responsavel, false);
                 repositorio.salvar(func);
             }
        } catch (RuntimeException e) {
        }
        
        this.funcionarioEmAcao = func;
        this.historicoBaseline = func.getHistorico().size();
    }

    @Given("o funcionário {string} possui registros de atendimentos anteriores")
    @Given("o funcionário {string} possui registros de escalas e atendimentos anteriores")
    public void o_funcionario_possui_registros_de_atendimentos_anteriores(String nome) {
        o_funcionario_possui_status(nome, "Ativo");

        if (funcionarioEmAcao != null && funcionarioEmAcao.getHistorico().size() <= 1) {
            UsuarioResponsavelId responsavel = getUsuarioId("SistemaSetup");
            funcionarioEmAcao.adicionarEntradaHistorico(AcaoHistorico.ATUALIZACAO, "Registro de setup de histórico (atendimento)", responsavel);
            repositorio.salvar(funcionarioEmAcao);
        }
        
        this.historicoBaseline = funcionarioEmAcao.getHistorico().size();
    }

    @Given("o funcionário {string} está ativo na função {string}")
    public void o_funcionario_esta_ativo_na_funcao(String nome, String funcao) {
        o_funcionario_possui_status(nome, "Ativo");
        
        // Simulação da RN 4 de atualização:
        if (funcao.equals("Recepcionista")) {
             this.temViculosAtivosFuncao = true; // Simula a função com muitos vínculos ativos
        } else {
             this.temViculosAtivosFuncao = false;
        }
    }
    
    @Given("o funcionário {string} possui plantões futuros agendados")
    public void o_funcionario_possui_plantoes_futuros_agendados(String nome) {
        o_funcionario_possui_status(nome, "Ativo");
        this.temAtividadesFuturas = true;
    }

    @Given("o administrador remove ou realoca os vínculos dessas atividades")
    public void o_administrador_remove_ou_realoca_os_vinculos_dessas_atividades() {
        this.temAtividadesFuturas = false;
    }

    // WHENs - Ação do Usuário

    @When("o administrador submete o formulário")
    public void o_administrador_submete_o_formulario() {
        UsuarioResponsavelId responsavel = getUsuarioId("Administrador");
        try {
            funcionarioServico.cadastrar(this.nomeFuncionario, this.funcaoFuncionario, this.contatoFuncionario, responsavel);
            this.ultimaMensagem = "Funcionário cadastrado com sucesso!";
        } catch (RuntimeException e) {
            this.excecao = e;
            this.ultimaMensagem = e.getMessage();
        }
    }

    @When("o administrador tenta submeter o formulário")
    public void o_administrador_tenta_submeter_o_formulario() {
        o_administrador_submete_o_formulario();
    }
    
    @When("preenche o campo {string} com {string}")
    public void preenche_o_campo_com(String campo, String valor) {
        // Reutiliza o método completo para manter a lógica centralizada
        o_administrador_preenche_o_campo_com(campo, valor);
    }
    
    
    @When("o administrador altera o campo {string} para {string}")
    public void o_administrador_altera_o_campo_para(String campo, String valor) {
        switch (campo) {
            case "Nome": this.nomeFuncionario = valor; break;
            case "Função": this.funcaoFuncionario = valor; break;
            case "Contato": this.contatoFuncionario = valor; break;
        }
    }
    
    @When("o administrador altera o campo {string} para vazio")
    public void o_administrador_altera_o_campo_para_vazio(String campo) {
        switch (campo) {
            case "Função": 
                this.funcaoFuncionario = ""; 
                break;
            case "Nome":
                this.nomeFuncionario = "";
                break;
            case "Contato":
                this.contatoFuncionario = "";
                break;
        }
    }
    
    @When("altera o campo {string} para {string}")
    public void altera_o_campo_para(String campo, String valor) {
        // Delega a lógica para o método longo que já contém o switch case
        o_administrador_altera_o_campo_para(campo, valor);
    }
    
    @When("o administrador confirma a atualização")
    public void o_administrador_confirma_a_atualizacao() {
        UsuarioResponsavelId responsavel = getUsuarioId("Administrador");
        try {
            if (this.excecao != null) return; 

            funcionarioServico.atualizarDadosCadastrais(
                    this.funcionarioEmAcao.getId(), 
                    this.nomeFuncionario, 
                    this.funcaoFuncionario, 
                    this.contatoFuncionario, 
                    responsavel,
                    this.temViculosAtivosFuncao 
            );
            this.ultimaMensagem = "Dados do funcionário atualizados com sucesso!";
        } catch (RuntimeException e) {
            this.excecao = e;
            this.ultimaMensagem = e.getMessage();
        }
    }

    @When("o administrador tenta alterar o campo {string} de {string} para {string}")
    public void o_administrador_tenta_alterar_o_campo_de_para(String campo, String valorAtual, String valorNovo) {
        // Simula a tentativa de manipular um campo proibido (Status)
        try {
            this.funcionarioEmAcao.validarAlteracaoCampoStatus(); 
        } catch (RuntimeException e) {
            this.excecao = e;
            this.ultimaMensagem = e.getMessage();
        }
    }
    
    @When("o sistema tenta atualizar também os registros históricos com a nova função")
    public void o_sistema_tenta_atualizar_tambem_os_registros_historicos_com_a_nova_funcao() {
        this.temViculosAtivosFuncao = true; 
    }
    
    @When("o administrador altera o status do funcionário para {string}")
    public void o_administrador_altera_o_status_do_funcionario_para(String novoStatus) {
        StatusFuncionario statusEnum = StatusFuncionario.valueOf(novoStatus.toUpperCase());
        UsuarioResponsavelId responsavel = getUsuarioId("Administrador");
        
        try {
            funcionarioServico.mudarStatus(
                    this.funcionarioEmAcao.getId(), 
                    statusEnum, 
                    responsavel, 
                    this.temAtividadesFuturas); 
            
            this.ultimaMensagem = statusEnum == StatusFuncionario.INATIVO ? 
                                  "Funcionário inativado com sucesso." : 
                                  "Status do funcionário alterado com sucesso!";

        } catch (RuntimeException e) {
            this.excecao = e;
            this.ultimaMensagem = e.getMessage();
        }
    }
    
    @When("o campo {string} com {string}")
    public void o_campo_com(String campo, String valor) {
        // Reutiliza o método principal de preenchimento
        o_administrador_preenche_o_campo_com(campo, valor);
    }
    
    @When("o administrador confirma a atribuição")
    public void o_administrador_confirma_a_atribuicao() {
        try {
            funcionarioServico.validarAtribuicaoParaNovaAtividade(this.funcionarioEmAcao.getId());
            this.ultimaMensagem = "Funcionário atribuído à escala com sucesso!";
        } catch (RuntimeException e) {
            this.excecao = e;
            this.ultimaMensagem = e.getMessage();
        }
    }
    
    

    @When("o sistema remove ou altera registros históricos indevidamente")
    public void o_sistema_remove_ou_altera_registros_historicos_indevidamente() {
        this.excecao = new IllegalStateException("Erro: O histórico do funcionário deve ser preservado.");
        this.ultimaMensagem = this.excecao.getMessage();
    }

    // THENs - Verificação de Resultados
    
    @Then("o sistema deve cadastrar o funcionário com sucesso")
    public void o_sistema_deve_cadastrar_o_funcionario_com_sucesso() {
        assertNull(excecao, "O cadastro falhou com exceção: " + (excecao != null ? excecao.getMessage() : ""));
        
        Optional<Funcionario> cadastrado = obterFuncionarioPorNomeEContato(nomeFuncionario, contatoFuncionario);
        assertTrue(cadastrado.isPresent(), "O funcionário não foi encontrado no repositório após o cadastro.");
    }

    @Then("o sistema deve impedir o cadastro")
    public void o_sistema_deve_impedir_o_cadastro() {
        assertNotNull(excecao, "O cadastro deveria ter falhado, mas foi bem-sucedido.");
    }
    
    @Then("o sistema deve salvar as alterações com sucesso")
    public void o_sistema_deve_salvar_as_alteracoes_com_sucesso() {
        assertNull(excecao, "A atualização falhou com exceção.");
    }
    
    @Then("o sistema deve impedir a alteração")
    public void o_sistema_deve_impedir_a_alteracao() {
        assertNotNull(excecao, "A alteração deveria ter sido impedida.");
    }

    @Then("o sistema deve impedir a atualização")
    public void o_sistema_deve_impedir_a_atualizacao() {
        assertNotNull(excecao, "A atualização deveria ter sido impedida.");
    }
    
    @Then("o sistema deve atualizar o status do funcionário com sucesso")
    public void o_sistema_deve_atualizar_o_status_do_funcionario_com_sucesso() {
        assertNull(excecao, "A mudança de status falhou.");
    }

    @Then("o sistema deve permitir a inativação")
    public void o_sistema_deve_permitir_a_inativacao() {
        assertNull(excecao, "A inativação deveria ter sido permitida, mas falhou.");
    }

    @Then("o sistema deve impedir a inclusão")
    public void o_sistema_deve_impedir_a_inclusao() {
        assertNotNull(excecao, "A inclusão/atribuição deveria ter sido impedida.");
    }

//    @Then("deve exibir a mensagem {string}")
//    public void deve_exibir_a_mensagem(String mensagemEsperada) {
//        assertEquals(mensagemEsperada, ultimaMensagem);
//    }
    

    @Then("o campo {string} deve ser automaticamente definido como {string}")
    public void o_campo_deve_ser_automaticamente_definido_como(String campo, String statusEsperado) {
        Optional<Funcionario> optFunc = obterFuncionarioPorNomeEContato(nomeFuncionario, contatoFuncionario);
        
        assertTrue(optFunc.isPresent(), "Funcionário não encontrado no repositório.");
        
        String statusReal = optFunc.get().getStatus().name(); // Retorna "ATIVO"
        
        assertTrue(statusReal.equalsIgnoreCase(statusEsperado), 
                   String.format("Status esperado: <%s> (case-insensitive), Status real: <%s>", 
                                 statusEsperado, statusReal));
        
    }

    @Then("o sistema deve salvar a alteração nos dados cadastrais")
    public void o_sistema_deve_salvar_a_alteracao_nos_dados_cadastrais() {
        assertNull(excecao, "A alteração deveria ter sido salva.");
        Funcionario atualizado = repositorio.obter(funcionarioEmAcao.getId());
        
        assertEquals(this.nomeFuncionario, atualizado.getNome());
        assertEquals(this.funcaoFuncionario, atualizado.getFuncao());
    }
    
    
    @Then("o sistema deve manter todos os registros históricos inalterados")
    public void o_sistema_deve_manter_todos_os_registros_historicos_inalterados() {
        Funcionario atualizado = repositorio.obter(funcionarioEmAcao.getId());
        
        if (ultimaMensagem != null && ultimaMensagem.contains("sucesso")) {
            assertTrue(atualizado.getHistorico().size() > historicoBaseline, "O histórico não foi atualizado após uma ação bem-sucedida.");
        }
        
        // A principal verificação é que NÃO HOUVE exceção de falha de histórico (RN 3)
        assertNotEquals("Erro: O histórico do funcionário deve ser preservado.", ultimaMensagem);
    }

    @Then("o sistema deve atualizar o status do funcionário para {string}")
    public void o_sistema_deve_atualizar_o_status_do_funcionario_para(String statusEsperado) {
        Funcionario atualizado = repositorio.obter(funcionarioEmAcao.getId());
        assertEquals(statusEsperado, atualizado.getStatus().name());
    }
}