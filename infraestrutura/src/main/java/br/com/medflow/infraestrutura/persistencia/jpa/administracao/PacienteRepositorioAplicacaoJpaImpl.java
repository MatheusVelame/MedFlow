package br.com.medflow.infraestrutura.persistencia.jpa.administracao;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import br.com.medflow.aplicacao.administracao.pacientes.PacienteDetalhes;
import br.com.medflow.aplicacao.administracao.pacientes.PacienteRepositorioAplicacao;
import br.com.medflow.aplicacao.administracao.pacientes.PacienteResumo;

@Repository
public class PacienteRepositorioAplicacaoJpaImpl implements PacienteRepositorioAplicacao {
    
    private final PacienteJpaRepositorio repositorioJpa;
    
    public PacienteRepositorioAplicacaoJpaImpl(PacienteJpaRepositorio repositorioJpa) {
        this.repositorioJpa = repositorioJpa;
    }
    
    @Override
    public Optional<PacienteResumo> buscarResumoPorId(int id) {
        return repositorioJpa.findById(id)
            .map(this::converterParaResumo);
    }
    
    @Override
    public Optional<PacienteDetalhes> buscarDetalhesPorId(int id) {
        return repositorioJpa.findById(id)
            .map(this::converterParaDetalhes);
    }
    
    @Override
    public Optional<PacienteResumo> buscarResumoPorCpf(String cpf) {
        return repositorioJpa.findByCpf(cpf)
            .map(this::converterParaResumo);
    }
    
    @Override
    public List<PacienteResumo> listarTodos() {
        return repositorioJpa.findAll().stream()
            .map(this::converterParaResumo)
            .collect(Collectors.toList());
    }
    
    @Override
    public boolean existePorCpf(String cpf) {
        return repositorioJpa.existsByCpf(cpf);
    }
    
    // Métodos privados de conversão
    
    private PacienteResumo converterParaResumo(PacienteJpa jpa) {
        return new PacienteResumo() {
            @Override
            public int getId() {
                return jpa.getId();
            }
            
            @Override
            public String getNome() {
                return jpa.getNome();
            }
            
            @Override
            public String getCpf() {
                return jpa.getCpf();
            }
            
            @Override
            public String getTelefone() {
                return jpa.getTelefone();
            }
        };
    }
    
    private PacienteDetalhes converterParaDetalhes(PacienteJpa jpa) {
        return new PacienteDetalhes() {
            @Override
            public int getId() {
                return jpa.getId();
            }
            
            @Override
            public String getNome() {
                return jpa.getNome();
            }
            
            @Override
            public String getCpf() {
                return jpa.getCpf();
            }
            
            @Override
            public String getDataNascimento() {
                return jpa.getDataNascimento();
            }
            
            @Override
            public String getTelefone() {
                return jpa.getTelefone();
            }
            
            @Override
            public String getEndereco() {
                return jpa.getEndereco();
            }
        };
    }
}