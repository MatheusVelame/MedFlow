package com.medflow.bdd.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.And;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

/**
 * Steps para o BDD de Tipos de Exames (MedFlow)
 * Regras cobertas:
 *  - Cadastro: descrição obrigatória; especialidade obrigatória; valor obrigatório; valor > 0; status inicial Ativo; código único
 *  - Atualização: código imutável; valor >= 0; proibido atualizar se houver agendamentos vinculados
 *  - Remoção/Inativação: não excluir se houver agendamentos (históricos ou futuros); não inativar se houver agendamentos futuros;
 *    tratar inexistentes (bloquear e informar "não encontrado")
 */
public class TiposExamesSteps {

    // =========================
    // Modelo e "repositório"
    // =========================
	
    static class TipoExame {
        String codigo;
        String descricao;
        String especialidade;
        String status; // "Ativo" | "Inativo"
        double valor;
    }

    private final Map<String, TipoExame> repo = new HashMap<>();
    private final Set<String> codigosCadastrados = new HashSet<>();

    // "Bancos" de agendamentos
    
    private final Set<String> agendHistOuFut = new HashSet<>();  // possui histórico e/ou futuros
    private final Set<String> agendFuturos   = new HashSet<>();  // possui futuros

    // =========================
    // Estados/mensagens
    // =========================
    
    private boolean criado;
    private boolean cadastroBloqueado;

    private boolean alterado;
    private boolean atualizacaoBloqueada;

    private boolean excluido;
    private boolean inativado;
    private boolean operacaoBloqueada;

    private String msg;   // mensagem exibida

    // buffers de atualização
    
    private String updCodigoAlvo;
    private boolean updPedido;
    private String updNovaDescricao;
    private String updNovaEspecialidade;
    private String updNovoStatus;
    private Double updNovoValor;
    private String tentativaNovoCodigo;
    
    // Informações de cadastro
    
    private String codigo;
    private String descricao;
    private String especialidade;
    private String valorStr;
    private String statusInicial;

    // =========================
    // Utilitários
    // =========================
    
    private void resetCadastro() {
        criado = false; cadastroBloqueado = false; msg = null;
    }
    
    private void resetUpdate() {
        updPedido = false;
        updCodigoAlvo = null;
        updNovaDescricao = null;
        updNovaEspecialidade = null;
        updNovoStatus = null;
        updNovoValor = null;
        tentativaNovoCodigo = null;
        alterado = false; atualizacaoBloqueada = false; msg = null;
    }
    
    private void resetRemocaoInativacao() {
        excluido = false; inativado = false; operacaoBloqueada = false; msg = null;
    }

    private static Double parseValor(String valorStr) {
        if (valorStr == null) return null;
        try { return Double.parseDouble(valorStr.replace(",", ".")); }
        catch (NumberFormatException e) { return null; }
    }

    // =========================
    // GIVEN — Contextos
    // =========================

    @Given("que não existe tipo de exame cadastrado com o código {string}")
    public void queNaoExisteTipoDeExameComCodigo(String codigo) {
        repo.remove(codigo);
        codigosCadastrados.remove(codigo);
        agendHistOuFut.remove(codigo);
        agendFuturos.remove(codigo);
        resetCadastro();
        resetUpdate();
        resetRemocaoInativacao();
    }

    @Given("que já existe tipo de exame cadastrado com o código {string}")
    public void queJaExisteTipoDeExameComCodigo(String codigo) {
        TipoExame t = new TipoExame();
        t.codigo = codigo;
        t.descricao = "EXISTENTE";
        t.especialidade = "Radiologia";
        t.status = "Ativo";
        t.valor = 100.0;
        repo.put(codigo, t);
        codigosCadastrados.add(codigo);
        resetUpdate();
        updCodigoAlvo = codigo;
    }

    @Given("existe uma especialidade {string} cadastrada no sistema")
    public void existeEspecialidade(String especialidade) {
        // Contexto informativo — não persiste ainda
    }

    @Given("que existe um tipo de exame com o código {string}")
    public void existeUmTipoDeExameComCodigo(String codigo) {
        if (!repo.containsKey(codigo)) {
            TipoExame t = new TipoExame();
            t.codigo = codigo;
            t.descricao = "EXISTENTE";
            t.especialidade = "Radiologia";
            t.status = "Ativo";
            t.valor = 100.0;
            repo.put(codigo, t);
            codigosCadastrados.add(codigo);
        }
        resetUpdate();
        updCodigoAlvo = codigo;
    }

