package br.com.medflow.aplicacao.administracao.funcionarios;

import java.util.List;
import java.util.Optional;
import br.com.medflow.dominio.administracao.funcionarios.StatusFuncionario;

// Repositório de leitura (Queries) otimizado para a Camada de Aplicação
// (Retorna DTOs de leitura, não entidades de domínio)
public interface FuncionarioRepositorioAplicacao {

    /**
     * Pesquisa todos os resumos de funcionários.
     * @return Uma lista de resumos de funcionário.
     */
    List<FuncionarioResumo> pesquisarResumos();

    /**
     * Obtém os detalhes completos de um funcionário pelo seu ID.
     * @param id O identificador do funcionário.
     * @return Um Optional contendo os detalhes, se encontrado.
     */
    Optional<FuncionarioDetalhes> obterDetalhesPorId(Integer id);

    /**
     * Pesquisa funcionários resumidos filtrando por status.
     * @param status O status do funcionário.
     * @return Uma lista de resumos de funcionário com o status especificado.
     */
    List<FuncionarioResumo> pesquisarPorStatus(StatusFuncionario status);

    /**
     * Pesquisa funcionários resumidos filtrando por função.
     * @param funcao A função do funcionário.
     * @return Uma lista de resumos de funcionário com a função especificada.
     */
    List<FuncionarioResumo> pesquisarPorFuncao(String funcao);
}
