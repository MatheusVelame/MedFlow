package br.com.medflow.dominio.referencia.tiposExames;

import static org.apache.commons.lang3.Validate.notNull;

import java.util.List;
import java.util.Optional;

public class TipoExameServico {
    private final TipoExameRepositorio repositorio;

    public TipoExameServico(TipoExameRepositorio repositorio) {
        notNull(repositorio, "O repositório de tipos de exame não pode ser nulo");
        this.repositorio = repositorio;
    }

    public TipoExame cadastrar(String codigo, String descricao, String especialidade, 
                             Double valor, UsuarioResponsavelId responsavelId) {
        // Regra de Negócio: O código do tipo de exame deve ser único
        var existente = repositorio.obterPorCodigo(codigo);
        if (existente.isPresent()) {
            throw new IllegalArgumentException("O tipo de exame com código '" + codigo + "' já está registrado no sistema.");
        }
        
        // Validações adicionais
        if (descricao == null || descricao.isBlank()) {
            throw new IllegalArgumentException("A descrição do tipo de exame é obrigatória");
        }
        
        if (especialidade == null || especialidade.isBlank()) {
            throw new IllegalArgumentException("A especialidade do tipo de exame é obrigatória");
        }
        
        if (valor == null) {
            throw new IllegalArgumentException("O valor do tipo de exame é obrigatório");
        }
        
        if (valor < 0) {
            throw new IllegalArgumentException("O valor deve ser maior ou igual a zero");
        }
        
        var novo = new TipoExame(codigo, descricao, especialidade, valor, responsavelId);
        repositorio.salvar(novo);
        return novo;
    }
    
    public TipoExame obter(TipoExameId id) {
        return repositorio.obter(id);
    }
    
    public void atualizarDescricao(TipoExameId id, String novaDescricao, UsuarioResponsavelId responsavelId, boolean temAgendamentos) {
        if (temAgendamentos) {
            throw new IllegalStateException("Não é permitido alterar tipos de exame com agendamentos vinculados");
        }
        
        var tipoExame = obter(id);
        tipoExame.atualizarDescricao(novaDescricao, responsavelId);
        repositorio.salvar(tipoExame);
    }
    
    public void atualizarEspecialidade(TipoExameId id, String novaEspecialidade, UsuarioResponsavelId responsavelId, boolean temAgendamentos) {
        if (temAgendamentos) {
            throw new IllegalStateException("Não é permitido alterar tipos de exame com agendamentos vinculados");
        }
        
        var tipoExame = obter(id);
        tipoExame.atualizarEspecialidade(novaEspecialidade, responsavelId);
        repositorio.salvar(tipoExame);
    }
    
    public void atualizarValor(TipoExameId id, Double novoValor, UsuarioResponsavelId responsavelId, boolean temAgendamentos) {
        if (temAgendamentos) {
            throw new IllegalStateException("Não é permitido alterar tipos de exame com agendamentos vinculados");
        }
        
        var tipoExame = obter(id);
        tipoExame.atualizarValor(novoValor, responsavelId);
        repositorio.salvar(tipoExame);
    }
    
    public void inativar(TipoExameId id, UsuarioResponsavelId responsavelId, boolean temAgendamentosFuturos) {
        if (temAgendamentosFuturos) {
            throw new IllegalStateException("Não é permitido inativar tipos de exame com agendamentos futuros");
        }
        
        var tipoExame = obter(id);
        tipoExame.inativar(responsavelId);
        repositorio.salvar(tipoExame);
    }
    
    public void excluir(TipoExameId id, UsuarioResponsavelId responsavelId, boolean temAgendamentos) {
        if (temAgendamentos) {
            throw new IllegalStateException("Não é permitido excluir tipos de exame com agendamentos vinculados");
        }
        
        // Verificar se o exame existe antes de excluir
        repositorio.obter(id); // Lançará exceção se não encontrar
        repositorio.excluir(id);
    }
    
    public Optional<TipoExame> pesquisar(String codigo) {
        return repositorio.obterPorCodigo(codigo);
    }

    public List<TipoExame> pesquisarComFiltroInativo() {
        return repositorio.pesquisarComFiltroInativo();
    }

    public List<TipoExame> pesquisarPadrao() {
        return repositorio.pesquisar();
    }
}