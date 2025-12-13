// Localização: infraestrutura/src/main/java/br/com/medflow/infraestrutura/persistencia/jpa/catalogo/MedicamentoRepositorioImpl.java

package br.com.medflow.infraestrutura.persistencia.jpa.catalogo;

import br.com.medflow.aplicacao.catalogo.medicamentos.MedicamentoDetalhes;
import br.com.medflow.aplicacao.catalogo.medicamentos.MedicamentoDetalhes.HistoricoDetalhes;
import br.com.medflow.aplicacao.catalogo.medicamentos.MedicamentoDetalhes.RevisaoPendenteDetalhes;
import br.com.medflow.aplicacao.catalogo.medicamentos.MedicamentoRepositorioAplicacao;
import br.com.medflow.aplicacao.catalogo.medicamentos.MedicamentoResumo;
import br.com.medflow.dominio.catalogo.medicamentos.*;

import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * ADAPTER: Implementa a porta de Escrita (Domain Repository) e a porta de Leitura (Application Repository).
 */
@Component
public class MedicamentoRepositorioImpl implements MedicamentoRepositorio, MedicamentoRepositorioAplicacao {

    private final MedicamentoJpaRepository jpaRepository;
    private final HistoricoEntradaJpaRepository historicoJpaRepository; 
    // Campo revisaoPendenteJpaRepository removido
    
    // Construtor ajustado (removido RevisaoPendenteJpaRepository)
    public MedicamentoRepositorioImpl(MedicamentoJpaRepository jpaRepository,
                                     HistoricoEntradaJpaRepository historicoJpaRepository) {
        this.jpaRepository = jpaRepository;
        this.historicoJpaRepository = historicoJpaRepository;
    }

    // =====================================================================
    // IMPLEMENTAÇÃO DO DOMAIN REPOSITORY (PORTA DE ESCRITA/CUD)
    // =====================================================================

    @Override
    public Optional<Medicamento> buscarPorId(MedicamentoId id) {
        return jpaRepository.findById(id.getId()) 
                .map(this::toDomain); 
    }
    
    @Override
    public Optional<Medicamento> obter(MedicamentoId id) { return buscarPorId(id); }
    @Override
    public List<Medicamento> pesquisar() { return jpaRepository.findAll().stream().map(this::toDomain).collect(Collectors.toList()); }
    @Override
    public List<Medicamento> pesquisarComFiltroArquivado() { return jpaRepository.findByStatus(StatusMedicamento.ARQUIVADO).stream().map(this::toDomain).collect(Collectors.toList()); }
    @Override
    public Optional<Medicamento> obterPorNome(String nome) { return Optional.empty(); }
    
    @Override
    public void salvar(Medicamento medicamento) {
        MedicamentoJpa jpa = toJpa(medicamento);
        jpaRepository.save(jpa);
    }
    
    // Mapeamento Domain <=> JPA
    
    private Medicamento toDomain(MedicamentoJpa jpa) {
        
        RevisaoPendente revisaoPendente = jpa.getRevisaoPendente() != null 
            ? toDomain(jpa.getRevisaoPendente()) : null;
        
        List<HistoricoEntrada> historico = historicoJpaRepository.findByMedicamentoId(jpa.getId()).stream()
            .map(this::toDomain)
            .collect(Collectors.toList());

        return new Medicamento(
            new MedicamentoId(jpa.getId()),
            jpa.getNome(),
            jpa.getUsoPrincipal(),
            jpa.getContraindicacoes(),
            jpa.getStatus(), 
            historico,
            revisaoPendente
        );
    }
    
    private HistoricoEntrada toDomain(HistoricoEntradaJpa jpa) {
        return new HistoricoEntrada(
            jpa.getAcao(),
            jpa.getDescricao(),
            new UsuarioResponsavelId(jpa.getResponsavelId()),
            jpa.getDataHora()
        );
    }
    
    private RevisaoPendente toDomain(RevisaoPendenteJpa jpa) {
        return new RevisaoPendente(
            jpa.getNovoValor(),
            new UsuarioResponsavelId(jpa.getSolicitanteId()),
            jpa.getStatus(),
            jpa.getRevisorId() != null ? new UsuarioResponsavelId(jpa.getRevisorId()) : null
        );
    }
    
