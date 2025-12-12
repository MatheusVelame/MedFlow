package br.com.medflow.infraestrutura.persistencia.jpa.administracao;

import br.com.medflow.aplicacao.administracao.funcionarios.FuncionarioDetalhes;
import br.com.medflow.aplicacao.administracao.funcionarios.FuncionarioRepositorioAplicacao;
import br.com.medflow.aplicacao.administracao.funcionarios.FuncionarioResumo;
import br.com.medflow.aplicacao.administracao.funcionarios.HistoricoEntradaResumo;
import br.com.medflow.dominio.administracao.funcionarios.StatusFuncionario;
import br.com.medflow.infraestrutura.persistencia.jpa.JpaMapeador;

import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// Este é o Adapter: implementa o Port (FuncionarioRepositorioAplicacao)
// e usa a tecnologia externa (Spring Data JPA)
@Component("funcionarioRepositorioAplicacaoImpl")
public class FuncionarioRepositorioAplicacaoImpl implements FuncionarioRepositorioAplicacao {

    private final FuncionarioJpaRepository jpaRepository;
    private final JpaMapeador mapeador;
    
    // O JpaMapeador deve ser injetado para traduzir entidades JPA em DTOs
    public FuncionarioRepositorioAplicacaoImpl(
            FuncionarioJpaRepository jpaRepository, 
            JpaMapeador mapeador) {
        this.jpaRepository = jpaRepository;
        this.mapeador = mapeador;
    }

    @Override
    public List<FuncionarioResumo> pesquisarResumos() {
        // Encontra todas as entidades JPA (sem histórico para resumo, mais eficiente)
        List<FuncionarioJpa> jpas = jpaRepository.findAll();
        
        // Mapeia cada entidade JPA para o DTO de Resumo da Camada de Aplicação
        return jpas.stream()
            .map(jpa -> mapeador.map(jpa, FuncionarioResumo.class))
            .collect(Collectors.toList());
    }

    @Override
    public Optional<FuncionarioDetalhes> obterDetalhesPorId(Integer id) {
        // Usa JOIN FETCH para carregar o histórico junto com o funcionário
        Optional<FuncionarioJpa> jpaOptional = jpaRepository.findByIdWithHistorico(id);

        if (jpaOptional.isEmpty()) {
            return Optional.empty();
        }
        
        // O histórico já está carregado via JOIN FETCH
        FuncionarioJpa jpa = jpaOptional.get();
        
        // Mapeia manualmente porque FuncionarioDetalhes é uma interface com HistoricoEntradaResumo também interface
        FuncionarioDetalhes detalhes = criarFuncionarioDetalhes(jpa);
        
        return Optional.of(detalhes);
    }
    
    /**
     * Cria uma implementação de FuncionarioDetalhes a partir de FuncionarioJpa.
     * Necessário porque FuncionarioDetalhes é uma interface e o histórico também é uma interface.
     */
    private FuncionarioDetalhes criarFuncionarioDetalhes(FuncionarioJpa jpa) {
        return new FuncionarioDetalhes() {
            @Override
            public Integer getId() {
                return jpa.getId();
            }

            @Override
            public String getNome() {
                return jpa.getNome();
            }

            @Override
            public String getFuncao() {
                return jpa.getFuncao();
            }

            @Override
            public String getContato() {
                return jpa.getContato();
            }

            @Override
            public br.com.medflow.dominio.administracao.funcionarios.StatusFuncionario getStatus() {
                return jpa.getStatus();
            }

            @Override
            public List<HistoricoEntradaResumo> getHistorico() {
                if (jpa.getHistorico() == null) {
                    return List.of();
                }
                
                return jpa.getHistorico().stream()
                    .map(historicoJpa -> new HistoricoEntradaResumo() {
                        @Override
                        public String getAcao() {
                            return historicoJpa.getAcao() != null ? historicoJpa.getAcao().name() : null;
                        }

                        @Override
                        public String getDescricao() {
                            return historicoJpa.getDescricao();
                        }

                        @Override
                        public Integer getResponsavelId() {
                            return historicoJpa.getResponsavelId();
                        }

                        @Override
                        public java.time.LocalDateTime getDataHora() {
                            return historicoJpa.getDataHora();
                        }
                    })
                    .collect(java.util.stream.Collectors.toList());
            }
        };
    }

    @Override
    public List<FuncionarioResumo> pesquisarPorStatus(StatusFuncionario status) {
        // Para resumo, não precisa do histórico, então usa a query simples
        List<FuncionarioJpa> jpas = jpaRepository.findByStatus(status);

        // Mapeia as entidades JPA para a lista de DTOs de Resumo
        return jpas.stream()
            .map(jpa -> mapeador.map(jpa, FuncionarioResumo.class))
            .collect(Collectors.toList());
    }

    @Override
    public List<FuncionarioResumo> pesquisarPorFuncao(String funcao) {
        // Para resumo, não precisa do histórico, então usa a query simples
        List<FuncionarioJpa> jpas = jpaRepository.findByFuncaoIgnoreCase(funcao);

        // Mapeia as entidades JPA para a lista de DTOs de Resumo
        return jpas.stream()
            .map(jpa -> mapeador.map(jpa, FuncionarioResumo.class))
            .collect(Collectors.toList());
    }
}
