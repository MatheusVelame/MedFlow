package br.com.medflow.dominio.atendimento.exames;

import static org.apache.commons.lang3.Validate.notNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import br.com.medflow.dominio.evento.EventoBarramento;
import br.com.medflow.dominio.evento.EventoObservador;

/**
 * Classe base para os testes BDD de Exames.
 * Responsável por configurar o ambiente (serviços in-memory) e simular o barramento de eventos.
 */
public class ExameFuncionalidadeBase implements EventoBarramento {

    protected ExameRepositorioMemoria repositorio;
    protected List<Object> eventos;

    // Contexto simulado de pacientes, médicos e tipos de exame
    protected Map<String, Paciente> pacientes;
    protected Map<String, Medico> medicos;
    protected Map<String, TipoExame> tiposExame;

    public ExameFuncionalidadeBase() {
        this.repositorio = new ExameRepositorioMemoria();
        this.eventos = new ArrayList<>();
        this.pacientes = new HashMap<>();
        this.medicos = new HashMap<>();
        this.tiposExame = new HashMap<>();

        // Ambiente inicial
        medicos.put("Dr. Carlos", new Medico("Dr. Carlos", true));
        medicos.put("Dr. José", new Medico("Dr. José", false));
        medicos.put("Dr. Ana", new Medico("Dr. Ana", true));

        pacientes.put("Ana", new Paciente("Ana", true));
        pacientes.put("Lucas", new Paciente("Lucas", true));
        pacientes.put("Desconhecido", new Paciente("Desconhecido", false));

        tiposExame.put("Raio-X", new TipoExame("Raio-X", true));
        tiposExame.put("Sangue", new TipoExame("Sangue", true));
        tiposExame.put("DNA Alienígena", new TipoExame("DNA Alienígena", false));
    }

    protected Optional<Exame> obterExamePorId(Long id) {
        return repositorio.obterPorId(id);
    }

    protected Paciente obterPaciente(String nome) {
        return pacientes.get(nome);
    }

    protected Medico obterMedico(String nome) {
        return medicos.get(nome);
    }

    protected TipoExame obterTipo(String nome) {
        return tiposExame.get(nome);
    }

    // Implementação mock do barramento de eventos
    @Override
    public <E> void adicionar(EventoObservador<E> observador) {
        // Não é necessário para esses cenários, mas mantido para compatibilidade arquitetural
        throw new UnsupportedOperationException();
    }

    @Override
    public <E> void postar(E evento) {
        notNull(evento, "Evento não pode ser nulo");
        eventos.add(evento);
    }
}
