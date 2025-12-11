package br.com.medflow.dominio.financeiro.convenios;

import java.time.LocalDateTime;
import static org.apache.commons.lang3.Validate.notNull;

/**
 * Evento de Domínio que é publicado quando um novo convênio é criado com sucesso.
 * Contém informações essenciais para que outros módulos reajam à criação do convênio.
 */
public class ConvenioCriadoEvent {

    private final ConvenioId convenioId;
    private final String nome;
    private final String codigoIdentificacao;
    private final StatusConvenio status;
    private final UsuarioResponsavelId responsavelId;
    private final LocalDateTime dataCriacao;

    public ConvenioCriadoEvent(ConvenioId convenioId, String nome, String codigoIdentificacao, 
                               StatusConvenio status, UsuarioResponsavelId responsavelId) {
        notNull(convenioId, "O ID do convênio não pode ser nulo ao criar o evento.");
        notNull(status, "O status do convênio não pode ser nulo ao criar o evento.");
        notNull(responsavelId, "O responsável não pode ser nulo ao criar o evento.");
        
        this.convenioId = convenioId;
        this.nome = nome;
        this.codigoIdentificacao = codigoIdentificacao;
        this.status = status;
        this.responsavelId = responsavelId;
        this.dataCriacao = LocalDateTime.now();
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

    public StatusConvenio getStatus() {
        return status;
    }

    public UsuarioResponsavelId getResponsavelId() {
        return responsavelId;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }
}

