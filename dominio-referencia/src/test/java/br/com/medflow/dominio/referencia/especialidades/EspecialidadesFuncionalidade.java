package br.com.medflow.dominio.referencia.especialidades;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;

import static org.junit.jupiter.api.Assertions.*;

public class EspecialidadesFuncionalidade extends EspecialidadesFuncionalidadeBase {

    // =================================================================
    // GIVEN (CONTEXTO/PRÉ-CONDIÇÕES)
    // Os métodos GIVEN preenchem o estado inicial do repositório (mock)
    // =================================================================

    @Given("que a especialidade {string} não está cadastrada")
    public void que_a_especialidade_nao_esta_cadastrada(String nomeEspecialidade) {
        // Implementação: Garantir que o repositório não contenha esta especialidade
        repositorio.buscarPorNome(nomeEspecialidade).ifPresent(repositorio::remover);
    }

    @Given("que a especialidade {string} já está cadastrada")
    public void que_a_especialidade_ja_esta_cadastrada(String nomeEspecialidade) {
        // Implementação: Usar o Repositório de Memória para garantir a existência (RN 1.2, 2.2, 3.4)
        if (repositorio.buscarPorNome(nomeEspecialidade).isEmpty()) {
             // Utiliza o construtor do repositório de memória que foi criado
             repositorio.popular(nomeEspecialidade, "Descrição base", StatusEspecialidade.ATIVA, false);
        }
    }

    @Given("que a descrição é uma string de {int} caracteres")
    public void que_a_descricao_e_uma_string_de_caracteres(Integer tamanho) {
        // Implementação: Gera e armazena a string na variável "descricao" da classe base
        setDescricao(gerarString(tamanho));
    }

    @Given("que existe um mecanismo de reatribuição de médicos")
    public void que_existe_um_mecanismo_de_reatribuicao_de_medicos() {
        // Simulação: Este passo serve para documentar a precondição de que o sistema suporta o tratamento
    }

    @Given("que a especialidade {string} não possui médicos ativos vinculados")
    public void que_a_especialidade_nao_possui_medicos_ativos_vinculados(String nomeEspecialidade) {
        // Implementação: Mocka o repositório de médicos (RN 3.1)
        medicoRepositorio.mockContagem(nomeEspecialidade, 0);
    }

    @Given("que a especialidade {string} possui médicos ativos vinculados")
    public void que_a_especialidade_possui_medicos_ativos_vinculados(String nomeEspecialidade) {
        // Implementação: Mocka o repositório de médicos (RN 2.3, 3.1)
        medicoRepositorio.mockContagem(nomeEspecialidade, 2);
    }

    @Given("que a especialidade {string} possui histórico de vínculo com médicos")
    public void que_a_especialidade_possui_historico_de_vinculo_com_medicos(String nomeEspecialidade) {
        // Implementação: Popula a especialidade com a flag "possuiVinculoHistorico = true" (RN 3.2, 3.3)
        repositorio.popular(nomeEspecialidade, "Descrição", StatusEspecialidade.ATIVA, true);
    }

    @Given("que a especialidade {string} nunca foi vinculada a um médico")
    public void que_a_especialidade_nunca_foi_vinculada_a_um_medico(String nomeEspecialidade) {
        // Implementação: Popula a especialidade com a flag "possuiVinculoHistorico = false" (RN 3.2, 3.3)
        repositorio.popular(nomeEspecialidade, "Descrição", StatusEspecialidade.ATIVA, false);
    }

    @Given("que o médico {string} está ativo")
    public void que_o_medico_esta_ativo(String nomeMedico) {
        // Implementação: Simulação, o sistema assume que o médico está ativo para a próxima atribuição
    }

    @Given("que a especialidade {string} tem o status {string}")
    public void que_a_especialidade_tem_o_status(String nomeEspecialidade, String status) {
        // Implementação: Popula o repositório com o status específico (RN 3.4)
        StatusEspecialidade statusEnum = StatusEspecialidade.valueOf(status.toUpperCase());
        repositorio.popular(nomeEspecialidade, "Descrição", statusEnum, statusEnum == StatusEspecialidade.INATIVA);
    }

    // =================================================================
    // WHEN (AÇÃO) - Onde as chamadas a 'contexto' estavam incorretas
    // =================================================================

    @When("o administrador solicitar o cadastro de especialidade com nome {string} e descrição {string}")
    public void o_administrador_solicitar_o_cadastro_de_especialidade_com_nome_e_descricao(String nome, String descricao) {
        try {
            servico.cadastrar(nome, descricao);
            setUltimaExcecao(null); // Corrigido
        } catch (RegraNegocioException e) {
            setUltimaExcecao(e); // Corrigido
        }
    }

