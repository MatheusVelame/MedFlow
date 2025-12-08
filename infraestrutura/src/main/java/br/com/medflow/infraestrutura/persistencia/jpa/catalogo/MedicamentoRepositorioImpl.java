package br.com.medflow.infraestrutura.persistencia.jpa.catalogo;

import br.com.medflow.dominio.catalogo.medicamentos.Medicamento;
import br.com.medflow.dominio.catalogo.medicamentos.MedicamentoId;
import br.com.medflow.dominio.catalogo.medicamentos.MedicamentoRepositorio;
import br.com.medflow.dominio.catalogo.medicamentos.StatusMedicamento;
import br.com.medflow.infraestrutura.persistencia.jpa.JpaMapeador;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// Implementa o contrato da Camada de Domínio (Commands)
@Component("medicamentoRepositorioImpl")
public class MedicamentoRepositorioImpl implements MedicamentoRepositorio { 

	private final MedicamentoJpaRepository jpaRepository;
	private final JpaMapeador mapeador;

	public MedicamentoRepositorioImpl(MedicamentoJpaRepository jpaRepository, JpaMapeador mapeador) {
		this.jpaRepository = jpaRepository;
		this.mapeador = mapeador;
	}

	@Override
	public void salvar(Medicamento medicamento) { 
		MedicamentoJpa jpa = mapeador.map(medicamento, MedicamentoJpa.class);

		jpaRepository.save(jpa);
	}

	@Override
	public Medicamento obter(MedicamentoId id) {
		Optional<MedicamentoJpa> jpaOptional = jpaRepository.findById(id.getId()); 
        
        MedicamentoJpa jpa = jpaOptional
            .orElseThrow(() -> new RuntimeException("Medicamento não encontrado: " + id.getId()));

		return mapeador.map(jpa, Medicamento.class);
	}
    
	@Override
    public List<Medicamento> pesquisar() { 
        List<MedicamentoJpa> jpas = jpaRepository.findByStatusNot(StatusMedicamento.ARQUIVADO);
            
        return jpas.stream()
            .map(jpa -> mapeador.map(jpa, Medicamento.class))
            .collect(Collectors.toList());
    }

	@Override
    public List<Medicamento> pesquisarComFiltroArquivado() { 
        List<MedicamentoJpa> jpas = jpaRepository.findAll();
        
        return jpas.stream()
            .map(jpa -> mapeador.map(jpa, Medicamento.class))
            .collect(Collectors.toList());
    }

	@Override
	public Optional<Medicamento> obterPorNome(String nome) { 

		Optional<MedicamentoJpa> jpaOptional = jpaRepository.findByNomeIgnoreCase(nome);

        if (jpaOptional.isEmpty()) {
            return Optional.empty();
        }
        
		return Optional.of(mapeador.map(jpaOptional.get(), Medicamento.class));
	}
}