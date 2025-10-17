package br.com.medflow.dominio.convenios;

import static org.apache.commons.lang3.Validate.notBlank;

public class Convenio {
    private String id;
    private String nome;
    private String codigoIdentificacao;
    private StatusConvenio status;

    private HistoricoRegistro ultimoHistorico;

    // Construtor para novo cadastro
    public Convenio(String nome, String codigoIdentificacao) {
        setNome(nome);
        setCodigoIdentificacao(codigoIdentificacao);
        this.status = StatusConvenio.ATIVO; // Regra: status padrão é ATIVO
    }

    // Construtor completo (usado pelo repositório)
    public Convenio(String id, String nome, String codigoIdentificacao, StatusConvenio status, HistoricoRegistro ultimoHistorico) {
        this.id = id;
        this.nome = nome;
        this.codigoIdentificacao = codigoIdentificacao;
        this.status = status;
        this.ultimoHistorico = ultimoHistorico;
    }

    
    // VALIDAÇÃO NÃO É IDEAL SER POR AQ
    // Setters com validação
    public void setNome(String nome) {
        notBlank(nome, "O nome do convênio é obrigatório");
        this.nome = nome.trim();
    }

    public void setCodigoIdentificacao(String codigoIdentificacao) {
        notBlank(codigoIdentificacao, "O código de identificação do convênio é obrigatório");
        if (codigoIdentificacao.matches(".*[\\s\\@\\#\\$\\%\\^\\&\\*\\(\\)].*")) {
            throw new IllegalArgumentException("O código de identificação contém caracteres inválidos.");
        }
        this.codigoIdentificacao = codigoIdentificacao.trim();
    }

    public void setStatus(StatusConvenio status) {
        if (status == null) {
            throw new IllegalArgumentException("O status do convênio não pode ser nulo.");
        }
        this.status = status;
    }

    public void registrarHistorico(HistoricoRegistro historico) {
        if (historico == null) {
            throw new IllegalArgumentException("O histórico de registro é obrigatório.");
        }
        this.ultimoHistorico = historico;
    }

    // Getters
    public String getId() { return id; }
    public String getNome() { return nome; }
    public String getCodigoIdentificacao() { return codigoIdentificacao; }
    public StatusConvenio getStatus() { return status; }
    public HistoricoRegistro getUltimoHistorico() { return ultimoHistorico; }


    @Override
    public String toString() {
        return nome;
    }
}
