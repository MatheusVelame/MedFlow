package br.com.medflow.dominio.financeiro.convenios;

import java.time.LocalDateTime;
import static org.apache.commons.lang3.Validate.notNull;

/**
 * Evento de Domínio que é publicado quando o nome de um convênio é alterado.
 * Contém informações sobre a alteração para que outros módulos reajam.
 */
public class ConvenioNomeAlteradoEvent {

    private final ConvenioId convenioId;
    private final String nomeAnterior;
    private final String novoNome;
    private final UsuarioResponsavelId responsavelId;
    private final LocalDateTime dataAlteracao;

    public ConvenioNomeAlteradoEvent(ConvenioId convenioId, String nomeAnterior, 
                                    String novoNome, UsuarioResponsavelId responsavelId) {
        notNull(convenioId, "O ID do convênio não pode ser nulo ao criar o evento.");
        notNull(nomeAnterior, "O nome anterior não pode ser nulo ao criar o evento.");
        notNull(novoNome, "O novo nome não pode ser nulo ao criar o evento.");
        notNull(responsavelId, "O responsável não pode ser nulo ao criar o evento.");
        
        this.convenioId = convenioId;
        this.nomeAnterior = nomeAnterior;
        this.novoNome = novoNome;
        this.responsavelId = responsavelId;
        this.dataAlteracao = LocalDateTime.now();
    }

    public ConvenioId getConvenioId() {
        return convenioId;
    }

    public String getNomeAnterior() {
        return nomeAnterior;
    }

    public String getNovoNome() {
        return novoNome;
    }

    public UsuarioResponsavelId getResponsavelId() {
        return responsavelId;
    }

    public LocalDateTime getDataAlteracao() {
        return dataAlteracao;
    }
}

