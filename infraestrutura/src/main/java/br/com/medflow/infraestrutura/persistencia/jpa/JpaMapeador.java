package br.com.medflow.infraestrutura.persistencia.jpa;

import br.com.medflow.dominio.catalogo.medicamentos.HistoricoEntrada;
import br.com.medflow.infraestrutura.persistencia.jpa.catalogo.HistoricoEntradaJpa;
import br.com.medflow.dominio.catalogo.medicamentos.Medicamento;
import br.com.medflow.infraestrutura.persistencia.jpa.catalogo.MedicamentoJpa;

// NOVOS IMPORTS PARA CONSULTAS
import br.com.medflow.aplicacao.atendimento.consultas.ConsultaDetalhes;
import br.com.medflow.aplicacao.atendimento.consultas.ConsultaResumo;
import br.com.medflow.infraestrutura.persistencia.jpa.atendimento.ConsultaJpa;

// IMPORTS PARA FUNCIONÁRIOS
import br.com.medflow.dominio.administracao.funcionarios.Funcionario;
import br.com.medflow.infraestrutura.persistencia.jpa.administracao.FuncionarioJpa;
// Não importar HistoricoEntradaJpa de administracao para evitar conflito com o de catalogo
import br.com.medflow.aplicacao.administracao.funcionarios.FuncionarioResumo;

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
		// Configura o ModelMapper para ser mais permissivo durante a inicialização
		this.getConfiguration().setSkipNullEnabled(true);
		this.getConfiguration().setAmbiguityIgnored(true);
		
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
            

        // === 3. CONFIGURAÇÃO DE MAPA PARA CONSULTAS (RESOLVE O PROBLEMA DO NULL) ===
        
        // Mapeamento de ConsultaJpa para ConsultaDetalhes (necessário para campos finais)
        createTypeMap(ConsultaJpa.class, ConsultaDetalhes.class);
        
        // Mapeamento de ConsultaJpa para ConsultaResumo (necessário para campos finais)
        createTypeMap(ConsultaJpa.class, ConsultaResumo.class);
        

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
        
        
            // Mapeamento de DOMÍNIO (HistoricoEntrada de Funcionario) para JPA
            createTypeMap(
                br.com.medflow.dominio.administracao.funcionarios.Funcionario.HistoricoEntrada.class, 
                br.com.medflow.infraestrutura.persistencia.jpa.administracao.HistoricoEntradaJpa.class)
                .addMappings(mapper -> {
                    // Ignora campos que não devem ser mapeados automaticamente
                    mapper.skip(br.com.medflow.infraestrutura.persistencia.jpa.administracao.HistoricoEntradaJpa::setId);
                    mapper.skip(br.com.medflow.infraestrutura.persistencia.jpa.administracao.HistoricoEntradaJpa::setFuncionario);
                    
                    // Mapeia explicitamente os campos que devem ser mapeados automaticamente
                    mapper.map(
                        br.com.medflow.dominio.administracao.funcionarios.Funcionario.HistoricoEntrada::getAcao,
                        br.com.medflow.infraestrutura.persistencia.jpa.administracao.HistoricoEntradaJpa::setAcao
                    );
                    mapper.map(
                        br.com.medflow.dominio.administracao.funcionarios.Funcionario.HistoricoEntrada::getDescricao,
                        br.com.medflow.infraestrutura.persistencia.jpa.administracao.HistoricoEntradaJpa::setDescricao
                    );
                    
                    // Mapeia o código do responsável (String) para Integer
                    // NOTA: UsuarioResponsavelId de funcionários usa getCodigo() (String), não getId()
                    mapper.map(
                        src -> {
                            if (src == null || src.getResponsavel() == null) {
                                return null;
                            }
                            String codigo = src.getResponsavel().getCodigo();
                            try {
                                return Integer.parseInt(codigo);
                            } catch (NumberFormatException e) {
                                throw new RuntimeException("Erro ao converter código do responsável para Integer: " + codigo, e);
                            }
                        }, 
                        br.com.medflow.infraestrutura.persistencia.jpa.administracao.HistoricoEntradaJpa::setResponsavelId
                    );
                    
                    // Garante o mapeamento do LocalDateTime (DATA_HORA)
                    mapper.map(
                        br.com.medflow.dominio.administracao.funcionarios.Funcionario.HistoricoEntrada::getDataHora, 
                        br.com.medflow.infraestrutura.persistencia.jpa.administracao.HistoricoEntradaJpa::setDataHora
                    );
                });
                
            // Mapeamento de DOMÍNIO (Funcionario) para JPA
            createTypeMap(Funcionario.class, FuncionarioJpa.class)
                .addMappings(mapper -> {
                    // Ignora a coleção bidirecional (a lógica está no RepositorioImpl)
                    mapper.skip(FuncionarioJpa::setHistorico);
                });
            

            createTypeMap(FuncionarioJpa.class, FuncionarioResumo.class);
            
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

        // === 7. Mapeamento de DOMÍNIO (HistoricoEntrada de Exame) para JPA ===
        try {
            createTypeMap(
                br.com.medflow.dominio.atendimento.exames.HistoricoEntrada.class,
                br.com.medflow.infraestrutura.persistencia.jpa.atendimento.HistoricoExameJpa.class)
                .addMappings(mapper -> {
                    mapper.skip(br.com.medflow.infraestrutura.persistencia.jpa.atendimento.HistoricoExameJpa::setId);
                    mapper.skip(br.com.medflow.infraestrutura.persistencia.jpa.atendimento.HistoricoExameJpa::setExame);

                    mapper.map(
                        br.com.medflow.dominio.atendimento.exames.HistoricoEntrada::getAcao,
                        br.com.medflow.infraestrutura.persistencia.jpa.atendimento.HistoricoExameJpa::setAcao
                    );

                    mapper.map(
                        br.com.medflow.dominio.atendimento.exames.HistoricoEntrada::getDescricao,
                        br.com.medflow.infraestrutura.persistencia.jpa.atendimento.HistoricoExameJpa::setDescricao
                    );

                    // Mapeia responsavel.getValor() (Long)
                    mapper.map(
                        src -> src.getUsuario().getValor(),
                        br.com.medflow.infraestrutura.persistencia.jpa.atendimento.HistoricoExameJpa::setResponsavelId
                    );

                    mapper.map(
                        br.com.medflow.dominio.atendimento.exames.HistoricoEntrada::getDataHora,
                        br.com.medflow.infraestrutura.persistencia.jpa.atendimento.HistoricoExameJpa::setDataHora
                    );
                });

            // Mapeamento inverso: JPA -> Domínio
            createTypeMap(
                br.com.medflow.infraestrutura.persistencia.jpa.atendimento.HistoricoExameJpa.class,
                br.com.medflow.dominio.atendimento.exames.HistoricoEntrada.class)
                .setProvider(request -> {
                    var jpa = (br.com.medflow.infraestrutura.persistencia.jpa.atendimento.HistoricoExameJpa) request.getSource();
                    return new br.com.medflow.dominio.atendimento.exames.HistoricoEntrada(
                        jpa.getAcao(),
                        jpa.getDescricao(),
                        new br.com.medflow.dominio.atendimento.exames.UsuarioResponsavelId(jpa.getResponsavelId())
                    );
                });
        } catch (Exception e) {
            System.err.println("Aviso: Não foi possível criar mapeamento explícito para HistoricoEntrada de Exame: " + e.getMessage());
        }

        // === 8. Mapeamento ExameJpa <-> Exame (simplificado, histórico tratado separadamente) ===
        try {
            createTypeMap(br.com.medflow.infraestrutura.persistencia.jpa.atendimento.ExameJpa.class, br.com.medflow.dominio.atendimento.exames.Exame.class)
                .setProvider(request -> {
                    var jpa = (br.com.medflow.infraestrutura.persistencia.jpa.atendimento.ExameJpa) request.getSource();

                    // Mapear historico via mapeador
                    var historicoDominio = jpa.getHistorico().stream()
                        .map(h -> map(h, br.com.medflow.dominio.atendimento.exames.HistoricoEntrada.class))
                        .toList();

                    return new br.com.medflow.dominio.atendimento.exames.Exame(
                        jpa.getId() == null ? null : new br.com.medflow.dominio.atendimento.exames.ExameId(jpa.getId()),
                        jpa.getPacienteId(),
                        jpa.getMedicoId(),
                        jpa.getTipoExame(),
                        jpa.getDataHora(),
                        jpa.getStatus(),
                        historicoDominio,
                        false,
                        false,
                        null
                    );
                });

            // Mapeamento domínio -> JPA (skip historico para evitar duplicação; será tratado manualmente no repositório)
            createTypeMap(br.com.medflow.dominio.atendimento.exames.Exame.class, br.com.medflow.infraestrutura.persistencia.jpa.atendimento.ExameJpa.class)
                .addMappings(mapper -> {
                    mapper.map(src -> src.getId() == null ? null : src.getId().getValor(), br.com.medflow.infraestrutura.persistencia.jpa.atendimento.ExameJpa::setId);
                    mapper.map(br.com.medflow.dominio.atendimento.exames.Exame::getPacienteId, br.com.medflow.infraestrutura.persistencia.jpa.atendimento.ExameJpa::setPacienteId);
                    mapper.map(br.com.medflow.dominio.atendimento.exames.Exame::getMedicoId, br.com.medflow.infraestrutura.persistencia.jpa.atendimento.ExameJpa::setMedicoId);
                    mapper.map(br.com.medflow.dominio.atendimento.exames.Exame::getTipoExame, br.com.medflow.infraestrutura.persistencia.jpa.atendimento.ExameJpa::setTipoExame);
                    mapper.map(br.com.medflow.dominio.atendimento.exames.Exame::getDataHora, br.com.medflow.infraestrutura.persistencia.jpa.atendimento.ExameJpa::setDataHora);
                    mapper.map(br.com.medflow.dominio.atendimento.exames.Exame::getStatus, br.com.medflow.infraestrutura.persistencia.jpa.atendimento.ExameJpa::setStatus);
                    mapper.skip(br.com.medflow.infraestrutura.persistencia.jpa.atendimento.ExameJpa::setHistorico);

                    mapper.map(src -> {
                        // extrair responsavel do último histórico, se existir
                        if (src.getHistorico() == null || src.getHistorico().isEmpty()) return null;
                        var ultima = src.getHistorico().get(src.getHistorico().size() - 1);
                        return ultima == null || ultima.getUsuario() == null ? null : ultima.getUsuario().getValor();
                    }, br.com.medflow.infraestrutura.persistencia.jpa.atendimento.ExameJpa::setResponsavelId);
                });
        } catch (Exception e) {
            System.err.println("Aviso: não foi possível criar mapeamento para Exame: " + e.getMessage());
        }

	}
}