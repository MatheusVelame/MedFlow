package br.com.medflow.dominio.atendimento.exames;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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

    /**
     * Implementação da lógica de conflito de agendamento para o Paciente.
     * RN: Paciente só pode ter um exame agendado por data e hora.
     */
    @Override
    public Optional<Exame> obterAgendamentoConflitante(Long pacienteId, LocalDateTime dataHora, ExameId idExcluido) {
        
        return exames.values().stream()
                // 1. Deve ser o mesmo paciente
                .filter(exame -> exame.getPacienteId().equals(pacienteId))
                // 2. Deve ser a mesma data e hora
                .filter(exame -> exame.getDataHora().equals(dataHora))
                // 3. Deve ignorar o próprio exame em caso de atualização (idExcluido)
                .filter(exame -> idExcluido == null || !exame.getId().equals(idExcluido))
                // Retorna o primeiro exame que satisfaz as condições de conflito
                .findFirst();
    }
    
    public void limpar() {
        exames.clear();
        sequenciaId = 0;
    }
}