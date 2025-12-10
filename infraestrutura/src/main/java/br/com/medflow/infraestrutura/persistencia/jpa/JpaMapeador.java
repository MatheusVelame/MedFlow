package br.com.medflow.infraestrutura.persistencia.jpa;

import br.com.medflow.dominio.catalogo.medicamentos.HistoricoEntrada;
import br.com.medflow.infraestrutura.persistencia.jpa.catalogo.HistoricoEntradaJpa;
import br.com.medflow.dominio.catalogo.medicamentos.Medicamento;
import br.com.medflow.infraestrutura.persistencia.jpa.catalogo.MedicamentoJpa;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class JpaMapeador extends ModelMapper {

	public JpaMapeador() {
		super();
		
        // === 1. Mapeamento de DOMÍNIO (HistoricoEntrada) para JPA ===
        createTypeMap(HistoricoEntrada.class, HistoricoEntradaJpa.class)
            .addMappings(mapper -> {
                // CORRIGIDO: Agora usa src.getResponsavel().getId()
                mapper.map(
                    src -> src.getResponsavel().getId(), 
                    HistoricoEntradaJpa::setResponsavelId
                );
                
                // Garante o mapeamento do LocalDateTime (DATA_HORA)
                mapper.map(
                    HistoricoEntrada::getDataHora, 
                    HistoricoEntradaJpa::setDataHora
                );
                
                // Ignora o mapeamento bidirecional neste sentido
                mapper.skip(HistoricoEntradaJpa::setMedicamento);
            });
            
        // === 2. Mapeamento de DOMÍNIO (Medicamento) para JPA ===
        createTypeMap(Medicamento.class, MedicamentoJpa.class)
            .addMappings(mapper -> {
                // Ignora a coleção bidirecional (a lógica está no RepositorioImpl)
                mapper.skip(MedicamentoJpa::setHistorico);
            });
            
        // Se precisar de mapeamento do ID do Medicamento (VO -> int/Integer)
        // createTypeMap(Medicamento.class, MedicamentoJpa.class)
        //     .addMappings(mapper -> {
        //         mapper.map(src -> src.getId().getId(), MedicamentoJpa::setId); 
        //     });
	}
}