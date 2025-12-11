package br.com.medflow.apresentacao.referencia.tiposExames;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public class TipoExameFormulario {

    @NotBlank(message = "O código é obrigatório.")
    private String codigo;

    @NotBlank(message = "A descrição é obrigatória.")
    private String descricao;

    @NotBlank(message = "A especialidade é obrigatória.")
    private String especialidade;

    @NotNull(message = "O valor é obrigatório.")
    @PositiveOrZero(message = "O valor deve ser maior ou igual a zero.")
    private Double valor;
    
    @NotNull(message = "O ID do responsável é obrigatório para a auditoria.")
    private Integer responsavelId;

    public TipoExameFormulario() {}

    // Getters e Setters
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    
    public String getEspecialidade() { return especialidade; }
    public void setEspecialidade(String especialidade) { this.especialidade = especialidade; }
    
    public Double getValor() { return valor; }
    public void setValor(Double valor) { this.valor = valor; }
    
    public Integer getResponsavelId() { return responsavelId; }
    public void setResponsavelId(Integer responsavelId) { this.responsavelId = responsavelId; }
}