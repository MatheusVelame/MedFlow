package br.com.medflow.dominio.atendimento.consultas;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

// --- Mocks de Infraestrutura ---

// Mock do Histórico para simplificação
class HistoricoRemarcacao {
    public final LocalDateTime dataHoraOriginal;
    public final LocalDateTime novaDataHora;

    public HistoricoRemarcacao(LocalDateTime dataHoraOriginal, LocalDateTime novaDataHora) {
        this.dataHoraOriginal = dataHoraOriginal;
        this.novaDataHora = novaDataHora;
    }
}

// Mock da entidade Consulta
class Consulta {
    private String medicoNome;
    private String pacienteNome;
    private LocalDateTime dataHora;
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
    
    // Getter para o campo privado 'historico'
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

// Mock do repositório
class ConsultaRepositorioMemoria {
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

// Mock do Serviço de Notificação (apenas registra se foi chamado)
class NotificacaoServicoMock {
    public List<String> notificacoesEnviadas = new ArrayList<>();
    
    public void enviarNotificacao(String destinatario, String tipo, String mensagem) {
        notificacoesEnviadas.add(String.format("%s:%s", destinatario, tipo));
    }
    public void clear() { notificacoesEnviadas.clear(); }
}


// --- Classe Base BDD ---
public class ConsultaFuncionalidadeBase {
    protected ConsultaRepositorioMemoria repositorio = new ConsultaRepositorioMemoria();
    protected Map<String, Medico> medicos = new HashMap<>();
    protected Map<String, Paciente> pacientes = new HashMap<>();
    protected NotificacaoServicoMock notificacaoServico = new NotificacaoServicoMock();
    
    protected RuntimeException excecao;
    protected String ultimaMensagem;
    protected Consulta consultaAtual;
    protected String usuarioAtual;
    
    // Campo para armazenar a consulta alvo em cenários de Remarcação
    protected Consulta consultaJoao; 
    
    // Mock do tempo do sistema
    protected LocalDateTime dataHoraAtual;
    protected LocalDateTime dataHoraConsulta;
    protected LocalDateTime dataHoraNovaConsulta;
    
    protected static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm");
    protected static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public ConsultaFuncionalidadeBase() {
        // Inicialização de dados fixos (para testes que não se importam com o usuário)
        medicos.put("Dr. Eduardo", new Medico("Dr. Eduardo", "Cardiologia", "E-mail"));
        medicos.put("Dra. Helena", new Medico("Dra. Helena", "Dermatologia", "E-mail"));
        medicos.put("Dr. Bruno", new Medico("Dr. Bruno", "Ortopedia", "E-mail"));
        medicos.put("Dr. House", new Medico("Dr. House", "Diagnóstico", "E-mail"));

        // Inicializa todos os pacientes usados nos testes
        pacientes.put("Ana Silva", new Paciente("Ana Silva", "E-mail"));
        pacientes.put("Pedro Alves", new Paciente("Pedro Alves", "SMS"));
        pacientes.put("Joana Lima", new Paciente("Joana Lima", "E-mail"));
        pacientes.put("João", new Paciente("João", "E-mail")); 
        pacientes.put("Gabriel", new Paciente("Gabriel", "E-mail")); 
        pacientes.put("João Costa", new Paciente("João Costa", "E-mail"));
        pacientes.put("Maria Lima", new Paciente("Maria Lima", "E-mail"));
    }

    protected void resetContexto() {
        repositorio.clear();
        notificacaoServico.clear();
        excecao = null;
        ultimaMensagem = null;
        consultaAtual = null;
        usuarioAtual = null;
        dataHoraAtual = null;
        dataHoraConsulta = null;
        dataHoraNovaConsulta = null;
        consultaJoao = null;
        
        // Resetar contadores de penalidade
        pacientes.values().forEach(p -> p.setCancelamentosRecentes(0)); 
    }
    
