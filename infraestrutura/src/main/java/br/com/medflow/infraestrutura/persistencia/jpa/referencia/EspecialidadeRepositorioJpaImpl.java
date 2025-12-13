package br.com.medflow.infraestrutura.persistencia.jpa.referencia;

import static org.apache.commons.lang3.Validate.notNull;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import br.com.medflow.dominio.referencia.especialidades.Especialidade;
import br.com.medflow.dominio.referencia.especialidades.EspecialidadeRepositorio;
import br.com.medflow.dominio.referencia.especialidades.RegraNegocioException;

@Component
public class EspecialidadeRepositorioJpaImpl 
        implements EspecialidadeRepositorio {

    private static final Logger LOGGER = Logger.getLogger(EspecialidadeRepositorioJpaImpl.class.getName());

    private final EspecialidadeJpaRepository jpaRepository;
    private final ModelMapper mapper;

    public EspecialidadeRepositorioJpaImpl(
            EspecialidadeJpaRepository jpaRepository, 
            @Qualifier("jpaMapeador") ModelMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    // ========== IMPLEMENTAÇÃO DO REPOSITÓRIO DE DOMÍNIO ==========

    @Override
    public void salvar(Especialidade especialidade) {
        notNull(especialidade, "A especialidade não pode ser nula");

        // Normalizar nome para evitar duplicidades por espaços/case
        String nomeNormalizado = especialidade.getNome() != null ? especialidade.getNome().trim() : null;
        especialidade.setNome(nomeNormalizado);

        // Se o domínio já tem um id, tentamos atualizar por id para evitar inserts duplicados
        if (especialidade.getId() != null) {
            Optional<EspecialidadeJpa> existById = jpaRepository.findById(especialidade.getId());
            if (existById.isPresent()) {
                EspecialidadeJpa jpa = existById.get();
                jpa.setNome(especialidade.getNome());
                jpa.setDescricao(especialidade.getDescricao());
                jpa.setStatus(especialidade.getStatus());
                jpa.setPossuiVinculoHistorico(especialidade.isPossuiVinculoHistorico());
                try {
                    // force persistence and flush to capture DB exceptions here
                    LOGGER.info("Saving existing Especialidade JPA id=" + jpa.getId() + " nome='" + jpa.getNome() + "'");
                    jpaRepository.saveAndFlush(jpa);
                } catch (DataIntegrityViolationException e) {
                    throw new RegraNegocioException("Já existe uma especialidade com este nome", java.util.Map.of("nome", "Já existe uma especialidade com este nome"));
                }
                return;
            }
            // Se não existe por id, continuamos para busca por nome/insert
        }

        // Tenta buscar a entidade JPA existente pelo nome (case-insensitive)
        EspecialidadeJpa jpa = jpaRepository.findByNomeIgnoreCase(especialidade.getNome())
                .orElse(null);

        if (jpa == null) {
            // Nova especialidade
            jpa = mapper.map(especialidade, EspecialidadeJpa.class);

            try {
                // force flush to trigger constraint violations inside this method
                LOGGER.info("Inserting new Especialidade JPA nome='" + jpa.getNome() + "'");
                jpa = jpaRepository.saveAndFlush(jpa);
                LOGGER.info("Inserted Especialidade JPA assigned id=" + jpa.getId());
            } catch (DataIntegrityViolationException e) {
                // Provável violação de unique: normalizamos para fornecer error map amigável para camada superior
                throw new RegraNegocioException("Já existe uma especialidade com este nome", java.util.Map.of("nome", "Já existe uma especialidade com este nome"));
            }
            // Atualiza o id no domínio após persistência
            especialidade.setId(jpa.getId());
        } else {
            // Atualização de existente
            jpa.setDescricao(especialidade.getDescricao());
            jpa.setStatus(especialidade.getStatus());
            jpa.setPossuiVinculoHistorico(especialidade.isPossuiVinculoHistorico());
            jpa.setNome(especialidade.getNome());
            try {
                LOGGER.info("Saving (merge) Especialidade JPA id=" + jpa.getId() + " nome='" + jpa.getNome() + "'");
                jpaRepository.saveAndFlush(jpa);
            } catch (DataIntegrityViolationException e) {
                throw new RegraNegocioException("Já existe uma especialidade com este nome", java.util.Map.of("nome", "Já existe uma especialidade com este nome"));
            }
            // Garante que o domínio tenha o id da JPA (caso não tenha)
            especialidade.setId(jpa.getId());
        }
    }

    @Override
    public Optional<Especialidade> buscarPorNome(String nome) {
        notNull(nome, "O nome não pode ser nulo");
        String n = nome.trim();
        return jpaRepository.findByNomeIgnoreCase(n)
                .map(jpa -> mapper.map(jpa, Especialidade.class));
    }

    @Override
    public boolean existePorNome(String nome) {
        notNull(nome, "O nome não pode ser nulo");
        String n = nome.trim();
        return jpaRepository.existsByNomeIgnoreCase(n);
    }

    @Override
    public void remover(Especialidade especialidade) {
        notNull(especialidade, "A especialidade não pode ser nula");
        String n = especialidade.getNome() == null ? null : especialidade.getNome().trim();
        if (n != null) {
            jpaRepository.findByNomeIgnoreCase(n)
                    .ifPresent(jpaRepository::delete);
        }
    }

    // Implementação direta (substitui os métodos do template que estavam causando resolução não encontrada)
    @Override
    public Optional<Especialidade> buscarPorId(Integer id) {
        notNull(id, "O id não pode ser nulo");
        return jpaRepository.findById(id)
                .map(jpa -> mapper.map(jpa, Especialidade.class));
    }

    @Override
    public List<Especialidade> buscarTodos() {
        List<EspecialidadeJpa> todas = jpaRepository.findAll();
        return todas.stream()
                .map(jpa -> mapper.map(jpa, Especialidade.class))
                .collect(Collectors.toList());
    }

    // NOTE: Mantive o restante do comportamento original; não alterei a lógica de persistência.
}