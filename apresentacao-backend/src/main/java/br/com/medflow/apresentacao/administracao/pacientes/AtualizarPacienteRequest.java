package br.com.medflow.apresentacao.administracao.pacientes;

public class AtualizarPacienteRequest {
    
    private String nome;
    private String cpf;
    private String dataNascimento;
    private String telefone;
    private String endereco;
    private int responsavelId;
    
    // Construtor padrão
    public AtualizarPacienteRequest() {}
    
    // Construtor completo
    public AtualizarPacienteRequest(String nome, String cpf, String dataNascimento, 
                                     String telefone, String endereco, int responsavelId) {
        this.nome = nome;
        this.cpf = cpf;
        this.dataNascimento = dataNascimento;
        this.telefone = telefone;
        this.endereco = endereco;
        this.responsavelId = responsavelId;
    }
    
    // Getters e Setters
    public String getNome() {
        return nome;
    }
    
    public void setNome(String nome) {
        this.nome = nome;
    }
    
    public String getCpf() {
        return cpf;
    }
    
    public void setCpf(String cpf) {
        this.cpf = cpf;
    }
    
    public String getDataNascimento() {
        return dataNascimento;
    }
    
    public void setDataNascimento(String dataNascimento) {
        this.dataNascimento = dataNascimento;
    }
    
    public String getTelefone() {
        return telefone;
    }
    
    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }
    
    public String getEndereco() {
        return endereco;
    }
    
    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }
    
    public int getResponsavelId() {
        return responsavelId;
    }
    
    public void setResponsavelId(int responsavelId) {
        this.responsavelId = responsavelId;
    }
}