    protected void simularPermissao(String usuario) {
        if (usuario.equals("Julia") || usuario.equals("Maria") || usuario.equals("João") || usuario.equals("Gabriel")) {
            usuarioAtual = "Recepcionista";
        } else if (usuario.contains("Dr.") || usuario.contains("Dra.")) {
            usuarioAtual = "Medico";
        } else {
            usuarioAtual = "UsuarioComum";
        }
    }
    
    protected void setSystemDate(String date) {
        this.dataHoraAtual = LocalDateTime.parse(date + " às 12:00", DATE_TIME_FORMATTER); 
    }
    
    protected LocalDateTime parseDateTime(String date, String time) {
        return LocalDateTime.parse(date + " às " + time, DATE_TIME_FORMATTER);
    }
    
    protected void cadastrarConsulta(String medicoNome, String pacienteNome, LocalDateTime dataHora) {
        Consulta nova = new Consulta(medicoNome, pacienteNome, dataHora);
        String chave = medicoNome + dataHora.toString();
        repositorio.salvar(chave, nova);
        consultaAtual = nova;
    }
    
    // Método que o ConsultaServico real faria:
    protected void marcarConsulta(String medicoNome, String pacienteNome, String especialidade, LocalDateTime dataHora, String usuario) throws Exception {
        if (!usuarioAtual.equals("Recepcionista")) {
            throw new SecurityException("Usuário não tem permissão de recepcionista.");
        }
        
        if (!repositorio.isHorarioLivre(medicoNome, dataHora)) {
            throw new IllegalArgumentException("Horário já está ocupado.");
        }
        
        if (pacientes.get(pacienteNome) == null) {
            throw new IllegalArgumentException("Paciente não encontrado.");
        }
        
        if (medicos.get(medicoNome) == null) {
            throw new IllegalArgumentException("Médico não encontrado.");
        }
        
        Medico medico = medicos.get(medicoNome);
        if (!medico.atendeEspecialidade(especialidade)) {
             throw new IllegalArgumentException(medico.getNome() + " não atende " + especialidade + ".");
        }
        
        if (dataHora.isBefore(dataHoraAtual.minusDays(1))) { 
            throw new IllegalArgumentException("Não é possível agendar consultas para datas passadas.");
        }

        cadastrarConsulta(medicoNome, pacienteNome, dataHora);

    }

    protected void remarcarConsulta(String pacienteNome, LocalDateTime novaDataHora, String usuario) throws Exception {
        if (!repositorio.isHorarioLivre(consultaAtual.getMedicoNome(), novaDataHora)) {
             throw new IllegalArgumentException("Já existe uma consulta marcada para o dia e o horário escolhido.");
        }
        
        if (consultaAtual.getRemarcacoesCount() >= 2) {
             throw new IllegalStateException("Limite máximo de 2 remarcações foi atingido.");
        }

        if (dataHoraAtual.plusHours(24).isAfter(consultaAtual.getDataHora())) {
             throw new IllegalStateException("A remarcação só é possível com mais de 24h de antecedência");
        }
        
        consultaAtual.remarcar(novaDataHora);
    }
    
    protected void cancelarConsulta(String motivo, String usuario) throws Exception {
        if (motivo == null || motivo.trim().isEmpty()) {
             throw new IllegalArgumentException("O motivo é obrigatório");
        }
        
        // Simula a regra das 24h
        if (dataHoraAtual.plusDays(1).isAfter(consultaAtual.getDataHora())) {
             throw new IllegalStateException("O prazo limite de 24 horas foi excedido");
        }
        
        Paciente paciente = pacientes.get(consultaAtual.getPacienteNome());
        
        // Simula a penalidade
        if (paciente.getCancelamentosRecentes() >= 2) {
            throw new IllegalStateException("Penalidade: restrição de agendamento por 30 dias");
        }
        
        consultaAtual.cancelar(motivo);
    }
}