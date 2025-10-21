package br.com.medflow.dominio.referencia.especialidades;

import io.cucumber.java.Before;
import org.apache.commons.lang3.RandomStringUtils;

// OBS: As classes a seguir (Especialidade, EspecialidadeServico, RegraNegocioException)
// DEVEM ser importadas ou criadas no seu projeto. Ajuste os imports conforme a sua estrutura de pacotes.
import br.com.medflow.dominio.referencia.especialidades.Especialidade;
import br.com.medflow.dominio.referencia.especialidades.EspecialidadeServico;
import br.com.medflow.dominio.RegraNegocioException;
import br.com.medflow.dominio.referencia.especialidades.EspecialidadesRepositorioMemoria;


/**
 * Classe base para os Steps Definitions de Especialidades.
 * Gerencia as dependências do domínio e o contexto de execução de cada cenário (Before hook).
 */
public class EspecialidadesFuncionalidadeBase {

    // DEPENDÊNCIAS DE DOMÍNIO
    // A implementação em memória é usada para testes isolados
    protected EspecialidadesRepositorioMemoria repositorio;
    protected EspecialidadeServico servico;

    // CONTEXTO DO CENÁRIO (Estado do Teste)
    private RegraNegocioException ultimaExcecao;
    private String descricao;
    private Especialidade ultimaEspecialidadeCadastrada;

    /**
     * Hook do Cucumber que é executado antes de cada cenário.
     * Garante que o estado seja limpo e as dependências reinicializadas
     * para isolar a execução de cada teste BDD.
     */
    @Before
    public void setup() {
        // 1. Inicializa o Repositório de Memória
        this.repositorio = new EspecialidadesRepositorioMemoria();
        
        // 2. Inicializa o Serviço com o Repositório (injeção de dependência manual para teste)
        this.servico = new EspecialidadeServico(repositorio);

        // 3. Limpa o estado do Repositório e do Contexto
        this.repositorio.limpar();
        this.ultimaExcecao = null;
        this.descricao = null;
        this.ultimaEspecialidadeCadastrada = null;
        
        // 4. PREENCHIMENTO DE DADOS DE CONTEXTO (Mock das Precondições - Opcional)
        // Cenários mais complexos que foram comentados na feature podem ser inicializados aqui:
        
        // Exemplo:
        // repositorio.salvar(new Especialidade("Pediatria", true, false, false));
        // repositorio.salvar(new Especialidade("Dermatologia", true, true, true)); // Com médicos ativos
        // repositorio.salvar(new Especialidade("Gastroenterologia", false, true, false)); // Inativa, com histórico
    }

    // ===========================================
    // GETTERS E SETTERS PARA O CONTEXTO DO CENÁRIO
    // ===========================================

    public RegraNegocioException getUltimaExcecao() {
        return ultimaExcecao;
    }

    public void setUltimaExcecao(RegraNegocioException ultimaExcecao) {
        this.ultimaExcecao = ultimaExcecao;
    }

    public String getDescricao() {
        // Método auxiliar para garantir que RN 1.4 utilize a string correta
        if (this.descricao == null) {
            // Garante que, se a string não foi gerada no Given, use um valor padrão.
            return ""; 
        }
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Especialidade getUltimaEspecialidadeCadastrada() {
        return ultimaEspecialidadeCadastrada;
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
        // Uso de uma biblioteca como Apache Commons Lang para geração segura de strings aleatórias.
        // Se a biblioteca não estiver disponível, uma implementação manual simples pode ser usada.
        if (tamanho > 0) {
            return RandomStringUtils.randomAlphanumeric(tamanho);
        }
        return "";
    }
}