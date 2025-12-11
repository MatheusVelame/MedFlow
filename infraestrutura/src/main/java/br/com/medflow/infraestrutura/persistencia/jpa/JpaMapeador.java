package br.com.medflow.infraestrutura.persistencia.jpa;

import br.com.medflow.dominio.catalogo.medicamentos.HistoricoEntrada;
import br.com.medflow.infraestrutura.persistencia.jpa.catalogo.HistoricoEntradaJpa;
import br.com.medflow.dominio.catalogo.medicamentos.Medicamento;
import br.com.medflow.infraestrutura.persistencia.jpa.catalogo.MedicamentoJpa;
import br.com.medflow.dominio.financeiro.convenios.Convenio;
import br.com.medflow.infraestrutura.persistencia.jpa.financeiro.convenio.ConvenioJpa;
import br.com.medflow.infraestrutura.persistencia.jpa.financeiro.convenio.HistoricoConvenioJpa;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

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
            
        // === 3. Mapeamento de DOMÍNIO (Convenio) para JPA ===
        createTypeMap(Convenio.class, ConvenioJpa.class)
            .addMappings(mapper -> {
                // Ignora a coleção bidirecional (a lógica está no RepositorioImpl)
                mapper.skip(ConvenioJpa::setHistorico);
            });
            
        // === 4. Mapeamento de DOMÍNIO (HistoricoEntrada de Convenio) para JPA ===
        // HistoricoEntrada é uma classe interna de Convenio, então usamos reflexão
        try {
            // Obtém a classe interna HistoricoEntrada de Convenio
            Class<?> historicoEntradaConvenioClass = null;
            Class<?>[] classesInternas = Convenio.class.getDeclaredClasses();
            for (Class<?> classeInterna : classesInternas) {
                if (classeInterna.getSimpleName().equals("HistoricoEntrada")) {
                    historicoEntradaConvenioClass = classeInterna;
                    break;
                }
            }
            
            if (historicoEntradaConvenioClass != null) {
                createTypeMap(historicoEntradaConvenioClass, HistoricoConvenioJpa.class)
                    .addMappings(mapper -> {
                        // Mapeia responsavel.getId() para responsavelId
                        mapper.map(
                            src -> {
                                try {
                                    Method getResponsavel = src.getClass().getMethod("getResponsavel");
                                    Object responsavel = getResponsavel.invoke(src);
                                    Method getId = responsavel.getClass().getMethod("getId");
                                    return getId.invoke(responsavel);
                                } catch (Exception e) {
                                    throw new RuntimeException("Erro ao mapear responsavelId", e);
                                }
                            },
                            HistoricoConvenioJpa::setResponsavelId
                        );
                        
                        // Garante o mapeamento do LocalDateTime (DATA_HORA)
                        mapper.map(
                            src -> {
                                try {
                                    Method getDataHora = src.getClass().getMethod("getDataHora");
                                    return getDataHora.invoke(src);
                                } catch (Exception e) {
                                    throw new RuntimeException("Erro ao mapear dataHora", e);
                                }
                            },
                            HistoricoConvenioJpa::setDataHora
                        );
                    });
            }
        } catch (Exception e) {
            // Se não conseguir criar o mapeamento, o ModelMapper tentará mapear automaticamente
            System.err.println("Aviso: Não foi possível criar mapeamento explícito para HistoricoEntrada de Convenio: " + e.getMessage());
        }
	}
}