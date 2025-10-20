package br.com.medflow.dominio.convenios;

import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Convenio {
    private ConvenioId id;
    private String nome;
    private String codigoIdentificacao;
    private StatusConvenio status;
    private List<HistoricoEntrada> historico = new ArrayList<>();

    // ===== CONSTRUTORES =====
    public Convenio(String nome, String codigoIdentificacao, UsuarioResponsavelId responsavelId) {
        notNull(responsavelId, "O responsável pela criação não pode ser nulo.");
        setNome(nome);
        setCodigoIdentificacao(codigoIdentificacao);
        this.status = StatusConvenio.ATIVO;
        adicionarEntradaHistorico(AcaoHistorico.CRIACAO, "Convênio criado com status ATIVO", responsavelId);
    }

    public Convenio(ConvenioId id, String nome, String codigoIdentificacao, StatusConvenio status, List<HistoricoEntrada> historico) {
        notNull(id, "O ID do convênio não pode ser nulo na reconstrução.");
        this.id = id;
        this.nome = nome;
        this.codigoIdentificacao = codigoIdentificacao;
        this.status = status;
        if (historico != null) {
            this.historico.addAll(historico);
        }
    }

    // ===== MÉTODOS DE STATUS =====
    public void mudarStatus(StatusConvenio novoStatus, UsuarioResponsavelId responsavelId) {
        
        if (this.status != novoStatus) {
            this.status = novoStatus;
            adicionarEntradaHistorico(AcaoHistorico.ATUALIZACAO, "Status alterado para: " + novoStatus.name(), responsavelId);
        }
    }

    
    public void validarExclusao(boolean temProcedimentoAtivo, UsuarioResponsavelId responsavelId) {
        notNull(responsavelId, "O responsável pela exclusão não pode ser nulo.");

        if (this.status == StatusConvenio.ATIVO && temProcedimentoAtivo) {
            throw new IllegalStateException("Convênio ATIVO com procedimento ATIVO não pode ser excluído.");
        }
        
        if (this.status == StatusConvenio.ATIVO && !temProcedimentoAtivo) {
             throw new IllegalStateException("O Convênio deve estar INATIVO para ser excluído.");
        }
        
    }

    // ===== MÉTODOS DE ALTERAÇÃO =====
    public void alterarNome(String novoNome, UsuarioResponsavelId responsavelId) {
        notNull(responsavelId, "O responsável pela alteração não pode ser nulo.");
        notBlank(novoNome, "O nome do convênio é obrigatório");
        
        // Regra de Negócio: Alteração só pode ocorrer se estiver ATIVO
        if (this.status != StatusConvenio.ATIVO) {
            throw new IllegalStateException("Não é possível alterar dados de um convênio INATIVO.");
        }
        
        if (!this.nome.trim().equalsIgnoreCase(novoNome.trim())) {
            this.nome = novoNome.trim();
            adicionarEntradaHistorico(AcaoHistorico.ATUALIZACAO, "Nome alterado para: " + novoNome, responsavelId);
        }
    }

    // ===== MÉTODOS DE CADASTRO =====
    public void setNome(String nome) {
        notBlank(nome, "O nome do convênio é obrigatório");
        this.nome = nome.trim();
    }

    public void setCodigoIdentificacao(String codigoIdentificacao) {
        notBlank(codigoIdentificacao, "O código de identificação do convênio é obrigatório");
        validarCodigoIdentificacao(codigoIdentificacao);
        this.codigoIdentificacao = codigoIdentificacao;
    }

    private void validarCodigoIdentificacao(String codigoIdentificacao) {
        String regex = "^[a-zA-Z0-9áéíóúÁÉÍÓÚãõñÃÕÑçÇàÀ.,\\s()\\-]+$";
        if (!codigoIdentificacao.matches(regex)) {
            throw new IllegalArgumentException("Código de identificação contém caracteres especiais inválidos.");
        }
    }


 public Convenio.HistoricoEntrada adicionarEntradaHistorico(AcaoHistorico acao, String descricao, UsuarioResponsavelId responsavelId) {
     notNull(responsavelId, "O responsável pela ação não pode ser nulo.");
     
     HistoricoEntrada novaEntrada = new HistoricoEntrada(acao, descricao, responsavelId, LocalDateTime.now());
     historico.add(novaEntrada);
     
     return novaEntrada;
 }

    // ===== GETTERS e CLASSE INTERNA HISTÓRICO (mantidos) =====
    // ... (Getters e HistoricoEntrada)
    public ConvenioId getId() { return id; }
    public String getNome() { return nome; }
    public String getCodigoIdentificacao() { return codigoIdentificacao; }
    public StatusConvenio getStatus() { return status; }
    public List<HistoricoEntrada> getHistorico() { return List.copyOf(historico); }

    public static class HistoricoEntrada {
        private final AcaoHistorico acao;
        private final String descricao;
        private final UsuarioResponsavelId responsavel;
        private final LocalDateTime dataHora;

        public HistoricoEntrada(AcaoHistorico acao, String descricao, UsuarioResponsavelId responsavel, LocalDateTime dataHora) {
            this.acao = acao;
            this.descricao = descricao;
            this.responsavel = responsavel;
            this.dataHora = dataHora;
        }

        public AcaoHistorico getAcao() { return acao; }
        public String getDescricao() { return descricao; }
        public UsuarioResponsavelId getResponsavel() { return responsavel; }
        public LocalDateTime getDataHora() { return dataHora; }
    }
}