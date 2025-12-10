package br.com.medflow.infraestrutura.persistencia.jpa.atendimento;

import br.com.medflow.aplicacao.atendimento.consultas.ConsultaDetalhes;
import br.com.medflow.aplicacao.atendimento.consultas.ConsultaRepositorioAplicacao;
import br.com.medflow.aplicacao.atendimento.consultas.ConsultaResumo;
import br.com.medflow.infraestrutura.persistencia.jpa.JpaMapeador;

import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// Este é o Adapter: implementa o Port (ConsultaRepositorioAplicacao)
// e usa a tecnologia externa (Spring Data JPA)
@Component("consultaRepositorioAplicacaoImpl")
public class ConsultaRepositorioAplicacaoImpl implements ConsultaRepositorioAplicacao {

    private final ConsultaJpaRepository jpaRepository;
    private final JpaMapeador mapeador;
    
    // O JpaMapeador deve ser injetado para traduzir entidades JPA em DTOs
    public ConsultaRepositorioAplicacaoImpl(
            ConsultaJpaRepository jpaRepository, 
            JpaMapeador mapeador) {
        this.jpaRepository = jpaRepository;
        this.mapeador = mapeador;
    }

    @Override
    public List<ConsultaResumo> pesquisarResumos() {
        // Encontra todas as entidades JPA
        List<ConsultaJpa> jpas = jpaRepository.findAll();
        
        // Mapeia cada entidade JPA para o DTO de Resumo da Camada de Aplicação
        // NOTA: Para um projeto real, seria necessário buscar os nomes do Médico/Paciente 
        // de outros Bounded Contexts (ou de uma Projection/View otimizada)
        return jpas.stream()
            .map(jpa -> mapeador.map(jpa, ConsultaResumo.class))
            .collect(Collectors.toList());
    }

    @Override
    public Optional<ConsultaDetalhes> obterDetalhesPorId(Integer id) {
        Optional<ConsultaJpa> jpaOptional = jpaRepository.findById(id);

        if (jpaOptional.isEmpty()) {
            return Optional.empty();
        }
        
        // Mapeia a entidade JPA completa para o DTO de Detalhes da Camada de Aplicação
        return jpaOptional.map(jpa -> mapeador.map(jpa, ConsultaDetalhes.class));
    }

    @Override
    public List<ConsultaResumo> pesquisarPorPacienteId(Integer pacienteId) {
        List<ConsultaJpa> jpas = jpaRepository.findByPacienteId(pacienteId);

        // Mapeia as entidades JPA para a lista de DTOs de Resumo
        return jpas.stream()
            .map(jpa -> mapeador.map(jpa, ConsultaResumo.class))
            .collect(Collectors.toList());
    }
}