// Localização: dominio-atendimento/src/test/java/br/com/medflow/dominio/atendimento/consultas/ConsultaRepositorioMemoria.java

package br.com.medflow.dominio.atendimento.consultas;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

// Classe de suporte para histórico de teste (mantida)
class HistoricoRemarcacao {
    public final LocalDateTime dataHoraOriginal;
    public final LocalDateTime novaDataHora;

    public HistoricoRemarcacao(LocalDateTime dataHoraOriginal, LocalDateTime novaDataHora) {
        this.dataHoraOriginal = dataHoraOriginal;
        this.novaDataHora = novaDataHora;
    }
    
    public LocalDateTime getDataHoraOriginal() { return dataHoraOriginal; }
    public LocalDateTime getNovaDataHora() { return novaDataHora; }
}

// O repositório implementa a interface de produção (Porta do Domínio).
public class ConsultaRepositorioMemoria implements ConsultaRepositorio {
    private Map<Integer, Consulta> consultas = new HashMap<>(); 
    private static int nextId = 1; 

    @Override
    public Optional<Consulta> buscarPorId(ConsultaId id) {
        return Optional.ofNullable(consultas.get(id.getValor()));
    }

    @Override
    public void salvar(Consulta consulta) {
        // Simula a geração de ID para novos Agregados
        if (consulta.getId() == null || consulta.getId().getValor() == null) {
            ConsultaId novoId = new ConsultaId(nextId++);
            consultas.put(novoId.getValor(), consulta);
        } else {
            // Se já tem ID, atualiza
            consultas.put(consulta.getId().getValor(), consulta);
        }
    }

    // MÉTODOS AUXILIARES PARA TESTE (Adaptados para a nova estrutura de Consulta)
    
    /** Simulação de busca que era usada pelos cenários de BDD. */
    public Optional<Consulta> obter(String chave) {
        // Simula a busca de teste pelo conteúdo da descrição (médico + data)
        return consultas.values().stream()
            .filter(c -> c.getDescricao().contains(chave))
            .findFirst();
    }
    
    /** Versão de salvar para teste que usa a chave original dos cenários de Cucumber. */
    public void salvar(String chave, Consulta consulta) {
        salvar(consulta);
    }
    
    /** Adapta isHorarioLivre para buscar pelo nome na descrição. */
    public boolean isHorarioLivre(String medicoNome, LocalDateTime dataHora) {
        // Assume que o nome do médico está na descrição para simular o horário livre
        return consultas.values().stream()
                .noneMatch(c -> c.getDescricao().contains(medicoNome) && 
                                c.getDataHora().equals(dataHora) &&
                                c.getStatus() == StatusConsulta.AGENDADA);
    }
    
    /** Adapta findByMedicoAndDate para buscar pelo nome na descrição. */
    public Optional<Consulta> findByMedicoAndDate(String medicoNome, LocalDateTime dataHora) {
        return consultas.values().stream()
                .filter(c -> c.getDescricao().contains(medicoNome) && c.getDataHora().equals(dataHora))
                .findFirst();
    }
    
    public List<Consulta> findAll() {
        return new ArrayList<>(consultas.values());
    }

    public void clear() {
        consultas.clear();
        nextId = 1;
    }
}