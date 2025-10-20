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
    
    // RN 3: Apenas alfabéticos
    private static final String REGEX_NOME = "^[a-zA-ZáàâãéèêíïóôõöúüçÇÁÀÂÃÉÈÊÍÏÓÔÕÖÚÜ\\s]+$";
    
    // RN 5: E-mail OU Telefone (10 ou 11 dígitos)
    private static final String REGEX_CONTATO = 
        "^\\d{10,11}$" + // Telefone (10 ou 11 dígitos)
        "|" + 
        "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

    /**
     * Construtor para NOVO CADASTRO.
     * RN 7: Status inicial ATIVO. O ID será setado pelo Repositório.
     */
    public Funcionario(String nome, String funcao, String contato, UsuarioResponsavelId responsavelId) {
        notNull(responsavelId, "O responsável pela criação não pode ser nulo.");
        // O ID é deixado nulo (ou atribuído externamente pelo Repositório após a persistência)
        // this.id = new FuncionarioId(); <== Removido, pois FuncionarioId(int) exige um ID positivo
        
        setNome(nome);          // RN 1 e RN 3
        setFuncao(funcao);      // RN 2
        setContato(contato);    // RN 4 e RN 5
        
        this.status = StatusFuncionario.ATIVO; // RN 7
        adicionarEntradaHistorico(AcaoHistorico.CRIACAO, "Funcionário cadastrado com status ATIVO", responsavelId);
    }
    
    /**
     * Construtor para reconstrução (Usado por repositórios) - Recebe o ID
     */
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

 // ===== MÉTODOS DE CADASTRO/VALIDAÇÃO (Setters Internos) =====

    /** RN 1: Nome obrigatório | RN 3: Apenas letras */
    public void setNome(String nome) {
        notBlank(nome, "O nome do funcionário é obrigatório."); 
        validarNome(nome);                                
        this.nome = nome.trim();
    }
    
    private void validarNome(String nome) {
        if (!nome.matches(REGEX_NOME)) {
            throw new IllegalArgumentException("O nome deve conter apenas letras.");
        }
    }

    /** RN 2: Função obrigatória */
    public void setFuncao(String funcao) {
        notBlank(funcao, "A função do funcionário é obrigatória."); 
        this.funcao = funcao.trim();
    }

    /** RN 4: Contato obrigatório | RN 5: Formato válido */
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
    
    /**
     * RN 1 (Atualização): Apenas nome, função e contato podem ser alterados.
     * RN 2 (Atualização): Devem seguir as validações do cadastro.
     * RN 4 (Atualização): Prevenção de alteração de função com vínculos ativos.
     */
    public void atualizarDados(String novoNome, String novaFuncao, String novoContato, UsuarioResponsavelId responsavelId, boolean temViculosAtivosFuncao) {
        notNull(responsavelId, "O responsável pela alteração não pode ser nulo.");
        
        // RN 2 (Atualização): Validações
        validarNome(novoNome);
        notBlank(novaFuncao, "A função do funcionário é obrigatória.");
        validarFormatoContato(novoContato);

        boolean houveAlteracao = false;
        
        if (!this.nome.trim().equalsIgnoreCase(novoNome.trim())) {
            this.nome = novoNome.trim();
            houveAlteracao = true;
        }

        if (!this.funcao.trim().equalsIgnoreCase(novaFuncao.trim())) {
           
            // RN 4 (Atualização): Impedir alteração de função ativa sem tratamento
            if (this.status == StatusFuncionario.ATIVO && temViculosAtivosFuncao) {
                // Simulação da falha: "Erro na operação."
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
    
    // ===== MÉTODOS DE STATUS =====

    /**
     * RN 1 (Gestão): Alteração de status permitida.
     * RN 4 (Gestão): Não é permitido inativar se houver atividades futuras.
     */
    public void mudarStatus(StatusFuncionario novoStatus, UsuarioResponsavelId responsavelId, boolean temAtividadesFuturas) {
        notNull(responsavelId, "O responsável pela alteração não pode ser nulo.");

        // RN 1 (Gestão) Falha: Salvar os dados sem alteração
        if (this.status == novoStatus) {
            throw new IllegalStateException("Status não atualizado."); // Mensagem do Cenário de Falha
        }

        // RN 4 (Gestão) Falha: Inativação com vínculos futuros
        if (novoStatus == StatusFuncionario.INATIVO && temAtividadesFuturas) {
             throw new IllegalStateException("Não é possível inativar o funcionário enquanto houver atividades futuras vinculadas.");
        }
        
        this.status = novoStatus;
        adicionarEntradaHistorico(AcaoHistorico.ATUALIZACAO, "Status alterado para: " + novoStatus.name(), responsavelId);
    }
    
    /**
     * RN 2 (Gestão): Funcionários Inativos não podem ser atribuídos a novas escalas, atendimentos ou agendamentos
     */
    public void validarAtribuicaoAtividade() {
        if (this.status == StatusFuncionario.INATIVO) {
            throw new IllegalStateException("Funcionários inativos não podem ser atribuídos a novas atividades.");
        }
    }
    
    /**
     * RN 1 (Atualização) Falha: Tentativa de alterar campo não permitido (Status)
     * Deve ser chamada se um método genérico tentar alterar o Status (que só pode ser por 'mudarStatus').
     */
    public void validarAlteracaoCampoStatus() {
        throw new IllegalArgumentException("Os dados informados são inválidos.");
    }
    
    // ===== MÉTODOS DE HISTÓRICO =====
    
    public void adicionarEntradaHistorico(AcaoHistorico acao, String descricao, UsuarioResponsavelId responsavelId) {
        notNull(responsavelId, "O responsável pela ação não pode ser nulo.");
        historico.add(new HistoricoEntrada(acao, descricao, responsavelId, LocalDateTime.now()));
    }
    
    
    // ===== GETTERS/SETTER ID (Para o Repositório) =====
    
    public FuncionarioId getId() { return id; }
    // Setter usado pelo Repositório no momento da persistência
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