    @Given("não existem agendamentos vinculados a esse exame")
    public void naoExistemAgendamentosVinculados() {
        if (updCodigoAlvo != null) {
            agendHistOuFut.remove(updCodigoAlvo);
            agendFuturos.remove(updCodigoAlvo);
        }
    }

    @Given("existem agendamentos vinculados a esse exame")
    public void existemAgendamentosVinculados() {
        if (updCodigoAlvo != null) {
            agendHistOuFut.add(updCodigoAlvo);
            agendFuturos.add(updCodigoAlvo); // se tem vinculados, podem incluir futuros
        }
    }

    @Given("não existem agendamentos \\(históricos ou futuros) vinculados a esse exame")
    public void naoExistemAgendamentosHistoricosOuFuturos() {
        if (updCodigoAlvo != null) {
            agendHistOuFut.remove(updCodigoAlvo);
            agendFuturos.remove(updCodigoAlvo);
        }
    }

    @Given("existem agendamentos vinculados a esse exame \\(históricos e\\/ou futuros)")
    public void existemAgendamentosHistoricosOuFuturos() {
        if (updCodigoAlvo != null) {
            agendHistOuFut.add(updCodigoAlvo);
            // não necessariamente futuros, mas para exclusão basta constar aqui
        }
    }

    @Given("não existem agendamentos \\(futuros) vinculados a esse exame")
    public void naoExistemAgendamentosFuturos() {
        if (updCodigoAlvo != null) agendFuturos.remove(updCodigoAlvo);
    }

    @Given("existem agendamentos vinculados a esse exame \\(futuros)")
    public void existemAgendamentosFuturos() {
        if (updCodigoAlvo != null) agendFuturos.add(updCodigoAlvo);
    }

    // =========================
    // WHEN — Cadastro
    // =========================

    @When("a gerente {string} solicita o cadastro do tipo de exame com o código {string}, descrição {string}, especialidade {string} e o valor {string}")
    public void solicitaCadastroCompleto(String gerente, String codigo, String descricao, String especialidade, String valorStr) {
        this.codigo = codigo;
        this.descricao = descricao;
        this.especialidade = especialidade;
        this.valorStr = valorStr;
    }
    
    @When("a gerente {string} solicita o cadastro do tipo de exame com o código {string}, descrição {string}, especialidade {string} e valor {string}")
    public void solicitaCadastroCompletoFormato2(String gerente, String codigo, String descricao, String especialidade, String valorStr) {
        this.codigo = codigo;
        this.descricao = descricao;
        this.especialidade = especialidade;
        this.valorStr = valorStr;
    }

    @When("a gerente {string} solicita o cadastro do tipo de exame com o código {string}, especialidade {string} e valor {string}")
    public void solicitaCadastroSemDescricao(String gerente, String codigo, String especialidade, String valorStr) {
        this.codigo = codigo;
        this.descricao = "";
        this.especialidade = especialidade;
        this.valorStr = valorStr;
    }

    @When("a gerente {string} solicita o cadastro do tipo de exame com o código {string}, descrição {string} e valor {string}")
    public void solicitaCadastroSemEspecialidade(String gerente, String codigo, String descricao, String valorStr) {
        this.codigo = codigo;
        this.descricao = descricao;
        this.especialidade = null;
        this.valorStr = valorStr;
    }

    @When("a gerente {string} solicita o cadastro do tipo de exame com o código {string}, descrição {string} e especialidade {string}")
    public void solicitaCadastroSemValor(String gerente, String codigo, String descricao, String especialidade) {
        this.codigo = codigo;
        this.descricao = descricao;
        this.especialidade = especialidade;
        this.valorStr = null;
    }

    @When("a gerente {string} solicita o cadastro do tipo de exame com código {string}, a descrição {string}, a especialidade {string} e o valor {string}")
    public void solicitaCadastroFormato2(String gerente, String codigo, String descricao, String especialidade, String valorStr) {
        this.codigo = codigo;
        this.descricao = descricao;
        this.especialidade = especialidade;
        this.valorStr = valorStr;
    }

    @When("a gerente {string} solicita o cadastro do tipo de exame com código {string}, descrição {string}, a especialidade {string} e o valor {string}")
    public void solicitaCadastroFormato3(String gerente, String codigo, String descricao, String especialidade, String valorStr) {
        this.codigo = codigo;
        this.descricao = descricao;
        this.especialidade = especialidade;
        this.valorStr = valorStr;
    }

