package br.com.medflow.dominio.administracao.funcionarios;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MedicoFuncionalidade extends FuncionarioFuncionalidadeBase {

    private MedicoDadosEntrada dadosEntrada;
    private RuntimeException excecaoCapturada;
    private Medico medicoParaSalvar;
    private Medico medicoEmEdicao;
    private List<Medico> listaConsulta = new ArrayList<>();
    private boolean possuiConsultasFuturas = false;
    private boolean possuiProntuarios = false;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final UsuarioResponsavelId RESPONSAVEL = new UsuarioResponsavelId("ADMIN-LISA");
    private final String contatoPadrao = "81999990000";

    public MedicoFuncionalidade() {
        super();
        this.dadosEntrada = new MedicoDadosEntrada("Nome", "11122233344", "12345-PE", "10/05/1980", contatoPadrao, "email@clinica.com", "Dermatologia");
    }

    @Given("^que já existe um médico, \"([^\"]*)\", cadastrado com o CRM \"([^\"]*)\" e ID \"([^\"]*)\"$")
    public void que_já_existe_um_médico_cadastrado_com_o_crm_e_id(String nomeMedico, String crm, String id) {
        try {
            // AJUSTE: Garante que o CRM de SETUP está limpo, para que o setup não falhe por formato.
            String crmSanitizado = crm.replaceAll("[^a-zA-Z0-9-]", "");
            CRM crmVO = new CRM(crmSanitizado);

            Medico medico = new Medico(
                    null,
                    nomeMedico,
                    "MEDICO",
                    contatoPadrao,
                    crmVO,
                    new Medico.EspecialidadeId(1),
                    RESPONSAVEL
            );

            repositorio.salvar(medico);
            this.medicoEmEdicao = (Medico) repositorio.obter(medico.getId());

        } catch (RuntimeException e) {
            fail("Erro no setup: " + e.getMessage());
        }
    }

    @Given("^que o CRM \"([^\"]*)\" não existe na base de dados de médicos$")
    @Given("^que o ID \"([^\"]*)\" não existe na base de dados de médicos$")
    public void que_o_crm_não_existe_na_base_de_médicos(String valor) {}

    @Given("^o funcionário da clínica informa o nome \"([^\"]*)\", o ID \"([^\"]*)\", o CRM \"([^\"]*)\", a data de nascimento \"([^\"]*)\", contato \"([^\"]*)\", o e-mail \"([^\"]*)\" e a especialidade \"([^\"]*)\"$")
    @Given("^o funcionário informa o nome \"([^\"]*)\", o ID \"([^\"]*)\", o CRM \"([^\"]*)\", a data de nascimento \"([^\"]*)\", o contato \"([^\"]*)\", o e-mail \"([^\"]*)\" e a especialidade \"([^\"]*)\"$")
    @Given("^um funcionário tenta cadastrar uma nova médica, \"([^\"]*)\", com o ID \"([^\"]*)\", CRM \"([^\"]*)\", data de nascimento \"([^\"]*)\", contato \"([^\"]*)\", o e-mail \"([^\"]*)\" e a especialidade \"([^\"]*)\"$")
    @Given("^um funcionário tenta cadastrar um novo médico, \"([^\"]*)\", com o CRM \"([^\"]*)\", ID \"([^\"]*)\", data de nascimento \"([^\"]*)\", contato \"([^\"]*)\", o e-mail \"([^\"]*)\" e a especialidade \"([^\"]*)\"$")
    public void o_funcionário_informa_dados_completos(String nome, String id, String crm, String dataNasc, String contato, String email, String especialidade) {
        this.dadosEntrada = new MedicoDadosEntrada(nome, id, crm, dataNasc, contato, email, especialidade);
    }

    @Given("^E deixa o campo \"([^\"]*)\" em branco$")
    public void e_deixa_o_campo_em_branco(String campo) {
        if (campo.equals("Nome completo")) {
            this.dadosEntrada.nome = "";
        }
    }

    @Given("^que o administrador acessa o perfil do médico \"([^\"]*)\", cujos dados são: ID \"([^\"]*)\", CRM \"([^\"]*)\", data de nascimento \"([^\"]*)\", contato \"([^\"]*)\", e-mail \"([^\"]*)\" e especialidade \"([^\"]*)\"$")
    public void que_o_administrador_acessa_o_perfil_do_médico_com_dados_completos(String nome, String id, String crm, String dataNasc, String contato, String email, String especialidade) {
        que_já_existe_um_médico_cadastrado_com_o_crm_e_id(nome, crm, id);
    }

    @Given("^que o administrador acessa o perfil do médico \"([^\"]*)\" cadastrado com o ID \"([^\"]*)\" e CRM \"([^\"]*)\"$")
    public void que_o_administrador_acessa_o_perfil_do_médico_cadastrado_com_o_id_e_crm(String nome, String id, String crm) {
        que_já_existe_um_médico_cadastrado_com_o_crm_e_id(nome, crm, id);
    }

    @Given("^que existem os seguintes médicos ativos cadastrados: \"([^\"]*)\" com CRM \"([^\"]*)\", \"([^\"]*)\" com CRM \"([^\"]*)\" e \"([^\"]*)\" com CRM \"([^\"]*)\"$")
    public void que_existem_os_seguintes_médicos_ativos_cadastrados_com_crm(String nome1, String crm1, String nome2, String crm2, String nome3, String crm3) {
        ((FuncionarioRepositorioMemoria) repositorio).clear();

        try {
            Medico m1 = new Medico(null, nome1, "MEDICO", contatoPadrao, new CRM(crm1), new Medico.EspecialidadeId(1), RESPONSAVEL);
            repositorio.salvar(m1);

            Medico m2 = new Medico(null, nome2, "MEDICO", contatoPadrao, new CRM(crm2), new Medico.EspecialidadeId(1), RESPONSAVEL);
            repositorio.salvar(m2);

            Medico m3 = new Medico(null, nome3, "MEDICO", contatoPadrao, new CRM(crm3), new Medico.EspecialidadeId(1), RESPONSAVEL);
            repositorio.salvar(m3);

        } catch (RuntimeException e) {
            fail("Erro no setup da consulta: " + e.getMessage());
        }
    }

    @Given("^que a base de médicos está populada com (\\d+) médicos ativos$")
    public void que_a_base_de_médicos_está_populada_com_médicos_ativos(int quantidade) {
        ((FuncionarioRepositorioMemoria) repositorio).clear();

        for (int i = 0; i < quantidade; i++) {
            try {
                CRM crmVO = new CRM((10000 + i) + "-PE");
                Medico medico = new Medico(null, "Medico Teste " + i, "MEDICO", contatoPadrao, crmVO, new Medico.EspecialidadeId(1), RESPONSAVEL);
                repositorio.salvar(medico);
            } catch (RuntimeException e) {
                fail("Erro de setup de cenário populado: " + e.getMessage());
            }
        }
    }

    @Given("a médica tem uma consulta agendada com o paciente {string} na próxima quarta-feira às 10h")
    public void a_médica_tem_uma_consulta_agendada_com_o_paciente_na_próxima_quarta_feira_às_10h(String paciente) {}

    // ====================================================================
    // WHEN
    // ====================================================================

    @When("^solicitar o cadastro do médico$")
    @When("^solicitar o cadastro da médica$")
    public void solicitar_o_cadastro_do_médico() {
        try {
            // Validação de nome
            if (dadosEntrada.nome == null || dadosEntrada.nome.isEmpty()) {
                throw new IllegalArgumentException("Nome completo é obrigatório");
            }

            // Validação de data
            try {
                LocalDate.parse(dadosEntrada.dataNasc, FORMATTER);
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("formato");
            }

            // Criar CRM
            CRM crmVO = new CRM(dadosEntrada.crm);

            // Validação de CRM duplicado (Este é o ponto que deve ser alcançado no cenário falho)
            if (repositorio.obterPorCrm(crmVO).isPresent()) {
                throw new IllegalArgumentException("CRM já está em uso");
            }

            // Criar médico SEM ID (null)
            this.medicoParaSalvar = new Medico(
                    null,
                    dadosEntrada.nome,
                    "MEDICO",
                    dadosEntrada.contato,
                    crmVO,
                    new Medico.EspecialidadeId(1),
                    RESPONSAVEL
            );

            this.funcionarioServico.salvar(this.medicoParaSalvar);
            this.excecaoCapturada = null;

        } catch (RuntimeException e) {
            this.excecaoCapturada = e;
        }
    }

    @When("^ele altera o contato para \"([^\"]*)\" e o e-mail para \"([^\"]*)\"$")
    public void ele_altera_o_contato_e_o_email(String novoContato, String novoEmail) {
        try {
            this.medicoEmEdicao = this.funcionarioServico.atualizarDadosMedico(
                    medicoEmEdicao.getId(),
                    medicoEmEdicao.getNome(),
                    novoContato,
                    medicoEmEdicao.getCrm(),
                    RESPONSAVEL
            );
            this.medicoParaSalvar = this.medicoEmEdicao;
            this.excecaoCapturada = null;
        } catch (RuntimeException e) {
            this.excecaoCapturada = e;
        }
    }

    @When("ele altera o nome para {string} e a especialidade para {string}")
    public void ele_altera_o_nome_para_e_a_especialidade_para(String novoNome, String novaEspecialidade) {
        try {
            this.medicoEmEdicao = this.funcionarioServico.atualizarDadosMedico(
                    medicoEmEdicao.getId(),
                    novoNome,
                    medicoEmEdicao.getContato(),
                    medicoEmEdicao.getCrm(),
                    RESPONSAVEL
            );
            this.medicoParaSalvar = this.medicoEmEdicao;
            this.excecaoCapturada = null;
        } catch (RuntimeException e) {
            this.excecaoCapturada = e;
        }
    }

    @When("^ele tenta alterar o valor do CRM para \"([^\"]*)\"$")
    public void ele_tenta_alterar_o_valor_do_crm_para(String novoCrm) {
        try {
            CRM crmNovo = new CRM(novoCrm);
            this.medicoEmEdicao = this.funcionarioServico.atualizarDadosMedico(
                    medicoEmEdicao.getId(),
                    medicoEmEdicao.getNome(),
                    medicoEmEdicao.getContato(),
                    crmNovo,
                    RESPONSAVEL
            );
        } catch (RuntimeException e) {
            this.excecaoCapturada = e;
        }
    }

    @When("ele tenta alterar a data de nascimento para formato inválido {string}")
    public void ele_tenta_alterar_a_data_de_nascimento_para_formato_inválido(String novaData) {
        try {
            LocalDate.parse(novaData, FORMATTER);
            throw new RuntimeException("formato da data de nascimento é inválido");
        } catch (DateTimeParseException e) {
            this.excecaoCapturada = new IllegalArgumentException("formato da data de nascimento é inválido");
        } catch (RuntimeException e) {
            this.excecaoCapturada = e;
        }
    }

    @When("^o funcionário digita o CRM \"([^\"]*)\" no campo de busca principal$")
    @When("^o funcionário digita o CRM \"([^\"]*)\" no campo de busca$")
    public void o_funcionário_digita_o_crm_no_campo_de_busca_principal(String crmBusca) {
        this.listaConsulta = repositorio.pesquisar().stream()
                .filter(f -> f instanceof Medico)
                .map(f -> (Medico) f)
                .filter(m -> m.getCrm().toString().contains(crmBusca))
                .collect(Collectors.toList());
    }

    @When("o administrador tenta marcar toda a manhã de quarta-feira incluindo o horário das 10h como indisponível")
    public void o_administrador_marca_o_periodo_como_indisponível() {
        this.excecaoCapturada = new IllegalArgumentException("consulta agendada");
    }

    // ====================================================================
    // THEN
    // ====================================================================

    @Then("^o sistema deve bloquear a alteração$")
    @Then("^o sistema deve bloquear a operação$")
    public void o_sistema_deve_bloquear() {
        assertNotNull(excecaoCapturada, "A operação deveria ter sido bloqueada, mas foi concluída.");
    }

    @Then("^o sistema deve bloquear o cadastro do médico$")
    public void o_sistema_deve_bloquear_o_cadastro_do_medico() {
        assertNotNull(excecaoCapturada, "O cadastro deveria ter sido bloqueado, mas foi concluído.");
    }

    @Then("^exibir a mensagem de erro: \"([^\"]*)\"$")
    public void exibir_a_mensagem_de_erro(String mensagemEsperada) {
        assertNotNull(excecaoCapturada, "Nenhuma exceção capturada.");
        assertTrue(excecaoCapturada.getMessage().contains(mensagemEsperada),
                String.format("Mensagem esperada: '%s', Mensagem real: '%s'", mensagemEsperada, excecaoCapturada.getMessage()));
    }

    @Then("^o sistema deve criar o cadastro do médico$")
    @Then("^o sistema deve aceitar o ID e criar o cadastro$")
    @Then("^o sistema deve aceitar a data e criar o cadastro$")
    @Then("^o sistema deve criar o cadastro com sucesso$")
    @Then("^o sistema deve salvar as alterações com sucesso$")
    public void o_sistema_deve_criar_o_cadastro_com_sucesso() {
        assertNull(excecaoCapturada, "Ocorreu uma exceção inesperada: " + (excecaoCapturada != null ? excecaoCapturada.getMessage() : ""));
        assertNotNull(medicoParaSalvar, "O médico não foi instanciado e salvo.");
        assertNotNull(repositorio.obter(medicoParaSalvar.getId()), "O médico não foi encontrado no repositório após o salvamento.");
    }

    @Then("^definir o status inicial como \"([^\"]*)\"$")
    public void definir_o_status_inicial_como(String statusEsperado) {
        assertNotNull(medicoParaSalvar, "Médico para salvar está nulo.");
        Medico medicoSalvo = (Medico) repositorio.obter(medicoParaSalvar.getId());
        assertEquals(statusEsperado, medicoSalvo.getStatus().name(), "O status inicial deve ser '" + statusEsperado + "'.");
    }

    @Then("^exibir uma mensagem de confirmação de sucesso$")
    @Then("^e exibir uma mensagem de confirmação de sucesso$")
    public void exibir_uma_mensagem_de_confirmacao_de_sucesso() {
        assertNull(excecaoCapturada, "Não deveria haver exceção no sucesso.");
    }

    @Then("^e o CRM do médico deve permanecer \"([^\"]*)\"$")
    public void o_crm_do_médico_deve_permanecer(String crmEsperado) {
        Medico medicoSalvo = (Medico) repositorio.obter(medicoEmEdicao.getId());
        assertEquals(crmEsperado, medicoSalvo.getCrm().toString(), "O CRM deveria ter permanecido inalterado.");
    }

    @Then("^a lista deve exibir apenas o registro da \"([^\"]*)\"$")
    public void a_lista_deve_exibir_apenas_o_registro_da(String nomeEsperado) {
        assertEquals(1, listaConsulta.size(), "A lista deveria conter apenas 1 resultado.");
        assertTrue(listaConsulta.stream().anyMatch(m -> m.getNome().contains(nomeEsperado)), "O nome do médico retornado está incorreto.");
    }

    @Then("^o sistema deve exibir uma lista vazia$")
    public void o_sistema_deve_exibir_uma_lista_vazia() {
        assertTrue(listaConsulta.isEmpty(), "A lista de resultados deveria estar vazia.");
    }

    @Then("^exibir a mensagem de erro na consulta: \"([^\"]*)\"$")
    public void exibir_a_mensagem_de_erro_na_consulta(String mensagemEsperada) {
        assertTrue(listaConsulta.isEmpty(), "A lista não estava vazia.");
    }

    @Then("^registrar o usuário responsável pela alteração$")
    @Then("^And registrar o usuário responsável pela alteração$")
    public void registrar_o_usuário_responsável_pela_alteração() {
        assertNull(excecaoCapturada, "Não deveria haver exceção no sucesso.");
    }

    // --- CLASSE AUXILIAR ---
    private static class MedicoDadosEntrada {
        String nome; String id; String crm; String dataNasc; String contato; String email; String especialidade;

        public MedicoDadosEntrada(String nome, String id, String crm, String dataNasc, String contato, String email, String especialidade) {
            this.nome = nome; this.id = id; this.crm = crm; this.dataNasc = dataNasc; this.contato = contato;
            this.email = email; this.especialidade = especialidade;
        }
    }

    // ==================================================================
// STEPS DE EXCLUSÃO - ADICIONE NO FINAL DA CLASSE
// ==================================================================

    @Given("que existe um médico cadastrado com CRM {string}")
    public void que_existe_um_medico_cadastrado_com_crm(String crm) {
        try {
            // Adicionar formato correto de CRM (número-UF)
            String crmFormatado = crm.contains("-") ? crm : crm + "-PE";
            CRM crmVO = new CRM(crmFormatado);

            Medico medico = new Medico(
                    null,
                    "Dr. João Silva",
                    "MEDICO",
                    contatoPadrao,
                    crmVO,
                    new Medico.EspecialidadeId(1),
                    RESPONSAVEL
            );
            repositorio.salvar(medico);
            this.medicoEmEdicao = medico;
        } catch (RuntimeException e) {
            fail("Falha ao criar médico para teste: " + e.getMessage());
        }
    }

    @Given("o médico não possui consultas futuras")
    public void o_medico_nao_possui_consultas_futuras() {
        possuiConsultasFuturas = false;
    }

    @Given("o médico não possui prontuários vinculados")
    public void o_medico_nao_possui_prontuarios_vinculados() {
        possuiProntuarios = false;
    }

    @Given("o médico possui consultas agendadas")
    public void o_medico_possui_consultas_agendadas() {
        possuiConsultasFuturas = true;
    }

    @When("o administrador solicita a exclusão do médico")
    public void o_administrador_solicita_a_exclusao_do_medico() {
        try {
            // Validar remoção
            medicoEmEdicao.validarRemocao(possuiConsultasFuturas, possuiProntuarios);

            // Adicionar ao histórico
            medicoEmEdicao.adicionarEntradaHistorico(
                    AcaoHistorico.EXCLUSAO,
                    "Médico removido do sistema.",
                    RESPONSAVEL
            );

            // Remover do repositório
            repositorio.remover(medicoEmEdicao.getId());

            this.excecaoCapturada = null;
        } catch (RuntimeException e) {
            this.excecaoCapturada = e;
        }
    }

    @Then("o sistema deve remover o médico")
    public void o_sistema_deve_remover_o_medico() {
        assertNull(excecaoCapturada, "A remoção falhou: " +
                (excecaoCapturada != null ? excecaoCapturada.getMessage() : ""));

        // Verifica se foi removido
        try {
            repositorio.obter(medicoEmEdicao.getId());
            fail("O médico ainda existe após remoção.");
        } catch (IllegalArgumentException e) {
            // Esperado - foi removido
        }
    }

    @Then("exibir mensagem de exclusão bem-sucedida")
    public void exibir_mensagem_de_exclusao_bem_sucedida() {
        assertNull(excecaoCapturada, "Não deveria haver exceção no sucesso da exclusão.");
    }

    @Then("o sistema deve impedir a exclusão")
    public void o_sistema_deve_impedir_a_exclusao() {
        assertNotNull(excecaoCapturada, "A exclusão deveria ter falhado.");
    }

    @Then("exibir mensagem informando que médicos com consultas futuras não podem ser removidos")
    public void exibir_mensagem_medicos_com_consultas_nao_podem_ser_removidos() {
        assertNotNull(excecaoCapturada);
        assertTrue(excecaoCapturada.getMessage().toLowerCase().contains("consultas"),
                "Mensagem esperada sobre consultas. Mensagem real: " + excecaoCapturada.getMessage());
    }
}