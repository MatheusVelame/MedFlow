package br.com.medflow.dominio.catalogo.medicamentos;

import static org.apache.commons.lang3.Validate.notBlank;

public class Medicamento {
    private String id;
    private String nome;
    private String usoPrincipal;
    private String contraindicacoes;
    private StatusMedicamento status;
    
    private String alteracaoPendente;
    private String responsavelSolicitacao; // Campo que estava dando aviso de "não usado"
    private StatusRevisao statusRevisao;

    // Construtor para novo cadastro
    public Medicamento(String nome, String usoPrincipal, String contraindicacoes) {
        setNome(nome);
        setUsoPrincipal(usoPrincipal);
        setContraindicacoes(contraindicacoes);
        this.status = StatusMedicamento.ATIVO;
        this.statusRevisao = StatusRevisao.NAO_APLICAVEL;
    }
    
    // Construtor completo (usado pelo Repositório)
    public Medicamento(String id, String nome, String usoPrincipal, String contraindicacoes, StatusMedicamento status, String alteracaoPendente, StatusRevisao statusRevisao, String responsavelSolicitacao) {
        this.id = id;
        this.nome = nome;
        this.usoPrincipal = usoPrincipal;
        this.contraindicacoes = contraindicacoes;
        this.status = status;
        this.alteracaoPendente = alteracaoPendente;
        this.statusRevisao = statusRevisao;
        this.responsavelSolicitacao = responsavelSolicitacao;
    }

    // Setters com validações
    public void setNome(String nome) {
        notBlank(nome, "O nome do medicamento é obrigatório");
        this.nome = nome;
    }

    public void setUsoPrincipal(String usoPrincipal) {
        notBlank(usoPrincipal, "O uso principal do medicamento é obrigatório");
        this.usoPrincipal = usoPrincipal;
    }
    
    public void setStatus(StatusMedicamento status) {
        this.status = status;
    }

    public void setContraindicacoes(String contraindicacoes) {
        if (contraindicacoes != null && !contraindicacoes.isBlank()) {
            if (contraindicacoes.matches(".*[\\@\\%\\$\\^\\|].*")) { 
                 throw new IllegalArgumentException("O campo Contraindicações contém caracteres especiais inválidos.");
            }
        }
        this.contraindicacoes = contraindicacoes != null && !contraindicacoes.isBlank() ? contraindicacoes.trim() : null;
    }

    public void setAlteracaoPendente(String alteracaoPendente) {
        this.alteracaoPendente = alteracaoPendente;
        this.statusRevisao = StatusRevisao.PENDENTE;
    }
    
    public void setResponsavelSolicitacao(String responsavelSolicitacao) { // NOVO SETTER
        this.responsavelSolicitacao = responsavelSolicitacao;
    }

    public void aplicarAlteracaoPendente() {
        if (this.alteracaoPendente != null) {
            this.contraindicacoes = this.alteracaoPendente;
            this.alteracaoPendente = null;
            this.statusRevisao = StatusRevisao.APROVADA;
        }
    }
    
    // Getters
    public String getId() { return id; }
    public String getNome() { return nome; }
    public String getUsoPrincipal() { return usoPrincipal; }
    public String getContraindicacoes() { return contraindicacoes; }
    public StatusMedicamento getStatus() { return status; }
    public String getAlteracaoPendente() { return alteracaoPendente; }
    public StatusRevisao getStatusRevisao() { return statusRevisao; }
    public String getResponsavelSolicitacao() { return responsavelSolicitacao; } // Getter
    
    @Override
    public String toString() { // PADRÃO DDD DO PROFESSOR
        return nome; 
    }
}