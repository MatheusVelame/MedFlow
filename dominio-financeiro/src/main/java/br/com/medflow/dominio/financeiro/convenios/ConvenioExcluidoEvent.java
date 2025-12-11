package br.com.medflow.dominio.financeiro.convenios;

import java.time.LocalDateTime;
import static org.apache.commons.lang3.Validate.notNull;

/**
 * Evento de Domínio que é publicado quando um convênio é excluído permanentemente.
 * Contém informações sobre a exclusão para que outros módulos reajam.
 */
public class ConvenioExcluidoEvent {

    private final ConvenioId convenioId;
    private final String nome;
    private final String codigoIdentificacao;
    private final StatusConvenio statusAntesExclusao;
    private final UsuarioResponsavelId responsavelId;
    private final LocalDateTime dataExclusao;

    public ConvenioExcluidoEvent(ConvenioId convenioId, String nome, String codigoIdentificacao,
                                 StatusConvenio statusAntesExclusao, UsuarioResponsavelId responsavelId) {
        notNull(convenioId, "O ID do convênio não pode ser nulo ao criar o evento.");
        notNull(statusAntesExclusao, "O status antes da exclusão não pode ser nulo ao criar o evento.");
        notNull(responsavelId, "O responsável não pode ser nulo ao criar o evento.");
        
        this.convenioId = convenioId;
        this.nome = nome;
        this.codigoIdentificacao = codigoIdentificacao;
        this.statusAntesExclusao = statusAntesExclusao;
        this.responsavelId = responsavelId;
        this.dataExclusao = LocalDateTime.now();
    }

    public ConvenioId getConvenioId() {
        return convenioId;
    }

    public String getNome() {
        return nome;
    }

    public String getCodigoIdentificacao() {
        return codigoIdentificacao;
    }

    public StatusConvenio getStatusAntesExclusao() {
        return statusAntesExclusao;
    }

    public UsuarioResponsavelId getResponsavelId() {
        return responsavelId;
    }

    public LocalDateTime getDataExclusao() {
        return dataExclusao;
    }
}

