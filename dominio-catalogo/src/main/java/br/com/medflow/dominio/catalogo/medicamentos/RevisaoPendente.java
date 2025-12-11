// Localização: dominio-catalogo/src/main/java/br/com/medflow/dominio/catalogo/medicamentos/RevisaoPendente.java

package br.com.medflow.dominio.catalogo.medicamentos;

import java.util.Optional;
import static org.apache.commons.lang3.Validate.notNull;

public class RevisaoPendente {
    private final String novoValor;
    private final UsuarioResponsavelId solicitanteId;
    private StatusRevisao status;
    private Optional<UsuarioResponsavelId> revisorId;

    public RevisaoPendente(String novoValor, UsuarioResponsavelId solicitanteId) {
        notNull(solicitanteId, "O solicitante não pode ser nulo.");
        this.novoValor = novoValor;
        this.solicitanteId = solicitanteId;
        this.status = StatusRevisao.PENDENTE;
        this.revisorId = Optional.empty();
    }
    
    // CONSTRUTOR CORRIGIDO: Necessário para a RECONSTRUÇÃO (Mapeamento JPA -> Domínio)
    public RevisaoPendente(String novoValor, UsuarioResponsavelId solicitanteId, StatusRevisao status, UsuarioResponsavelId revisorId) {
        this.novoValor = novoValor;
        this.solicitanteId = solicitanteId;
        this.status = status;
        this.revisorId = Optional.ofNullable(revisorId);
    }

    public void aprovar(UsuarioResponsavelId revisorId) {
        if (this.status != StatusRevisao.PENDENTE) {
            throw new IllegalStateException("Apenas revisões PENDENTES podem ser aprovadas.");
        }
        this.status = StatusRevisao.APROVADA;
        this.revisorId = Optional.of(revisorId);
    }
    
    public void rejeitar(UsuarioResponsavelId revisorId) {
        if (this.status != StatusRevisao.PENDENTE) {
            throw new IllegalStateException("Apenas revisões PENDENTES podem ser rejeitadas.");
        }
        this.status = StatusRevisao.REPROVADA;
        this.revisorId = Optional.of(revisorId);
    }

    // Getters
    public String getNovoValor() { return novoValor; }
    public UsuarioResponsavelId getSolicitanteId() { return solicitanteId; }
    public StatusRevisao getStatus() { return status; }
    public Optional<UsuarioResponsavelId> getRevisorId() { return revisorId; }
}