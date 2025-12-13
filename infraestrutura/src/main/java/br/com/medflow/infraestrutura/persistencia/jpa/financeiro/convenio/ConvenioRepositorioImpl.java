package br.com.medflow.infraestrutura.persistencia.jpa.financeiro.convenio;

import br.com.medflow.dominio.financeiro.convenios.Convenio;
import br.com.medflow.dominio.financeiro.convenios.ConvenioId;
import br.com.medflow.dominio.financeiro.convenios.ConvenioRepositorio;
import br.com.medflow.dominio.financeiro.convenios.StatusConvenio;
import br.com.medflow.dominio.financeiro.convenios.UsuarioResponsavelId;
import br.com.medflow.dominio.financeiro.convenios.AcaoHistorico;
import br.com.medflow.infraestrutura.persistencia.jpa.JpaMapeador;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// Implementa o contrato da Camada de Domínio (Commands)
@Component("convenioRepositorioImpl")
public class ConvenioRepositorioImpl implements ConvenioRepositorio {

	private final ConvenioJpaRepository jpaRepository;
	private final JpaMapeador mapeador;

	public ConvenioRepositorioImpl(ConvenioJpaRepository jpaRepository, JpaMapeador mapeador) {
		this.jpaRepository = jpaRepository;
		this.mapeador = mapeador;
	}
	
	// Helper method para mapear histórico (HistoricoEntrada é classe interna, não pode ser referenciada diretamente)
	private HistoricoConvenioJpa mapearHistorico(Object historicoEntrada) {
		return mapeador.map(historicoEntrada, HistoricoConvenioJpa.class);
	}

	@Override
	@Transactional
	public void salvar(Convenio convenio) {
		ConvenioId idConvenio = convenio.getId();
		
		// --- 1. Lógica para NOVOS OBJETOS (INSERT) ---
		if (idConvenio == null || idConvenio.getId() == 0) {
			ConvenioJpa novaJpa = mapeador.map(convenio, ConvenioJpa.class);
			
			// Mapeia o histórico manualmente (já que foi ignorado no mapeamento principal)
			if (convenio.getHistorico() != null && !convenio.getHistorico().isEmpty()) {
				@SuppressWarnings("unchecked")
				List<Object> historicoComoObject = (List<Object>) (List<?>) convenio.getHistorico();
				List<HistoricoConvenioJpa> historicoJpa = historicoComoObject.stream()
					.map(this::mapearHistorico)
					.collect(java.util.stream.Collectors.toList());
				novaJpa.setHistorico(historicoJpa);
			}
			
			jpaRepository.save(novaJpa);
			return;
		}
		
		// --- 2. Lógica para ATUALIZAÇÃO (PUT/PATCH) ---
		Integer idAtualizacao = idConvenio.getId();
		ConvenioJpa jpaExistente = jpaRepository.findById(idAtualizacao)
			.orElseThrow(() -> new RuntimeException("Convênio JPA não encontrado para atualização (ID: " + idAtualizacao + ")"));
		
		// Mapeia o objeto de Domínio ATUALIZADO para a Entidade JPA gerenciada
		mapeador.map(convenio, jpaExistente);
		
		// Mapeia o histórico manualmente (já que foi ignorado no mapeamento principal)
		// IMPORTANTE: Com orphanRemoval = true, não podemos substituir a referência da coleção.
		// Devemos modificar a coleção existente para evitar o erro "collection was no longer referenced"
		List<HistoricoConvenioJpa> historicoExistente = jpaExistente.getHistorico();
		historicoExistente.clear(); // Limpa a coleção existente
		
		if (convenio.getHistorico() != null && !convenio.getHistorico().isEmpty()) {
			@SuppressWarnings("unchecked")
			List<Object> historicoComoObject = (List<Object>) (List<?>) convenio.getHistorico();
			List<HistoricoConvenioJpa> historicoJpa = historicoComoObject.stream()
				.map(this::mapearHistorico)
				.collect(java.util.stream.Collectors.toList());
			historicoExistente.addAll(historicoJpa); // Adiciona os novos itens à coleção existente
		}
		
		jpaRepository.save(jpaExistente);
	}

	@Override
	public Convenio obter(ConvenioId id) {
		Optional<ConvenioJpa> jpaOptional = jpaRepository.findById(id.getId());

		ConvenioJpa jpa = jpaOptional
			.orElseThrow(() -> new RuntimeException("Convênio não encontrado: " + id.getId()));

		// Usa o método helper que garante o mapeamento correto do ID
		return mapearJpaParaDominio(jpa);
	}
	
