package br.com.medflow.dominio.administracao.pacientes;

import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe de Domínio que representa o Paciente e implementa as Regras de Negócio.
 */
public class Paciente {
    private PacienteId id;
    private String nome;
    private String cpf;
    private String dataNascimento;
    private String telefone;
    private String endereco;
    private List<HistoricoEntrada> historico = new ArrayList<>();
    
    private static final String REGEX_NOME = "^[a-zA-ZáàâãéèêíïóôõöúüçÇÁÀÂÃÉÈÊÍÏÓÔÕÖÚÜ\\s]+$";
    private static final String REGEX_CPF = "^\\d{11}$";
    private static final String REGEX_DATA_NASCIMENTO = "^\\d{2}/\\d{2}/\\d{4}$";
    private static final String REGEX_TELEFONE = "^\\d{10,11}$";

    public Paciente(String nome, String cpf, String dataNascimento, String telefone, String endereco, UsuarioResponsavelId responsavelId) {
        notNull(responsavelId, "O responsável pela criação não pode ser nulo.");
        
        setNome(nome);
        setCpf(cpf);
        setDataNascimento(dataNascimento);
        setTelefone(telefone);
        this.endereco = endereco;
        
        adicionarEntradaHistorico(AcaoHistorico.CRIACAO, "Paciente cadastrado", responsavelId);
    }
    
    public Paciente(PacienteId id, String nome, String cpf, String dataNascimento, String telefone, String endereco, List<HistoricoEntrada> historico) {
        notNull(id, "O ID do paciente não pode ser nulo na reconstrução.");
        this.id = id;
        this.nome = nome;
        this.cpf = cpf;
        this.dataNascimento = dataNascimento;
        this.telefone = telefone;
        this.endereco = endereco;
        if (historico != null) {
            this.historico.addAll(historico);
        }
    }
    
    public void setNome(String nome) {
        notBlank(nome, "O nome do paciente é obrigatório.");
        validarNome(nome);
        this.nome = nome.trim();
    }
    
    private void validarNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome do paciente é obrigatório.");
        }
        
        if (!nome.matches(REGEX_NOME)) {
            throw new IllegalArgumentException("O nome deve conter apenas letras.");
        }
    }
    
    public void setCpf(String cpf) {
        notBlank(cpf, "O CPF do paciente é obrigatório.");
        validarCpf(cpf);
        this.cpf = cpf.trim();
    }
    
    private void validarCpf(String cpf) {
        if (cpf == null || cpf.trim().isEmpty()) {
            throw new IllegalArgumentException("O CPF do paciente é obrigatório.");
        }
        
        if (cpf.trim().length() != 11) {
            throw new IllegalArgumentException("O CPF deve conter exatamente 11 dígitos numéricos.");
        }
        
        if (!cpf.matches(REGEX_CPF)) {
            throw new IllegalArgumentException("O CPF deve conter apenas dígitos (11 caracteres).");
        }
    }
    
    public void setDataNascimento(String dataNascimento) {
        notBlank(dataNascimento, "A data de nascimento do paciente é obrigatória.");
        validarDataNascimento(dataNascimento);
        this.dataNascimento = dataNascimento.trim();
    }
    
    private void validarDataNascimento(String dataNascimento) {
        if (dataNascimento == null || dataNascimento.trim().isEmpty()) {
            throw new IllegalArgumentException("A data de nascimento do paciente é obrigatória.");
        }
        
        if (!dataNascimento.matches(REGEX_DATA_NASCIMENTO)) {
            throw new IllegalArgumentException("A data de nascimento deve estar no formato dd/mm/aaaa.");
        }
    }
    
    public void setTelefone(String telefone) {
        notBlank(telefone, "O telefone do paciente é obrigatório.");
        validarTelefone(telefone);
        this.telefone = telefone.trim();
    }
    
    private void validarTelefone(String telefone) {
        if (telefone == null || telefone.trim().isEmpty()) {
            throw new IllegalArgumentException("O telefone do paciente é obrigatório.");
        }
        
        if (!telefone.matches(REGEX_TELEFONE)) {
            throw new IllegalArgumentException("O telefone deve conter entre 10 e 11 dígitos numéricos.");
        }
    }
    
    public void atualizarDados(String novoNome, String novoCpf, String novaDataNascimento, String novoTelefone, String novoEndereco, UsuarioResponsavelId responsavelId) {
        notNull(responsavelId, "O responsável pela alteração não pode ser nulo.");
        
        // Verificar se o CPF foi alterado
        if (!this.cpf.equals(novoCpf)) {
            throw new IllegalStateException("Não é permitido alterar o CPF de um paciente.");
        }
        
        validarNome(novoNome);
        validarDataNascimento(novaDataNascimento);
        validarTelefone(novoTelefone);
        
        boolean houveAlteracao = false;
        
        if (!this.nome.trim().equalsIgnoreCase(novoNome.trim())) {
            this.nome = novoNome.trim();
            houveAlteracao = true;
        }
        
        if (!this.dataNascimento.trim().equals(novaDataNascimento.trim())) {
            this.dataNascimento = novaDataNascimento.trim();
            houveAlteracao = true;
        }
        
        if (!this.telefone.trim().equals(novoTelefone.trim())) {
            this.telefone = novoTelefone.trim();
            houveAlteracao = true;
        }
        
        if ((novoEndereco == null && this.endereco != null) ||
            (novoEndereco != null && !novoEndereco.equals(this.endereco))) {
            this.endereco = novoEndereco;
            houveAlteracao = true;
        }
        
        if (houveAlteracao) {
            adicionarEntradaHistorico(AcaoHistorico.ATUALIZACAO, "Dados cadastrais atualizados.", responsavelId);
        }
    }
    
    public void validarRemocao(boolean temProntuario, boolean temConsulta, boolean temExame) {
        if (temProntuario) {
            throw new IllegalStateException("Pacientes com prontuário não podem ser removidos.");
        }
        
        if (temConsulta) {
            throw new IllegalStateException("Pacientes com consultas agendadas não podem ser removidos.");
        }
        
        if (temExame) {
            throw new IllegalStateException("Pacientes com exames agendados não podem ser removidos.");
        }
    }
    
    public void adicionarEntradaHistorico(AcaoHistorico acao, String descricao, UsuarioResponsavelId responsavelId) {
        notNull(responsavelId, "O responsável pela ação não pode ser nulo.");
        historico.add(new HistoricoEntrada(acao, descricao, responsavelId, LocalDateTime.now()));
    }
    
    public PacienteId getId() { return id; }
    public void setId(PacienteId id) { this.id = id; }
    
    public String getNome() { return nome; }
    public String getCpf() { return cpf; }
    public String getDataNascimento() { return dataNascimento; }
    public String getTelefone() { return telefone; }
    public String getEndereco() { return endereco; }
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