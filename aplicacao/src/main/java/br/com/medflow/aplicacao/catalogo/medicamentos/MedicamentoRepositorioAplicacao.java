// Localização: aplicacao/src/main/java/br/com/medflow/aplicacao/catalogo/medicamentos/MedicamentoRepositorioAplicacao.java

package br.com.medflow.aplicacao.catalogo.medicamentos;

import br.com.medflow.dominio.catalogo.medicamentos.StatusMedicamento;
import java.util.List;
import java.util.Optional;

/**
 * Interface (Porta) para acesso de dados de LEITURA (Queries) na camada de Aplicação.
 * Será implementada pela camada de Infraestrutura (o Adapter).
 */
public interface MedicamentoRepositorioAplicacao {

    /**
     * Pesquisa e retorna um resumo de todos os medicamentos ativos.
     * @return Lista de MedicamentoResumo.
     */
    List<MedicamentoResumo> pesquisarResumos();

    /**
     * Obtém os detalhes completos de um medicamento específico.
     * @param id O ID numérico do medicamento.
     * @return Optional contendo MedicamentoDetalhes ou vazio se não encontrado.
     */
    Optional<MedicamentoDetalhes> obterDetalhesPorId(Integer id);

    /**
     * Pesquisa medicamentos filtrando por um status específico.
     * @param status O status pelo qual filtrar.
     * @return Lista de MedicamentoResumo.
     */
    List<MedicamentoResumo> findByStatus(StatusMedicamento status);

    /**
     * Pesquisa e retorna um resumo de medicamentos que possuem revisão de contraindicação pendente.
     * @return Lista de MedicamentoResumo.
     */
    List<MedicamentoResumo> pesquisarMedicamentosComRevisaoPendente();
}