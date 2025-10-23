package br.com.medflow.dominio.financeiro.convenios;

import static org.apache.commons.lang3.Validate.notNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import br.com.medflow.dominio.financeiro.convenios.Convenio;
import br.com.medflow.dominio.financeiro.convenios.ConvenioServico;
import br.com.medflow.dominio.financeiro.convenios.UsuarioResponsavelId;
import br.com.medflow.dominio.financeiro.evento.EventoBarramento;
import br.com.medflow.dominio.financeiro.evento.EventoObservador;

public class ConvenioFuncionalidadeBase implements EventoBarramento {
		protected ConvenioServico convenioServico;
		protected ConvenioRepositorioMemoria repositorio;
		
		private Map<String, UsuarioResponsavelId> usuariosId = new HashMap<>(); 
		private Map<String, List<String>> permissoes = new HashMap<>();
		
		protected List<Object> eventos;

		public ConvenioFuncionalidadeBase() {
			this.repositorio = new ConvenioRepositorioMemoria();
			this.convenioServico = new ConvenioServico(repositorio);
			this.eventos = new ArrayList<>();
			
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
		
		// ADICIONE ESTE MÉTODO:
		protected void limparUsuarios() {
		    this.usuariosId.clear();
		}
}