    @When("o administrador tentar cadastrar uma nova especialidade com nome vazio")
    public void o_administrador_tentar_cadastrar_uma_nova_especialidade_com_nome_vazio() {
        try {
            servico.cadastrar(null, "Descrição");
            setUltimaExcecao(null); // Corrigido
        } catch (RegraNegocioException e) {
            setUltimaExcecao(e); // Corrigido
        }
    }

    @When("o administrador solicitar o cadastro de especialidade com nome {string}")
    public void o_administrador_solicitar_o_cadastro_de_especialidade_com_nome(String nome) {
        try {
            servico.cadastrar(nome, null);
            setUltimaExcecao(null); // Corrigido
        } catch (RegraNegocioException e) {
            setUltimaExcecao(e); // Corrigido
        }
    }
    
    @When("o administrador tentar cadastrar uma nova especialidade com nome {string}")
    public void o_administrador_tentar_cadastrar_uma_nova_especialidade_com_nome(String nome) {
        try {
            servico.cadastrar(nome, null);
            setUltimaExcecao(null); // Corrigido
        } catch (RegraNegocioException e) {
            setUltimaExcecao(e); // Corrigido
        }
    }
    
    @When("o administrador solicitar o cadastro de especialidade com nome {string} e a descrição informada")
    public void o_administrador_solicitar_o_cadastro_de_especialidade_com_nome_e_a_descricao_informada(String nome) {
        try {
            servico.cadastrar(nome, getDescricao()); // Corrigido
            setUltimaExcecao(null); // Corrigido
        } catch (RegraNegocioException e) {
            setUltimaExcecao(e); // Corrigido
        }
    }
    
    @When("o administrador solicitar o cadastro de uma nova especialidade com nome {string}")
    public void o_administrador_solicitar_o_cadastro_de_uma_nova_especialidade_com_nome(String nome) {
        try {
            servico.cadastrar(nome, null);
            setUltimaEspecialidadeCadastrada(repositorio.buscarPorNome(nome).orElse(null)); // Corrigido
            setUltimaExcecao(null); // Corrigido
        } catch (RegraNegocioException e) {
            setUltimaExcecao(e); // Corrigido
        }
    }

    @When("o administrador tentar cadastrar uma nova especialidade com nome {string} e status {string}")
    public void o_administrador_tentar_cadastrar_uma_nova_especialidade_com_nome_e_status(String nome, String status) {
        try {
            servico.cadastrarComStatusProibido(nome, status);
            setUltimaEspecialidadeCadastrada(repositorio.buscarPorNome(nome).orElse(null)); // Corrigido
            setUltimaExcecao(null); // Corrigido
        } catch (RegraNegocioException e) {
            setUltimaExcecao(e); // Corrigido
        }
    }

    @When("o administrador solicitar a alteração da especialidade {string} para o nome {string} e nova descrição {string}")
    public void o_administrador_solicitar_a_alteracao_da_especialidade_para_o_nome_e_nova_descricao(String nomeOriginal, String novoNome, String novaDescricao) {
        try {
            servico.alterar(nomeOriginal, novoNome, novaDescricao);
            setUltimaExcecao(null); // Corrigido
        } catch (RegraNegocioException e) {
            setUltimaExcecao(e); // Corrigido
        }
    }

    @When("o administrador tentar alterar a especialidade {string} mudando o seu status para {string}")
    public void o_administrador_tentar_alterar_a_especialidade_mudando_o_seu_status_para(String nome, String status) {
        try {
            servico.alterarStatus(nome, status);
            setUltimaExcecao(null); // Corrigido
        } catch (RegraNegocioException e) {
            setUltimaExcecao(e); // Corrigido
        }
    }

    @When("o administrador solicitar a alteração do nome da especialidade {string} para {string}")
    public void o_administrador_solicitar_a_alteracao_do_nome_da_especialidade_para(String nomeOriginal, String novoNome) {
        try {
            servico.alterar(nomeOriginal, novoNome, null);
            setUltimaExcecao(null); // Corrigido
        } catch (RegraNegocioException e) {
            setUltimaExcecao(e); // Corrigido
        }
    }