    private MedicamentoJpa toJpa(Medicamento medicamento) {
        MedicamentoJpa jpa = new MedicamentoJpa();
        
        if (medicamento.getId() != null) {
            jpa.setId(medicamento.getId().getId()); 
        }
        jpa.setNome(medicamento.getNome());
        jpa.setUsoPrincipal(medicamento.getUsoPrincipal());
        jpa.setContraindicacoes(medicamento.getContraindicacoes());
        jpa.setStatus(medicamento.getStatus()); 
        
        // Mapeia Revisão Pendente
        medicamento.getRevisaoPendente().ifPresent(rp -> jpa.setRevisaoPendente(toJpa(rp)));
        
        return jpa;
    }
    
    private RevisaoPendenteJpa toJpa(RevisaoPendente revisaoPendente) {
        RevisaoPendenteJpa jpa = new RevisaoPendenteJpa();
        jpa.setNovoValor(revisaoPendente.getNovoValor());
        jpa.setStatus(revisaoPendente.getStatus());
        jpa.setSolicitanteId(revisaoPendente.getSolicitanteId().getId()); 
        revisaoPendente.getRevisorId().ifPresent(id -> jpa.setRevisorId(id.getId()));
        return jpa;
    }

    // =====================================================================
    // IMPLEMENTAÇÃO DO APPLICATION REPOSITORY (PORTA DE LEITURA/QUERY)
    // =====================================================================

    @Override
    public List<MedicamentoResumo> pesquisarResumos() {
        return jpaRepository.findAll().stream()
                .map(this::toResumoDto) 
                .collect(Collectors.toList());
    }

    @Override
    public Optional<MedicamentoDetalhes> obterDetalhesPorId(Integer id) {
        return jpaRepository.findById(id)
                .map(this::toDetalhesDto); 
    }

    @Override
    public List<MedicamentoResumo> findByStatus(StatusMedicamento status) {
        return jpaRepository.findByStatus(status).stream()
                .map(this::toResumoDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<MedicamentoResumo> pesquisarMedicamentosComRevisaoPendente() {
        return jpaRepository.findByRevisaoPendenteStatus().stream()
                .map(this::toResumoDto)
                .collect(Collectors.toList());
    }
    
    // Mapeamento JPA -> DTO
    
    private MedicamentoResumo toResumoDto(MedicamentoJpa jpa) {
        boolean temRevisaoPendente = jpa.getRevisaoPendente() != null && jpa.getRevisaoPendente().getStatus() == StatusRevisao.PENDENTE;
        
        return new MedicamentoResumo(
            jpa.getId(), 
            jpa.getNome(), 
            jpa.getUsoPrincipal(), 
            jpa.getContraindicacoes(), // <-- ARGUMENTO DE CONTRAINDICAÇÕES ADICIONADO AQUI
            jpa.getStatus(), 
            temRevisaoPendente
        );
    }

    private MedicamentoDetalhes toDetalhesDto(MedicamentoJpa jpa) {
        // Busca e mapeia o histórico completo
        List<HistoricoDetalhes> historico = historicoJpaRepository.findByMedicamentoId(jpa.getId()).stream()
            .map(this::toHistoricoDetalhesDto)
            .collect(Collectors.toList());
            
        RevisaoPendenteDetalhes revisaoDetalhes = jpa.getRevisaoPendente() != null 
            ? toRevisaoDetalhesDto(jpa.getRevisaoPendente()) : null;
            
        return new MedicamentoDetalhes(
            jpa.getId(), 
            jpa.getNome(), 
            jpa.getUsoPrincipal(),
            jpa.getContraindicacoes(), 
            jpa.getStatus(),
            revisaoDetalhes,
            historico
        );
    }
    
    private HistoricoDetalhes toHistoricoDetalhesDto(HistoricoEntradaJpa jpa) {
        return new HistoricoDetalhes(
            jpa.getAcao().name(),
            jpa.getDescricao(),
            jpa.getResponsavelId(),
            jpa.getDataHora()
        );
    }
    
    private RevisaoPendenteDetalhes toRevisaoDetalhesDto(RevisaoPendenteJpa jpa) {
        return new RevisaoPendenteDetalhes(
            jpa.getNovoValor(),
            jpa.getStatus(),
            jpa.getSolicitanteId(),
            jpa.getRevisorId()
        );
    }
}