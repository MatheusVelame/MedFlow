package br.com.medflow.dominio.referencia.especialidades;

/**
 * Entidade Aggregate Root do Catálogo de Especialidades.
 */
public class Especialidade {
    
    private Integer id; // Identificador persistente (estável)
    private String nome;
    private String descricao;
    private StatusEspecialidade status;
    private boolean possuiVinculoHistorico; // RN 3.2: Flag para controle de exclusão física

    // Construtor sem-argumentos necessário para frameworks (Mapeamento/ModelMapper/JPA)
    public Especialidade() {
        // Intencionalmente vazio - usado apenas para desserialização e mapeamento
    }

    // Construtor para novas especialidades (RN 1.5 - Status Inicial Ativa)
    public Especialidade(String nome, String descricao) {
        validarNomeObrigatorio(nome);
        this.nome = nome.trim();
        this.descricao = (descricao != null) ? validarDescricao(descricao) : null;
        this.status = StatusEspecialidade.ATIVA; // RN 1.5
        this.possuiVinculoHistorico = false;
    }
    
    // Construtor para carga de dados (uso em Repositórios e Mocks)
    public Especialidade(String nome, String descricao, StatusEspecialidade status, boolean possuiVinculoHistorico) {
        validarNomeObrigatorio(nome);
        this.nome = nome.trim();
        this.descricao = (descricao != null) ? validarDescricao(descricao) : null;
        this.status = status;
        this.possuiVinculoHistorico = possuiVinculoHistorico;
    }

    // Getter/Setter para ID (necessário para mapeamento JPA <-> DOMÍNIO)
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    
    // Métodos de Regra de Negócio

    /**
     * RN 2.1 e 2.2: Altera nome. A unicidade é verificada pelo Serviço.
     */
    public void alterarNome(String novoNome) {
        if (novoNome == null || novoNome.trim().isEmpty()) {
            throw new RegraNegocioException("O nome da especialidade é obrigatório"); // RN 1.1 / 2.2
        }
        validarNomeAlfanumerico(novoNome); // RN 1.3 / 2.2
        this.nome = novoNome.trim();
    }
    
    /**
     * RN 2.1: Altera descrição.
     */
    public void alterarDescricao(String novaDescricao) {
        this.descricao = (novaDescricao != null) ? validarDescricao(novaDescricao) : null;
    }

    /**
     * RN 3.3: Inativa a especialidade.
     */
    public void inativar() {
        this.status = StatusEspecialidade.INATIVA;
    }
    
    /**
     * Marca a especialidade como tendo vínculo histórico (chamado na atribuição de um médico).
     */
    public void registrarVinculoHistorico() {
        this.possuiVinculoHistorico = true;
    }
    
    // Métodos de Validação (Reutilizados no construtor e alteração)

    private void validarNomeObrigatorio(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new RegraNegocioException("O nome da especialidade é obrigatório"); // RN 1.1
        }
        validarNomeAlfanumerico(nome);
    }
    
    private void validarNomeAlfanumerico(String nome) {
        // RN 1.3: Permite letras, espaços e acentos. O regex [\p{L}\s]+ cobre isso.
        if (!nome.matches("[\\p{L}\\s]+")) {
            throw new RegraNegocioException("O nome da especialidade deve conter apenas caracteres alfabéticos e espaços");
        }
    }
    
    private String validarDescricao(String descricao) {
        // RN 1.4: Máximo de 255 caracteres
        if (descricao.length() > 255) {
            throw new RegraNegocioException("A descrição não pode exceder 255 caracteres");
        }
        return descricao;
    }

    // Getters
    public String getNome() { return nome; }
    public String getDescricao() { return descricao; }
    public StatusEspecialidade getStatus() { return status; }
    public boolean isPossuiVinculoHistorico() { return possuiVinculoHistorico; }

    // Setters públicos para auxiliar frameworks/ModelMapper na reconstrução do Aggregate
    public void setNome(String nome) { this.nome = nome; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public void setStatus(StatusEspecialidade status) { this.status = status; }
    public void setPossuiVinculoHistorico(boolean possuiVinculoHistorico) { this.possuiVinculoHistorico = possuiVinculoHistorico; }
}