package br.com.medflow.dominio.atendimento.consultas;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

// Mocks de Infraestrutura para Testes

// Mock do Histórico para simplificação
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

// Mock da entidade Consulta
class Consulta {
    private String medicoNome;
    private String pacienteNome;
    private LocalDateTime dataHora;
    // Usando StatusConsulta do código principal
    private StatusConsulta status = StatusConsulta.AGENDADA; 
    private int remarcaçõesCount = 0;
    private List<HistoricoRemarcacao> historico = new ArrayList<>();
    private String motivoCancelamento;

    public Consulta(String medicoNome, String pacienteNome, LocalDateTime dataHora) {
        this.medicoNome = medicoNome;
        this.pacienteNome = pacienteNome;
        this.dataHora = dataHora;
    }

    public String getMedicoNome() { return medicoNome; }
    public String getPacienteNome() { return pacienteNome; }
    public LocalDateTime getDataHora() { return dataHora; }
    public StatusConsulta getStatus() { return status; }
    public int getRemarcacoesCount() { return remarcaçõesCount; }
    public String getMotivoCancelamento() { return motivoCancelamento; }
    public List<HistoricoRemarcacao> getHistorico() { return historico; }
    
    public void remarcar(LocalDateTime novaDataHora) {
        historico.add(new HistoricoRemarcacao(this.dataHora, novaDataHora));
        this.dataHora = novaDataHora;
        this.remarcaçõesCount++;
    }

    public void cancelar(String motivo) {
        this.status = StatusConsulta.CANCELADA;
        this.motivoCancelamento = motivo; 
    }
}

// Mock do repositório em memória
public class ConsultaRepositorioMemoria {
    private Map<String, Consulta> consultas = new HashMap<>();

    public void salvar(String chave, Consulta consulta) {
        consultas.put(chave, consulta);
    }

    public Optional<Consulta> obter(String chave) {
        return Optional.ofNullable(consultas.get(chave));
    }

    public boolean isHorarioLivre(String medicoNome, LocalDateTime dataHora) {
        // Verifica se existe alguma consulta ativa para este médico neste horário
        return consultas.values().stream()
                .noneMatch(c -> c.getMedicoNome().equals(medicoNome) && 
                                c.getDataHora().equals(dataHora) &&
                                c.getStatus() == StatusConsulta.AGENDADA);
    }
    
    public Optional<Consulta> findByMedicoAndDate(String medicoNome, LocalDateTime dataHora) {
        return consultas.values().stream()
                .filter(c -> c.getMedicoNome().equals(medicoNome) && c.getDataHora().equals(dataHora))
                .findFirst();
    }
    
    public List<Consulta> findAll() {
        return new ArrayList<>(consultas.values());
    }

    public void clear() {
        consultas.clear();
    }
}