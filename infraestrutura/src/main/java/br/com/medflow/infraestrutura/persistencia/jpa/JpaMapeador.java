package br.com.medflow.infraestrutura.persistencia.jpa;

import br.com.medflow.dominio.catalogo.medicamentos.HistoricoEntrada;
import br.com.medflow.infraestrutura.persistencia.jpa.catalogo.HistoricoEntradaJpa;
import br.com.medflow.dominio.catalogo.medicamentos.Medicamento;
import br.com.medflow.infraestrutura.persistencia.jpa.catalogo.MedicamentoJpa;

// NOVOS IMPORTS PARA CONSULTAS
import br.com.medflow.aplicacao.atendimento.consultas.ConsultaDetalhes;
import br.com.medflow.aplicacao.atendimento.consultas.ConsultaResumo;
import br.com.medflow.infraestrutura.persistencia.jpa.atendimento.ConsultaJpa;

// NOVOS IMPORTS PARA PRONTUÁRIO
import br.com.medflow.aplicacao.prontuario.ProntuarioDetalhes;
import br.com.medflow.aplicacao.prontuario.ProntuarioResumo;
import br.com.medflow.infraestrutura.persistencia.jpa.prontuario.ProntuarioJpa; 

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
        
        // === 4. NOVOS MAPEAMENTOS JPA (ProntuarioJpa) para DTOs de Aplicação (Queries) ===
        // Mapeamento para DTO de Detalhes
        createTypeMap(ProntuarioJpa.class, ProntuarioDetalhes.class);
        
        // Mapeamento para DTO de Resumo
        createTypeMap(ProntuarioJpa.class, ProntuarioResumo.class);
        
        // === 5. Mapeamento de DOMÍNIO (Convenio) para JPA ===
        createTypeMap(Convenio.class, ConvenioJpa.class)
            .addMappings(mapper -> {
                // Ignora a coleção bidirecional (a lógica está no RepositorioImpl)
                mapper.skip(ConvenioJpa::setHistorico);
            });
            
        // === 6. Mapeamento de DOMÍNIO (HistoricoEntrada de Convenio) para JPA ===
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