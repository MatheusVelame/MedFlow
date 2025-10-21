package br.com.medflow.dominio.atendimento.exames;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import br.com.medflow.dominio.evento.EventoBarramento;
import br.com.medflow.dominio.evento.EventoObservador;

/**
 * Classe base para os testes BDD de Exames. 
 * Responsável por configurar o ambiente (serviços in-memory) e simular as dependências.
 */
public class ExameFuncionalidadeBase {
	
	// Variáveis de domínio/ambiente - Deve ser 'protected' para acesso pelo Step
	protected ExameServico exameServico;
	protected ExameRepositorioMemoria repositorio;
	protected Exame exameAgendado; // Exame de referência para cenários de Update/Delete
	
	// Mocks de sistemas externos e dados simulados - Deve ser 'protected'
	// protected List<Object> eventos; // Eventos capturados (Mock de EventoBarramento)
	protected final List<Object> eventos = new ArrayList<>();
	private final Map<Long, Boolean> pacientesCadastrados;
    private final Map<Long, Boolean> medicosAtivos;
    private final Map<Long, Map<LocalDateTime, Boolean>> medicosDisponiveis; // Médico -> Data/Hora -> Disponível
    private final Map<String, Boolean> tiposExameCadastrados;
	
	// IDs simulados para simplificar o mapeamento
	public static final Long ID_PACIENTE_LUCAS = 100L;
	public static final Long ID_PACIENTE_PAULO = 101L;
    public static final Long ID_PACIENTE_MARIA = 102L;
    public static final Long ID_PACIENTE_CARLA = 103L;
    public static final Long ID_PACIENTE_MARINA = 104L;
    public static final Long ID_PACIENTE_BEATRIZ = 105L;
	
	public static final Long ID_MEDICO_ANA = 200L;
	public static final Long ID_MEDICO_PAULO = 201L;
	public static final Long ID_MEDICO_CARLOS = 202L;

    private final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    // --- Mocks de Interfaces ---

    /** Mock do VerificadorExternoServico: Simula o acesso a outros Bounded Contexts. */
    private class VerificadorExternoMock implements VerificadorExternoServico {
        @Override
        public boolean pacienteEstaCadastrado(Long pacienteId) {
            return pacientesCadastrados.getOrDefault(pacienteId, false);
        }

        @Override
        public boolean medicoEstaCadastrado(Long medicoId) {
            // Se está ativo ou inativo, está cadastrado.
            return medicosAtivos.containsKey(medicoId);
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
            // Verifica se existe uma entrada específica de conflito para este horário
            Map<LocalDateTime, Boolean> agendaMedico = medicosDisponiveis.getOrDefault(medicoId, new HashMap<>());
            // Retorna TRUE se não houver entrada para o horário (ou se a entrada for TRUE)
            return agendaMedico.getOrDefault(dataHora, true);
        }
    }
    
    /** Mock do EventoBarramento: Captura eventos em uma lista. */
    protected class EventoBarramentoMock implements EventoBarramento {

        @Override
        public <E> void adicionar(EventoObservador<E> observador) {
            // Não precisa fazer nada aqui no mock
        }

        @Override
        public <E> void postar(E evento) {
            eventos.add(evento); // agora funciona, pois a classe não é estática
        }
    }

    // --- Construtor e Inicialização ---

	public ExameFuncionalidadeBase() {
        // Inicializa estruturas de dados de mock
        this.pacientesCadastrados = new HashMap<>();
        this.medicosAtivos = new HashMap<>();
        this.medicosDisponiveis = new HashMap<>();
        this.tiposExameCadastrados = new HashMap<>();
        // this.eventos = new ArrayList<>();
        eventos.clear();
        this.repositorio = new ExameRepositorioMemoria();
        
        // Inicializa o ExameServico com os Mocks
		this.exameServico = new ExameServico(this.repositorio, new VerificadorExternoMock(), new EventoBarramentoMock());
        
        // Dados de Setup Mínimo (Default para a maioria dos cenários)
        pacientesCadastrados.put(ID_PACIENTE_LUCAS, true);
        pacientesCadastrados.put(ID_PACIENTE_PAULO, false);
        medicosAtivos.put(ID_MEDICO_ANA, true);
        medicosAtivos.put(ID_MEDICO_PAULO, true);
        medicosAtivos.put(ID_MEDICO_CARLOS, true);
        tiposExameCadastrados.put("Raio-X", true);
        tiposExameCadastrados.put("Ultrassonografia", true);
	}
	
	// --- Métodos de Utilidade para Steps (GIVEN/THEN) ---
	
