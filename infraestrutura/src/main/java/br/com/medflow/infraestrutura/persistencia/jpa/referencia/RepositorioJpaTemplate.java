package br.com.medflow.infraestrutura.persistencia.jpa.referencia;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;

/**
 * Template Method para operações de repositório JPA.
 * Define o esqueleto do algoritmo de busca e conversão.
 */
public abstract class RepositorioJpaTemplate<JPA, DOMINIO> {
    
    protected final ModelMapper mapper;
    protected final Class<DOMINIO> dominioClass;
    
    public RepositorioJpaTemplate(ModelMapper mapper, Class<DOMINIO> dominioClass) {
        this.mapper = mapper;
        this.dominioClass = dominioClass;
    }
    
    /**
     * TEMPLATE METHOD - Define o algoritmo de busca e conversão.
     * Passos:
     * 1. Buscar entidade JPA (método abstrato - implementado pelas subclasses)
     * 2. Converter para domínio (já implementado aqui)
     * 3. Retornar resultado
     */
    public final Optional<DOMINIO> buscarPorId(Integer id) {
        // Passo 1: Buscar (delegado para subclasse)
        Optional<JPA> jpaOptional = buscarEntidadeJpa(id);
        
        // Passo 2: Converter (implementado no template)
        return jpaOptional.map(this::converterParaDominio);
    }
    
    /**
     * TEMPLATE METHOD - Buscar todos e converter.
     */
    public final List<DOMINIO> buscarTodos() {
        // Passo 1: Buscar todos (delegado para subclasse)
        List<JPA> jpaList = buscarTodasEntidades();
        
        // Passo 2: Converter todos (implementado no template)
        return jpaList.stream()
                .map(this::converterParaDominio)
                .collect(Collectors.toList());
    }
    
    /**
     * Método de conversão comum (parte fixa do algoritmo).
     */
    protected final DOMINIO converterParaDominio(JPA jpa) {
        try {
            // tentativa normal
            return mapper.map(jpa, dominioClass);
        } catch (org.modelmapper.MappingException ex) {
            // fallback: instanciar manualmente e mapear para a instância (evita ModelMapper tentar criar a instância)
            try {
                var ctor = dominioClass.getDeclaredConstructor();
                ctor.setAccessible(true);
                DOMINIO instancia = ctor.newInstance();
                mapper.map(jpa, instancia);
                return instancia;
            } catch (Exception e) {
                throw ex; // reapresenta a exception original para manter stacktrace útil
            }
        }
    }
    
    // ========== MÉTODOS ABSTRATOS (implementados pelas subclasses) ==========
    
    /**
     * Operação primitiva: buscar entidade JPA por ID.
     * Cada subclasse implementa de acordo com seu repositório.
     */
    protected abstract Optional<JPA> buscarEntidadeJpa(Integer id);
    
    /**
     * Operação primitiva: buscar todas as entidades JPA.
     * Cada subclasse implementa de acordo com seu repositório.
     */
    protected abstract List<JPA> buscarTodasEntidades();
}