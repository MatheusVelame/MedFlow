// Localização: dominio-catalogo/src/main/java/br/com/medflow/dominio/catalogo/medicamentos/MedicamentoServico.java

package br.com.medflow.dominio.catalogo.medicamentos;

import static org.apache.commons.lang3.Validate.notNull;

/**
 * Serviço de Domínio que orquestra as regras de negócio de alto nível (CUD).
 */
public class MedicamentoServico {
    
    private final MedicamentoRepositorio repositorio;
    // Em um cenário real, injetaríamos serviços externos ou de referência aqui.

    public MedicamentoServico(MedicamentoRepositorio repositorio) {
        notNull(repositorio, "O repositório de medicamento não pode ser nulo.");
        this.repositorio = repositorio;
    }

    /**
     * Comando: Cadastrar um novo medicamento.
     * Retorna o AR recém-criado para uso na camada de aplicação/teste.
     */
    public Medicamento cadastrar(String nome, String usoPrincipal, String contraindicacoes, UsuarioResponsavelId responsavelId) { // MUDANÇA: Retorna Medicamento
        // 1. Lógica de validação de negócio antes da criação
        // Ex: verificar se o nome já existe (repositorio.obterPorNome(nome))
        
        // 2. Criar a Aggregate Root
        var novoMedicamento = new Medicamento(nome, usoPrincipal, contraindicacoes, responsavelId);
        
        // 3. Persistir o estado inicial
        repositorio.salvar(novoMedicamento);
        
        return novoMedicamento; // NOVO: Retorna a instância.
    }
    
    /**
     * Comando: Atualizar o uso principal (exige que a AR execute o comportamento).
     */
    public void atualizarUsoPrincipal(MedicamentoId id, String novoUsoPrincipal, UsuarioResponsavelId responsavelId) {
        Medicamento medicamento = repositorio.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Medicamento não encontrado."));
                
        medicamento.atualizarUsoPrincipal(novoUsoPrincipal, responsavelId);
        
        repositorio.salvar(medicamento);
    }

    /**
     * Comando: Mudar o status (inclui arquivamento).
     */
    public void mudarStatus(MedicamentoId id, StatusMedicamento novoStatus, UsuarioResponsavelId responsavelId, boolean temPrescricaoAtiva) {
        Medicamento medicamento = repositorio.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Medicamento não encontrado."));
        
        if (novoStatus == StatusMedicamento.ARQUIVADO) {
            medicamento.arquivar(temPrescricaoAtiva, responsavelId);
        } else {
            medicamento.mudarStatus(novoStatus, responsavelId);
        }
        
        repositorio.salvar(medicamento);
    }
    
    /**
     * Comando: Solicitar revisão de contraindicações (lança RevisaoPendenteException).
     */
    public void solicitarRevisaoContraindicacoes(MedicamentoId id, String novaContraindicacao, UsuarioResponsavelId responsavelId) {
        Medicamento medicamento = repositorio.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Medicamento não encontrado."));
        
        medicamento.solicitarRevisaoContraindicacoes(novaContraindicacao, responsavelId);
        
        repositorio.salvar(medicamento); // Salva o estado PENDENTE
    }
    
    /**
     * Comando: Aprovar revisão pendente.
     */
    public void aprovarRevisao(MedicamentoId id, UsuarioResponsavelId revisorId) {
        Medicamento medicamento = repositorio.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Medicamento não encontrado."));
        
        medicamento.aprovarRevisao(revisorId);
        
        repositorio.salvar(medicamento);
    }
    
    /**
     * Comando: Rejeitar revisão pendente.
     */
    public void rejeitarRevisao(MedicamentoId id, UsuarioResponsavelId revisorId) {
        Medicamento medicamento = repositorio.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Medicamento não encontrado."));
        
        medicamento.rejeitarRevisao(revisorId);
        
        repositorio.salvar(medicamento);
    }

    public void arquivar(MedicamentoId id, UsuarioResponsavelId responsavelId, boolean temPrescricaoAtiva) {
        mudarStatus(id, StatusMedicamento.ARQUIVADO, responsavelId, temPrescricaoAtiva);
    }
}