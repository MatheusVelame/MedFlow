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
                .map(this::toDomain); 
    }

    @Override
    public void salvar(Consulta consulta) {
        ConsultaJpa jpa = toJpa(consulta);
        jpaRepository.save(jpa);
    }

    @Override
    public boolean existePorMedicoId(Integer medicoId) {
        if (medicoId == null) return false;
        return jpaRepository.existsByMedicoId(medicoId);
    }

    // Métodos de mapeamento interno (Domain <=> JPA)
    
    private Consulta toDomain(ConsultaJpa jpa) {
        // CORREÇÃO CRÍTICA: Passa pacienteId e medicoId para o construtor de reconstrução
        return new Consulta(
            new ConsultaId(jpa.getId()),
            jpa.getDataHora(),
            jpa.getDescricao(),
            StatusConsulta.valueOf(jpa.getStatus()), 
            jpa.getPacienteId(), // NOVO
            jpa.getMedicoId(),   // NOVO
            List.of() // Histórico (simplificado)
        );
    }
    
    private ConsultaJpa toJpa(Consulta consulta) {
        ConsultaJpa jpa = new ConsultaJpa();
        
        if (consulta.getId() != null) {
            jpa.setId(consulta.getId().getValor());
        }
        jpa.setDataHora(consulta.getDataHora());
        jpa.setDescricao(consulta.getDescricao());
        jpa.setStatus(consulta.getStatus().name()); 
        
        // Mapeamento dos IDs do Domínio para o JPA
        jpa.setPacienteId(consulta.getPacienteId()); 
        jpa.setMedicoId(consulta.getMedicoId());   
        
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
    
    @Override
    public boolean existsByPacienteId(int pacienteId) {
        // Apenas repassa a chamada, sem conversão complicada
        return jpaRepository.existsByPacienteId(pacienteId);
    }
    
    // Métodos de mapeamento interno (JPA -> DTO)

    private ConsultaResumo toResumoDto(ConsultaJpa jpa) {
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