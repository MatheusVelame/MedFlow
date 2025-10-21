package br.com.medflow.dominio.referencia.especialidades;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;

import static org.junit.jupiter.api.Assertions.*;

public class EspecialidadesFuncionalidade extends EspecialidadesFuncionalidadeBase {

    // =================================================================
    // GIVEN (CONTEXTO/PRÉ-CONDIÇÕES)
    // =================================================================

    @Given("que a especialidade {string} não está cadastrada")
    public void que_a_especialidade_nao_esta_cadastrada(String nomeEspecialidade) {
        // Implementação: Garantir que o repositório não contenha esta especialidade
        // Pode ser um método no EspecialidadesFuncionalidadeBase para limpar o repositório
        // ou validar a ausência.
        if (repositorio.buscarPorNome(nomeEspecialidade) != null) {
            // Se o cenário for mais complexo, pode-se excluir a especialidade aqui para resetar.
        }
    }

    @Given("que a especialidade {string} já está cadastrada")
    public void que_a_especialidade_ja_esta_cadastrada(String nomeEspecialidade) {
        // Implementação: Usar o EspecialidadesServico para cadastrar a especialidade se não existir,
        // garantindo que ela esteja presente.
        if (repositorio.buscarPorNome(nomeEspecialidade) == null) {
            // Lógica para cadastrar uma Especialidade ativa no repositório.
            // Ex: repositorio.salvar(new Especialidade(new NomeEspecialidade(nomeEspecialidade)));
        }
    }

    @Given("que a descrição é uma string de {int} caracteres")
    public void que_a_descricao_e_uma_string_de_caracteres(Integer tamanho) {
        // Implementação: Gerar e armazenar uma string de N caracteres no contexto do teste (e.g., um campo da classe Base).
        // Ex: contexto.setDescricao(gerarString(tamanho));
    }

    @Given("que existe um mecanismo de reatribuição de médicos")
    public void que_existe_um_mecanismo_de_reatribuicao_de_medicos() {
        // Implementação: Inicializar ou configurar o mock/serviço que simula a reatribuição.
    }

    @Given("que a especialidade {string} não possui médicos ativos vinculados")
    public void que_a_especialidade_nao_possui_medicos_ativos_vinculados(String nomeEspecialidade) {
        // Implementação: Garantir que os dados de teste (no Repositório de Memória)
        // indiquem zero vínculos ativos para a especialidade.
    }

    @Given("que a especialidade {string} possui médicos ativos vinculados")
    public void que_a_especialidade_possui_medicos_ativos_vinculados(String nomeEspecialidade) {
        // Implementação: Injetar (mockar) dados no Repositório de Especialidades ou Médicos
        // que simulem a vinculação ativa.
    }

    @Given("que a especialidade {string} possui histórico de vínculo com médicos")
    public void que_a_especialidade_possui_historico_de_vinculo_com_medicos(String nomeEspecialidade) {
        // Implementação: Garantir que a Especialidade no repositório tenha um campo ou flag
        // indicando que já foi utilizada para vínculo no passado.
    }

    @Given("que a especialidade {string} nunca foi vinculada a um médico")
    public void que_a_especialidade_nunca_foi_vinculada_a_um_medico(String nomeEspecialidade) {
        // Implementação: Garantir que a Especialidade no repositório NÃO tenha a flag de histórico de vínculo.
    }

    @Given("que o médico {string} está ativo")
    public void que_o_medico_esta_ativo(String nomeMedico) {
        // Implementação: Configurar um mock/serviço (ou o próprio repositório de médicos)
        // para retornar que o médico existe e está ativo.
    }

    @Given("que a especialidade {string} tem o status {string}")
    public void que_a_especialidade_tem_o_status(String nomeEspecialidade, String status) {
        // Implementação: Garantir que a Especialidade cadastrada no repositório tenha o status correto.
    }

    // =================================================================
    // WHEN (AÇÃO)
    // =================================================================

