package br.com.medflow.infraestrutura.persistencia.jpa.financeiro.convenio;

import br.com.medflow.dominio.financeiro.convenios.Convenio;
import br.com.medflow.dominio.financeiro.convenios.ConvenioId;
import br.com.medflow.dominio.financeiro.convenios.ConvenioRepositorio;
import br.com.medflow.dominio.financeiro.convenios.StatusConvenio;
import br.com.medflow.infraestrutura.persistencia.jpa.JpaMapeador;
import org.springframework.stereotype.Component;

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

	@Override
	public void salvar(Convenio convenio) {
		ConvenioJpa jpa = mapeador.map(convenio, ConvenioJpa.class);

		jpaRepository.save(jpa);
	}

	@Override
	public Convenio obter(ConvenioId id) {
		Optional<ConvenioJpa> jpaOptional = jpaRepository.findById(id.getId());

		ConvenioJpa jpa = jpaOptional
			.orElseThrow(() -> new RuntimeException("Convênio não encontrado: " + id.getId()));

		return mapeador.map(jpa, Convenio.class);
	}

	@Override
	public Optional<Convenio> obterPorNome(String nome) {
		Optional<ConvenioJpa> jpaOptional = jpaRepository.findByNomeIgnoreCase(nome);

		if (jpaOptional.isEmpty()) {
			return Optional.empty();
		}

		return Optional.of(mapeador.map(jpaOptional.get(), Convenio.class));
	}

	@Override
	public Optional<Convenio> obterPorCodigoIdentificacao(String codigoIdentificacao) {
		Optional<ConvenioJpa> jpaOptional = jpaRepository.findByCodigoIdentificacaoIgnoreCase(codigoIdentificacao);

		if (jpaOptional.isEmpty()) {
			return Optional.empty();
		}

		return Optional.of(mapeador.map(jpaOptional.get(), Convenio.class));
	}

	@Override
	public List<Convenio> pesquisar() {
		List<ConvenioJpa> jpas = jpaRepository.findByStatusNot(StatusConvenio.ARQUIVADO);

		return jpas.stream()
			.map(jpa -> mapeador.map(jpa, Convenio.class))
			.collect(Collectors.toList());
	}

	@Override
	public void remover(ConvenioId id) {
		jpaRepository.deleteById(id.getId());
	}
}

