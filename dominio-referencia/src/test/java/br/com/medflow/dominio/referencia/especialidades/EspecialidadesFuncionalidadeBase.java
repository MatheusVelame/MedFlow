package br.com.medflow.dominio.referencia.especialidades;

// import io.cucumber.java.Before;
import org.apache.commons.lang3.RandomStringUtils;

/**
 * Classe base para os Steps Definitions de Especialidades.
 * Gerencia as dependências do domínio e o estado (contexto) de execução de cada cenário.

 */
public class EspecialidadesFuncionalidadeBase {

    // DEPENDÊNCIAS DE DOMÍNIO (Injetadas e Mockadas)
    protected EspecialidadesRepositorioMemoria repositorio;
    protected MedicoRepositorioMemoria medicoRepositorio;
    protected EspecialidadeServico servico;

    // ESTADO DO CENÁRIO (Variáveis de Contexto)
    private RegraNegocioException ultimaExcecao;
    private String descricao;
    private Especialidade ultimaEspecialidadeCadastrada;


    // @Before
    public void setup() {

        this.repositorio = new EspecialidadesRepositorioMemoria();
        this.medicoRepositorio = new MedicoRepositorioMemoria();
     
        this.servico = new EspecialidadeServico(repositorio, medicoRepositorio);

        this.repositorio.limpar();
        this.medicoRepositorio.limpar();
        this.ultimaExcecao = null;
        this.descricao = null;
        this.ultimaEspecialidadeCadastrada = null;

        this.repositorio.popular("Pediatria", "Pediatria Geral", StatusEspecialidade.ATIVA, false);
        this.medicoRepositorio.mockContagem("Pediatria", 0); 

        this.repositorio.popular("Dermatologia", "Dermatologia Estética", StatusEspecialidade.ATIVA, true);
        this.medicoRepositorio.mockContagem("Dermatologia", 2); 

        this.repositorio.popular("Gastroenterologia", "Tratamento Gastro", StatusEspecialidade.ATIVA, true);
        this.medicoRepositorio.mockContagem("Gastroenterologia", 0);
    }

    // ===========================================
    // GETTERS E SETTERS (Ponto de contato para EspecialidadesFuncionalidade)
    // ===========================================

    // Getters
    public RegraNegocioException getUltimaExcecao() {
        return ultimaExcecao;
    }

    public String getDescricao() {
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

    protected String gerarString(int tamanho) {
        if (tamanho > 0) {
            return RandomStringUtils.randomAlphanumeric(tamanho);
        }
        return "";
    }
}