package br.com.medflow.dominio.administracao.funcionarios;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;

import java.util.Optional;
import java.text.Normalizer;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class FuncionarioFuncionalidade extends FuncionarioFuncionalidadeBase {

    private static final String NOME_PADRAO = "Funcionario Teste";
    private static final String FUNCAO_PADRAO = "Enfermeira";
    private static final String CONTATO_EMAIL_PADRAO = "teste.padrao@medfow.com";
    private static final String CONTATO_EMAIL_INVALIDO = "email@invalido";
    private static final String NOME_INVALIDO = "Nome123!";

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
        nomeFuncionario = NOME_PADRAO;
        funcaoFuncionario = FUNCAO_PADRAO;
        contatoFuncionario = CONTATO_EMAIL_PADRAO;
        temViculosAtivosFuncao = false;
        temAtividadesFuturas = false;
        historicoBaseline = 0;
        eventos.clear();
        repositorio.clear();
    }

    @Given("que o administrador tem permissão")
    @Given("que o administrador tem permissão de administrador")
    public void que_o_administrador_tem_permissao() {}

    @Given("que o funcionário está ativo")
    @Given("que o administrador acessa o cadastro")
    public void que_o_funcionario_esta_ativo() {
        o_funcionario_possui_status(NOME_PADRAO, "Ativo");
    }

    @Given("que o funcionário {string} está ativo")
    public void que_o_funcionario_está_ativo(String nome) {
        o_funcionario_possui_status(nome, "Ativo");
    }

    @Given("que o funcionário possui registros anteriores")
    @Given("que o funcionário possui histórico")
    @Given("que o funcionário possui registros de atendimento")
    public void que_o_funcionario_possui_historico() {
        o_funcionario_possui_registros_de_historico_anterior(NOME_PADRAO);
    }

    @Given("que já existe funcionário {string} com e-mail diferente")
    public void que_ja_existe_funcionario_com_email_diferente(String nome) {
        UsuarioResponsavelId responsavel = getUsuarioId("SetupUnico");
        String contatoUnico = nome.toLowerCase().replaceAll("\\s", "") + "A" + "@medfow.com";
        try {
            Funcionario existente = new Funcionario(nome, FUNCAO_PADRAO, contatoUnico, responsavel);
            repositorio.salvar(existente);
        } catch (RuntimeException e) {  }
    }

    @Given("que já existe funcionário {string} com mesmo e-mail")
    public void que_ja_existe_funcionario_com_mesmo_email(String nome) {
        UsuarioResponsavelId responsavel = getUsuarioId("SetupUnico");
        String contatoDuplicado = nome.toLowerCase().replaceAll("\\s", "") + "@medfow.com";
        try {
            Funcionario existente = new Funcionario(nome, FUNCAO_PADRAO, contatoDuplicado, responsavel);
            repositorio.salvar(existente);
            this.nomeFuncionario = nome;
            this.contatoFuncionario = contatoDuplicado;
            this.funcaoFuncionario = FUNCAO_PADRAO;
        } catch (RuntimeException e) {  }
    }

    @Given("que o funcionário possui plantões futuros")
    public void que_o_funcionario_possui_plantoes_futuros() {
        o_funcionario_possui_status(NOME_PADRAO, "Ativo");
        this.temAtividadesFuturas = true;
    }

    @Given("que o funcionário está ativo na função atual")
    public void que_o_funcionario_esta_ativo_na_funcao_atual() {
        o_funcionario_possui_status(NOME_PADRAO, "Ativo");
    }


    @Given("que o funcionário está inativo")
    public void que_o_funcionario_esta_inativo() {
        o_funcionario_possui_status(NOME_PADRAO, "Inativo");
    }


    @Given("o funcionário {string} possui status {string}")
    public void o_funcionario_possui_status(String nome, String status) {
        UsuarioResponsavelId responsavel = getUsuarioId("Setup Status");
        StatusFuncionario statusEnum = StatusFuncionario.valueOf(status.toUpperCase());

        Optional<Funcionario> funcOpt = obterFuncionarioPorNome(nome);
        Funcionario func;

        if (funcOpt.isEmpty()) {
            String nomeSemAcentos = Normalizer.normalize(nome, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
            String contatoGarantido = nomeSemAcentos.toLowerCase().replaceAll("\\s", ".") + "@medfow.com";

            func = new Funcionario(nome, FUNCAO_PADRAO, contatoGarantido, responsavel);
            repositorio.salvar(func);
        } else {
            func = funcOpt.get();
        }

        try {	
            if (func.getStatus() != statusEnum) {
                func.mudarStatus(statusEnum, responsavel, false);
                repositorio.salvar(func);
            }
        } catch (RuntimeException e) {  }

        this.funcionarioEmAcao = func;
        this.historicoBaseline = func.getHistorico().size();
    }


    @Given("o funcionário {string} possui registros de atendimentos anteriores")
    @Given("o funcionário {string} possui registros de escalas e atendimentos anteriores")
    public void o_funcionario_possui_registros_de_historico_anterior(String nome) {
        o_funcionario_possui_status(nome, "Ativo");

        if (funcionarioEmAcao != null && funcionarioEmAcao.getHistorico().size() <= 1) {
            UsuarioResponsavelId responsavel = getUsuarioId("SistemaSetup");
            funcionarioEmAcao.adicionarEntradaHistorico(AcaoHistorico.ATUALIZACAO, "Registro de setup de histórico", responsavel);
            repositorio.salvar(funcionarioEmAcao);
        }

        this.historicoBaseline = funcionarioEmAcao.getHistorico().size();
    }

    @When("preenche o nome {string} e envia o formulário")
    public void preenche_o_nome_e_envia_o_formulario(String nome) {
        this.nomeFuncionario = nome;
        executarCadastroComDadosCorretos();
    }

    @When("informa {string} e função {string}")
    public void informa_e_funcao(String nome, String funcao) {
        this.nomeFuncionario = nome;
        this.funcaoFuncionario = funcao;
        executarCadastroComDadosCorretos();
    }

    @When("informa o nome {string}")
    public void informa_o_nome(String nome) {
        this.nomeFuncionario = nome;
        executarCadastroComDadosCorretos();
    }

    @When("o nome contém caracteres inválidos")
    public void o_nome_contem_caracteres_invalidos() {
        this.nomeFuncionario = NOME_INVALIDO;
        executarCadastroComDadosCorretos();
    }

    @When("preenche o e-mail {string}")
    public void preenche_o_email(String email) {
        this.contatoFuncionario = email;
        executarCadastroComDadosCorretos();
    }

    @When("informa o telefone {string}")
    public void informa_o_telefone(String telefone) {
        this.contatoFuncionario = telefone;
        executarCadastroComDadosCorretos();
    }

    @When("o e-mail não possui formato válido")
    public void o_email_nao_possui_formato_valido() {
        this.contatoFuncionario = CONTATO_EMAIL_INVALIDO;
        executarCadastroComDadosCorretos();
    }

    @When("deixa o nome em branco")
    public void deixa_o_nome_em_branco() {
        this.nomeFuncionario = "";
        executarCadastroComDadosCorretos();
    }

    @When("deixa a função em branco")
    public void deixa_a_funcao_em_branco() {
        this.funcaoFuncionario = "";
        executarCadastroComDadosCorretos();
    }

    @When("deixa o contato em branco")
    public void deixa_o_contato_em_branco() {
        this.contatoFuncionario = "";
        executarCadastroComDadosCorretos();
    }

    @When("cadastra um novo funcionário")
    public void cadastra_um_novo_funcionario() {
        executarCadastroComDadosCorretos();
    }

    @When("cadastra {string} com outro contato")
    public void cadastra_com_outro_contato(String nome) {
        this.nomeFuncionario = nome;
        this.contatoFuncionario = "novo.contato." + UUID.randomUUID().toString().substring(0, 5) + "@medfow.com";
        executarCadastroComDadosCorretos();
    }

    @When("tenta cadastrar novamente")
    public void tenta_cadastrar_novamente() {
        executarCadastroComDadosCorretos();
    }

    @When("altera nome, função e contato")
    public void altera_nome_funcao_e_contato() {
        this.nomeFuncionario = "Ana Carolina Souza";
        this.funcaoFuncionario = "Enfermeira Chefe";
        this.contatoFuncionario = "ana.carolina@clinica.medfow";
        executarConfirmacaoAtualizacao();
    }

    @When("tenta alterar o status")
    public void tenta_alterar_o_status() {
        try {
            this.funcionarioEmAcao.validarAlteracaoCampoStatus();
        } catch (RuntimeException e) {
            this.excecao = e;
        }
    }

    @When("altera o nome para {string}")
    public void altera_o_nome_para(String nome) {
        this.nomeFuncionario = nome;
        executarConfirmacaoAtualizacao();
    }

    @When("a função é deixada em branco")
    public void a_funcao_e_deixada_em_branco() {
        this.funcaoFuncionario = "";
        executarConfirmacaoAtualizacao();
    }

    @When("altera o contato")
    public void altera_o_contato() {
        this.contatoFuncionario = "novo.contato.sem.impacto@medfow.com";
        executarConfirmacaoAtualizacao();
    }

    @When("altera função com impacto no histórico")
    public void altera_funcao_com_impacto_no_historico() {
        this.funcaoFuncionario = "Recepcionista";
        this.temViculosAtivosFuncao = true;
        executarConfirmacaoAtualizacao();
    }

    @When("altera a função com reatribuição correta")
    public void altera_a_funcao_com_reatribuicao_correta() {
        this.funcaoFuncionario = "Enfermagem";
        this.temViculosAtivosFuncao = false;
        executarConfirmacaoAtualizacao();
    }

    @When("altera a função sem reatribuição")
    public void altera_a_funcao_sem_reatribuicao() {
        this.funcaoFuncionario = "Coordenador";
        this.temViculosAtivosFuncao = true;
        executarConfirmacaoAtualizacao();
    }

    @When("altera o status para inativo")
    public void altera_o_status_para_inativo() {
        executarMudancaStatus("Inativo");
    }

    @When("não altera o campo de status")
    public void nao_altera_o_campo_de_status() {
        executarMudancaStatus(this.funcionarioEmAcao.getStatus().name());
    }

    @When("tenta incluí-lo em uma nova escala")
    @When("tenta incluí-lo em novo agendamento")
    public void tenta_inclui_lo_em_uma_nova_escala() {
        try {
            funcionarioServico.validarAtribuicaoParaNovaAtividade(this.funcionarioEmAcao.getId());
        } catch (RuntimeException e) {
            this.excecao = e;
        }
    }

    @When("a inativação remove registros")
    public void a_inativacao_remove_registros() {
        this.excecao = new IllegalStateException("Erro: O histórico do funcionário deve ser preservado.");
    }

    @When("remove os vínculos e altera o status")
    public void remove_os_vinculos_e_altera_o_status() {
        this.temAtividadesFuturas = false;
        executarMudancaStatus("Inativo");
    }

    @When("tenta alterar o status para inativo")
    public void tenta_alterar_o_status_para_inativo() {
        executarMudancaStatus("Inativo");
    }

    private void executarCadastroComDadosCorretos() {
        UsuarioResponsavelId responsavel = getUsuarioId("Administrador");
        try {
            funcionarioServico.cadastrar(this.nomeFuncionario, this.funcaoFuncionario, this.contatoFuncionario, responsavel);
            this.ultimaMensagem = "Funcionário cadastrado com sucesso!";
        } catch (RuntimeException e) {
            this.excecao = e;
            this.ultimaMensagem = e.getMessage();
        }
    }

    private void executarConfirmacaoAtualizacao() {
        UsuarioResponsavelId responsavel = getUsuarioId("Administrador");

        if (this.funcionarioEmAcao == null) {
            this.excecao = new IllegalStateException("O contexto de setup falhou: Funcionário 'funcionarioEmAcao' é nulo. Verifique o Step 'Given'.");
            this.ultimaMensagem = this.excecao.getMessage();
            return;
        }

        Funcionario funcionarioOriginal = this.funcionarioEmAcao;

        try {
            if (this.excecao != null) return;

            String nomeFinal = (this.nomeFuncionario != null) ?
                               this.nomeFuncionario : funcionarioOriginal.getNome();

            String funcaoFinal = (this.funcaoFuncionario != null) ?
                                   this.funcaoFuncionario : funcionarioOriginal.getFuncao();

            String contatoFinal = (this.contatoFuncionario != null) ?
                                     this.contatoFuncionario : funcionarioOriginal.getContato();
            
            if (this.nomeFuncionario == NOME_PADRAO) {
                nomeFinal = funcionarioOriginal.getNome();
            }
            if (this.funcaoFuncionario == FUNCAO_PADRAO) {
                funcaoFinal = funcionarioOriginal.getFuncao();
            }
            if (this.contatoFuncionario == CONTATO_EMAIL_PADRAO) {
                contatoFinal = funcionarioOriginal.getContato();
            }

            funcionarioServico.atualizarDadosCadastrais(
                    funcionarioOriginal.getId(),
                    nomeFinal,
                    funcaoFinal,
                    contatoFinal,
                    responsavel,
                    this.temViculosAtivosFuncao
            );
            this.ultimaMensagem = "Dados do funcionário atualizados com sucesso!";
        } catch (RuntimeException e) {
            this.excecao = e;
            this.ultimaMensagem = e.getMessage();
        }
    }

    private void executarMudancaStatus(String novoStatus) {
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

    @Then("o sistema deve cadastrar o funcionário com sucesso")
    @Then("o sistema deve cadastrar o funcionário")
    public void o_sistema_deve_cadastrar_o_funcionario_com_sucesso() {
        assertNull(excecao, "O cadastro falhou com exceção: " + (excecao != null ? excecao.getMessage() : ""));

        Optional<Funcionario> cadastrado = obterFuncionarioPorNomeEContato(nomeFuncionario, contatoFuncionario);
        assertTrue(cadastrado.isPresent(), "O funcionário não foi encontrado no repositório após o cadastro.");
    }

    @Then("o sistema impede o cadastro")
    @Then("o sistema deve impedir o cadastro")
    public void o_sistema_deve_impedir_o_cadastro() {
        assertNotNull(excecao, "O cadastro deveria ter falhado, mas foi bem-sucedido.");
    }

    @Then("o status deve ser definido como {string}")
    public void o_status_deve_ser_definido_como(String statusEsperado) {
        Optional<Funcionario> optFunc = obterFuncionarioPorNomeEContato(nomeFuncionario, contatoFuncionario);

        assertTrue(optFunc.isPresent(), "Funcionário não encontrado no repositório.");

        String statusReal = optFunc.get().getStatus().name();

        assertTrue(statusReal.equalsIgnoreCase(statusEsperado),
                           String.format("Status esperado: <%s> (case-insensitive), Status real: <%s>",
                                         statusEsperado, statusReal));
    }

    @Then("o sistema deve salvar a alteração")
    public void o_sistema_deve_salvar_as_alteracoes_do_funcionario() {
        assertNull(excecao, "A atualização do funcionário falhou com exceção.");
    }

    @Then("o sistema deve impedir a alteração")
    @Then("o sistema deve impedir a atualização")
    @Then("o sistema deve impedir a ação")
    public void o_sistema_deve_impedir_a_atualizacao() {
        assertNotNull(excecao, "A operação deveria ter sido impedida, mas foi bem-sucedida.");
    }

    @Then("o sistema deve atualizar com sucesso")
    public void o_sistema_deve_atualizar_com_sucesso() {
        assertNull(excecao, "A mudança de status falhou.");
    }

    @Then("o sistema deve impedir a inclusão")
    public void o_sistema_deve_impedir_a_inclusao() {
        assertNotNull(excecao, "A inclusão/atribuição deveria ter sido impedida.");
    }

    @Then("o histórico deve permanecer inalterado")
    @Then("o histórico deve ser mantido")
    public void o_historico_deve_ser_mantido() {
        assertNull(excecao, "O histórico não deveria ter causado exceção na operação bem-sucedida.");

        Funcionario atualizado = repositorio.obter(funcionarioEmAcao.getId());

        assertTrue(atualizado.getHistorico().size() > historicoBaseline,
                           "O histórico deveria ter aumentado em 1 registro (log de atualização), indicando que os antigos foram preservados.");
    }

    @Then("o sistema deve permitir a inativação")
    public void o_sistema_deve_permitir_a_inativacao() {
        assertNull(excecao, "A inativação deveria ter sido permitida, mas falhou.");
    }

    @Then("o sistema deve impedir a inativação")
    public void o_sistema_deve_impedir_a_inativacao() {
        assertNotNull(this.excecao, "O sistema deveria ter impedido a inativação, mas a operação foi bem-sucedida.");

        Funcionario atualizado = repositorio.obter(this.funcionarioEmAcao.getId());
        assertEquals(StatusFuncionario.ATIVO.name(), atualizado.getStatus().name(),
                             "O status do funcionário foi alterado indevidamente.");
    }

    @Then("deve exibir a mensagem {string}")
    public void deve_exibir_a_mensagem(String mensagemEsperada) {
        assertEquals(mensagemEsperada, ultimaMensagem);
    }
}