    @When("deixa a descrição em branco")
    public void deixaDescricaoEmBranco() {
        this.descricao = "";
    }

    @When("não informa a especialidade")
    public void naoInformaEspecialidade() { 
        this.especialidade = null; 
    }

    @When("não informa o valor")
    public void naoInformaValor() { 
        this.valorStr = null; 
    }

    @When("define o status inicial como {string}")
    public void defineStatusInicialComoQuando(String status) {
        this.statusInicial = status;
    }

    // =========================
    // WHEN — Atualização
    // =========================

    @When("a gerente {string} solicitar a alteração da descrição para {string}")
    public void solicitarAlteracaoDescricao(String gerente, String novaDescricao) {
        updPedido = true;
        updNovaDescricao = novaDescricao;
    }

    @When("alterar a especialidade para {string}")
    public void alterarEspecialidadePara(String novaEsp) {
        updPedido = true;
        updNovaEspecialidade = novaEsp;
    }

    @When("alterar o valor para {string}")
    public void alterarValorPara(String valorStr) {
        updPedido = true;
        updNovoValor = parseValor(valorStr);
        if (updNovoValor == null) { // formato inválido; tratado como negativo para bloquear
            updNovoValor = -1.0;
        }
    }

    @When("alterar o status para {string}")
    public void alterarStatusPara(String novoStatus) {
        updPedido = true;
        updNovoStatus = novoStatus;
    }

    @When("manter o código do exame como {string}")
    public void manterCodigoInalterado(String codigo) {
        updPedido = true;
        // no-op: apenas reforça que não haverá troca de código
    }

    @When("manter o código {string} inalterado")
    public void manterCodigoIalterado(String codigo) {
        updPedido = true;
        // no-op: apenas reforça que não haverá troca de código
    }

    @When("mantiver os demais dados sem alteração")
    public void mantiverDemaisDadosSemAlteracao() {
        updPedido = true;
        // no-op: apenas reforça que os outros dados não serão alterados
    }

    @When("a gerente {string} solicitar a alteração do código {string} para {string}")
    public void solicitarTrocaDeCodigo(String gerente, String antigo, String novo) {
        updPedido = true;
        updCodigoAlvo = antigo;
        tentativaNovoCodigo = novo; // RN: deve bloquear troca de código
    }

    @When("a gerente {string} solicitar a alteração do valor para {string}")
    public void solicitarAlteracaoValor(String gerente, String novoValor) {
        updPedido = true;
        updNovoValor = parseValor(novoValor);
        if (updNovoValor == null) { // formato inválido; tratado como negativo para bloquear
            updNovoValor = -1.0;
        }
    }

    // =========================
    // WHEN — Remoção / Inativação
    // =========================

    @When("a gerente {string} solicitar a exclusão do tipo de exame com o código {string}")
    public void solicitarExclusao(String gerente, String codigo) {
        resetRemocaoInativacao();
        if (!repo.containsKey(codigo)) {
            operacaoBloqueada = true;
            msg = "TIPO_EXAME_NAO_ENCONTRADO";
            return;
        }
        if (agendHistOuFut.contains(codigo)) {
            operacaoBloqueada = true;
            msg = "NAO_PODE_EXCLUIR_COM_AGENDAMENTOS";
            return;
        }
        repo.remove(codigo);
        codigosCadastrados.remove(codigo);
        agendHistOuFut.remove(codigo);
        agendFuturos.remove(codigo);
        excluido = true;
    }

    @When("a gerente {string} solicitar a inativação do tipo de exame com o código {string}")
    public void solicitarInativacao(String gerente, String codigo) {
        resetRemocaoInativacao();
        TipoExame t = repo.get(codigo);
        if (t == null) {
            operacaoBloqueada = true;
            msg = "TIPO_EXAME_NAO_ENCONTRADO";
            return;
        }
        if (agendFuturos.contains(codigo)) {
            operacaoBloqueada = true;
            msg = "NAO_PODE_INATIVAR_COM_AGENDAMENTOS_FUTUROS";
            return;
        }
        t.status = "Inativo";
        inativado = true;
    }

    // =========================
    // THEN — Asserções
    // =========================

    @Then("o sistema deve criar o tipo de exame")
    public void sistemaDeveCriarTipoExame() {
        // Executa o cadastro aqui
        executarCadastro();
        
        // E então valida
        assertTrue(criado, "Tipo de exame não foi criado quando deveria ter sido");
        assertFalse(cadastroBloqueado, "Cadastro foi bloqueado quando não deveria ter sido");
    }

