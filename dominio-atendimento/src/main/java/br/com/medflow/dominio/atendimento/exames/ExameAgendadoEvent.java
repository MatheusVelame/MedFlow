package br.com.medflow.dominio.atendimento.exames;

import java.time.LocalDateTime;
import static org.apache.commons.lang3.Validate.notNull;

/**
 * Evento de Domínio que é publicado quando um novo exame é agendado com sucesso.
 * Contém informações essenciais para que outros módulos (ex: Financeiro, Notificacao) reajam.
 */
public class ExameAgendadoEvent {

    private final ExameId exameId;
    private final Long pacienteId;
    private final Long medicoId;
    private final LocalDateTime dataHora;
    private final String tipoExame;
    private final LocalDateTime dataCriacao;

    public ExameAgendadoEvent(Exame exame) {
        notNull(exame.getId(), "O ID do exame não pode ser nulo ao criar o evento.");
        
        this.exameId = exame.getId();
        this.pacienteId = exame.getPacienteId();
        this.medicoId = exame.getMedicoId();
        
        // Assumindo que os getters getTipoExame() e getDataHora() existem na Entidade Exame
        this.tipoExame = exame.getTipoExame();
        this.dataHora = exame.getDataHora();
        
        this.dataCriacao = LocalDateTime.now();
    }
    
    // --- Getters ---

    public ExameId getExameId() {
        return exameId;
    }

    public Long getPacienteId() {
        return pacienteId;
    }
    
    public Long getMedicoId() {
        return medicoId;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public String getTipoExame() {
        return tipoExame;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }
}