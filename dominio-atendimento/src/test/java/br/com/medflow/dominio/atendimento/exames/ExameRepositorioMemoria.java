package br.com.medflow.dominio.atendimento.exames;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.List;
import java.util.ArrayList;

public class ExameRepositorioMemoria implements ExameRepositorio {

    private final Map<ExameId, Exame> exames = new HashMap<>();
    private long sequenciaId = 0;
    
    // Método auxiliar interno para gerar o próximo ID (não faz parte da interface)
    private ExameId proximoId() {
        sequenciaId++;
        return new ExameId(sequenciaId);
    }

    @Override
    public Optional<Exame> obterPorId(ExameId id) {
        return Optional.ofNullable(exames.get(id));
    }

    @Override
    public Exame salvar(Exame exame) {
        if (exame.getId() == null) {
            exame.setId(proximoId());
        }
        exames.put(exame.getId(), exame);
        return exame;
    }

    @Override
    public void excluir(Exame exame) {
        exames.remove(exame.getId());
    }

    
    @Override
    public Optional<Exame> obterAgendamentoConflitante(Long pacienteId, LocalDateTime dataHora, ExameId idExcluido) {
        
        return exames.values().stream()
                .filter(exame -> exame.getPacienteId().equals(pacienteId))
                .filter(exame -> exame.getDataHora().equals(dataHora))
                .filter(exame -> idExcluido == null || !exame.getId().equals(idExcluido))
                .findFirst();
    }

    @Override
    public boolean existePorMedicoId(Integer medicoId) {
        if (medicoId == null) return false;

        return exames.values().stream()
                .anyMatch(exame ->
                        exame.getMedicoId() != null &&
                                // Conversão necessária: Exame usa Long, mas o parâmetro veio como Integer
                                exame.getMedicoId().equals(medicoId.longValue())
                );
    }

    public void limpar() {
        exames.clear();
        sequenciaId = 0;
    }

    @Override
    public List<Exame> listarTodos() {
        return new ArrayList<>(exames.values());
    }
}