    @Then("definir o status inicial como {string}")
    public void thenDefinirStatusInicial(String esperado) {
        // validado no cadastro; aqui apenas existir sucesso
        assertTrue(criado || alterado || inativado);
    }

    @Then("exibir confirmação de sucesso")
    public void exibirConfirmacaoSucesso() {
        assertNull(msg);
        assertTrue(criado || alterado || excluido || inativado);
    }

    @Then("o sistema deve bloquear o cadastro")
    public void sistemaDeveBloquearCadastro() {
        // Executa o cadastro e verifica se foi bloqueado
        executarCadastro();
        
        assertTrue(cadastroBloqueado, "Cadastro não foi bloqueado quando deveria ter sido");
        assertFalse(criado, "Tipo de exame foi criado quando não deveria ter sido");
    }

    @And("exibir mensagem informando que a descrição é obrigatória")
    public void msgDescricaoObrigatoria() {
        assertEquals("DESCRICAO_OBRIGATORIA", msg);
    }

    @And("exibir mensagem informando que a especialidade é obrigatória")
    public void msgEspecialidadeObrigatoria() {
        assertEquals("ESPECIALIDADE_OBRIGATORIA", msg);
    }

    @And("exibir mensagem informando que o valor é obrigatório")
    public void msgValorObrigatorio() {
        assertEquals("VALOR_OBRIGATORIO", msg);
    }

    @And("exibir mensagem informando que o valor deve ser maior ou igual a 0")
    public void msgValorMaiorOuIgualZero() {
        assertEquals("VALOR_DEVE_SER_MAIOR_OU_IGUAL_A_ZERO", msg);
    }

    @And("exibir mensagem informando que o valor deve ser maior que {int}")
    public void msgValorMaiorQue(Integer valor) {
        assertEquals("VALOR_DEVE_SER_MAIOR_OU_IGUAL_A_ZERO", msg);
    }

    @And("exibir mensagem informando que o status inicial deve ser {string}")
    public void msgStatusInicialDeveSer(String esperado) {
        assertEquals("STATUS_INICIAL_DEVE_SER_ATIVO", msg);
    }

    @And("exibir mensagem informando que o código deve ser único")
    public void msgCodigoUnico() {
        assertEquals("CODIGO_DEVE_SER_UNICO", msg);
    }

    @Then("o sistema deve salvar as alterações")
    public void sistemaDeveSalvarAlteracoes() {
        executarAtualizacao();
        assertTrue(alterado);
        assertFalse(atualizacaoBloqueada);
    }

    @Then("o sistema deve salvar a alteração do valor")
    public void sistemaDeveSalvarAlteracaoValor() {
        executarAtualizacao();
        assertTrue(alterado);
        assertFalse(atualizacaoBloqueada);
    }

    @Then("o sistema deve bloquear a alteração")
    public void sistemaDeveBloquearAlteracao() {
        executarAtualizacao();
        assertTrue(atualizacaoBloqueada);
        assertFalse(alterado);
    }

    @And("exibir mensagem informando que o código do tipo de exame não pode ser alterado")
    public void msgCodigoImutavel() {
        assertEquals("CODIGO_IMUTAVEL", msg);
    }

    @Then("o sistema deve excluir o tipo de exame")
    public void sistemaDeveExcluirTipoDeExame() {
        assertTrue(excluido);
        assertFalse(operacaoBloqueada);
    }

    @Then("o sistema deve inativar o tipo de exame")
    public void sistemaDeveInativarTipoDeExame() {
        assertTrue(inativado);
        assertFalse(operacaoBloqueada);
    }

    @Then("o sistema deve bloquear a operação")
    public void sistemaDeveBloquearOperacao() {
        assertTrue(operacaoBloqueada);
        assertFalse(excluido && inativado);
    }

    @Then("o sistema deve bloquear a exclusão")
    public void sistemaDeveBloquearExclusao() {
        assertTrue(operacaoBloqueada);
        assertFalse(excluido);
    }

    @Then("o sistema deve bloquear a inativação")
    public void sistemaDeveBloquearInativacao() {
        assertTrue(operacaoBloqueada);
        assertFalse(inativado);
    }

    @And("exibir mensagem informando que o tipo de exame não foi encontrado")
    public void msgTipoExameNaoEncontrado() {
        assertEquals("TIPO_EXAME_NAO_ENCONTRADO", msg);
    }

