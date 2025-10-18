package br.com.medflow.dominio.catalogo.medicamentos;

import static org.apache.commons.lang3.Validate.notNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import br.com.medflow.dominio.evento.EventoBarramento;
import br.com.medflow.dominio.evento.EventoObservador;
	

/**
 * Classe base para os testes BDD de Farmácia. 
 * Responsável por configurar o ambiente (serviços in-memory) e simular o barramento de eventos.
 */
public class MedicamentoFuncionalidadeBase implements EventoBarramento {
	// Variáveis de domínio/ambiente
	protected MedicamentoServico medicamentoServico;
	protected MedicamentoRepositorioMemoria repositorio;
	
	// Mocks de Usuários e Permissões
	private Map<String, UsuarioResponsavelId> usuariosId = new HashMap<>(); 
	private Map<String, List<String>> permissoes = new HashMap<>(); 

	// Eventos capturados
	protected List<Object> eventos;

	public MedicamentoFuncionalidadeBase() {
		this.repositorio = new MedicamentoRepositorioMemoria();
		this.medicamentoServico = new MedicamentoServico(repositorio);
		this.eventos = new ArrayList<>();
		
		// Simulação do sistema de permissões
		permissoes.put("Administrador", List.of("cadastrar", "atualizar", "arquivar", "revisar"));
		permissoes.put("Administrador Sênior", List.of("cadastrar", "atualizar", "arquivar", "revisar", "excluir_permanente"));
		permissoes.put("Médico", List.of("atualizar")); 
		permissoes.put("Enfermeiro", List.of());
		permissoes.put("Recepção", List.of("consultar"));
	}
	
	protected Optional<Medicamento> obterMedicamento(String nome) {
		return repositorio.obterPorNome(nome);
	}

	protected UsuarioResponsavelId getUsuarioId(String nome) {
		// Simula a obtenção de um ID único para cada nome de usuário
		return usuariosId.computeIfAbsent(nome, k -> new UsuarioResponsavelId(usuariosId.size() + 1));
	}
	
	protected boolean temPermissao(String perfil, String acao) {
		// Verifica se o perfil configurado tem a permissão para a ação
		return permissoes.getOrDefault(perfil, List.of()).contains(acao);
	}
	
	// Implementação mock do EventoBarramento (apenas para teste)
	@Override
	// ASSINATURA CORRIGIDA: Inclui o tipo de interface 'EventoObservador'
	public <E> void adicionar(EventoObservador<E> observador) { 
		// Não necessário para este conjunto de cenários, mas mantém a arquitetura
		throw new UnsupportedOperationException();
	}

	@Override
	public <E> void postar(E evento) {
		notNull(evento, "O evento não pode ser nulo");
		eventos.add(evento);
	}
}