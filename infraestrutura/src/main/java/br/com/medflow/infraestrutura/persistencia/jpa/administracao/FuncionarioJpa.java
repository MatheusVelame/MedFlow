package br.com.medflow.infraestrutura.persistencia.jpa.administracao;

import br.com.medflow.dominio.administracao.funcionarios.StatusFuncionario;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "funcionarios")
public class FuncionarioJpa {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(nullable = false)
	private String nome;

	@Column(nullable = false)
	private String funcao;

	@Column(nullable = false)
	private String contato;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private StatusFuncionario status;

	@OneToMany(mappedBy = "funcionario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<HistoricoEntradaJpa> historico = new ArrayList<>();

    public FuncionarioJpa() {}
    
    public FuncionarioJpa(Integer id, String nome, String funcao, String contato, 
                          StatusFuncionario status, List<HistoricoEntradaJpa> historico) {
        this.id = id;
        this.nome = nome;
        this.funcao = funcao;
        this.contato = contato;
        this.status = status;
        this.historico = historico != null ? historico : new ArrayList<>();
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getFuncao() { return funcao; }
    public void setFuncao(String funcao) { this.funcao = funcao; }
    public String getContato() { return contato; }
    public void setContato(String contato) { this.contato = contato; }
    public StatusFuncionario getStatus() { return status; }
    public void setStatus(StatusFuncionario status) { this.status = status; }
    public List<HistoricoEntradaJpa> getHistorico() { return historico; }
    
    public void setHistorico(List<HistoricoEntradaJpa> historico) { 
        this.historico = historico;
        if (historico != null) {
            historico.forEach(h -> h.setFuncionario(this));
        }
    }
}

