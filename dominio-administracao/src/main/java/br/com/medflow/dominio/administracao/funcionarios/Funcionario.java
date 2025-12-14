package br.com.medflow.dominio.administracao.funcionarios;

import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe de Domínio que representa o Funcionário e implementa as Regras de Negócio.
 */
public class Funcionario {
    private FuncionarioId id;
    private String nome;
    private String funcao;
    private String contato;
    private StatusFuncionario status;
    private List<HistoricoEntrada> historico = new ArrayList<>();

    private static final String REGEX_NOME = "^[a-zA-ZáàâãéèêíïóôõöúüçÇÁÀÂÃÉÈÊÍÏÓÔÕÖÚÜ\\s]+$";

    private static final String REGEX_CONTATO =
            // Padrão 1: Telefone (10 ou 11 dígitos, permitindo: (DD) 9XXXX-XXXX, DD 9XXXX-XXXX, etc.)
            "^(?:\\(?\\d{2}\\)?\\s?)?\\d{4,5}-?\\d{4}$" +
                    "|" +
                    // Padrão 2: E-mail (existente)
                    "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";


    public Funcionario(String nome, String funcao, String contato, UsuarioResponsavelId responsavelId) {
        notNull(responsavelId, "O responsável pela criação não pode ser nulo.");

        setNome(nome);
        setFuncao(funcao);
        setContato(contato);

        this.status = StatusFuncionario.ATIVO;
        adicionarEntradaHistorico(AcaoHistorico.CRIACAO, "Funcionário cadastrado com status ATIVO", responsavelId);
    }


    public Funcionario(FuncionarioId id, String nome, String funcao, String contato, StatusFuncionario status, List<HistoricoEntrada> historico) {
        notNull(id, "O ID do funcionário não pode ser nulo na reconstrução.");
        this.id = id;
        this.nome = nome;
        this.funcao = funcao;
        this.contato = contato;
        this.status = status;
        if (historico != null) {
            this.historico.addAll(historico);
        }
    }


    public void setNome(String nome) {
        notBlank(nome, "O nome do funcionário é obrigatório.");
        validarNome(nome);
        this.nome = nome.trim();
    }

    private void validarNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome é obrigatório.");
        }
        // Aceita letras (com acentos), espaços, pontos e números
        if (!nome.matches("^[\\p{L}0-9\\s.]+$")) {
            throw new IllegalArgumentException("O nome deve conter apenas letras.");
        }
    }

    public void setFuncao(String funcao) {
        notBlank(funcao, "A função do funcionário é obrigatória.");
        this.funcao = funcao.trim();
    }

    public void setContato(String contato) {
        notBlank(contato, "O contato é obrigatório.");
        validarFormatoContato(contato);
        this.contato = contato.trim();
    }

    private void validarFormatoContato(String contato) {
        if (!contato.matches(REGEX_CONTATO)) {
            throw new IllegalArgumentException("Formato de contato inválido.");
        }
    }

    public void atualizarDados(String novoNome, String novaFuncao, String novoContato, UsuarioResponsavelId responsavelId, boolean temViculosAtivosFuncao) {
        notNull(responsavelId, "O responsável pela alteração não pode ser nulo.");

        validarNome(novoNome);
        notBlank(novaFuncao, "A função do funcionário é obrigatória.");
        validarFormatoContato(novoContato);

        boolean houveAlteracao = false;

        if (!this.nome.trim().equalsIgnoreCase(novoNome.trim())) {
            this.nome = novoNome.trim();
            houveAlteracao = true;
        }

        if (!this.funcao.trim().equalsIgnoreCase(novaFuncao.trim())) {

            if (this.status == StatusFuncionario.ATIVO && temViculosAtivosFuncao) {
                throw new IllegalStateException("Erro na operação.");
            }
            this.funcao = novaFuncao.trim();
            houveAlteracao = true;
        }

        if (!this.contato.trim().equalsIgnoreCase(novoContato.trim())) {
            this.contato = novoContato.trim();
            houveAlteracao = true;
        }


        if (houveAlteracao) {
            adicionarEntradaHistorico(AcaoHistorico.ATUALIZACAO, "Dados cadastrais atualizados.", responsavelId);
        }
    }


    public void mudarStatus(StatusFuncionario novoStatus, UsuarioResponsavelId responsavelId, boolean temAtividadesFuturas) {
        notNull(responsavelId, "O responsável pela alteração não pode ser nulo.");

        if (this.status == novoStatus) {
            throw new IllegalStateException("Status não atualizado."); // Mensagem do Cenário de Falha
        }

        if (novoStatus == StatusFuncionario.INATIVO && temAtividadesFuturas) {
            throw new IllegalStateException("Não é possível inativar o funcionário enquanto houver atividades futuras vinculadas.");
        }

        this.status = novoStatus;
        adicionarEntradaHistorico(AcaoHistorico.ATUALIZACAO, "Status alterado para: " + novoStatus.name(), responsavelId);
    }


    public void validarAtribuicaoAtividade() {
        if (this.status == StatusFuncionario.INATIVO) {
            throw new IllegalStateException("Funcionários inativos não podem ser atribuídos a novas atividades.");
        }
    }


    public void validarAlteracaoCampoStatus() {
        throw new IllegalArgumentException("Os dados informados são inválidos.");
    }


    public void adicionarEntradaHistorico(AcaoHistorico acao, String descricao, UsuarioResponsavelId responsavelId) {
        notNull(responsavelId, "O responsável pela ação não pode ser nulo.");
        historico.add(new HistoricoEntrada(acao, descricao, responsavelId, LocalDateTime.now()));
    }


    // ===== GETTERS/SETTER ID (Para o Repositório) =====

    public FuncionarioId getId() { return id; }
    public void setId(FuncionarioId id) { this.id = id; }

    public String getNome() { return nome; }
    public String getFuncao() { return funcao; }
    public String getContato() { return contato; }
    public StatusFuncionario getStatus() { return status; }
    public List<HistoricoEntrada> getHistorico() { return List.copyOf(historico); }
    public static class HistoricoEntrada {
        private final AcaoHistorico acao;
        private final String descricao;
        private final UsuarioResponsavelId responsavel;
        private final LocalDateTime dataHora;

        public HistoricoEntrada(AcaoHistorico acao, String descricao, UsuarioResponsavelId responsavel, LocalDateTime dataHora) {
            this.acao = acao;
            this.descricao = descricao;
            this.responsavel = responsavel;
            this.dataHora = dataHora;
        }

        public AcaoHistorico getAcao() { return acao; }
        public String getDescricao() { return descricao; }
        public UsuarioResponsavelId getResponsavel() { return responsavel; }
        public LocalDateTime getDataHora() { return dataHora; }
    }
}