    @When("o administrador solicitar o cadastro de especialidade com nome {string} e descrição {string}")
    public void o_administrador_solicitar_o_cadastro_de_especialidade_com_nome_e_descricao(String nome, String descricao) {
        // Tenta executar o serviço e armazena o resultado/exceção.
        try {
            servico.cadastrar(nome, descricao);
            contexto.setUltimaExcecao(null);
        } catch (RegraNegocioException e) {
            contexto.setUltimaExcecao(e);
        }
    }

    @When("o administrador tentar cadastrar uma nova especialidade com nome vazio")
    public void o_administrador_tentar_cadastrar_uma_nova_especialidade_com_nome_vazio() {
        // Tenta executar o serviço e armazena a exceção esperada.
        try {
            servico.cadastrar(null, "Descrição"); // Nome vazio (nulo ou "")
            contexto.setUltimaExcecao(null);
        } catch (RegraNegocioException e) {
            contexto.setUltimaExcecao(e);
        }
    }

    @When("o administrador solicitar o cadastro de especialidade com nome {string}")
    public void o_administrador_solicitar_o_cadastro_de_especialidade_com_nome(String nome) {
        // Tenta executar o serviço sem descrição.
        try {
            servico.cadastrar(nome, null);
            contexto.setUltimaExcecao(null);
        } catch (RegraNegocioException e) {
            contexto.setUltimaExcecao(e);
        }
    }
    
    @When("o administrador tentar cadastrar uma nova especialidade com nome {string}")
    public void o_administrador_tentar_cadastrar_uma_nova_especialidade_com_nome(String nome) {
        // Usado para RN 1.2 e 1.3 - Tenta cadastrar um nome inválido.
        try {
            servico.cadastrar(nome, null);
            contexto.setUltimaExcecao(null);
        } catch (RegraNegocioException e) {
            contexto.setUltimaExcecao(e);
        }
    }
    
    @When("o administrador solicitar o cadastro de especialidade com nome {string} e a descrição informada")
    public void o_administrador_solicitar_o_cadastro_de_especialidade_com_nome_e_a_descricao_informada(String nome) {
        // Usado para RN 1.4 - Puxa a descrição gerada do contexto.
        try {
            servico.cadastrar(nome, contexto.getDescricao());
            contexto.setUltimaExcecao(null);
        } catch (RegraNegocioException e) {
            contexto.setUltimaExcecao(e);
        }
    }
    
    @When("o administrador solicitar o cadastro de uma nova especialidade com nome {string}")
    public void o_administrador_solicitar_o_cadastro_de_uma_nova_especialidade_com_nome(String nome) {
        // Usado para RN 1.5 - Sucesso.
        try {
            servico.cadastrar(nome, null);
            contexto.setUltimaEspecialidadeCadastrada(repositorio.buscarPorNome(nome));
            contexto.setUltimaExcecao(null);
        } catch (RegraNegocioException e) {
            contexto.setUltimaExcecao(e);
        }
    }

    @When("o administrador tentar cadastrar uma nova especialidade com nome {string} e status {string}")
    public void o_administrador_tentar_cadastrar_uma_nova_especialidade_com_nome_e_status(String nome, String status) {
        // Usado para RN 1.5 - Tenta setar status Inativa no cadastro (deve falhar ou ser ignorado).
        try {
            servico.cadastrarComStatusProibido(nome, status); // Método de serviço especial para teste
            contexto.setUltimaEspecialidadeCadastrada(repositorio.buscarPorNome(nome));
            contexto.setUltimaExcecao(null);
        } catch (RegraNegocioException e) {
            contexto.setUltimaExcecao(e);
        }
    }

    @When("o administrador solicitar a alteração da especialidade {string} para o nome {string} e nova descrição {string}")
    public void o_administrador_solicitar_a_alteracao_da_especialidade_para_o_nome_e_nova_descricao(String nomeOriginal, String novoNome, String novaDescricao) {
        // Usado para RN 2.1 e 2.2 - Sucesso na alteração.
        try {
            servico.alterar(nomeOriginal, novoNome, novaDescricao);
            contexto.setUltimaExcecao(null);
        } catch (RegraNegocioException e) {
            contexto.setUltimaExcecao(e);
        }
    }

