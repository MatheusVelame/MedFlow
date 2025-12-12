package br.com.medflow.infraestrutura.persistencia.jpa.administracao;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import br.com.medflow.dominio.administracao.pacientes.Paciente;
import br.com.medflow.dominio.administracao.pacientes.PacienteId;
import br.com.medflow.dominio.administracao.pacientes.PacienteRepositorio;

@Component
@Qualifier("pacienteRepositorioJpa")
public class PacienteRepositorioJpaAdapter implements PacienteRepositorio {
    
    private final PacienteJpaRepositorio repositorioJpa;
    
    public PacienteRepositorioJpaAdapter(PacienteJpaRepositorio repositorioJpa) {
        this.repositorioJpa = repositorioJpa;
    }
    
    @Override
    public void salvar(Paciente paciente) {
        PacienteJpa jpa = PacienteJpaMapeador.paraJpa(paciente);
        PacienteJpa salvo = repositorioJpa.save(jpa);
        
        // Atualizar o ID no objeto de domínio se for um novo paciente
        if (paciente.getId() == null) {
            paciente.setId(new PacienteId(salvo.getId()));
        }
    }
    
    @Override
    public Paciente obter(PacienteId id) {
        PacienteJpa jpa = repositorioJpa.findById(id.getId())
            .orElseThrow(() -> new IllegalArgumentException(
                "Paciente com ID " + id.getId() + " não encontrado."
            ));
        
        return PacienteJpaMapeador.paraDominio(jpa);
    }
    
    @Override
    public Optional<Paciente> obterPorCpf(String cpf) {
        Optional<PacienteJpa> jpa = repositorioJpa.findByCpf(cpf);
        return jpa.map(PacienteJpaMapeador::paraDominio);
    }
    
    @Override
    public List<Paciente> pesquisar() {
        List<PacienteJpa> listaJpa = repositorioJpa.findAll();
        return PacienteJpaMapeador.paraDominioLista(listaJpa);
    }
    
    @Override
    public void remover(PacienteId id) {
        if (!repositorioJpa.existsById(id.getId())) {
            throw new IllegalArgumentException(
                "Paciente com ID " + id.getId() + " não encontrado para remoção."
            );
        }
        
        repositorioJpa.deleteById(id.getId());
    }
}