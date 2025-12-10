package br.com.medflow.infraestrutura.persistencia.jpa.catalogo;

import br.com.medflow.dominio.catalogo.medicamentos.Medicamento;
import br.com.medflow.dominio.catalogo.medicamentos.MedicamentoId;
import br.com.medflow.dominio.catalogo.medicamentos.MedicamentoRepositorio;
import br.com.medflow.dominio.catalogo.medicamentos.StatusMedicamento;
import br.com.medflow.infraestrutura.persistencia.jpa.JpaMapeador;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional // Garante que a operação de persistência é atômica
	public void salvar(Medicamento medicamento) { 
        
        MedicamentoId idMedicamento = medicamento.getId();
        
        // --- 1. Lógica para NOVOS OBJETOS (INSERT) ---
        // Se não tem ID, é um POST. O Hibernate fará o INSERT.
        if (idMedicamento == null || idMedicamento.getId() == 0) {
             MedicamentoJpa novaJpa = mapeador.map(medicamento, MedicamentoJpa.class);
             
             // Configura o bidirecional para as novas entradas de histórico
             if (novaJpa.getHistorico() != null) {
                novaJpa.getHistorico().forEach(h -> h.setMedicamento(novaJpa));
             }
             jpaRepository.save(novaJpa);
             return; // Finaliza o fluxo de INSERT
        }
        
        // --- 2. Lógica para ATUALIZAÇÃO (PUT/PATCH) ---
        
        // Carrega a Entidade JPA existente (gerenciada pelo Persistence Context)
        Integer idAtualizacao = idMedicamento.getId();
        MedicamentoJpa jpaExistente = jpaRepository.findById(idAtualizacao)
            .orElseThrow(() -> new RuntimeException("Medicamento JPA não encontrado para atualização (ID: " + idAtualizacao + ")"));
        
        // Mapeia o objeto de Domínio ATUALIZADO para a Entidade JPA gerenciada.
        // O ModelMapper copia NOME, USO_PRINCIPAL, STATUS, etc., e a nova lista de HISTORICO.
        // O ID é ignorado pelo mapeador, pois ele já está definido em jpaExistente (gerenciada).
        mapeador.map(medicamento, jpaExistente); 

        // 3. Garante que o vínculo bidirecional seja estabelecido para o novo histórico.
        if (jpaExistente.getHistorico() != null) {
            jpaExistente.getHistorico().forEach(h -> {
                // Essencial para que o Hibernate preencha a FK (medicamento_id) na tabela de histórico.
                h.setMedicamento(jpaExistente); 
            });
        }
        
		// O save() aqui faz o UPDATE/MERGE porque a Entidade jpaExistente é gerenciada e tem ID.
		jpaRepository.save(jpaExistente);
	}

	@Override
	public Medicamento obter(MedicamentoId id) {
		// id.getId() retorna o primitivo int
		Optional<MedicamentoJpa> jpaOptional = jpaRepository.findById(id.getId());	
        
        MedicamentoJpa jpa = jpaOptional
            .orElseThrow(() -> new RuntimeException("Medicamento não encontrado: " + id.getId()));

		// Mapeamento reverso (JPA -> Domínio)
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
        
		// Mapeamento reverso (JPA -> Domínio)
		return Optional.of(mapeador.map(jpaOptional.get(), Medicamento.class));
	}
}