    @And("exibir mensagem informando que não é possível excluir tipos de exame com agendamentos vinculados")
    public void msgNaoPodeExcluirComAgendamentos() {
        assertEquals("NAO_PODE_EXCLUIR_COM_AGENDAMENTOS", msg);
    }

    @And("exibir mensagem informando que não é possível inativar tipos de exame com agendamentos vinculados")
    public void msgNaoPodeInativarComAgendamentos() {
        assertEquals("NAO_PODE_INATIVAR_COM_AGENDAMENTOS_FUTUROS", msg);
    }

    @And("exibir mensagem informando que não é possível alterar exames com agendamentos vinculados")
    public void msgNaoPodeAlterarComAgendamentosVinculados() {
        assertEquals("NAO_PODE_ATUALIZAR_COM_AGENDAMENTOS", msg);
    }

    // =========================
    // Implementações de negócio
    // =========================

    private void executarCadastro() {
        resetCadastro();

        // Validações na ordem correta:
        
        // 1. RN6 — código único
        if (codigosCadastrados.contains(codigo)) {
            cadastroBloqueado = true;
            msg = "CODIGO_DEVE_SER_UNICO";
            return;
        }

        // 2. RN5 — status inicial deve ser Ativo (se informado)
        if (statusInicial != null && "Inativo".equalsIgnoreCase(statusInicial)) {
            cadastroBloqueado = true;
            msg = "STATUS_INICIAL_DEVE_SER_ATIVO";
            return;
        }

        // 3. RN1 — descrição obrigatória
        if (descricao == null || descricao.isBlank()) {
            cadastroBloqueado = true;
            msg = "DESCRICAO_OBRIGATORIA";
            return;
        }

        // 4. RN2 — especialidade obrigatória
        if (especialidade == null || especialidade.isBlank()) {
            cadastroBloqueado = true;
            msg = "ESPECIALIDADE_OBRIGATORIA";
            return;
        }

        // 5. RN3 — valor obrigatório
        if (valorStr == null || valorStr.isBlank()) {
            cadastroBloqueado = true;
            msg = "VALOR_OBRIGATORIO";
            return;
        }

        // 6. RN4 — valor > 0
        Double valor = parseValor(valorStr);
        if (valor == null) {
            cadastroBloqueado = true;
            msg = "VALOR_OBRIGATORIO";
            return;
        }
        
        if (valor <= 0) {
            cadastroBloqueado = true;
            msg = "VALOR_DEVE_SER_MAIOR_OU_IGUAL_A_ZERO";
            return;
        }

        // sucesso
        TipoExame t = new TipoExame();
        t.codigo = codigo;
        t.descricao = descricao;
        t.especialidade = especialidade;
        t.valor = valor;
        t.status = "Ativo";
        repo.put(codigo, t);
        codigosCadastrados.add(codigo);
        criado = true;
        msg = null;
    }

    private void executarAtualizacao() {
        if (!updPedido) return; // nada a fazer

        TipoExame t = repo.get(updCodigoAlvo);
        if (t == null) {
            atualizacaoBloqueada = true;
            msg = "TIPO_EXAME_NAO_ENCONTRADO";
            return;
        }

        // RN3 (Atualização) — Não pode atualizar se houver agendamentos vinculados
        if (agendHistOuFut.contains(updCodigoAlvo)) {
            atualizacaoBloqueada = true;
            msg = "NAO_PODE_ATUALIZAR_COM_AGENDAMENTOS";
            return;
        }

        // RN1 (Atualização) — Código imutável
        if (tentativaNovoCodigo != null && !tentativaNovoCodigo.equals(updCodigoAlvo)) {
            atualizacaoBloqueada = true;
            msg = "CODIGO_IMUTAVEL";
            return;
        }

        // RN2 (Atualização) — Valor >= 0 (na atualização, 0 é permitido)
        if (updNovoValor != null && updNovoValor < 0) {
            atualizacaoBloqueada = true;
            msg = "VALOR_DEVE_SER_MAIOR_OU_IGUAL_A_ZERO";
            return;
        }

        // aplica mudanças
        if (updNovaDescricao != null) t.descricao = updNovaDescricao;
        if (updNovaEspecialidade != null) t.especialidade = updNovaEspecialidade;
        if (updNovoStatus != null) t.status = updNovoStatus;
        if (updNovoValor != null) t.valor = updNovoValor;

        repo.put(t.codigo, t);
        alterado = true;
        msg = null;
    }
}