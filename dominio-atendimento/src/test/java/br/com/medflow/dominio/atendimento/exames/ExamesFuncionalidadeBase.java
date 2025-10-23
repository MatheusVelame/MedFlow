package br.com.medflow.dominio.atendimento.exames;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.medflow.dominio.evento.EventoBarramento;
import br.com.medflow.dominio.evento.EventoObservador;

/**
 * Classe base para os Steps Definitions de Exames.
 * Gerencia as dependências do domínio e o estado (contexto) de execução de cada cenário.
 */
public class ExamesFuncionalidadeBase { 

    // DEPENDÊNCIAS DE DOMÍNIO
    protected ExameServico exameServico;
    protected ExameRepositorioMemoria repositorio;
    
    // ===========================================
    // ESTADO DO CENÁRIO (Variáveis de Contexto) 
    // ===========================================
    private Exame exameEmTeste; 
    private ExcecaoDominio excecaoCapturada; 
    
    private Long idPacienteAgendamento;
    private Long idMedicoAgendamento;
    private String tipoExameAgendamento;
    private LocalDateTime dataHoraAgendamento;
    private Long idExameReferencia; 
    
    private List<Object> eventos; 

    // Mocks e dados simulados
    private final Map<Long, Boolean> pacientesCadastrados = new HashMap<>();
    private final Map<Long, Boolean> medicosCadastrados = new HashMap<>();
    private final Map<Long, Boolean> medicosAtivos = new HashMap<>();
    private final Map<String, Boolean> tiposExameCadastrados = new HashMap<>();
    private final Map<Long, Map<LocalDateTime, Boolean>> agendaMedicos = new HashMap<>();
    private final Map<String, Long> pacienteIds = new HashMap<>();
    private final Map<String, Long> medicoIds = new HashMap<>();
    private long pacienteIdCounter = 1L;
    private long medicoIdCounter = 1L;

    /** Mock do VerificadorExternoServico */
    private class VerificadorExternoMock implements VerificadorExternoServico { 
        @Override
        public boolean pacienteEstaCadastrado(Long pacienteId) {
            return pacientesCadastrados.getOrDefault(pacienteId, false);
        }

        @Override
        public boolean medicoEstaCadastrado(Long medicoId) {
            return medicosCadastrados.getOrDefault(medicoId, false);
        }

        @Override
        public boolean medicoEstaAtivo(Long medicoId) {
            return medicosAtivos.getOrDefault(medicoId, false);
        }

        @Override
        public boolean tipoExameEstaCadastrado(String tipoExame) {
            return tiposExameCadastrados.getOrDefault(tipoExame, false);
        }

        @Override
        public boolean medicoEstaDisponivel(Long medicoId, LocalDateTime dataHora) {
            return agendaMedicos.getOrDefault(medicoId, new HashMap<>()).getOrDefault(dataHora, true);
        }
    }
    
    /** Mock do EventoBarramento */
    protected class EventoBarramentoMock implements EventoBarramento {
        @Override
        public <E> void adicionar(EventoObservador<E> observador) {}

        @Override
        public <E> void postar(E evento) {
            eventos.add(evento);
        }
    }

    public ExamesFuncionalidadeBase() {
        this.eventos = new ArrayList<>();
        this.repositorio = new ExameRepositorioMemoria();
        this.exameServico = new ExameServico(this.repositorio, new VerificadorExternoMock(), new EventoBarramentoMock());
    }
    
    // O Hook @Before foi movido para a subclasse.
    protected void resetarContexto() {
        // Limpeza de Repositórios e Mocks
        repositorio.limpar();
        eventos.clear();
        pacientesCadastrados.clear();
        medicosCadastrados.clear();
        medicosAtivos.clear();
        tiposExameCadastrados.clear();
        agendaMedicos.clear();
        pacienteIds.clear();
        medicoIds.clear();
        
        // Limpeza do Estado do Cenário
        exameEmTeste = null;
        excecaoCapturada = null;
        idPacienteAgendamento = null;
        idMedicoAgendamento = null;
        tipoExameAgendamento = null;
        dataHoraAgendamento = null;
        idExameReferencia = null;
    }

    // ===========================================
    // GETTERS E SETTERS (Ponto de contato para ExamesFuncionalidade)
    // ===========================================

    public Exame getExameEmTeste() { return exameEmTeste; }
    public void setExameEmTeste(Exame exameEmTeste) { this.exameEmTeste = exameEmTeste; }

    public ExcecaoDominio getExcecaoCapturada() { return excecaoCapturada; }
    public void setExcecaoCapturada(ExcecaoDominio excecaoCapturada) { this.excecaoCapturada = excecaoCapturada; }

    public Long getIdPacienteAgendamento() { return idPacienteAgendamento; }
    public void setIdPacienteAgendamento(Long idPacienteAgendamento) { this.idPacienteAgendamento = idPacienteAgendamento; }

    public Long getIdMedicoAgendamento() { return idMedicoAgendamento; }
    public void setIdMedicoAgendamento(Long idMedicoAgendamento) { this.idMedicoAgendamento = idMedicoAgendamento; }

    public String getTipoExameAgendamento() { return tipoExameAgendamento; }
    public void setTipoExameAgendamento(String tipoExameAgendamento) { this.tipoExameAgendamento = tipoExameAgendamento; }

    public LocalDateTime getDataHoraAgendamento() { return dataHoraAgendamento; }
    public void setDataHoraAgendamento(LocalDateTime dataHoraAgendamento) { this.dataHoraAgendamento = dataHoraAgendamento; }
    
    public Long getIdExameReferencia() { return idExameReferencia; }
    public void setIdExameReferencia(Long idExameReferencia) { this.idExameReferencia = idExameReferencia; }
    
    public List<Object> getEventos() { return eventos; }

    // --- Métodos de Utilidade para Steps ---
	
    protected Long getPacienteId(String nome) {
	    return pacienteIds.computeIfAbsent(nome, k -> pacienteIdCounter++);
	}
	
	protected Long getMedicoId(String nome) {
	    return medicoIds.computeIfAbsent(nome, k -> medicoIdCounter++);
	}
    
    protected UsuarioResponsavelId getUsuarioResponsavel(String nomeUsuario) {
        return new UsuarioResponsavelId(1L); // ID fixo para testes
    }
    
    protected UsuarioResponsavelId getUsuarioResponsavelId(String nomeUsuario) {
        return getUsuarioResponsavel(nomeUsuario); 
    }
	
    protected LocalDateTime parseDataHora(String data, String hora) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate localDate = LocalDate.parse(data, dateFormatter);
        int horaInt = Integer.parseInt(hora.replace("h", ""));
        return LocalDateTime.of(localDate, LocalTime.of(horaInt, 0));
    }
    
    protected void simularIndisponibilidadeMedico(Long medicoId, LocalDateTime dataHora) {
        agendaMedicos.computeIfAbsent(medicoId, k -> new HashMap<>()).put(dataHora, false);
    }
    
    protected void simularPaciente(String nome, boolean cadastrado) {
        pacientesCadastrados.put(getPacienteId(nome), cadastrado);
    }

    protected void simularMedico(String nome, boolean cadastrado, boolean ativo) {
        medicosCadastrados.put(getMedicoId(nome), cadastrado);
        medicosAtivos.put(getMedicoId(nome), ativo);
    }
    
    protected void simularTipoExame(String tipoExame, boolean cadastrado) {
        tiposExameCadastrados.put(tipoExame, cadastrado);
    }
}