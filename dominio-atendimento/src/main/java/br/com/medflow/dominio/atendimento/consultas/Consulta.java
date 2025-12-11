// Localização: dominio-atendimento/src/main/java/br/com/medflow/dominio/atendimento/consultas/Consulta.java

package br.com.medflow.dominio.atendimento.consultas;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects; 

/**
 * Entidade Raiz de Agregado (Aggregate Root) Consulta.
 * Implementa ColecaoHistorico para aplicar o Padrão Iterator.
 */
public class Consulta implements ColecaoHistorico<HistoricoConsultaEntrada> { 
    private ConsultaId id;
    private LocalDateTime dataHora;
    private String descricao;
    private StatusConsulta status;
    
    private Integer pacienteId; 
    private Integer medicoId; 
    
    private List<HistoricoConsultaEntrada> historico = new ArrayList<>(); 

    // Construtor padrão para JPA/ModelMapper (se for necessário)
    public Consulta() {}
    
    // NOVO CONSTRUTOR DE RECONSTRUÇÃO (USADO PELO REPOSITÓRIO NA LEITURA)
    public Consulta(ConsultaId id, LocalDateTime dataHora, String descricao, StatusConsulta status, Integer pacienteId, Integer medicoId, List<HistoricoConsultaEntrada> historico) {
        Objects.requireNonNull(id, "O ID da consulta não pode ser nulo na reconstrução."); // Equivalente a notNull
        this.id = id;
        this.dataHora = dataHora;
        this.descricao = descricao;
        this.status = status;
        this.pacienteId = pacienteId; 
        this.medicoId = medicoId;     
        this.historico.addAll(historico);
    }
    
    // Construtor de criação (usado pelo Serviço de Domínio para novos agendamentos)
    public Consulta(LocalDateTime dataHora, String descricao, Integer pacienteId, Integer medicoId, UsuarioResponsavelId responsavelId) {
        this.id = null; 
        this.dataHora = dataHora;
        this.descricao = descricao;
        this.status = StatusConsulta.AGENDADA;
        this.pacienteId = pacienteId; 
        this.medicoId = medicoId;     
        
        // Registra a criação no histórico
        adicionarEntradaHistorico(AcaoHistorico.CRIACAO, "Consulta agendada.", responsavelId);
    }

    // MODIFICADO: Agora aceita o UsuarioResponsavelId para registro no histórico
    public void mudarStatus(StatusConsulta novoStatus, UsuarioResponsavelId responsavelId) {
        Objects.requireNonNull(responsavelId, "O ID do usuário responsável é obrigatório para mudanças de status.");

        if (this.status == novoStatus) {
            return;
        }

        this.status = novoStatus;
        adicionarEntradaHistorico(AcaoHistorico.ATUALIZACAO_STATUS, 
                                 "Status alterado para: " + novoStatus.name(), 
                                 responsavelId);
    }
    
    // NOVO MÉTODO DE DOMÍNIO PARA REMARCAÇÃO (Ação específica que gera histórico)
    public void remarcar(LocalDateTime novaDataHora, UsuarioResponsavelId responsavelId) {
        Objects.requireNonNull(responsavelId, "O ID do usuário responsável é obrigatório para remarcação.");
        
        if (this.dataHora.equals(novaDataHora)) {
            return; // Nenhuma mudança de data/hora.
        }
        
        // 1. Gera o histórico com a ação correta
        adicionarEntradaHistorico(AcaoHistorico.ATUALIZACAO_DATA_HORA, 
                                 String.format("Data/Hora alterada de %s para %s.", this.dataHora.toString(), novaDataHora.toString()), 
                                 responsavelId);
                                 
        // 2. Altera a data/hora
        this.dataHora = novaDataHora;
        // O status permanece AGENDADA, mas a ação é registrada.
    }
    
    // Método de ajuda para adicionar entrada no histórico
    private void adicionarEntradaHistorico(AcaoHistorico acao, String descricao, UsuarioResponsavelId responsavelId) {
        var entrada = new HistoricoConsultaEntrada(acao, descricao, responsavelId, LocalDateTime.now());
        this.historico.add(entrada);
    }

    // IMPLEMENTAÇÃO DO PADRÃO ITERATOR: Método de fábrica do iterador
    @Override
    public IteradorHistorico<HistoricoConsultaEntrada> criarIterador() {
        // Ponto de Robustez: Retorna uma cópia defensiva (imutável) da lista para o Iterador
        return new ConsultaHistoricoIterator(List.copyOf(this.historico));
    }


    // Getters existentes
    public Integer getPacienteId() { return pacienteId; }
    public Integer getMedicoId() { return medicoId; }
    public ConsultaId getId() { return id; }
    public LocalDateTime getDataHora() { return dataHora; }
    public String getDescricao() { return descricao; }
    public StatusConsulta getStatus() { return status; }
    
    // Ponto de Robustez: Desencoraja o acesso direto à lista interna (similar ao Medicamento.java)
    @Deprecated 
    public List<HistoricoConsultaEntrada> getHistorico() { return List.copyOf(historico); }
}