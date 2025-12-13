package br.com.medflow.dominio.referencia.especialidades;

import java.util.List;
import java.util.Optional;

/**
 * Interface que define o contrato para operações de Especialidades.
 * O Padrão Proxy exige que tanto o RealSubject quanto o Proxy implementem esta interface.
 */
public interface IEspecialidadeServico {

    // MÉTODOS ORIGINAIS (COMANDOS DE DOMÍNIO)
    Especialidade cadastrar(String nome, String descricao);

    Especialidade cadastrarComStatusProibido(String nome, String status);

    Especialidade alterar(String nomeOriginal, String novoNome, String novaDescricao);

    Especialidade tentarAlterarComVinculo(String nomeOriginal, String novoNome);

    Especialidade alterarStatus(String nome, String status);

    void excluir(String nome);

    void tentarInativarDuranteExclusao(String nome, String status);

    void atribuirMedico(String nomeMedico, String nomeEspecialidade);

    // MÉTODOS PARA A CAMADA DE APRESENTAÇÃO (QUERIES/DTOs)
    List<Especialidade> listarTodas();

    Optional<Especialidade> buscarPorNome(String nome);

    Optional<Especialidade> buscarPorId(Integer id);

    /**
     * Método de conveniência para criação a partir da camada de apresentação.
     * Internamente delega para 'cadastrar'.
     */
    Especialidade criar(String nome, String descricao);
}