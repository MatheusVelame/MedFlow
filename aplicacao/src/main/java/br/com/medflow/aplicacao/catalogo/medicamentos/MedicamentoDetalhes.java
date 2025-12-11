// Localização: aplicacao/src/main/java/br/com/medflow/aplicacao/catalogo/medicamentos/MedicamentoDetalhes.java

package br.com.medflow.aplicacao.catalogo.medicamentos;

import br.com.medflow.dominio.catalogo.medicamentos.StatusMedicamento;
import br.com.medflow.dominio.catalogo.medicamentos.StatusRevisao;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO para exibir todos os detalhes de um medicamento, incluindo histórico e revisão pendente.
 */
public class MedicamentoDetalhes {
    private final Integer id;
    private final String nome;
    private final String usoPrincipal;
    private final String contraindicacoes;
    private final StatusMedicamento status;
    
    private final RevisaoPendenteDetalhes revisaoPendente;
    private final List<HistoricoDetalhes> historico;

    public MedicamentoDetalhes(
        Integer id, 
        String nome, 
        String usoPrincipal, 
        String contraindicacoes, 
        StatusMedicamento status, 
        RevisaoPendenteDetalhes revisaoPendente, 
        List<HistoricoDetalhes> historico) {
        
        this.id = id;
        this.nome = nome;
        this.usoPrincipal = usoPrincipal;
        this.contraindicacoes = contraindicacoes;
        this.status = status;
        this.revisaoPendente = revisaoPendente;
        this.historico = historico;
    }

    // Classes/Records internas para estruturar os dados complexos (simplificando)

    /** Detalhes simplificados da entrada de histórico para o DTO. */
    public record HistoricoDetalhes(
        String acao, 
        String descricao, 
        Integer responsavelId,
        LocalDateTime dataHora) {}

    /** Detalhes simplificados da revisão pendente para o DTO. */
    public record RevisaoPendenteDetalhes(
        String novoValor,
        StatusRevisao status,
        Integer solicitanteId,
        Integer revisorId) {}
        
    // Getters
    public Integer getId() { return id; }
    public String getNome() { return nome; }
    public String getUsoPrincipal() { return usoPrincipal; }
    public String getContraindicacoes() { return contraindicacoes; }
    public StatusMedicamento getStatus() { return status; }
    public RevisaoPendenteDetalhes getRevisaoPendente() { return revisaoPendente; }
    public List<HistoricoDetalhes> getHistorico() { return historico; }
}