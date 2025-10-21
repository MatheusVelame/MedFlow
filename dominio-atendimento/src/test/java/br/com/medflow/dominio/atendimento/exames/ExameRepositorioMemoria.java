package br.com.medflow.dominio.atendimento.exames;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import static org.apache.commons.lang3.Validate.notNull;

/**
 * Implementação em memória do repositório para uso exclusivo em testes BDD.
 * Simula a persistência do Aggregate Root Exame.
 */
public class ExameRepositorioMemoria implements ExameRepositorio {
	
    // Usamos Long como chave para o Map, representando o ID do ExameId
    private final Map<Long, Exame> exames = new ConcurrentHashMap<>();
    private final AtomicLong sequenciaId = new AtomicLong(1);

    @Override
    public Optional<Exame> obterPorId(ExameId id) {
        notNull(id, "O ID do exame não pode ser nulo");
        return Optional.ofNullable(exames.get(id.getValor()));
    }

    @Override
    public Exame salvar(Exame exame) {
        notNull(exame, "O exame não pode ser nulo");
        
        // Simula a geração de ID (para novos agregados)
        if (exame.getId() == null || exame.getId().getValor() == null || exame.getId().getValor() == 0) {
            ExameId novoId = new ExameId(sequenciaId.getAndIncrement());
            
            // Atribuir o novo ID (em um ambiente real, isso seria feito pelo ORM/JPA)
            try {
                java.lang.reflect.Field idField = Exame.class.getDeclaredField("id");
                idField.setAccessible(true);
                idField.set(exame, novoId);
            } catch (Exception e) {
                // Em um teste real, isso não deve acontecer, mas é um truque para BDD.
                throw new IllegalStateException("Falha ao injetar ID no Exame para teste", e);
            }
        }
        
        exames.put(exame.getId().getValor(), exame);
        return exame;
    }

    @Override
    public void excluir(Exame exame) {
        notNull(exame.getId(), "O ID do exame não pode ser nulo na exclusão");
        exames.remove(exame.getId().getValor());
    }

    @Override
    public Optional<Exame> obterAgendamentoConflitante(Long pacienteId, LocalDateTime dataHora, ExameId idExcluido) {
        // Implementação da RN4 (Conflito de horário do paciente)
        return exames.values().stream()
            .filter(e -> e.getPacienteId().equals(pacienteId))
            .filter(e -> e.getDataHora().equals(dataHora))
            // Exclui o próprio exame da verificação durante a atualização
            .filter(e -> idExcluido == null || !e.getId().equals(idExcluido))
            .findFirst();
    }
    
    /**
     * Limpa o repositório em memória. Essencial para isolamento de testes BDD.
     */
    public void clear() {
        exames.clear();
        sequenciaId.set(1);
    }
    
    /**
     * Retorna o número de exames persistidos.
     */
    public int count() {
        return exames.size();
    }
}