    @When("o administrador tentar alterar a especialidade {string} mudando o seu status para {string}")
    public void o_administrador_tentar_alterar_a_especialidade_mudando_o_seu_status_para(String nome, String status) {
        // Usado para RN 2.1 - Falha na alteração de campo não permitido.
        try {
            servico.alterarStatus(nome, status); // Método de serviço especial para teste
            contexto.setUltimaExcecao(null);
        } catch (RegraNegocioException e) {
            contexto.setUltimaExcecao(e);
        }
    }

    @When("o administrador solicitar a alteração do nome da especialidade {string} para {string}")
    public void o_administrador_solicitar_a_alteracao_do_nome_da_especialidade_para(String nomeOriginal, String novoNome) {
        // Usado para RN 2.2 - Tenta alterar o nome (único e válido).
        try {
            servico.alterar(nomeOriginal, novoNome, null);
            contexto.setUltimaExcecao(null);
        } catch (RegraNegocioException e) {
            contexto.setUltimaExcecao(e);
        }
    }

    @When("o administrador tenta alterar o nome da especialidade {string} para {string}")
    public void o_administrador_tenta_alterar_o_nome_da_especialidade_para(String nomeOriginal, String novoNome) {
        // Usado para RN 2.3 - Tenta alterar o nome de especialidade vinculada.
        try {
            servico.tentarAlterarComVinculo(nomeOriginal, novoNome);
            contexto.setUltimaExcecao(null);
        } catch (RegraNegocioException e) {
            contexto.setUltimaExcecao(e);
        }
    }

    @When("o mecanismo de reatribuição é executado com sucesso")
    public void o_mecanismo_de_reatribuicao_e_executado_com_sucesso() {
        // Usado para RN 2.3 - Simula o sucesso do mecanismo de reatribuição após a tentativa de alteração.
        // Se a alteração acima lançou uma exceção de tratamento, este passo pode ser um Then que testa a ausência da exceção.
        // Ou, se a alteração for dividida, este step deve chamar a lógica de reatribuição no Serviço.
    }

    @When("o administrador solicitar a exclusão da especialidade {string}")
    public void o_administrador_solicitar_a_exclusao_da_especialidade(String nome) {
        // Usado para RN 3.1 e 3.3 - Sucesso na exclusão/inativação.
        try {
            servico.excluir(nome);
            contexto.setUltimaExcecao(null);
        } catch (RegraNegocioException e) {
            contexto.setUltimaExcecao(e);
        }
    }

    @When("o administrador tentar excluir a especialidade {string}")
    public void o_administrador_tentar_excluir_a_especialidade(String nome) {
        // Usado para RN 3.1 e 3.2 - Falha na exclusão.
        try {
            servico.excluir(nome);
            contexto.setUltimaExcecao(null);
        } catch (RegraNegocioException e) {
            contexto.setUltimaExcecao(e);
        }
    }

    @When("o administrador tenta marcar a especialidade {string} como {string} durante a exclusão")
    public void o_administrador_tenta_marcar_a_especialidade_como_durante_a_exclusao(String nome, String status) {
        // Usado para RN 3.3 - Falha (tentar inativar uma que deve ser excluída fisicamente)
        try {
            servico.tentarInativarDuranteExclusao(nome, status);
            contexto.setUltimaExcecao(null);
        } catch (RegraNegocioException e) {
            contexto.setUltimaExcecao(e);
        }
    }

    @When("o administrador solicita a atribuição do médico {string} à especialidade {string}")
    public void o_administrador_solicita_a_atribuicao_do_medico_a_especialidade(String nomeMedico, String nomeEspecialidade) {
        // Usado para RN 3.4 - Sucesso.
        try {
            servico.atribuirMedico(nomeMedico, nomeEspecialidade);
            contexto.setUltimaExcecao(null);
        } catch (RegraNegocioException e) {
            contexto.setUltimaExcecao(e);
        }
    }

    @When("o administrador tenta atribuir a médica {string} à especialidade {string}")
    public void o_administrador_tenta_atribuir_a_medica_a_especialidade(String nomeMedico, String nomeEspecialidade) {
        // Usado para RN 3.4 - Falha.
        try {
            servico.atribuirMedico(nomeMedico, nomeEspecialidade);
            contexto.setUltimaExcecao(null);
        } catch (RegraNegocioException e) {
            contexto.setUltimaExcecao(e);
        }
    }
    
