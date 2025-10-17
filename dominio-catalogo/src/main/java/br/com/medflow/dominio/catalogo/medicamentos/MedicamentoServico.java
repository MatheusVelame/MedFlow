package br.com.medflow.dominio.catalogo.medicamentos;

import static org.apache.commons.lang3.Validate.notNull;
import java.util.List;

public class MedicamentoServico {
    private final MedicamentoRepositorio repositorio;
    private final PrescricaoServicoExterno prescricaoServico = new PrescricaoServicoExterno();

    public MedicamentoServico(MedicamentoRepositorio repositorio) {
        notNull(repositorio, "O repositório de Medicamentos não pode ser nulo");
        this.repositorio = repositorio;
    }

    public String cadastrar(String nome, String usoPrincipal, String contraindicacoes, String responsavel, boolean temPermissao) {
        if (!temPermissao) {
             throw new SecurityException("Usuário não tem permissão para cadastrar medicamentos.");
        }
        
        if (repositorio.obterPorNome(nome).isPresent()) {
            throw new IllegalArgumentException("O nome do medicamento já está em uso: " + nome);
        }

        Medicamento novoMedicamento = new Medicamento(nome, usoPrincipal, contraindicacoes);
        
        String novoId = repositorio.salvar(novoMedicamento);
        
        HistoricoRegistro historico = new HistoricoRegistro(novoId, responsavel, "Criação do medicamento", "Medicamento cadastrado com status ATIVO.");
        repositorio.getHistoricoRepositorio().salvar(historico);
        
        return novoId;
    }
    
    public void atualizar(String nomeMedicamento, MedicamentoUpdateDto dto, String responsavel, boolean temPermissao) {
        if (!temPermissao) {
             throw new SecurityException("Usuário não tem permissão para alterar dados.");
        }
        
        Medicamento medicamento = repositorio.obterPorNome(nomeMedicamento)
                                             .orElseThrow(() -> new IllegalArgumentException("Medicamento não encontrado: " + nomeMedicamento));

        if (dto.usoPrincipal != null && dto.usoPrincipal.isBlank()) {
            throw new IllegalArgumentException("Não é permitido alterar campos obrigatórios para valor em branco: Uso Principal");
        }

        // Verifica Alteração Crítica (Contraindicações)
        if (dto.contraindicacoes != null && !dto.contraindicacoes.equals(medicamento.getContraindicacoes())) {
            
            if(dto.contraindicacoes.contains("Gravidez")) { 
                
                medicamento.setAlteracaoPendente(dto.contraindicacoes);
                medicamento.setResponsavelSolicitacao(responsavel); // CORREÇÃO: Atribui o responsável pela solicitação
                repositorio.salvar(medicamento);
                
                HistoricoRegistro historico = new HistoricoRegistro(medicamento.getId(), responsavel, "Solicitação de Alteração Crítica", "Novo valor: " + dto.contraindicacoes);
                repositorio.getHistoricoRepositorio().salvar(historico);
                
                throw new IllegalStateException("Alteração crítica detectada. Status: Pendente de Revisão.");
            } else {
                medicamento.setContraindicacoes(dto.contraindicacoes);
                // Se a alteração não for crítica, registra o histórico imediatamente
                HistoricoRegistro historico = new HistoricoRegistro(medicamento.getId(), responsavel, "Atualização de Contraindicações", "Novo valor: " + dto.contraindicacoes);
                repositorio.getHistoricoRepositorio().salvar(historico);
            }
        }
        
        // Aplica alterações não-críticas
        if (dto.usoPrincipal != null && !dto.usoPrincipal.equals(medicamento.getUsoPrincipal())) {
            medicamento.setUsoPrincipal(dto.usoPrincipal);
            HistoricoRegistro historico = new HistoricoRegistro(medicamento.getId(), responsavel, "Atualização de Uso Principal", "Novo valor: " + dto.usoPrincipal);
            repositorio.getHistoricoRepositorio().salvar(historico);
        }
        
        if (dto.status != null && dto.status != medicamento.getStatus()) {
            medicamento.setStatus(dto.status);
            HistoricoRegistro historico = new HistoricoRegistro(medicamento.getId(), responsavel, "Atualização de Status", "Novo status: " + dto.status.name());
            repositorio.getHistoricoRepositorio().salvar(historico);
        }
        
        repositorio.salvar(medicamento);
    }
    
    public void aprovarAlteracao(String nomeMedicamento, String responsavel, boolean temPermissaoRevisor) {
        if (!temPermissaoRevisor) {
             throw new SecurityException("Usuário não tem permissão de revisor para aprovar alterações.");
        }
        
        Medicamento medicamento = repositorio.obterPorNome(nomeMedicamento)
                                             .orElseThrow(() -> new IllegalArgumentException("Medicamento não encontrado: " + nomeMedicamento));
        
        if (medicamento.getStatusRevisao() == StatusRevisao.PENDENTE) {
            medicamento.aplicarAlteracaoPendente();
            repositorio.salvar(medicamento);
            
            HistoricoRegistro historico = new HistoricoRegistro(medicamento.getId(), responsavel, "Alteração Crítica APROVADA", "Alteração pendente aplicada: " + medicamento.getContraindicacoes());
            repositorio.getHistoricoRepositorio().salvar(historico);
        }
    }
    
    public void arquivar(String nomeMedicamento, String responsavel, boolean temPermissaoAltaAutoridade, boolean tentarExcluir) {
        if (!temPermissaoAltaAutoridade) {
             throw new SecurityException("Usuário não tem permissão para arquivar/remover medicamentos.");
        }
        
        Medicamento medicamento = repositorio.obterPorNome(nomeMedicamento)
                                             .orElseThrow(() -> new IllegalArgumentException("Medicamento não encontrado: " + nomeMedicamento));
        
        if (prescricaoServico.possuiPrescricaoAtiva(medicamento.getId())) {
             throw new IllegalStateException("Ação não pode ser realizada devido a vínculos com prescrições ativas.");
        }

        if (tentarExcluir) {
            throw new IllegalArgumentException("Exclusão permanente requer justificativa e aprovação específica. Sugere-se manter o registro arquivado.");
        }
        
        medicamento.setStatus(StatusMedicamento.ARQUIVADO);
        repositorio.salvar(medicamento);
        
        HistoricoRegistro historico = new HistoricoRegistro(medicamento.getId(), responsavel, "Arquivamento de Medicamento", "Status alterado para ARQUIVADO.");
        repositorio.getHistoricoRepositorio().salvar(historico);
    }
    
    public Medicamento obterPorNome(String nome) {
        return repositorio.obterPorNome(nome).orElse(null);
    }
    
    public List<Medicamento> pesquisar(String nome, boolean incluirArquivados) {
        List<Medicamento> resultados = repositorio.listarTodos();
        
        return resultados.stream()
            .filter(m -> m.getNome().contains(nome))
            .filter(m -> incluirArquivados || m.getStatus() != StatusMedicamento.ARQUIVADO)
            .toList();
    }
}