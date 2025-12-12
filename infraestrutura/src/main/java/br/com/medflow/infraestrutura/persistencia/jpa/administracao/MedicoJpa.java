package br.com.medflow.infraestrutura.persistencia.jpa.administracao;

import br.com.medflow.infraestrutura.persistencia.jpa.administracao.FuncionarioJpa;
import br.com.medflow.infraestrutura.persistencia.jpa.administracao.HistoricoEntradaJpa;
import br.com.medflow.dominio.administracao.funcionarios.StatusFuncionario;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidade JPA para Médico.
 * Herda de FuncionarioJpa usando estratégia JOINED (tabelas separadas).
 *
 * Mapeamento:
 * - Tabela 'funcionarios' (pai) contém: id, nome, funcao, contato, status
 * - Tabela 'medicos' (filha) contém: funcionario_id, crm_numero, crm_uf, especialidade_id, data_nascimento
 */
@Entity
@Table(name = "medicos")
@PrimaryKeyJoinColumn(name = "funcionario_id")
public class MedicoJpa extends FuncionarioJpa {

    @Column(name = "crm_numero", nullable = false, length = 20)
    private String crmNumero;

    @Column(name = "crm_uf", nullable = false, length = 2)
    private String crmUf;

    @Column(name = "especialidade_id", nullable = false)
    private Integer especialidadeId;

    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;

    @OneToMany(mappedBy = "medico", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<DisponibilidadeJpa> disponibilidades = new ArrayList<>();

    // Construtor vazio para JPA
    public MedicoJpa() {
        super();
    }

    // Construtor completo
    public MedicoJpa(
            Integer id,
            String nome,
            String funcao,
            String contato,
            StatusFuncionario status,
            List<HistoricoEntradaJpa> historico,
            String crmNumero,
            String crmUf,
            Integer especialidadeId,
            LocalDate dataNascimento,
            List<DisponibilidadeJpa> disponibilidades) {

        super(id, nome, funcao, contato, status, historico);
        this.crmNumero = crmNumero;
        this.crmUf = crmUf;
        this.especialidadeId = especialidadeId;
        this.dataNascimento = dataNascimento;
        this.disponibilidades = disponibilidades != null ? disponibilidades : new ArrayList<>();
    }

    // Getters e Setters
    public String getCrmNumero() {
        return crmNumero;
    }

    public void setCrmNumero(String crmNumero) {
        this.crmNumero = crmNumero;
    }

    public String getCrmUf() {
        return crmUf;
    }

    public void setCrmUf(String crmUf) {
        this.crmUf = crmUf;
    }

    public Integer getEspecialidadeId() {
        return especialidadeId;
    }

    public void setEspecialidadeId(Integer especialidadeId) {
        this.especialidadeId = especialidadeId;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public List<DisponibilidadeJpa> getDisponibilidades() {
        return disponibilidades;
    }

    public void setDisponibilidades(List<DisponibilidadeJpa> disponibilidades) {
        this.disponibilidades = disponibilidades;
        if (disponibilidades != null) {
            disponibilidades.forEach(d -> d.setMedico(this));
        }
    }

    /**
     * Retorna o CRM completo no formato "numero-UF"
     */
    public String getCrmCompleto() {
        return crmNumero + "-" + crmUf;
    }
}