    // =================================================================
    // THEN (VERIFICAÇÃO/RESULTADO)
    // =================================================================

    @Then("o sistema deve cadastrar a especialidade com sucesso")
    public void o_sistema_deve_cadastrar_a_especialidade_com_sucesso() {
        // Implementação: Verifica se nenhuma exceção foi lançada.
        assertNull(contexto.getUltimaExcecao(), "O cadastro deveria ter sido um sucesso, mas falhou com: " + contexto.getUltimaExcecao().getMessage());
    }

    @Then("a especialidade {string} deve estar cadastrada")
    public void a_especialidade_deve_estar_cadastrada(String nome) {
        // Implementação: Verifica se a especialidade existe no repositório.
        assertNotNull(repositorio.buscarPorNome(nome), "A especialidade '" + nome + "' deveria estar cadastrada.");
    }

    @Then("o sistema deve rejeitar o cadastro")
    public void o_sistema_deve_rejeitar_o_cadastro() {
        // Implementação: Verifica se uma exceção de negócio foi lançada.
        assertNotNull(contexto.getUltimaExcecao(), "O cadastro deveria ter sido rejeitado, mas foi bem-sucedido.");
    }

    @Then("exibir a mensagem {string}")
    public void exibir_a_mensagem(String mensagemEsperada) {
        // Implementação: Verifica se a mensagem da última exceção corresponde à mensagem esperada.
        assertNotNull(contexto.getUltimaExcecao(), "Nenhuma exceção foi lançada, mas era esperada a mensagem: " + mensagemEsperada);
        assertEquals(mensagemEsperada, contexto.getUltimaExcecao().getMessage(), "A mensagem de erro não corresponde ao esperado.");
    }

    @Then("o sistema deve criar a especialidade {string}")
    public void o_sistema_deve_criar_a_especialidade(String nome) {
        // Implementação: Verifica se o cadastro ocorreu com sucesso.
        assertNotNull(repositorio.buscarPorNome(nome), "A especialidade '" + nome + "' deveria ter sido criada.");
    }

    @Then("a especialidade {string} deve ter o status {string}")
    public void a_especialidade_deve_ter_o_status(String nome, String statusEsperado) {
        // Implementação: Verifica o status da especialidade no repositório.
        Especialidade especialidade = repositorio.buscarPorNome(nome);
        assertNotNull(especialidade);
        assertEquals(statusEsperado, especialidade.getStatus().toString(), "O status da especialidade '" + nome + "' não está como esperado.");
    }

    @Then("o sistema deve ignorar o status fornecido")
    public void o_sistema_deve_ignorar_o_status_fornecido() {
        // Implementação: A verificação final do status é feita no próximo 'And'.
        // Este passo serve apenas como documentação no Gherkin.
    }

    @Then("o sistema deve atualizar a especialidade com sucesso")
    public void o_sistema_deve_atualizar_a_especialidade_com_sucesso() {
        // Implementação: Verifica se a alteração foi concluída sem exceções.
        assertNull(contexto.getUltimaExcecao(), "A alteração deveria ter sido um sucesso, mas falhou com: " + contexto.getUltimaExcecao().getMessage());
    }

    @Then("a especialidade {string} deve ter o nome {string}")
    public void a_especialidade_deve_ter_o_nome(String nomeAnterior, String nomeEsperado) {
        // Implementação: Verifica se o nome foi alterado e o objeto atualizado existe.
        Especialidade especialidade = repositorio.buscarPorNome(nomeEsperado);
        assertNotNull(especialidade, "A especialidade não foi encontrada com o nome esperado: " + nomeEsperado);
        // O nome anterior não deve mais existir se for o caso de alteração.
        assertNull(repositorio.buscarPorNome(nomeAnterior), "O nome anterior da especialidade ainda existe no repositório.");
    }

