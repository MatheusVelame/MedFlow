package br.com.medflow.infraestrutura.persistencia.jpa;

import br.com.medflow.dominio.catalogo.medicamentos.HistoricoEntrada;
import br.com.medflow.infraestrutura.persistencia.jpa.catalogo.HistoricoEntradaJpa;
import br.com.medflow.dominio.catalogo.medicamentos.Medicamento;
import br.com.medflow.infraestrutura.persistencia.jpa.catalogo.MedicamentoJpa;

// NOVOS IMPORTS PARA CONSULTAS
import br.com.medflow.aplicacao.atendimento.consultas.ConsultaDetalhes;
import br.com.medflow.aplicacao.atendimento.consultas.ConsultaResumo;
import br.com.medflow.infraestrutura.persistencia.jpa.atendimento.ConsultaJpa; // Assumindo o pacote

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

        // === 3. NOVOS MAPEAMENTOS JPA (ConsultaJpa) para DTOs de Aplicação (Queries) ===
        // O ModelMapper mapeará automaticamente os campos com o mesmo nome (ex: id, pacienteNome)
        
        // Mapeamento para DTO de Detalhes
        createTypeMap(ConsultaJpa.class, ConsultaDetalhes.class);
        
        // Mapeamento para DTO de Resumo
        createTypeMap(ConsultaJpa.class, ConsultaResumo.class);
	}
}