package br.com.medflow.dominio.convenios;

import static org.apache.commons.lang3.Validate.notNull;

public class ConvenioFuncionalidadeBase implements EventoBarramento {
	// Variáveis de domínio/ambiente
		protected ConvenioServico convenioServico;
		protected ConvenioRepositorioMemoria repositorio;
		
		// Mocks de Usuários e Permissões
		private Map<String, UsuarioResponsavelId> usuariosId = new HashMap<>(); 
		private Map<String, List<String>> permissoes = new HashMap<>();
		
		// Eventos capturados
		protected List<Object> eventos;

		public ConvenioFuncionalidadeBase() {
			this.repositorio = new ConvenioRepositorioMemoria();
			this.convenioServico = new ConvenioServico(repositorio);
			this.eventos = new ArrayList<>();
			
			// Simulação do sistema de permissões
			permissoes.put("Administrador", List.of("cadastrar", "atualizar", "arquivar", "revisar"));
			permissoes.put("Administrador Sênior", List.of("cadastrar", "atualizar", "arquivar", "revisar", "excluir_permanente"));
			permissoes.put("Médico", List.of("atualizar")); 
			permissoes.put("Enfermeiro", List.of());
			permissoes.put("Recepção", List.of("consultar"));
		}
		
		protected Optional<Convenio> obterConvenioNome(String nome) {
			return repositorio.obterPorNome(nome);
		}
		
		protected Optional<Convenio> obterConvenioCodigo(String codigoIdentificacao) {
			return repositorio.obterPorCodigoIdentificacao(codigoIdentificacao);
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
