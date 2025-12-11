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
		if (convenio.getHistorico() != null && !convenio.getHistorico().isEmpty()) {
			@SuppressWarnings("unchecked")
			List<Object> historicoComoObject = (List<Object>) (List<?>) convenio.getHistorico();
			List<HistoricoConvenioJpa> historicoJpa = historicoComoObject.stream()
				.map(this::mapearHistorico)
				.collect(java.util.stream.Collectors.toList());
			jpaExistente.setHistorico(historicoJpa);
		}
		
		jpaRepository.save(jpaExistente);
	}

	@Override
	public Convenio obter(ConvenioId id) {
		Optional<ConvenioJpa> jpaOptional = jpaRepository.findById(id.getId());

		ConvenioJpa jpa = jpaOptional
			.orElseThrow(() -> new RuntimeException("Convênio não encontrado: " + id.getId()));

		Convenio convenio = mapeador.map(jpa, Convenio.class);
		
		// Mapeia o histórico manualmente (já que foi ignorado no mapeamento principal)
		if (jpa.getHistorico() != null && !jpa.getHistorico().isEmpty()) {
			// Mapeia o histórico de JPA para Domínio
			@SuppressWarnings({"unchecked", "rawtypes"})
			List historicoMapeado = (List) jpa.getHistorico().stream()
				.map(this::mapearHistoricoParaDominio)
				.collect(java.util.stream.Collectors.toList());
			
			// Reconstrói o Convenio com o histórico usando o construtor que aceita histórico
			// O construtor aceita List<HistoricoEntrada>, então fazemos o cast
			return new Convenio(
				convenio.getId(),
				convenio.getNome(),
				convenio.getCodigoIdentificacao(),
				convenio.getStatus(),
				historicoMapeado
			);
		}
		
		return convenio;
	}
	
	// Helper method para mapear ConvenioJpa para Convenio (com histórico e ID)
	private Convenio mapearJpaParaDominio(ConvenioJpa jpa) {
		Convenio convenio = mapeador.map(jpa, Convenio.class);
		
		// Mapeia o ID manualmente (Integer -> ConvenioId) usando reflexão
		if (jpa.getId() != null && jpa.getId() > 0) {
			try {
				ConvenioId convenioId = new ConvenioId(jpa.getId());
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
			
			// Reconstrói o Convenio com o histórico usando o construtor que aceita histórico
			return new Convenio(
				convenio.getId(),  // O ID já foi mapeado acima
				convenio.getNome(),
				convenio.getCodigoIdentificacao(),
				convenio.getStatus(),
				historicoMapeado
			);
		}
		
		return convenio;
	}
	
	// Helper method para mapear histórico de JPA para Domínio
	private Object mapearHistoricoParaDominio(HistoricoConvenioJpa historicoJpa) {
		try {
			// Obtém a classe interna HistoricoEntrada
			Class<?> historicoEntradaClass = null;
			Class<?>[] classesInternas = Convenio.class.getDeclaredClasses();
			for (Class<?> classeInterna : classesInternas) {
				if (classeInterna.getSimpleName().equals("HistoricoEntrada")) {
					historicoEntradaClass = classeInterna;
					break;
				}
			}
			
			if (historicoEntradaClass == null) {
				throw new RuntimeException("Classe interna HistoricoEntrada não encontrada");
			}
			
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


