package br.com.medflow.infraestrutura.persistencia.jpa.administracao;

import br.com.medflow.aplicacao.administracao.funcionarios.FuncionarioDetalhes;
import br.com.medflow.aplicacao.administracao.funcionarios.FuncionarioRepositorioAplicacao;
import br.com.medflow.aplicacao.administracao.funcionarios.FuncionarioResumo;
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
        // Encontra todas as entidades JPA
        List<FuncionarioJpa> jpas = jpaRepository.findAll();
        
        // Mapeia cada entidade JPA para o DTO de Resumo da Camada de Aplicação
        return jpas.stream()
            .map(jpa -> mapeador.map(jpa, FuncionarioResumo.class))
            .collect(Collectors.toList());
    }

    @Override
    public Optional<FuncionarioDetalhes> obterDetalhesPorId(Integer id) {
        Optional<FuncionarioJpa> jpaOptional = jpaRepository.findById(id);

        if (jpaOptional.isEmpty()) {
            return Optional.empty();
        }
        
        // Carrega o histórico com JOIN FETCH se necessário
        FuncionarioJpa jpa = jpaOptional.get();
        // O histórico já deve estar carregado se usar @EntityGraph ou JOIN FETCH na query
        // Por enquanto, assumimos que está carregado via LAZY quando necessário
        
        // Mapeia a entidade JPA completa para o DTO de Detalhes da Camada de Aplicação
        return Optional.of(mapeador.map(jpa, FuncionarioDetalhes.class));
    }

    @Override
    public List<FuncionarioResumo> pesquisarPorStatus(StatusFuncionario status) {
        List<FuncionarioJpa> jpas = jpaRepository.findByStatus(status);

        // Mapeia as entidades JPA para a lista de DTOs de Resumo
        return jpas.stream()
            .map(jpa -> mapeador.map(jpa, FuncionarioResumo.class))
            .collect(Collectors.toList());
    }

    @Override
    public List<FuncionarioResumo> pesquisarPorFuncao(String funcao) {
        List<FuncionarioJpa> jpas = jpaRepository.findByFuncaoIgnoreCase(funcao);

        // Mapeia as entidades JPA para a lista de DTOs de Resumo
        return jpas.stream()
            .map(jpa -> mapeador.map(jpa, FuncionarioResumo.class))
            .collect(Collectors.toList());
    }
}
