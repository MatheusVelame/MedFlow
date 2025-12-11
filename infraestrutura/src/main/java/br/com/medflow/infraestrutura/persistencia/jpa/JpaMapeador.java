// Localização: infraestrutura/src/main/java/br/com/medflow/infraestrutura/persistencia/jpa/JpaMapeador.java

package br.com.medflow.infraestrutura.persistencia.jpa;

import br.com.medflow.dominio.catalogo.medicamentos.HistoricoEntrada;
import br.com.medflow.infraestrutura.persistencia.jpa.catalogo.HistoricoEntradaJpa;
import br.com.medflow.dominio.catalogo.medicamentos.Medicamento;
import br.com.medflow.infraestrutura.persistencia.jpa.catalogo.MedicamentoJpa;

// NOVOS IMPORTS PARA CONSULTAS
import br.com.medflow.aplicacao.atendimento.consultas.ConsultaDetalhes;
import br.com.medflow.aplicacao.atendimento.consultas.ConsultaResumo;
import br.com.medflow.infraestrutura.persistencia.jpa.atendimento.ConsultaJpa; 

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class JpaMapeador extends ModelMapper {

	public JpaMapeador() {
		super();
		
        // === 1. Mapeamento de DOMÍNIO (HistoricoEntrada) para JPA ===
        createTypeMap(HistoricoEntrada.class, HistoricoEntradaJpa.class)
            .addMappings(mapper -> {
                // Mapeia o ID do VO UsuarioResponsavelId para o campo Integer do JPA
                mapper.map(
                    src -> src.getResponsavel().getId(), 
                    HistoricoEntradaJpa::setResponsavelId
                );
                
                // Garante o mapeamento do LocalDateTime (DATA_HORA)
                mapper.map(
                    HistoricoEntrada::getDataHora, 
                    HistoricoEntradaJpa::setDataHora
                );
                
                // Linha de skip removida, pois setMedicamento não existe mais no Jpa.
            });
            
        // === 2. Mapeamento de DOMÍNIO (Medicamento) para JPA ===
        createTypeMap(Medicamento.class, MedicamentoJpa.class)
            .addMappings(mapper -> {
                // Linha de skip removida, pois setHistorico não existe mais no Jpa.
            });
            
        // === 3. NOVOS MAPEAMENTOS JPA (ConsultaJpa) para DTOs de Aplicação (Queries) ===
        // Mapeamento para DTO de Detalhes
        createTypeMap(ConsultaJpa.class, ConsultaDetalhes.class);
        
        // Mapeamento para DTO de Resumo
        createTypeMap(ConsultaJpa.class, ConsultaResumo.class);
	}
}