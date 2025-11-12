package br.com.medflow.aplicacao.catalogo.medicamentos;

import br.com.medflow.dominio.catalogo.medicamentos.StatusMedicamento;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MedicamentoDetalhes {
	
	Integer getId();
	String getNome();
	String getUsoPrincipal();
	String getContraindicacoes();
	StatusMedicamento getStatus();
	
	List<HistoricoEntradaResumo> getHistorico();
	Optional<RevisaoPendenteResumo> getRevisaoPendente();
}

interface HistoricoEntradaResumo {
    String getAcao(); 
    String getDescricao();
    String getResponsavelNome();
    LocalDateTime getDataHora();
}

interface RevisaoPendenteResumo {
    String getStatusRevisao();
    String getNovoValor();
    String getSolicitanteNome();
}