package br.com.medflow.aplicacao.financeiro.convenios;

import br.com.medflow.dominio.financeiro.convenios.ConvenioCriadoEvent;
import br.com.medflow.dominio.financeiro.convenios.ConvenioExcluidoEvent;
import br.com.medflow.dominio.financeiro.convenios.ConvenioNomeAlteradoEvent;
import br.com.medflow.dominio.financeiro.convenios.ConvenioStatusAlteradoEvent;
import br.com.medflow.dominio.financeiro.evento.EventoObservador;

/**
 * Observador de eventos de Convenio responsável por registrar ações de auditoria.
 * Este observador reage a todos os eventos relacionados a convênios para fins de auditoria e rastreabilidade.
 */
public class ConvenioAuditoriaObservador implements EventoObservador<Object> {

	@Override
	public void observarEvento(Object evento) {
		// Usar pattern matching (instanceof) para tratar cada tipo de evento de Convenio
		if (evento instanceof ConvenioCriadoEvent criado) {
			registrarCriacao(criado);
		} else if (evento instanceof ConvenioStatusAlteradoEvent statusAlterado) {
			registrarAlteracaoStatus(statusAlterado);
		} else if (evento instanceof ConvenioNomeAlteradoEvent nomeAlterado) {
			registrarAlteracaoNome(nomeAlterado);
		} else if (evento instanceof ConvenioExcluidoEvent excluido) {
			registrarExclusao(excluido);
		}
		// Ignora outros tipos de eventos que não são de Convenio
	}

	private void registrarCriacao(ConvenioCriadoEvent evento) {
		// Registrar criação do convênio no sistema de auditoria
		registrarAuditoria("CRIACAO", 
			"Convênio criado: " + evento.getNome() + 
			" (ID: " + evento.getConvenioId() + 
			", Código: " + evento.getCodigoIdentificacao() + 
			", Status: " + evento.getStatus() + 
			", Responsável: " + evento.getResponsavelId() + ")");
	}

	private void registrarAlteracaoStatus(ConvenioStatusAlteradoEvent evento) {
		// Registrar alteração de status no sistema de auditoria
		registrarAuditoria("ALTERACAO_STATUS", 
			"Status do convênio alterado: " + evento.getConvenioId() + 
			" de " + evento.getStatusAnterior() + 
			" para " + evento.getNovoStatus() + 
			" (Responsável: " + evento.getResponsavelId() + ")");
	}

	private void registrarAlteracaoNome(ConvenioNomeAlteradoEvent evento) {
		// Registrar alteração de nome no sistema de auditoria
		registrarAuditoria("ALTERACAO_NOME", 
			"Nome do convênio alterado: " + evento.getConvenioId() + 
			" de '" + evento.getNomeAnterior() + 
			"' para '" + evento.getNovoNome() + 
			"' (Responsável: " + evento.getResponsavelId() + ")");
	}

	private void registrarExclusao(ConvenioExcluidoEvent evento) {
		// Registrar exclusão do convênio no sistema de auditoria
		registrarAuditoria("EXCLUSAO", 
			"Convênio excluído: " + evento.getNome() + 
			" (ID: " + evento.getConvenioId() + 
			", Código: " + evento.getCodigoIdentificacao() + 
			", Status antes da exclusão: " + evento.getStatusAntesExclusao() + 
			", Responsável: " + evento.getResponsavelId() + ")");
	}

	/**
	 * Método auxiliar para registrar eventos de auditoria.
	 * Em uma implementação real, isso poderia enviar para um sistema de logs,
	 * banco de dados de auditoria, ou serviço externo.
	 */
	private void registrarAuditoria(String tipoAcao, String detalhes) {
		// TODO: Implementar integração com sistema de auditoria real
		// Por exemplo: enviar para banco de dados, sistema de logs, ou serviço externo
		System.out.println("[AUDITORIA] " + tipoAcao + " - " + detalhes);
	}
}

