package br.com.medflow.dominio.referencia.especialidades;

/**
 * Interface que define o contrato para operações de Especialidades.
 * O Padrão Proxy exige que tanto o RealSubject quanto o Proxy implementem esta interface.
 */
public interface IEspecialidadeServico {

    Especialidade cadastrar(String nome, String descricao);

    Especialidade cadastrarComStatusProibido(String nome, String status);

    Especialidade alterar(String nomeOriginal, String novoNome, String novaDescricao);

    Especialidade tentarAlterarComVinculo(String nomeOriginal, String novoNome);

    Especialidade alterarStatus(String nome, String status);

    void excluir(String nome);

    void tentarInativarDuranteExclusao(String nome, String status);

    void atribuirMedico(String nomeMedico, String nomeEspecialidade);
}