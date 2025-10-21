package br.com.medflow.dominio.referencia.tiposExames.test;

import static org.apache.commons.lang3.Validate.notNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import br.com.medflow.dominio.evento.EventoBarramento;
import br.com.medflow.dominio.evento.EventoObservador;
import br.com.medflow.dominio.referencia.tiposExames.TipoExame;
import br.com.medflow.dominio.referencia.tiposExames.TipoExameId;
import br.com.medflow.dominio.referencia.tiposExames.TipoExameRepositorioMemoria;
import br.com.medflow.dominio.referencia.tiposExames.TipoExameServico;
import br.com.medflow.dominio.referencia.tiposExames.UsuarioResponsavelId;

/**
 * Classe base para os testes BDD de Tipos de Exames. 
 * Responsável por configurar o ambiente (serviços in-memory) e simular o barramento de eventos.
 */
public class TipoExameFuncionalidadeBase implements EventoBarramento {
    // Variáveis de domínio/ambiente
    protected TipoExameServico tipoExameServico;
    protected TipoExameRepositorioMemoria repositorio;
    
    // Mocks de Usuários e Permissões
    protected Map<String, UsuarioResponsavelId> usuariosId = new HashMap<>(); 
    protected Map<String, List<String>> permissoes = new HashMap<>();
    
    // Mock de especialidades e agendamentos
    protected Map<String, Boolean> especialidadesExistentes = new HashMap<>();
    protected Map<String, Boolean> examesComAgendamentos = new HashMap<>();
    protected Map<String, Boolean> examesComAgendamentosFuturos = new HashMap<>();
    
    // Eventos capturados
    protected List<Object> eventos;

    public TipoExameFuncionalidadeBase() {
        this.repositorio = new TipoExameRepositorioMemoria();
        this.tipoExameServico = new TipoExameServico(repositorio);
        this.eventos = new ArrayList<>();
        
        // Simulação do sistema de permissões
        permissoes.put("Gerente", List.of("cadastrar", "atualizar", "inativar", "excluir"));
        
        // Especialidades pré-configuradas
        especialidadesExistentes.put("Radiologia", true);
        especialidadesExistentes.put("Cardiologia", true);
        especialidadesExistentes.put("Hematologia", true);
    }
    
    protected Optional<TipoExame> obterTipoExame(String codigo) {
        return repositorio.obterPorCodigo(codigo);
    }

    protected UsuarioResponsavelId getUsuarioId(String nome) {
        return usuariosId.computeIfAbsent(nome, k -> new UsuarioResponsavelId(usuariosId.size() + 1));
    }
    
    protected boolean verificarEspecialidadeExistente(String especialidade) {
        return especialidadesExistentes.getOrDefault(especialidade, false);
    }
    
    protected void marcarAgendamento(String codigoExame) {
        examesComAgendamentos.put(codigoExame, true);
    }
    
    protected void removerAgendamento(String codigoExame) {
        examesComAgendamentos.remove(codigoExame);
    }
    
    protected boolean verificarAgendamentosExistentes(String codigoExame) {
        return examesComAgendamentos.getOrDefault(codigoExame, false);
    }
    
    protected void marcarAgendamentoFuturo(String codigoExame) {
        examesComAgendamentosFuturos.put(codigoExame, true);
    }
    
    protected void removerAgendamentoFuturo(String codigoExame) {
        examesComAgendamentosFuturos.remove(codigoExame);
    }
    
    protected boolean verificarAgendamentosFuturos(String codigoExame) {
        return examesComAgendamentosFuturos.getOrDefault(codigoExame, false);
    }
    
    // Implementação mock do EventoBarramento (apenas para teste)
    @Override
    public <E> void adicionar(EventoObservador<E> observador) { 
        throw new UnsupportedOperationException();
    }

    @Override
    public <E> void postar(E evento) {
        notNull(evento, "O evento não pode ser nulo");
        eventos.add(evento);
    }
}