package br.com.medflow.dominio.financeiro.folhapagamento;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FolhaPagamentoFuncionalidadeBase {
    // Variáveis de domínio/ambiente
    protected FolhaPagamentoServico folhaPagamentoServico;
    protected FolhaPagamentoRepositorioMemoria repositorio;

    // Mocks de Usuários e Funcionários
    private Map<String, UsuarioResponsavelId> usuariosId = new HashMap<>();
    public Map<String, Integer> funcionariosId = new HashMap<>();
    private Map<Integer, Boolean> funcionariosAtivos = new HashMap<>();

    // Eventos capturados
    protected List<Object> eventos;

    public FolhaPagamentoFuncionalidadeBase() {
        this.repositorio = new FolhaPagamentoRepositorioMemoria();
        this.folhaPagamentoServico = new FolhaPagamentoServico(repositorio);
        this.eventos = new ArrayList<>();
    }

    protected UsuarioResponsavelId getUsuarioId(String nome) {
        return usuariosId.computeIfAbsent(nome, k -> new UsuarioResponsavelId(usuariosId.size() + 1));
    }

    protected Integer getFuncionarioId(String nome) {
        return funcionariosId.computeIfAbsent(nome, k -> funcionariosId.size() + 1);
    }

    protected void setFuncionarioAtivo(int funcionarioId, boolean ativo) {
        funcionariosAtivos.put(funcionarioId, ativo);
    }

    protected boolean isFuncionarioAtivo(int funcionarioId) {
        return funcionariosAtivos.getOrDefault(funcionarioId, true); // padrão é ativo
    }

}