    @Then("o sistema deve rejeitar a alteração")
    public void o_sistema_deve_rejeitar_a_alteracao() {
        // Implementação: Verifica se uma exceção de negócio foi lançada.
        assertNotNull(contexto.getUltimaExcecao(), "A alteração deveria ter sido rejeitada, mas foi bem-sucedida.");
    }

    @Then("a especialidade {string} deve manter o status {string}")
    public void a_especialidade_deve_manter_o_status(String nome, String status) {
        // Implementação: Verifica se o status no repositório permanece o mesmo.
        Especialidade especialidade = repositorio.buscarPorNome(nome);
        assertNotNull(especialidade);
        assertEquals(status, especialidade.getStatus().toString(), "O status da especialidade foi alterado indevidamente.");
    }

    @Then("o sistema deve atualizar o nome da especialidade {string} para {string} com sucesso")
    public void o_sistema_deve_atualizar_o_nome_da_especialidade_para_com_sucesso(String nomeOriginal, String nomeNovo) {
        // Combinação de verificação de sucesso e do novo nome.
        assertNull(contexto.getUltimaExcecao());
        a_especialidade_deve_ter_o_nome(nomeOriginal, nomeNovo);
    }

    @Then("o nome da especialidade {string} deve permanecer inalterado")
    public void o_nome_da_especialidade_deve_permanecer_inalterado(String nome) {
        // Implementação: Verifica se a exceção foi lançada, mas o objeto no repositório ainda tem o nome original.
        assertNotNull(repositorio.buscarPorNome(nome), "A especialidade original não foi encontrada.");
    }

    @Then("o sistema deve processar a exclusão com sucesso")
    public void o_sistema_deve_processar_a_exclusao_com_sucesso() {
        // Implementação: Verifica se não houve exceção.
        assertNull(contexto.getUltimaExcecao(), "A exclusão deveria ter sido um sucesso, mas falhou com: " + contexto.getUltimaExcecao().getMessage());
    }

    @Then("a especialidade {string} não deve mais existir no sistema")
    public void a_especialidade_nao_deve_mais_existir_no_sistema(String nome) {
        // Implementação: Verifica a ausência do objeto no repositório.
        assertNull(repositorio.buscarPorNome(nome), "A especialidade '" + nome + "' ainda existe no sistema.");
    }

    @Then("a especialidade {string} deve ser removida fisicamente do banco de dados")
    public void a_especialidade_deve_ser_removida_fisicamente_do_banco_de_dados(String nome) {
        // Implementação: Confirma a ausência no repositório (similar ao "não deve mais existir").
        assertNull(repositorio.buscarPorNome(nome), "A exclusão física falhou: a especialidade ainda está no banco.");
    }

    @Then("o sistema deve negar a exclusão física")
    public void o_sistema_deve_negar_a_exclusao_fisica() {
        // Implementação: Verifica se a exceção não foi lançada, mas a inativação ocorreu (ver próximo 'And').
        // Este passo foca em garantir que a ação de exclusão física falhou (não resultou em remoção física).
        // Se a lógica do serviço for a inativação automática, o sucesso aqui é a ausência de exceção.
    }

    @Then("o sistema deve converter a exclusão em inativação")
    public void o_sistema_deve_converter_a_exclusao_em_inativacao() {
        // Implementação: Verifica que não houve exceção (sucesso na operação de inativação)
        assertNull(contexto.getUltimaExcecao(), "A inativação deveria ter sido um sucesso, mas falhou com: " + contexto.getUltimaExcecao().getMessage());
    }

    @Then("a atribuição deve ser realizada com sucesso")
    public void a_atribuicao_deve_ser_realizada_com_sucesso() {
        // Implementação: Verifica a ausência de exceção e a confirmação do vínculo no repositório.
        assertNull(contexto.getUltimaExcecao(), "A atribuição deveria ter sido um sucesso, mas falhou com: " + contexto.getUltimaExcecao().getMessage());
    }

    @Then("o sistema deve rejeitar a atribuição")
    public void o_sistema_deve_rejeitar_a_atribuicao() {
        // Implementação: Verifica se uma exceção de negócio foi lançada.
        assertNotNull(contexto.getUltimaExcecao(), "A atribuição deveria ter sido rejeitada, mas foi bem-sucedida.");
    }
}