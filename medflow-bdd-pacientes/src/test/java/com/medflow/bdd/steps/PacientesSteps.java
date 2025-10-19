package com.medflow.bdd.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PacientesSteps {

    static class Paciente {
        String nome;
        String cpf;
        String telefone;
        String endereco;
        String dataNascimento;
        boolean prontuario;
        boolean consultasAgendadas;
        boolean examesAgendados;
    }

    private final Map<String, Paciente> repo = new HashMap<>();
    private final Set<String> cpfsCadastrados = new HashSet<>(); // mantém unicidade rápida

    // Estados (cadastro)
    private boolean pacienteCriado;
    private boolean cadastroBloqueado;

    // Estados (atualização)
    private boolean atualizacaoSalva;
    private boolean atualizacaoBloqueada;

    // Estados (remoção)
    private boolean remocaoEfetuada;
    private boolean remocaoBloqueada;

    // Mensagem “genérica” (de erro/bloqueio)
    private String mensagemErro;

    // Buffer de atualização (armazena pedido e valida no THEN)
    private String updCpfAlvo;
    private String updNovoNome;
    private String updNovoTelefone;
    private String updNovoEndereco;
    private String updNovaData;
    private String updNovoCpf;      // se alguém tentar trocar o CPF
    private boolean updRecebido;    // marca que houve um pedido de update
    private boolean updDeixarNomeEmBranco;
    private boolean updDeixarCpfEmBranco;
    private boolean updDeixarTelefoneEmBranco;
    private boolean updDeixarDataEmBranco;

    // Buffer de remoção
    private String cpfParaRemocao;
    private boolean pedidoRemocaoRecebido;

    private static final DateTimeFormatter BR_STRICT =
            DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);

    // Utils
    private boolean apenasDigitos(String s) {
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c < '0' || c > '9') return false;
        }
        return true;
    }
    private boolean dataBrValida(String data) {
        try { LocalDate.parse(data, BR_STRICT); return true; }
        catch (Exception e) { return false; }
    }

    private void resetCadastro() {
        pacienteCriado = false;
        cadastroBloqueado = false;
        mensagemErro = null;
    }
    private void resetUpdateBuffers() {
        updCpfAlvo = null;
        updNovoNome = null;
        updNovoTelefone = null;
        updNovoEndereco = null;
        updNovaData = null;
        updNovoCpf = null;
        updRecebido = false;
        updDeixarNomeEmBranco = false;
        updDeixarCpfEmBranco = false;
        updDeixarTelefoneEmBranco = false;
        updDeixarDataEmBranco = false;
        atualizacaoSalva = false;
        atualizacaoBloqueada = false;
        mensagemErro = null;
    }
    private void resetRemocao() {
        cpfParaRemocao = null;
        pedidoRemocaoRecebido = false;
        remocaoEfetuada = false;
        remocaoBloqueada = false;
        mensagemErro = null;
    }

    // ===================================================
    // GIVEN – Contextos compartilhados / Cadastro
    // ===================================================
    
    @Given("que não existe paciente com o CPF {string}")
    public void queNaoExistePacienteComOCpf(String cpf) {
        repo.remove(cpf);
        cpfsCadastrados.remove(cpf);
    }

    @Given("que não existe paciente cadastrado com o CPF {string}")
    public void queNaoExistePacienteCadastradoComOCpf(String cpf) {
        queNaoExistePacienteComOCpf(cpf);
    }

    @Given("não há paciente cadastrado com o CPF {string}")
    public void naoHaPacienteCadastradoComOCpf(String cpf) {
        queNaoExistePacienteComOCpf(cpf);
    }

    @Given("que já existe um paciente cadastrado com o CPF {string}")
    public void queJaExisteUmPacienteCadastradoComOCpf(String cpf) {
        Paciente p = repo.getOrDefault(cpf, new Paciente());
        p.cpf = cpf;
        if (p.nome == null) p.nome = "Paciente Existente";
        if (p.telefone == null) p.telefone = "81990000000";
        if (p.endereco == null) p.endereco = "Endereco X";
        if (p.dataNascimento == null) p.dataNascimento = "10/10/1990";
        repo.put(cpf, p);
        cpfsCadastrados.add(cpf);
    }

    // Regras “declarativas” (cadastro)
    @Given("que nome é uma informação obrigatória") public void nomeObrigatorio() {}
    @Given("que CPF é uma informação obrigatória") public void cpfObrigatorio() {}
    @Given("que data de nascimento é uma informação obrigatória") public void dataObrigatoria() {}
    @Given("que telefone é uma informação obrigatória") public void telefoneObrigatorio() {}
    @Given("que o CPF deve ter 11 dígitos") public void cpfDeveTer11Digitos() {}
    @Given("que o CPF deve ter apenas caracteres numéricos") public void cpfApenasNumeros() {}
    @Given("que a data de nascimento deve estar no formato {string}") public void dataDeveEstarNoFormato(String f) {}

    // ===================================================
    // WHEN – Cadastro
    // ===================================================
    
    @When("a funcionária {string} solicita o cadastro do paciente com nome {string}, CPF {string}, telefone {string}, endereço {string} e data de nascimento {string}")
    public void solicitarCadastroCompleto(String funcionaria, String nome, String cpf, String telefone, String endereco, String dataNascimento) {
        validarCadastro(nome, cpf, telefone, endereco, dataNascimento);
    }
    @When("a funcionária {string} solicitar o cadastro do paciente com nome {string}, CPF {string}, telefone {string}, endereço {string} e data de nascimento {string}")
    public void solicitarCadastroCompletoComSolicitar(String funcionaria, String nome, String cpf,
                                                      String telefone, String endereco, String dataNascimento) {
        solicitarCadastroCompleto(funcionaria, nome, cpf, telefone, endereco, dataNascimento);
    }
    @When("a funcionária {string} solicitar o cadastro do paciente com CPF {string}, telefone {string}, endereço {string} e data de nascimento {string}")
    public void solicitarCadastroSemNome(String funcionaria, String cpf, String telefone, String endereco, String dataNascimento) {
        validarCadastro(null, cpf, telefone, endereco, dataNascimento);
    }
    @When("a funcionária {string} solicitar o cadastro do paciente com nome {string}, telefone {string}, endereço {string} e data de nascimento {string}")
    public void solicitarCadastroSemCpf(String funcionaria, String nome, String telefone, String endereco, String dataNascimento) {
        validarCadastro(nome, null, telefone, endereco, dataNascimento);
    }
    @When("a funcionária {string} solicitar o cadastro do paciente com nome {string}, CPF {string}, telefone {string} e endereço {string}")
    public void solicitarCadastroSemData(String funcionaria, String nome, String cpf, String telefone, String endereco) {
        validarCadastro(nome, cpf, telefone, endereco, null);
    }
    @When("a funcionária {string} solicitar o cadastro do paciente com nome {string}, CPF {string}, endereço {string} e data de nascimento {string}")
    public void solicitarCadastroSemTelefone(String funcionaria, String nome, String cpf, String endereco, String dataNascimento) {
        validarCadastro(nome, cpf, null, endereco, dataNascimento);
    }

    private void validarCadastro(String nome, String cpf, String telefone, String endereco, String dataNascimento) {
        resetCadastro();
        // RN1 – obrigatórios
        if (nome == null || nome.isBlank()) { bloquearCadastro("NOME_OBRIGATORIO"); return; }
        if (cpf == null || cpf.isBlank()) { bloquearCadastro("CPF_OBRIGATORIO"); return; }
        if (telefone == null || telefone.isBlank()) { bloquearCadastro("TELEFONE_OBRIGATORIO"); return; }
        if (dataNascimento == null || dataNascimento.isBlank()) { bloquearCadastro("DATA_NASCIMENTO_OBRIGATORIA"); return; }

        // RN2 – prioridade: apenas dígitos -> tamanho
        if (!apenasDigitos(cpf)) { bloquearCadastro("CPF_APENAS_DIGITOS_11"); return; }
        if (cpf.length() != 11)  { bloquearCadastro("CPF_DEVE_TER_11_DIGITOS"); return; }

        // RN3 – data dd/MM/aaaa
        if (!dataBrValida(dataNascimento)) { bloquearCadastro("DATA_NASCIMENTO_FORMATO_INVALIDO_DDMMYYYY"); return; }

        // RN4 – CPF único
        if (cpfsCadastrados.contains(cpf)) { bloquearCadastro("CPF_JA_CADASTRADO"); return; }

        // sucesso
        Paciente p = new Paciente();
        p.nome = nome; p.cpf = cpf; p.telefone = telefone; p.endereco = endereco; p.dataNascimento = dataNascimento;
        repo.put(cpf, p);
        cpfsCadastrados.add(cpf);
        pacienteCriado = true;
    }
    private void bloquearCadastro(String erro) {
        cadastroBloqueado = true;
        mensagemErro = erro;
    }

    // ===================================================
    // THEN – Cadastro
    // ===================================================
    
    @Then("o sistema deve criar o paciente")
    public void sistemaDeveCriarOPaciente() {
        assertTrue(pacienteCriado);
        assertFalse(cadastroBloqueado);
    }
    @Then("exibir confirmação de sucesso")
    public void exibirConfirmacaoDeSucesso() {
        assertTrue(pacienteCriado || atualizacaoSalva || remocaoEfetuada);
        assertNull(mensagemErro);
    }
    @Then("o sistema deve bloquear o cadastro")
    public void sistemaDeveBloquearOCadastro() { assertTrue(cadastroBloqueado); assertFalse(pacienteCriado); }
    @Then("exibir mensagem indicando que nome é obrigatório")
    public void msgNomeObrigatorio() { assertEquals("NOME_OBRIGATORIO", mensagemErro); }
    @Then("exibir mensagem indicando que CPF é obrigatório")
    public void msgCpfObrigatorio() { assertEquals("CPF_OBRIGATORIO", mensagemErro); }
    @Then("exibir mensagem indicando que data de nascimento é obrigatória")
    public void msgDataObrigatoria() { assertEquals("DATA_NASCIMENTO_OBRIGATORIA", mensagemErro); }
    @Then("exibir mensagem indicando que telefone é obrigatório")
    public void msgTelefoneObrigatorio() { assertEquals("TELEFONE_OBRIGATORIO", mensagemErro); }
    @Then("exibir mensagem informando que o CPF deve conter exatamente 11 dígitos numéricos")
    public void msgCpf11() { assertEquals("CPF_DEVE_TER_11_DIGITOS", mensagemErro); }
    @Then("exibir mensagem informando que o CPF deve conter apenas dígitos \\(11 caracteres)")
    public void msgCpfApenasDigitos() { assertEquals("CPF_APENAS_DIGITOS_11", mensagemErro); }
    @Then("exibir mensagem informando que a data de nascimento deve estar no formato dd/mm/aaaa")
    public void msgDataFormato() { assertEquals("DATA_NASCIMENTO_FORMATO_INVALIDO_DDMMYYYY", mensagemErro); }
    @Then("exibir mensagem informando que o CPF já está cadastrado")
    public void msgCpfJaCadastrado() { assertEquals("CPF_JA_CADASTRADO", mensagemErro); }
    @Then("exibir mensagem informando que a data de nascimento deve estar no formato dd\\/mm\\/aaaa")
    public void msgDataFormatoEscapado() { msgDataFormato(); }

    // ===================================================
    // GIVEN – Alteração
    // ===================================================
    
    @Given("existe um paciente com CPF {string}")
    public void existePacienteComCpf(String cpf) {
        Paciente p = repo.getOrDefault(cpf, new Paciente());
        if (p.cpf == null) {
            p.cpf = cpf; p.nome = "Nome"; p.telefone = "81990000000"; p.endereco = "End"; p.dataNascimento = "01/01/1990";
        }
        repo.put(cpf, p);
        cpfsCadastrados.add(cpf);
        resetUpdateBuffers();
        updCpfAlvo = cpf;
    }

    // ===================================================
    // WHEN – Alteração (armazena o pedido e valida no THEN)
    // ===================================================
    
    @When("a funcionária {string} solicitar a atualização dos dados do paciente para nome {string}, telefone {string}, data de nascimento {string} e endereço {string}")
    public void solicitarAtualizacaoTodosCampos(String func, String nome, String tel, String data, String end) {
        updRecebido = true;
        updNovoNome = nome; updNovoTelefone = tel; updNovaData = data; updNovoEndereco = end;
    }

    @When("mantém o CPF {string} inalterado")
    public void mantemCpfInalterado(String cpf) {
        // no-op: reforça a intenção de não trocar o CPF
    }

    // <<< ADIÇÃO >>> passo “no-op” usado nos cenários
    @When("mantém os demais dados inalterados")
    public void mantemDemaisDadosInalterados() {
        
    }

    @When("a funcionária {string} tentar alterar o CPF para {string}")
    public void tentarAlterarCpf(String func, String novoCpf) {
        updRecebido = true;
        updNovoCpf = novoCpf; // tentativa de troca -> deve bloquear na execução
    }

    @When("a funcionária {string} solicita alteração dos dados para nome {string}, telefone {string} e data de nascimento {string}")
    public void solicitarAtualizacaoSemEndereco(String func, String nome, String tel, String data) {
        updRecebido = true;
        updNovoNome = nome; updNovoTelefone = tel; updNovaData = data;
    }

    // <<< ADIÇÕES >>> variações usadas pelo .feature
    @When("a funcionária {string} solicita alteração dos dados para telefone {string}")
    public void solicitarAtualizacaoSomenteTelefone(String func, String tel) {
        updRecebido = true;
        updNovoTelefone = tel;
    }

    @When("a funcionária {string} solicita alteração dos dados para nome {string}")
    public void solicitarAtualizacaoSomenteNome(String func, String nome) {
        updRecebido = true;
        updNovoNome = nome;
    }

    @When("a funcionária {string} solicita alteração dos dados para nome {string} e data de nascimento {string}")
    public void solicitarAtualizacaoNomeEData(String func, String nome, String data) {
        updRecebido = true;
        updNovoNome = nome; updNovaData = data;
    }

    @When("a funcionária {string} solicita alteração dos dados para nome {string} e telefone {string}")
    public void solicitarAtualizacaoNomeETelefone(String func, String nome, String tel) {
        updRecebido = true;
        updNovoNome = nome; updNovoTelefone = tel;
    }
    
    @When("a funcionária {string} solicita a atualização dos dados do paciente para nome {string}, telefone {string}, data de nascimento {string} e endereço {string}")
    public void solicitaAtualizacaoTodosCampos(String func, String nome, String tel, String data, String end) {
        
        solicitarAtualizacaoTodosCampos(func, nome, tel, data, end);
    }

    // Passos “deixa campo em branco”
    @When("deixa o campo nome em branco")
    public void deixaNomeEmBranco() { updDeixarNomeEmBranco = true; }
    @When("deixa o campo CPF em branco")
    public void deixaCpfEmBranco() { updDeixarCpfEmBranco = true; }
    @When("deixa o campo telefone em branco")
    public void deixaTelefoneEmBranco() { updDeixarTelefoneEmBranco = true; }
    @When("deixa o campo data de nascimento em branco")
    public void deixaDataEmBranco() { updDeixarDataEmBranco = true; }

    // ===================================================
    // Execução da atualização (chamada no THEN correspondente)
    // ===================================================
    
    private void executarAtualizacaoSeNecessario() {
        if (!updRecebido) return; // nada a fazer
        atualizacaoSalva = false;
        atualizacaoBloqueada = false;
        mensagemErro = null;

        Paciente p = repo.get(updCpfAlvo);
        if (p == null) {
            atualizacaoBloqueada = true;
            mensagemErro = "PACIENTE_INEXISTENTE";
            return;
        }

        // RN1 — CPF não pode ser alterado
        if (updNovoCpf != null && !updNovoCpf.equals(p.cpf)) {
            atualizacaoBloqueada = true;
            mensagemErro = "CPF_NAO_PODE_SER_ALTERADO";
            return;
        }

        // Aplicar “em branco”
        String nome = updDeixarNomeEmBranco ? "" : (updNovoNome != null ? updNovoNome : p.nome);
        String telefone = updDeixarTelefoneEmBranco ? "" : (updNovoTelefone != null ? updNovoTelefone : p.telefone);
        String data = updDeixarDataEmBranco ? "" : (updNovaData != null ? updNovaData : p.dataNascimento);
        // Para CPF, o requisito manda manter mesmo; “deixar CPF em branco” significa inválido:
        String cpfParaValidacao = updDeixarCpfEmBranco ? "" : p.cpf;

        // RN2 — obrigatórios (nome, CPF, telefone, data)
        if (nome == null || nome.isBlank() ||
            cpfParaValidacao == null || cpfParaValidacao.isBlank() ||
            telefone == null || telefone.isBlank() ||
            data == null || data.isBlank()) {
            atualizacaoBloqueada = true;
            mensagemErro = "OBRIGATORIOS_ATUALIZACAO";
            return;
        }

        // RN3 — data dd/MM/aaaa na atualização
        if (!dataBrValida(data)) {
            atualizacaoBloqueada = true;
            mensagemErro = "DATA_NASCIMENTO_FORMATO_INVALIDO_DDMMYYYY";
            return;
        }

        // sucesso: persiste
        p.nome = nome; p.telefone = telefone; p.endereco = (updNovoEndereco != null ? updNovoEndereco : p.endereco); p.dataNascimento = data;
        repo.put(p.cpf, p);
        atualizacaoSalva = true;
    }

    // ===================================================
    // THEN – Alteração
    // ===================================================
    
    @Then("o sistema deve salvar as alterações")
    public void sistemaDeveSalvarAsAlteracoes() {
        executarAtualizacaoSeNecessario();
        assertTrue(atualizacaoSalva);
        assertFalse(atualizacaoBloqueada);
    }

    @Then("o sistema deve bloquear a atualização")
    public void sistemaDeveBloquearAtualizacao() {
        executarAtualizacaoSeNecessario();
        assertTrue(atualizacaoBloqueada);
        assertFalse(atualizacaoSalva);
    }

    @Then("exibir mensagem informando que o CPF não pode ser alterado")
    public void msgCpfNaoPodeSerAlterado() {
        assertEquals("CPF_NAO_PODE_SER_ALTERADO", mensagemErro);
    }

    @Then("exibir mensagem indicando que nome, CPF, telefone e data de nascimento são obrigatórios")
    public void msgObrigatoriosAtualizacao() {
        assertEquals("OBRIGATORIOS_ATUALIZACAO", mensagemErro);
    }

    // ===================================================
    // GIVEN – Remoção
    // ===================================================
    
    @Given("o paciente não possui prontuário")
    public void pacienteSemProntuario() {
        if (updCpfAlvo != null && repo.containsKey(updCpfAlvo)) {
            repo.get(updCpfAlvo).prontuario = false;
        }
    }

    @Given("o paciente possui prontuário eletrônico")
    public void pacienteComProntuario() {
        if (updCpfAlvo != null && repo.containsKey(updCpfAlvo)) {
            repo.get(updCpfAlvo).prontuario = true;
        }
    }

    @Given("o paciente não possui consultas agendadas \\(históricas ou futuras)")
    public void pacienteSemConsultas() {
        if (updCpfAlvo != null && repo.containsKey(updCpfAlvo)) {
            repo.get(updCpfAlvo).consultasAgendadas = false;
        }
    }

    @Given("o paciente possui consulta agendada \\(histórica ou futura)")
    public void pacienteComConsultas() {
        if (updCpfAlvo != null && repo.containsKey(updCpfAlvo)) {
            repo.get(updCpfAlvo).consultasAgendadas = true;
        }
    }

    @Given("o paciente não possui exames agendados \\(históricos ou futuros)")
    public void pacienteSemExames() {
        if (updCpfAlvo != null && repo.containsKey(updCpfAlvo)) {
            repo.get(updCpfAlvo).examesAgendados = false;
        }
    }

    @Given("o paciente possui exame agendado \\(histórico ou futuro)")
    public void pacienteComExames() {
        if (updCpfAlvo != null && repo.containsKey(updCpfAlvo)) {
            repo.get(updCpfAlvo).examesAgendados = true;
        }
    }

    // ===================================================
    // WHEN – Remoção
    // ===================================================
    
    @When("a funcionária {string} solicitar a remoção do cadastro")
    public void solicitarRemocao(String func) {
        pedidoRemocaoRecebido = true;
        cpfParaRemocao = updCpfAlvo; // usa o paciente do contexto atual
    }

    // Execução da remoção (no THEN)
    private void executarRemocaoSeNecessario() {
        if (!pedidoRemocaoRecebido) return;
        remocaoEfetuada = false;
        remocaoBloqueada = false;
        mensagemErro = null;

        if (cpfParaRemocao == null) {
            remocaoBloqueada = true;
            mensagemErro = "PACIENTE_NAO_EXISTE";
            return;
        }
        Paciente p = repo.get(cpfParaRemocao);
        if (p == null) {
            remocaoBloqueada = true;
            mensagemErro = "PACIENTE_NAO_EXISTE";
            return;
        }

        // Regras de bloqueio
        if (p.prontuario) {
            remocaoBloqueada = true;
            mensagemErro = "PACIENTE_COM_PRONTUARIO";
            return;
        }
        if (p.consultasAgendadas) {
            remocaoBloqueada = true;
            mensagemErro = "PACIENTE_COM_CONSULTAS";
            return;
        }
        if (p.examesAgendados) {
            remocaoBloqueada = true;
            mensagemErro = "PACIENTE_COM_EXAMES";
            return;
        }

        // Sucesso
        repo.remove(cpfParaRemocao);
        cpfsCadastrados.remove(cpfParaRemocao);
        remocaoEfetuada = true;
    }

    // ===================================================
    // THEN – Remoção
    // ===================================================
    
    @Then("o sistema deve remover o paciente")
    public void sistemaDeveRemoverPaciente() {
        executarRemocaoSeNecessario();
        assertTrue(remocaoEfetuada);
        assertFalse(remocaoBloqueada);
    }

    @Then("o sistema deve bloquear a remoção")
    public void sistemaDeveBloquearRemocao() {
        executarRemocaoSeNecessario();
        assertTrue(remocaoBloqueada);
        assertFalse(remocaoEfetuada);
    }

    @Then("exibir mensagem informando que o paciente não existe")
    public void msgPacienteNaoExiste() {
        assertEquals("PACIENTE_NAO_EXISTE", mensagemErro);
    }

    @Then("exibir mensagem informando que pacientes com prontuário não podem ser removidos")
    public void msgPacienteComProntuario() {
        assertEquals("PACIENTE_COM_PRONTUARIO", mensagemErro);
    }

    @Then("exibir mensagem informando que pacientes com consultas agendadas não podem ser removidos")
    public void msgPacienteComConsultas() {
        assertEquals("PACIENTE_COM_CONSULTAS", mensagemErro);
    }

    @Then("exibir mensagem informando que pacientes com exames agendados não podem ser removidos")
    public void msgPacienteComExames() {
        assertEquals("PACIENTE_COM_EXAMES", mensagemErro);
    }
}