    /** Retorna o ID do paciente simulado. */
	protected Long getPacienteId(String nome) {
	    // Implementação de mapeamento conforme os cenários do .feature
	    if (nome.equals("Lucas")) return ID_PACIENTE_LUCAS;
	    if (nome.equals("Paulo")) return ID_PACIENTE_PAULO;
	    if (nome.equals("Maria")) return ID_PACIENTE_MARIA;
	    if (nome.equals("Carla")) return ID_PACIENTE_CARLA;
	    if (nome.equals("Marina")) return ID_PACIENTE_MARINA;
	    if (nome.equals("Beatriz")) return ID_PACIENTE_BEATRIZ;
	    // Outros nomes recebem um ID padrão (e não estarão cadastrados, se não mockados)
	    return 999L; 
	}
	
    /** Retorna o ID do médico simulado. */
	protected Long getMedicoId(String nome) {
	    if (nome.equals("Dr. Ana")) return ID_MEDICO_ANA;
	    if (nome.equals("Dr. Paulo")) return ID_MEDICO_PAULO;
	    if (nome.equals("Dr. Carlos")) return ID_MEDICO_CARLOS;
	    return 888L; 
	}

    /** Retorna um ID de usuário responsável padrão para as ações. */
    protected UsuarioResponsavelId getUsuarioResponsavelId(String nomeUsuario) {
        return new UsuarioResponsavelId(1L); // Simplesmente retorna o ID 1
    }
	
    /** Converte data e hora do Gherkin para LocalDateTime. */
	/* protected LocalDateTime parseDataHora(String data, String hora) {
		// Ajuste para o formato "10/10/2025" e "09h"
		int dia = Integer.parseInt(data.substring(0, 2));
		int mes = Integer.parseInt(data.substring(3, 5));
		int ano = Integer.parseInt(data.substring(6, 10));
		int h = Integer.parseInt(hora.substring(0, hora.indexOf('h')));
		
		return LocalDateTime.of(ano, mes, dia, h, 0);
	} */
    /* protected LocalDateTime parseDataHora(String data, String hora) {
		
        // Formatter robusto para a data "dd/MM/yyyy"
        final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        
        // Formatter robusto para a hora "Hh" (ex: "14h")
        final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("H'h'");
        
        // Parsing seguro
        LocalDate localDate = LocalDate.parse(data, DATE_FORMATTER); // DÁ ERRO
        LocalTime localTime = LocalTime.parse(hora, TIME_FORMATTER);
        
        return LocalDateTime.of(localDate, localTime);
	} */
    protected LocalDateTime parseDataHora(String data, String hora) {
        LocalDate localDate;
        try {
            // tenta "dd/MM/yyyy"
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            localDate = LocalDate.parse(data, formatter);
        } catch (Exception e) {
            // fallback para "yyyy-MM-dd"
            localDate = LocalDate.parse(data);
        }

        // suporta "14h", "09h" ou "09:00"
        int horaNum;
        if (hora.contains("h")) {
            horaNum = Integer.parseInt(hora.replace("h", "").trim());
        } else if (hora.contains(":")) {
            horaNum = Integer.parseInt(hora.split(":")[0]);
        } else {
            horaNum = Integer.parseInt(hora.trim());
        }

        return LocalDateTime.of(localDate, LocalTime.of(horaNum, 0));
    }

    
    
    
    // --- Métodos de Simulação para GIVEN ---
    
    /** Simula o status de cadastro do paciente (RN1). */
	protected void simularPaciente(String nome, boolean cadastrado) {
	    pacientesCadastrados.put(getPacienteId(nome), cadastrado);
	}

    /** Simula o status do médico (RN1, RN5) e a disponibilidade para qualquer horário (RN6). */
	protected void simularMedico(String nome, boolean cadastradoAtivo, boolean disponivelDefault) {
	    Long id = getMedicoId(nome);
	    
	    // RN1/RN5: Status de cadastro e ativo
        if (cadastradoAtivo) {
            medicosAtivos.put(id, true);
        } else {
            medicosAtivos.put(id, false); // Inativo (RN5)
        }
        
        // Se precisar de simulação de disponibilidade (RN6), use o método específico.
        // O padrão é 'disponível' (true) se não houver um conflito explícito.
	}

    /** Simula a disponibilidade do médico em um horário específico (RN6). */
    protected void simularDisponibilidadeMedico(String nomeMedico, boolean disponivel, LocalDateTime dataHora) {
        Long id = getMedicoId(nomeMedico);
        medicosDisponiveis.computeIfAbsent(id, k -> new HashMap<>()).put(dataHora, disponivel);
    }
    
    /** Simula o status de cadastro do tipo de exame (RN2). */
	protected void simularTipoExame(String tipo, boolean cadastrado) {
	    tiposExameCadastrados.put(tipo, cadastrado);
	}
}