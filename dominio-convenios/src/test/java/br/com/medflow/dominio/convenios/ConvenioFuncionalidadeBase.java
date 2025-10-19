package br.com.medflow.dominio.convenios;

import static org.apache.commons.lang3.Validate.notNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import br.com.medflow.dominio.evento.EventoBarramento;
import br.com.medflow.dominio.evento.EventoObservador;

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
			permissoes.put("Administrador", List.of("cadastrar", "alterar", "excluir"));
			permissoes.put("Administrador Sênior", List.of("cadastrar", "alterar", "excluir"));
			permissoes.put("Médico", List.of("alterar")); 
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
			return usuariosId.computeIfAbsent(nome, k -> new UsuarioResponsavelId(usuariosId.size() + 1));
		}
		
		protected boolean temPermissao(String perfil, String acao) {
			return permissoes.getOrDefault(perfil, List.of()).contains(acao);
		}
		
		@Override
		public <E> void adicionar(EventoObservador<E> observador) { 
			throw new UnsupportedOperationException();
		}
		

		@Override
		public <E> void postar(E evento) {
			notNull(evento, "O evento não pode ser nulo");
			eventos.add(evento);
		}
}
