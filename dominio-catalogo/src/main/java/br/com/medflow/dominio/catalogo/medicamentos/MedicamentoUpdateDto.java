package br.com.medflow.dominio.catalogo.medicamentos;

public class MedicamentoUpdateDto {
    public String nome;
    public String usoPrincipal;
    public String contraindicacoes;
    public StatusMedicamento status;
    public String justificativaExclusao;
    
    public MedicamentoUpdateDto(String nome, String usoPrincipal, String contraindicacoes, StatusMedicamento status) {
        this.nome = nome;
        this.usoPrincipal = usoPrincipal;
        this.contraindicacoes = contraindicacoes;
        this.status = status;
    }
}