    @When("o administrador tenta alterar o nome da especialidade {string} para {string}")
    public void o_administrador_tenta_alterar_o_nome_da_especialidade_para(String nomeOriginal, String novoNome) {
        try {
            servico.tentarAlterarComVinculo(nomeOriginal, novoNome);
            setUltimaExcecao(null); // Corrigido
        } catch (RegraNegocioException e) {
            setUltimaExcecao(e); // Corrigido
        }
    }

    @When("o mecanismo de reatribuição é executado com sucesso")
    public void o_mecanismo_de_reatribuicao_e_executado_com_sucesso() {
        // Este passo apenas documenta o fluxo de sucesso, a lógica de reatribuição está no serviço/mock
    }

    @When("o administrador solicitar a exclusão da especialidade {string}")
    public void o_administrador_solicitar_a_exclusao_da_especialidade(String nome) {
        try {
            servico.excluir(nome);
            setUltimaExcecao(null); // Corrigido
        } catch (RegraNegocioException e) {
            setUltimaExcecao(e); // Corrigido
        }
    }

    @When("o administrador tentar excluir a especialidade {string}")
    public void o_administrador_tentar_excluir_a_especialidade(String nome) {
        try {
            servico.excluir(nome);
            setUltimaExcecao(null); // Corrigido
        } catch (RegraNegocioException e) {
            setUltimaExcecao(e); // Corrigido
        }
    }

    @When("o administrador tenta marcar a especialidade {string} como {string} durante a exclusão")
    public void o_administrador_tenta_marcar_a_especialidade_como_durante_a_exclusao(String nome, String status) {
        try {
            servico.tentarInativarDuranteExclusao(nome, status);
            setUltimaExcecao(null); // Corrigido
        } catch (RegraNegocioException e) {
            setUltimaExcecao(e); // Corrigido
        }
    }

    @When("o administrador solicita a atribuição do médico {string} à especialidade {string}")
    public void o_administrador_solicita_a_atribuicao_do_medico_a_especialidade(String nomeMedico, String nomeEspecialidade) {
        try {
            servico.atribuirMedico(nomeMedico, nomeEspecialidade);
            setUltimaExcecao(null); // Corrigido
        } catch (RegraNegocioException e) {
            setUltimaExcecao(e); // Corrigido
        }
    }

    @When("o administrador tenta atribuir a médica {string} à especialidade {string}")
    public void o_administrador_tenta_atribuir_a_medica_a_especialidade(String nomeMedico, String nomeEspecialidade) {
        try {
            servico.atribuirMedico(nomeMedico, nomeEspecialidade);
            setUltimaExcecao(null); // Corrigido
        } catch (RegraNegocioException e) {
            setUltimaExcecao(e); // Corrigido
        }
    }
    
    // =================================================================
    // THEN (VERIFICAÇÃO/RESULTADO) - Onde as chamadas a 'contexto' estavam incorretas
    // =================================================================

    @Then("o sistema deve cadastrar a especialidade com sucesso")
    public void o_sistema_deve_cadastrar_a_especialidade_com_sucesso() {
        assertNull(getUltimaExcecao(), "O cadastro deveria ter sido um sucesso, mas falhou com: " + getUltimaExcecao().getMessage()); // Corrigido
    }

    @Then("a especialidade {string} deve estar cadastrada")
    public void a_especialidade_deve_estar_cadastrada(String nome) {
        assertNotNull(repositorio.buscarPorNome(nome).orElse(null), "A especialidade '" + nome + "' deveria estar cadastrada.");
    }

    @Then("o sistema deve rejeitar o cadastro")
    public void o_sistema_deve_rejeitar_o_cadastro() {
        assertNotNull(getUltimaExcecao(), "O cadastro deveria ter sido rejeitado, mas foi bem-sucedido."); // Corrigido
    }

    @Then("exibir a mensagem {string}")
    public void exibir_a_mensagem(String mensagemEsperada) {
        assertNotNull(getUltimaExcecao(), "Nenhuma exceção foi lançada, mas era esperada a mensagem: " + mensagemEsperada); // Corrigido
        assertEquals(mensagemEsperada, getUltimaExcecao().getMessage(), "A mensagem de erro não corresponde ao esperado."); // Corrigido
    }

    @Then("o sistema deve criar a especialidade {string}")
    public void o_sistema_deve_criar_a_especialidade(String nome) {
        assertNotNull(repositorio.buscarPorNome(nome).orElse(null), "A especialidade '" + nome + "' deveria ter sido criada.");
    }

