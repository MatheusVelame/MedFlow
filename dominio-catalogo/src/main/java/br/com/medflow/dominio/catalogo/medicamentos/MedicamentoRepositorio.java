// Localização: dominio-catalogo/src/main/java/br/com/medflow/dominio/catalogo/medicamentos/MedicamentoRepositorio.java

package br.com.medflow.dominio.catalogo.medicamentos;

import java.util.List;
import java.util.Optional;

public interface MedicamentoRepositorio {
    
    // Métodos de persistência essenciais (DDD)
    Optional<Medicamento> buscarPorId(MedicamentoId id); 
    void salvar(Medicamento medicamento);
    
    // Métodos que o compilador estava exigindo (do código original)
    Optional<Medicamento> obter(MedicamentoId id); 
    List<Medicamento> pesquisar();
    List<Medicamento> pesquisarComFiltroArquivado();
    Optional<Medicamento> obterPorNome(String nome);
}