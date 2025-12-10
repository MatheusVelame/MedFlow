// Localização: infraestrutura/src/main/java/br/com/medflow/infraestrutura/persistencia/jpa/atendimento/ConsultaRepositorioImpl.java

package br.com.medflow.infraestrutura.persistencia.jpa.atendimento;

import br.com.medflow.aplicacao.atendimento.consultas.ConsultaDetalhes;
import br.com.medflow.aplicacao.atendimento.consultas.ConsultaResumo;
import br.com.medflow.aplicacao.atendimento.consultas.ConsultaRepositorioAplicacao;
import br.com.medflow.dominio.atendimento.consultas.Consulta; 
import br.com.medflow.dominio.atendimento.consultas.ConsultaId; 
import br.com.medflow.dominio.atendimento.consultas.ConsultaRepositorio; 
import br.com.medflow.dominio.atendimento.consultas.StatusConsulta;

import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * ADAPTER: Implementa a porta de Escrita (Domain Repository) e a porta de Leitura (Application Repository).
 * Traduz objetos JPA (tecnologia) para objetos de Domínio/Aplicação (contrato).
 */
@Component
public class ConsultaRepositorioImpl implements ConsultaRepositorio, ConsultaRepositorioAplicacao {

    private final ConsultaJpaRepository jpaRepository;

    public ConsultaRepositorioImpl(ConsultaJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    // =====================================================================
    // IMPLEMENTAÇÃO DO DOMAIN REPOSITORY (PORTA DE ESCRITA/CUD)
    // =====================================================================

    @Override
    public Optional<Consulta> buscarPorId(ConsultaId id) {
        return jpaRepository.findById(id.getValor())
                .map(this::toDomain); // Converte ConsultaJpa para a Entidade de Domínio Consulta
    }

    @Override
    public void salvar(Consulta consulta) {
        // Converte a Entidade de Domínio Consulta para ConsultaJpa para persistir
        ConsultaJpa jpa = toJpa(consulta);
        jpaRepository.save(jpa);
    }
    
    // Métodos de mapeamento interno (Domain <=> JPA)
    
    private Consulta toDomain(ConsultaJpa jpa) {
        // Mapeamento simples de reconstrução
        return new Consulta(
            new ConsultaId(jpa.getId()),
            jpa.getDataHora(),
            jpa.getDescricao(),
            StatusConsulta.valueOf(jpa.getStatus()), 
            List.of() // Histórico (simplificado)
        );
    }
    
    private ConsultaJpa toJpa(Consulta consulta) {
        ConsultaJpa jpa = new ConsultaJpa();
        // Mapeamento do ID para atualização ou criação
        if (consulta.getId() != null) {
            jpa.setId(consulta.getId().getValor());
        }
        jpa.setDataHora(consulta.getDataHora());
        jpa.setDescricao(consulta.getDescricao());
        jpa.setStatus(consulta.getStatus().name()); 
        // Lógica para obter e setar pacienteId/medicoId do objeto Consulta real...
        // Exemplo: jpa.setPacienteId(consulta.getPacienteId().getValor()); 
        // Exemplo: jpa.setMedicoId(consulta.getMedicoId().getValor()); 
        
        return jpa;
    }

    // =====================================================================
    // IMPLEMENTAÇÃO DO APPLICATION REPOSITORY (PORTA DE LEITURA/QUERY)
    // =====================================================================

    @Override
    public List<ConsultaResumo> pesquisarResumos() {
        return jpaRepository.findAll().stream()
                .map(this::toResumoDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ConsultaDetalhes> obterDetalhesPorId(Integer id) {
        return jpaRepository.findById(id)
                .map(this::toDetalhesDto);
    }

    @Override
    public List<ConsultaResumo> pesquisarPorStatus(StatusConsulta status) {
        return jpaRepository.findByStatus(status.name()).stream()
                .map(this::toResumoDto)
                .collect(Collectors.toList());
    }
    
    // Métodos de mapeamento interno (JPA -> DTO)

    private ConsultaResumo toResumoDto(ConsultaJpa jpa) {
        // Simulação da busca de nomes
        String nomePaciente = "Paciente " + jpa.getPacienteId();
        String nomeMedico = "Médico " + jpa.getMedicoId();
        
        return new ConsultaResumo(
            jpa.getId(), 
            jpa.getDataHora(), 
            nomePaciente, 
            nomeMedico, 
            StatusConsulta.valueOf(jpa.getStatus())
        );
    }

    private ConsultaDetalhes toDetalhesDto(ConsultaJpa jpa) {
        // Simulação de detalhes
        return new ConsultaDetalhes(
            jpa.getId(), 
            jpa.getDataHora(), 
            jpa.getDescricao(), 
            StatusConsulta.valueOf(jpa.getStatus()),
            String.valueOf(jpa.getPacienteId()),
            "Paciente Detalhes",
            String.valueOf(jpa.getMedicoId()),
            "Médico Detalhes",
            List.of()
        );
    }
}