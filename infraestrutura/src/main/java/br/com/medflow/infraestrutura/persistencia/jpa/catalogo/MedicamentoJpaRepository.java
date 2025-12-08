package br.com.medflow.infraestrutura.persistencia.jpa.catalogo;

import br.com.medflow.aplicacao.catalogo.medicamentos.MedicamentoDetalhes;
import br.com.medflow.aplicacao.catalogo.medicamentos.MedicamentoRepositorioAplicacao;
import br.com.medflow.aplicacao.catalogo.medicamentos.MedicamentoResumo;
import br.com.medflow.dominio.catalogo.medicamentos.StatusMedicamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MedicamentoJpaRepository 
    extends JpaRepository<MedicamentoJpa, Integer>, 
            MedicamentoRepositorioAplicacao {

    // QUERY para a Camada de Aplicação (Queries Service)
    @Override
    @Query("SELECT m FROM MedicamentoJpa m WHERE m.status <> 'ARQUIVADO'")
    List<MedicamentoResumo> pesquisarResumos(); 

    // QUERY para a Camada de Aplicação (Detalhes)
    @Override
    @Query("SELECT m FROM MedicamentoJpa m LEFT JOIN FETCH m.historico h LEFT JOIN FETCH m.revisaoPendente r WHERE m.id = :id")
    Optional<MedicamentoDetalhes> obterDetalhesPorId(Integer id);
    
    // QUERY para a Camada de Aplicação (Filtro de revisão)
    @Override
    @Query("SELECT m FROM MedicamentoJpa m JOIN m.revisaoPendente rp WHERE rp.status = 'PENDENTE'")
    List<MedicamentoResumo> pesquisarMedicamentosComRevisaoPendente();
    
    // Suporte para o método 'pesquisar()' do Domínio
    List<MedicamentoJpa> findByStatusNot(StatusMedicamento status);
    
    // Suporte para o método 'obterPorNome(String)' do Domínio
    Optional<MedicamentoJpa> findByNomeIgnoreCase(String nome);
    
    // Suporte para buscar pelo ID
    Optional<MedicamentoJpa> findById(Integer id);
}