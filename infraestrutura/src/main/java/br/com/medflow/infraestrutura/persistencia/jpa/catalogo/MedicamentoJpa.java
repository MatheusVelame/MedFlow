// Localização: infraestrutura/src/main/java/br/com/medflow/infraestrutura/persistencia/jpa/catalogo/MedicamentoJpa.java

package br.com.medflow.infraestrutura.persistencia.jpa.catalogo;

import jakarta.persistence.*;
import br.com.medflow.dominio.catalogo.medicamentos.StatusMedicamento;

@Entity
@Table(name = "medicamentos")
public class MedicamentoJpa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String nome;
    
    @Column(name = "uso_principal", nullable = false)
    private String usoPrincipal;
    
    @Column(columnDefinition = "TEXT")
    private String contraindicacoes;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusMedicamento status;
    
    // Mapeamento opcional para Revisão Pendente (Relacionamento OneToOne)
    // Usamos CascadeType.ALL para garantir que a Revisão seja salva ou deletada junto.
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "revisao_pendente_id", unique = true)
    private RevisaoPendenteJpa revisaoPendente;
    
    public MedicamentoJpa() {}
    
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getUsoPrincipal() { return usoPrincipal; }
    public void setUsoPrincipal(String usoPrincipal) { this.usoPrincipal = usoPrincipal; }
    public String getContraindicacoes() { return contraindicacoes; }
    public void setContraindicacoes(String contraindicacoes) { this.contraindicacoes = contraindicacoes; }
    public StatusMedicamento getStatus() { return status; }
    public void setStatus(StatusMedicamento status) { this.status = status; }
    public RevisaoPendenteJpa getRevisaoPendente() { return revisaoPendente; }
    public void setRevisaoPendente(RevisaoPendenteJpa revisaoPendente) { this.revisaoPendente = revisaoPendente; }
}