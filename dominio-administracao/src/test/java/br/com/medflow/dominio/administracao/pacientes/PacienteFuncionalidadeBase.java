package br.com.medflow.dominio.administracao.pacientes;

import static org.apache.commons.lang3.Validate.notNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import br.com.medflow.dominio.administracao.evento.EventoBarramento;
import br.com.medflow.dominio.administracao.evento.EventoObservador;

public class PacienteFuncionalidadeBase implements EventoBarramento {
    // Variáveis de domínio/ambiente
    protected PacienteServico pacienteServico;
    protected PacienteRepositorioMemoria repositorio;
    
    // Mocks de Usuários e Permissões
    private Map<String, UsuarioResponsavelId> usuariosId = new HashMap<>(); 
    private Map<String, List<String>> permissoes = new HashMap<>();
    
    // Eventos capturados
    protected List<Object> eventos;

    public PacienteFuncionalidadeBase() {
        this.repositorio = new PacienteRepositorioMemoria();
        this.pacienteServico = new PacienteServico(repositorio);
        this.eventos = new ArrayList<>();
        
        permissoes.put("Administrador", List.of("cadastrar", "alterar", "remover", "consultar"));
        permissoes.put("Médico", List.of("consultar", "alterar"));
        permissoes.put("Recepcionista", List.of("cadastrar", "alterar", "consultar"));
        permissoes.put("Enfermeiro", List.of("consultar"));
    }
    
    protected Optional<Paciente> obterPacientePorCpf(String cpf) {
        return repositorio.obterPorCpf(cpf);
    }

    protected UsuarioResponsavelId getUsuarioId(String nome) {
        return usuariosId.computeIfAbsent(nome, k -> new UsuarioResponsavelId(usuariosId.size() + 1));
    }
    
    protected boolean temPermissao(String perfil, String acao) {
        return permissoes.getOrDefault(perfil, List.of()).contains(acao);
    }
    
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