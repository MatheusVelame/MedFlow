package br.com.medflow.dominio.atendimento.consultas;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


class NotificacaoServicoMock {
    public List<String> notificacoesEnviadas = new ArrayList<>();
    
    public void enviarNotificacao(String destinatario, String tipo, String mensagem) {
        notificacoesEnviadas.add(String.format("%s:%s", destinatario, tipo));
    }
    public void clear() { notificacoesEnviadas.clear(); }
}


public class ConsultaFuncionalidadeBase {
    protected ConsultaRepositorioMemoria repositorio = new ConsultaRepositorioMemoria(); 
    protected Map<String, Medico> medicos = new HashMap<>();
    protected Map<String, Paciente> pacientes = new HashMap<>();
    protected NotificacaoServicoMock notificacaoServico = new NotificacaoServicoMock();
    
    protected RuntimeException excecao;
    protected String ultimaMensagem;
    protected Consulta consultaAtual;
    protected String usuarioAtual;
    
    protected Consulta consultaJoao;
    
    protected LocalDateTime dataHoraAtual;
    protected LocalDateTime dataHoraConsulta;
    protected LocalDateTime dataHoraNovaConsulta;
    
    protected static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm");
    protected static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public ConsultaFuncionalidadeBase() {
        medicos.put("Dr. Eduardo", new Medico("Dr. Eduardo", "Cardiologia", "E-mail"));
        medicos.put("Dra. Helena", new Medico("Dra. Helena", "Dermatologia", "E-mail"));
        medicos.put("Dr. Bruno", new Medico("Dr. Bruno", "Ortopedia", "E-mail"));
        medicos.put("Dr. House", new Medico("Dr. House", "Diagnóstico", "E-mail"));

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
        
        if (dataHoraAtual.plusDays(1).isAfter(consultaAtual.getDataHora())) {
             throw new IllegalStateException("O prazo limite de 24 horas foi excedido");
        }
        
        Paciente paciente = pacientes.get(consultaAtual.getPacienteNome());
        
        if (paciente.getCancelamentosRecentes() >= 2) {
            throw new IllegalStateException("Penalidade: restrição de agendamento por 30 dias");
        }
        
        consultaAtual.cancelar(motivo);
    }
}