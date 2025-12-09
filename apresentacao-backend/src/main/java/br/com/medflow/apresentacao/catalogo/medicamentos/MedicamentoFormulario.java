package br.com.medflow.apresentacao.catalogo.medicamentos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class MedicamentoFormulario {

    @NotBlank(message = "O nome é obrigatório.")
    private String nome;

    @NotBlank(message = "O uso principal é obrigatório.")
    private String usoPrincipal;

    private String contraindicacoes;
    
    @NotNull(message = "O ID do responsável é obrigatório para a auditoria.")
    private Integer responsavelId;

    public MedicamentoFormulario() {}

    // Construtor completo e Getters/Setters omitidos para brevidade (mas devem ser incluídos)
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getUsoPrincipal() { return usoPrincipal; }
    public void setUsoPrincipal(String usoPrincipal) { this.usoPrincipal = usoPrincipal; }
    public String getContraindicacoes() { return contraindicacoes; }
    public void setContraindicacoes(String contraindicacoes) { this.contraindicacoes = contraindicacoes; }
    public Integer getResponsavelId() { return responsavelId; }
    public void setResponsavelId(Integer responsavelId) { this.responsavelId = responsavelId; }
}