package br.com.medflow.dominio.referencia.especialidades;

import io.cucumber.java.Before;
import org.apache.commons.lang3.RandomStringUtils;

/**
 * Classe base para os Steps Definitions de Especialidades.
 * Gerencia as dependências do domínio e o estado (contexto) de execução de cada cenário.
 *
 * NOTA: O MedicoRepositorioMemoria é uma dependência crucial para simular
 * as Regras de Negócio de vínculo (RNs 2.3 e 3.1).
 */
public class EspecialidadesFuncionalidadeBase {

    // DEPENDÊNCIAS DE DOMÍNIO (Injetadas e Mockadas)
    protected EspecialidadesRepositorioMemoria repositorio;
    protected MedicoRepositorioMemoria medicoRepositorio; // NOVO: Dependência para verificar vínculos
    protected EspecialidadeServico servico;

    // ESTADO DO CENÁRIO (Variáveis de Contexto)
    private RegraNegocioException ultimaExcecao;
    private String descricao;
    private Especialidade ultimaEspecialidadeCadastrada;

    /**
     * Hook do Cucumber que é executado ANTES de cada cenário.
     * Garante que o estado seja limpo e as dependências reinicializadas.
     */
    @Before
    public void setup() {
        // 1. Inicializa Repositórios de Memória
        this.repositorio = new EspecialidadesRepositorioMemoria();
        this.medicoRepositorio = new MedicoRepositorioMemoria(); // NOVO: Inicialização do Mock
        
        // 2. Inicializa o Serviço (Injeção de dependências)
        this.servico = new EspecialidadeServico(repositorio, medicoRepositorio); // NOVO: Injeção do MedicoRepositorio

        // 3. Limpa o estado (Mocked Repositories e Contexto)
        this.repositorio.limpar();
        this.medicoRepositorio.limpar(); // NOVO: Limpeza do Mock de Médicos
        this.ultimaExcecao = null;
        this.descricao = null;
        this.ultimaEspecialidadeCadastrada = null;
    }

    // ===========================================
    // GETTERS E SETTERS (Ponto de contato para EspecialidadesFuncionalidade)
    // ===========================================

    // Getters
    public RegraNegocioException getUltimaExcecao() {
        return ultimaExcecao;
    }

    public String getDescricao() {
        // Retorna a descrição gerada para o teste (RN 1.4)
        return (descricao != null) ? descricao : "";
    }

    public Especialidade getUltimaEspecialidadeCadastrada() {
        return ultimaEspecialidadeCadastrada;
    }
    
    // Setters
    public void setUltimaExcecao(RegraNegocioException ultimaExcecao) {
        this.ultimaExcecao = ultimaExcecao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public void setUltimaEspecialidadeCadastrada(Especialidade ultimaEspecialidadeCadastrada) {
        this.ultimaEspecialidadeCadastrada = ultimaEspecialidadeCadastrada;
    }
    
    // ===========================================
    // MÉTODOS AUXILIARES (HELPERS)
    // ===========================================

    /**
     * Método auxiliar para gerar strings de um determinado tamanho.
     * Útil para testar limites de caracteres (RN 1.4).
     */
    protected String gerarString(int tamanho) {
        // Uso de uma biblioteca como Apache Commons Lang.
        if (tamanho > 0) {
            // Este método gera uma string de 255 caracteres
            return RandomStringUtils.randomAlphanumeric(tamanho);
        }
        return "";
    }
}