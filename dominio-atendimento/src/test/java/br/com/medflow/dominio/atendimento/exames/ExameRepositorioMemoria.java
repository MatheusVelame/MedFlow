package br.com.medflow.dominio.atendimento.exames;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.List;
import java.util.stream.Collectors;

public class ExameRepositorioMemoria implements ExameRepositorio {

    private final Map<ExameId, Exame> exames = new HashMap<>();
    private long sequenciaId = 0;

    @Override
    public ExameId proximoId() {
        sequenciaId++;
        return new ExameId(sequenciaId);
    }

    @Override
    public void salvar(Exame exame) {
        if (exame.getId() == null) {
            exame.setId(proximoId());
        }
        exames.put(exame.getId(), exame);
    }

    @Override
    public Optional<Exame> obterPorId(ExameId id) {
        return Optional.ofNullable(exames.get(id));
    }

    @Override
    public List<Exame> obterPorPaciente(Long pacienteId) {
        return exames.values().stream()
                .filter(exame -> exame.getPacienteId().equals(pacienteId))
                .collect(Collectors.toList());
    }
    
    public void limpar() {
        exames.clear();
        sequenciaId = 0;
    }
}