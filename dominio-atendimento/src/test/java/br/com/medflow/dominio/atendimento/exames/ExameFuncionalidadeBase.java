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

public class ExameFuncionalidadeBase {

    protected ExameServico exameServico;
    protected ExameRepositorioMemoria repositorio;
    
    // Variáveis de estado para os cenários
    protected Exame exameResultado; 
    protected ExcecaoDominio excecaoCapturada;
    
    // Mocks e dados simulados
    protected final List<Object> eventos = new ArrayList<>();
    private final Map<Long, Boolean> pacientesCadastrados = new HashMap<>();
    private final Map<Long, Boolean> medicosCadastrados = new HashMap<>();
    private final Map<Long, Boolean> medicosAtivos = new HashMap<>();
    private final Map<String, Boolean> tiposExameCadastrados = new HashMap<>();
    private final Map<Long, Map<LocalDateTime, Boolean>> agendaMedicos = new HashMap<>();

    // IDs para mapeamento de nomes
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

    public ExameFuncionalidadeBase() {
        this.repositorio = new ExameRepositorioMemoria();
        this.exameServico = new ExameServico(this.repositorio, new VerificadorExternoMock(), new EventoBarramentoMock());
    }
    
    protected void resetarContexto() {
        repositorio.limpar();
        eventos.clear();
        pacientesCadastrados.clear();
        medicosCadastrados.clear();
        medicosAtivos.clear();
        tiposExameCadastrados.clear();
        agendaMedicos.clear();
        pacienteIds.clear();
        medicoIds.clear();
        exameResultado = null;
        excecaoCapturada = null;
    }

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
	
    protected LocalDateTime parseDataHora(String data, String hora) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate localDate = LocalDate.parse(data, dateFormatter);
        int horaInt = Integer.parseInt(hora.replace("h", ""));
        return LocalDateTime.of(localDate, LocalTime.of(horaInt, 0));
    }
}