	// Helper method para mapear ConvenioJpa para Convenio (com histórico e ID)
	private Convenio mapearJpaParaDominio(ConvenioJpa jpa) {
		Convenio convenio = mapeador.map(jpa, Convenio.class);
		
		// Mapeia o ID manualmente (Integer -> ConvenioId) usando reflexão
		ConvenioId convenioId = null;
		if (jpa.getId() != null && jpa.getId() > 0) {
			convenioId = new ConvenioId(jpa.getId());
			try {
				java.lang.reflect.Field idField = Convenio.class.getDeclaredField("id");
				idField.setAccessible(true);
				idField.set(convenio, convenioId);
			} catch (Exception e) {
				throw new RuntimeException("Erro ao setar id no Convenio", e);
			}
		}
		
		// Mapeia o histórico manualmente (já que foi ignorado no mapeamento principal)
		if (jpa.getHistorico() != null && !jpa.getHistorico().isEmpty()) {
			// Mapeia o histórico de JPA para Domínio
			@SuppressWarnings({"unchecked", "rawtypes"})
			List historicoMapeado = (List) jpa.getHistorico().stream()
				.map(this::mapearHistoricoParaDominio)
				.collect(java.util.stream.Collectors.toList());
			
			// Reconstrói o Convenio usando valores diretamente do JPA para garantir que tudo esteja mapeado
			return new Convenio(
				convenioId,
				jpa.getNome(),
				jpa.getCodigoIdentificacao(),
				jpa.getStatus(),  // Usa diretamente do JPA para garantir que não seja null
				historicoMapeado
			);
		}
		
		// Se não há histórico, garante que o status esteja correto
		if (convenio.getStatus() == null && jpa.getStatus() != null) {
			try {
				java.lang.reflect.Field statusField = Convenio.class.getDeclaredField("status");
				statusField.setAccessible(true);
				statusField.set(convenio, jpa.getStatus());
			} catch (Exception e) {
				throw new RuntimeException("Erro ao setar status no Convenio", e);
			}
		}
		
		return convenio;
	}
	
	// Helper method para mapear histórico de JPA para Domínio
	private Object mapearHistoricoParaDominio(HistoricoConvenioJpa historicoJpa) {
		try {
			// Obtém a classe HistoricoEntrada usando o nome completo
			// HistoricoEntrada está no mesmo pacote que Convenio (não é classe interna)
			Class<?> historicoEntradaClass = Class.forName("br.com.medflow.dominio.financeiro.convenios.HistoricoEntrada");
			
			// Cria UsuarioResponsavelId
			UsuarioResponsavelId responsavelId = new UsuarioResponsavelId(historicoJpa.getResponsavelId());
			
			// Cria HistoricoEntrada usando o construtor
			java.lang.reflect.Constructor<?> constructor = historicoEntradaClass.getDeclaredConstructor(
				AcaoHistorico.class,
				String.class,
				UsuarioResponsavelId.class,
				java.time.LocalDateTime.class
			);
			constructor.setAccessible(true);
			
			return constructor.newInstance(
				historicoJpa.getAcao(),
				historicoJpa.getDescricao(),
				responsavelId,
				historicoJpa.getDataHora()
			);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Classe HistoricoEntrada não encontrada no pacote br.com.medflow.dominio.financeiro.convenios", e);
		} catch (Exception e) {
			throw new RuntimeException("Erro ao mapear histórico de JPA para Domínio", e);
		}
	}

	@Override
	public Optional<Convenio> obterPorNome(String nome) {
		Optional<ConvenioJpa> jpaOptional = jpaRepository.findByNomeIgnoreCase(nome);

		if (jpaOptional.isEmpty()) {
			return Optional.empty();
		}

		return Optional.of(mapearJpaParaDominio(jpaOptional.get()));
	}

	@Override
	public Optional<Convenio> obterPorCodigoIdentificacao(String codigoIdentificacao) {
		Optional<ConvenioJpa> jpaOptional = jpaRepository.findByCodigoIdentificacaoIgnoreCase(codigoIdentificacao);

		if (jpaOptional.isEmpty()) {
			return Optional.empty();
		}

		return Optional.of(mapearJpaParaDominio(jpaOptional.get()));
	}

	@Override
	public List<Convenio> pesquisar() {
		List<ConvenioJpa> jpas = jpaRepository.findByStatusNot(StatusConvenio.ARQUIVADO);

		return jpas.stream()
			.map(this::mapearJpaParaDominio)
			.collect(Collectors.toList());
	}

	@Override
	public void remover(ConvenioId id) {
		jpaRepository.deleteById(id.getId());
	}
}


