package br.com.medflow.dominio.administracao.pacientes;

import static org.apache.commons.lang3.Validate.notNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class PacienteRepositorioMemoria implements PacienteRepositorio {
    private Map<PacienteId, Paciente> pacientes = new HashMap<>();
    private int sequenciaId = 0;
    
    @Override
    public void salvar(Paciente paciente) {
        notNull(paciente, "O paciente não pode ser nulo");
        
        if (paciente.getId() == null) {
            sequenciaId++;
            PacienteId novoId = new PacienteId(sequenciaId);
            
            Paciente novo = new Paciente(
                novoId,
                paciente.getNome(),
                paciente.getCpf(),
                paciente.getDataNascimento(),
                paciente.getTelefone(),
                paciente.getEndereco(),
                paciente.getHistorico()
            );
            pacientes.put(novoId, novo);
            
            paciente.setId(novoId);
            
        } else {
            pacientes.put(paciente.getId(), paciente);
        }
    }
    
    @Override
    public Paciente obter(PacienteId id) {
        notNull(id, "O id do paciente não pode ser nulo");
        var paciente = pacientes.get(id);
        
        return Optional.ofNullable(paciente)
                .orElseThrow(() -> new IllegalArgumentException("Paciente não encontrado com ID: " + id.getId()));
    }

    @Override
    public Optional<Paciente> obterPorCpf(String cpf) {
        return pacientes.values().stream()
                .filter(p -> p.getCpf().equals(cpf))
                .findFirst();
    }
    
    @Override
    public List<Paciente> pesquisar() {
        return List.copyOf(pacientes.values());
    }
    
    @Override
    public void remover(PacienteId id) {
        notNull(id, "O ID para remoção não pode ser nulo.");
        
        if (!pacientes.containsKey(id)) {
            throw new IllegalArgumentException("Paciente com ID " + id.toString() + " não está no repositório.");
        }
        pacientes.remove(id);
    }
    
    public void clear() {
        pacientes.clear();
        sequenciaId = 0;
    }
}