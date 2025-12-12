package br.com.medflow.dominio.financeiro.convenios;

import java.time.LocalDateTime;
import static org.apache.commons.lang3.Validate.notNull;

/**
 * Evento de Domínio que é publicado quando o status de um convênio é alterado.
 * Contém informações sobre a mudança de status para que outros módulos reajam.
 */
public class ConvenioStatusAlteradoEvent {

    private final ConvenioId convenioId;
    private final StatusConvenio statusAnterior;
    private final StatusConvenio novoStatus;
    private final UsuarioResponsavelId responsavelId;
    private final LocalDateTime dataAlteracao;

    public ConvenioStatusAlteradoEvent(ConvenioId convenioId, StatusConvenio statusAnterior, 
                                       StatusConvenio novoStatus, UsuarioResponsavelId responsavelId) {
        notNull(convenioId, "O ID do convênio não pode ser nulo ao criar o evento.");
        notNull(statusAnterior, "O status anterior não pode ser nulo ao criar o evento.");
        notNull(novoStatus, "O novo status não pode ser nulo ao criar o evento.");
        notNull(responsavelId, "O responsável não pode ser nulo ao criar o evento.");
        
        this.convenioId = convenioId;
        this.statusAnterior = statusAnterior;
        this.novoStatus = novoStatus;
        this.responsavelId = responsavelId;
        this.dataAlteracao = LocalDateTime.now();
    }

    public ConvenioId getConvenioId() {
        return convenioId;
    }

    public StatusConvenio getStatusAnterior() {
        return statusAnterior;
    }

    public StatusConvenio getNovoStatus() {
        return novoStatus;
    }

    public UsuarioResponsavelId getResponsavelId() {
        return responsavelId;
    }

    public LocalDateTime getDataAlteracao() {
        return dataAlteracao;
    }
}

