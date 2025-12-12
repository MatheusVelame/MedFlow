// Localização: dominio-atendimento/src/main/java/br/com/medflow/dominio/atendimento/consultas/ConsultaServico.java

package br.com.medflow.dominio.atendimento.consultas;

import static org.apache.commons.lang3.Validate.notNull;

import java.time.LocalDateTime;

/**
 * Serviço de Domínio que orquestra as regras de negócio de alto nível (CUD).
 */
public class ConsultaServico {
    
    private final ConsultaRepositorio repositorio;

    public ConsultaServico(ConsultaRepositorio repositorio) {
        notNull(repositorio, "O repositório de consulta não pode ser nulo.");
        this.repositorio = repositorio;
    }

    /**
     * Comando: Cadastrar/Agendar uma nova consulta.
     * @param dataHora Data e hora do agendamento.
     * @param descricao Descrição da consulta.
     * @param pacienteId ID do Paciente (usado na simulação).
     * @param medicoId ID do Médico (usado na simulação).
     * @param usuarioId ID do usuário responsável pela ação (NOVO).
     */
    public void agendar(LocalDateTime dataHora, String descricao, Integer pacienteId, Integer medicoId, Integer usuarioId) {
        // 1. Validar regras de agendamento (Ex: verificar disponibilidade do médico)
        // Lógica de domínio aqui...
        
        // Cria o VO para rastreabilidade (Responsável)
        UsuarioResponsavelId responsavelId = new UsuarioResponsavelId(usuarioId);
        
        // 2. Criar a Aggregate Root (Passando o Responsável, conforme nova assinatura)
        var novaConsulta = new Consulta(dataHora, descricao, pacienteId, medicoId, responsavelId);
        
        // 3. Persistir usando a Porta (Interface) do repositório
        repositorio.salvar(novaConsulta);
    }
    
    /**
     * Comando: Mudar o status de uma consulta existente.
     * @param id ID da consulta a ser alterada.
     * @param novoStatus Novo status desejado.
     * @param usuarioId ID do usuário responsável pela ação (NOVO).
     */
    public void mudarStatus(ConsultaId id, StatusConsulta novoStatus, Integer usuarioId) {
        // 1. Buscar o Aggregate Root (garantindo o limite transacional)
        Consulta consulta = repositorio.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Consulta não encontrada."));
        
        // Cria o VO para rastreabilidade (Responsável)
        UsuarioResponsavelId responsavelId = new UsuarioResponsavelId(usuarioId);
        
        // 2. Executar o comportamento (regra de negócio) na própria Aggregate Root (Passando o Responsável)
        consulta.mudarStatus(novoStatus, responsavelId);
        
        // 3. Persistir o estado alterado
        repositorio.salvar(consulta);
    }
    
    // ... Outros comandos: atualizarData, cancelar, finalizar, etc.
}