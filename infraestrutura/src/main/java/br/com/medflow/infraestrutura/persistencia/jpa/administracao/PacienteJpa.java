package br.com.medflow.infraestrutura.persistencia.jpa.administracao;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "pacientes")
public class PacienteJpa {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "paciente_id")
    private Integer id;
    
    @Column(name = "nome_paciente", nullable = false)
    private String nome;
    
    @Column(name = "cpf_paciente", nullable = false, unique = true, length = 11)
    private String cpf;
    
    @Column(name = "data_nascimento_paciente", nullable = false)
    private String dataNascimento;
    
    @Column(name = "telefone_paciente", nullable = false)
    private String telefone;
    
    @Column(name = "endereco_paciente")
    private String endereco;
    
    @ElementCollection
    @CollectionTable(
        name = "pacientes_historico",
        joinColumns = @JoinColumn(name = "paciente_id")
    )
    @OrderColumn(name = "ordem_historico")
    private List<PacienteHistoricoEntradaJpa> historico = new ArrayList<>();
    
    // Construtor padrão (obrigatório para JPA)
    protected PacienteJpa() {}
    
    public PacienteJpa(String nome, String cpf, String dataNascimento, String telefone, String endereco) {
        this.nome = nome;
        this.cpf = cpf;
        this.dataNascimento = dataNascimento;
        this.telefone = telefone;
        this.endereco = endereco;
    }
    
    // Getters e Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    
    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }
    
    public String getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(String dataNascimento) { this.dataNascimento = dataNascimento; }
    
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    
    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }
    
    public List<PacienteHistoricoEntradaJpa> getHistorico() { return historico; }
    public void setHistorico(List<PacienteHistoricoEntradaJpa> historico) { this.historico = historico; }
    
    public void adicionarHistorico(PacienteHistoricoEntradaJpa entrada) {
        this.historico.add(entrada);
    }
}