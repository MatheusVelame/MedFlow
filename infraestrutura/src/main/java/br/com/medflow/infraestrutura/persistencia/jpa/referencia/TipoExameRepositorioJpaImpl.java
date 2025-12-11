package br.com.medflow.infraestrutura.persistencia.jpa.referencia;

import static org.apache.commons.lang3.Validate.notNull;

import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import br.com.medflow.dominio.referencia.tiposExames.TipoExame;
import br.com.medflow.dominio.referencia.tiposExames.TipoExameId;
import br.com.medflow.dominio.referencia.tiposExames.TipoExameRepositorio;
import br.com.medflow.dominio.referencia.tiposExames.StatusTipoExame;

import org.springframework.beans.factory.annotation.Qualifier;

@Component
public class TipoExameRepositorioJpaImpl 
        extends RepositorioJpaTemplate<TipoExameJpa, TipoExame> 
        implements TipoExameRepositorio {
    
    private final TipoExameJpaRepository jpaRepository;
    
    public TipoExameRepositorioJpaImpl(
            TipoExameJpaRepository jpaRepository, 
            @Qualifier("tipoExameJpaMapeador") ModelMapper mapper) {
        super(mapper, TipoExame.class);
        this.jpaRepository = jpaRepository;
    }
    
    // ========== IMPLEMENTAÇÃO DOS MÉTODOS ABSTRATOS (Template Method) ==========
    
    @Override
    protected Optional<TipoExameJpa> buscarEntidadeJpa(Integer id) {
        return jpaRepository.findById(id);
    }
    
    @Override
    protected List<TipoExameJpa> buscarTodasEntidades() {
        return jpaRepository.findAll();
    }
    
    // ========== IMPLEMENTAÇÃO DA INTERFACE DO DOMÍNIO ==========
    
    @Override
    public void salvar(TipoExame tipoExame) {
        notNull(tipoExame, "O tipo de exame não pode ser nulo");
        
        TipoExameJpa jpa;
        
        if (tipoExame.getId() == null) {
            // Novo tipo de exame
            jpa = mapper.map(tipoExame, TipoExameJpa.class);
        } else {
            // Atualização - busca existente
            jpa = jpaRepository.findById(tipoExame.getId().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Tipo de exame não encontrado"));
            
            // Atualiza campos
            jpa.setCodigo(tipoExame.getCodigo());
            jpa.setDescricao(tipoExame.getDescricao());
            jpa.setEspecialidade(tipoExame.getEspecialidade());
            jpa.setValor(tipoExame.getValor());
            jpa.setStatus(tipoExame.getStatus());
        }
        
        jpaRepository.save(jpa);
    }
    
    @Override
    public TipoExame obter(TipoExameId id) {
        notNull(id, "O id não pode ser nulo");
        
        // USA O TEMPLATE METHOD! 🎯
        return buscarPorId(id.getId())
                .orElseThrow(() -> new IllegalArgumentException("Tipo de exame não encontrado"));
    }
    
    @Override
    public Optional<TipoExame> obterPorCodigo(String codigo) {
        return jpaRepository.findByCodigo(codigo)
                .map(jpa -> mapper.map(jpa, TipoExame.class));
    }
    
    @Override
    public List<TipoExame> pesquisar() {
        // Retorna todos EXCETO os INATIVOS
    	return jpaRepository.findByStatusNot(StatusTipoExame.INATIVO, org.springframework.data.domain.Sort.unsorted()).stream()
                .map(jpa -> mapper.map(jpa, TipoExame.class))
                .toList();
    }
    
    @Override
    public List<TipoExame> pesquisarComFiltroInativo() {
        // USA O TEMPLATE METHOD! 🎯
        return buscarTodos();
    }
    
    @Override
    public void excluir(TipoExameId id) {
        notNull(id, "O id não pode ser nulo");
        jpaRepository.deleteById(id.getId());
    }
}