package br.com.medflow.dominio.financeiro.faturamentos;

import java.util.ArrayList;
import java.util.List;

public abstract class FaturamentoFuncionalidadeBase {
    private static FaturamentoRepositorioMemoria repositorioCompartilhado;
    private static TabelaPrecosServico tabelaPrecosCompartilhado;
    private static FaturamentoServico servicoCompartilhado;
    
    protected FaturamentoRepositorioMemoria repositorio;
    protected TabelaPrecosServico tabelaPrecos;
    protected FaturamentoServico servico;
    protected List<String> eventos = new ArrayList<>();

    public void configurarContexto() {
        // Usar repositório compartilhado para todos os testes
        if (repositorioCompartilhado == null) {
            repositorioCompartilhado = new FaturamentoRepositorioMemoria();
            tabelaPrecosCompartilhado = new TabelaPrecosServico();
            servicoCompartilhado = new FaturamentoServico(repositorioCompartilhado, tabelaPrecosCompartilhado);
        }
        
        this.repositorio = repositorioCompartilhado;
        this.tabelaPrecos = tabelaPrecosCompartilhado;
        this.servico = servicoCompartilhado;
        
        // Simulação de sistema de permissões
        // Em um sistema real, isso seria integrado com um serviço de autenticação/autorização
    }

    public void resetContexto() {
        if (repositorio != null) {
            repositorio.clear();
        }
        eventos.clear();
    }
}
