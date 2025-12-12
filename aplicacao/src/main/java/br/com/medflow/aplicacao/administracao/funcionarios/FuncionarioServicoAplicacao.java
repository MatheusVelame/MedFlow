package br.com.medflow.aplicacao.administracao.funcionarios;

import static org.apache.commons.lang3.Validate.notNull;

import java.util.List;
import br.com.medflow.dominio.administracao.funcionarios.StatusFuncionario;

// Serviço de Aplicação para os funcionários (Queries)
public class FuncionarioServicoAplicacao {

    private final FuncionarioRepositorioAplicacao repositorio;

    public FuncionarioServicoAplicacao(FuncionarioRepositorioAplicacao repositorio) {
        // Validação idêntica ao MedicamentoServicoAplicacao
        notNull(repositorio, "O repositório de funcionário não pode ser nulo"); 

        this.repositorio = repositorio;
    }

    /**
     * Lista todos os funcionários resumidos, delegando ao repositório de aplicação.
     * @return Lista de FuncionarioResumo.
     */
    public List<FuncionarioResumo> pesquisarResumos() {
        return repositorio.pesquisarResumos();
    }

    /**
     * Obtém os detalhes de um funcionário, lançando exceção se não for encontrado.
     * @param id O ID do funcionário.
     * @return FuncionarioDetalhes.
     */
    public FuncionarioDetalhes obterDetalhes(Integer id) {
        return repositorio.obterDetalhesPorId(id)
            .orElseThrow(() -> new RuntimeException("Funcionário não encontrado: " + id));
    }

    /**
     * Lista funcionários por status, delegando ao repositório de aplicação.
     * @param status O status do funcionário.
     * @return Lista de FuncionarioResumo.
     */
    public List<FuncionarioResumo> pesquisarPorStatus(StatusFuncionario status) {
        notNull(status, "O status não pode ser nulo para a pesquisa.");
        return repositorio.pesquisarPorStatus(status);
    }

    /**
     * Lista funcionários por função, delegando ao repositório de aplicação.
     * @param funcao A função do funcionário.
     * @return Lista de FuncionarioResumo.
     */
    public List<FuncionarioResumo> pesquisarPorFuncao(String funcao) {
        notNull(funcao, "A função não pode ser nula para a pesquisa.");
        return repositorio.pesquisarPorFuncao(funcao);
    }
}
