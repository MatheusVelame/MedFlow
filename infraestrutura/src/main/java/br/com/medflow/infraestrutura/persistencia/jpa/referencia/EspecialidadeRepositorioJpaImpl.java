package br.com.medflow.infraestrutura.persistencia.jpa.referencia;

import static org.apache.commons.lang3.Validate.notNull;

import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import br.com.medflow.dominio.referencia.especialidades.Especialidade;
import br.com.medflow.dominio.referencia.especialidades.EspecialidadeRepositorio;

@Component
public class EspecialidadeRepositorioJpaImpl 
        extends RepositorioJpaTemplate<EspecialidadeJpa, Especialidade> 
        implements EspecialidadeRepositorio {

    private final EspecialidadeJpaRepository jpaRepository;

    public EspecialidadeRepositorioJpaImpl(
            EspecialidadeJpaRepository jpaRepository, 
            ModelMapper mapper) {
        super(mapper, Especialidade.class);
        this.jpaRepository = jpaRepository;
    }

    // ========== IMPLEMENTAÇÃO DO TEMPLATE METHOD ==========

    @Override
    protected Optional<EspecialidadeJpa> buscarEntidadeJpa(Integer id) {
        return jpaRepository.findById(id);
    }

    @Override
    protected List<EspecialidadeJpa> buscarTodasEntidades() {
        return jpaRepository.findAll();
    }

    // ========== IMPLEMENTAÇÃO DO REPOSITÓRIO DE DOMÍNIO ==========

    @Override
    public void salvar(Especialidade especialidade) {
        notNull(especialidade, "A especialidade não pode ser nula");
        
        // Tenta buscar a entidade JPA existente pelo nome (Chave de Negócio)
        // Nota: Como o domínio Especialidade não possui ID explícito, usamos o nome.
        // Em caso de alteração de nome, o serviço deve garantir a consistência ou o domínio deve evoluir para ter ID.
        EspecialidadeJpa jpa = jpaRepository.findByNome(especialidade.getNome())
                .orElse(null);

        if (jpa == null) {
            // Nova especialidade
            jpa = mapper.map(especialidade, EspecialidadeJpa.class);
        } else {
            // Atualização de existente
            jpa.setDescricao(especialidade.getDescricao());
            jpa.setStatus(especialidade.getStatus());
            jpa.setPossuiVinculoHistorico(especialidade.isPossuiVinculoHistorico());
            // Nome não precisa atualizar pois é a chave de busca, a menos que tenha mudado (caso complexo sem ID)
        }

        jpaRepository.save(jpa);
    }

    @Override
    public Optional<Especialidade> buscarPorNome(String nome) {
        notNull(nome, "O nome não pode ser nulo");
        return jpaRepository.findByNome(nome)
                .map(jpa -> mapper.map(jpa, Especialidade.class));
    }

    @Override
    public boolean existePorNome(String nome) {
        notNull(nome, "O nome não pode ser nulo");
        return jpaRepository.existsByNome(nome);
    }

    @Override
    public void remover(Especialidade especialidade) {
        notNull(especialidade, "A especialidade não pode ser nula");
        
        jpaRepository.findByNome(especialidade.getNome())
                .ifPresent(jpaRepository::delete);
    }
}