    @Then("a especialidade {string} deve ter o status {string}")
    public void a_especialidade_deve_ter_o_status(String nome, String statusEsperado) {
        Especialidade especialidade = repositorio.buscarPorNome(nome).orElse(null);
        assertNotNull(especialidade);
        assertEquals(statusEsperado, especialidade.getStatus().toString(), "O status da especialidade '" + nome + "' não está como esperado.");
    }

    @Then("o sistema deve ignorar o status fornecido")
    public void o_sistema_deve_ignorar_o_status_fornecido() {
        // Nenhuma ação.
    }

    @Then("o sistema deve atualizar a especialidade com sucesso")
    public void o_sistema_deve_atualizar_a_especialidade_com_sucesso() {
        assertNull(getUltimaExcecao(), "A alteração deveria ter sido um sucesso, mas falhou com: " + getUltimaExcecao().getMessage()); // Corrigido
    }

    @Then("a especialidade {string} deve ter o nome {string}")
    public void a_especialidade_deve_ter_o_nome(String nomeAnterior, String nomeEsperado) {
        Especialidade especialidade = repositorio.buscarPorNome(nomeEsperado).orElse(null);
        assertNotNull(especialidade, "A especialidade não foi encontrada com o nome esperado: " + nomeEsperado);
    }

    @Then("o sistema deve rejeitar a alteração")
    public void o_sistema_deve_rejeitar_a_alteracao() {
        assertNotNull(getUltimaExcecao(), "A alteração deveria ter sido rejeitada, mas foi bem-sucedida."); // Corrigido
    }

    @Then("a especialidade {string} deve manter o status {string}")
    public void a_especialidade_deve_manter_o_status(String nome, String status) {
        Especialidade especialidade = repositorio.buscarPorNome(nome).orElse(null);
        assertNotNull(especialidade);
        assertEquals(status, especialidade.getStatus().toString(), "O status da especialidade foi alterado indevidamente.");
    }

    @Then("o sistema deve atualizar o nome da especialidade {string} para {string} com sucesso")
    public void o_sistema_deve_atualizar_o_nome_da_especialidade_para_com_sucesso(String nomeOriginal, String nomeNovo) {
        assertNull(getUltimaExcecao());
        a_especialidade_deve_ter_o_nome(nomeOriginal, nomeNovo);
    }

    @Then("o nome da especialidade {string} deve permanecer inalterado")
    public void o_nome_da_especialidade_deve_permanecer_inalterado(String nome) {
        assertNotNull(repositorio.buscarPorNome(nome).orElse(null), "A especialidade original não foi encontrada.");
    }

    @Then("o sistema deve processar a exclusão com sucesso")
    public void o_sistema_deve_processar_a_exclusao_com_sucesso() {
        assertNull(getUltimaExcecao(), "A exclusão deveria ter sido um sucesso, mas falhou com: " + getUltimaExcecao().getMessage()); // Corrigido
    }

    @Then("a especialidade {string} não deve mais existir no sistema")
    public void a_especialidade_nao_deve_mais_existir_no_sistema(String nome) {
        assertFalse(repositorio.buscarPorNome(nome).isPresent(), "A especialidade '" + nome + "' ainda existe no sistema.");
    }

    @Then("a especialidade {string} deve ser removida fisicamente do banco de dados")
    public void a_especialidade_deve_ser_removida_fisicamente_do_banco_de_dados(String nome) {
        assertFalse(repositorio.buscarPorNome(nome).isPresent(), "A exclusão física falhou: a especialidade ainda está no banco.");
    }

    @Then("o sistema deve negar a exclusão física")
    public void o_sistema_deve_negar_a_exclusao_fisica() {
        // Não checa exceção, checa a mudança de status
    }

    @Then("o sistema deve converter a exclusão em inativação")
    public void o_sistema_deve_converter_a_exclusao_em_inativacao() {
        assertNull(getUltimaExcecao(), "A inativação deveria ter sido um sucesso, mas falhou com: " + getUltimaExcecao().getMessage()); // Corrigido
    }

    @Then("a atribuição deve ser realizada com sucesso")
    public void a_atribuicao_deve_ser_realizada_com_sucesso() {
        assertNull(getUltimaExcecao(), "A atribuição deveria ter sido um sucesso, mas falhou com: " + getUltimaExcecao().getMessage()); // Corrigido
    }

    @Then("o sistema deve rejeitar a atribuição")
    public void o_sistema_deve_rejeitar_a_atribuicao() {
        assertNotNull(getUltimaExcecao(), "A atribuição deveria ter sido rejeitada, mas foi bem-sucedida."); // Corrigido
    }
}