package br.com.medflow.dominio.referencia.especialidades;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * RealSubject: Contém a lógica de negócio real.
 */
public class EspecialidadeServicoImpl implements IEspecialidadeServico {

    private final EspecialidadeRepositorio especialidadeRepositorio;
    private final MedicoRepositorio medicoRepositorio;

    public EspecialidadeServicoImpl(EspecialidadeRepositorio especialidadeRepositorio, MedicoRepositorio medicoRepositorio) {
        this.especialidadeRepositorio = especialidadeRepositorio;
        this.medicoRepositorio = medicoRepositorio;
    }

    @Override
    public Especialidade cadastrar(String nome, String descricao) {
        if (especialidadeRepositorio.existePorNome(nome)) {
            throw new RegraNegocioException("Já existe uma especialidade com este nome", Map.of("nome", "Já existe uma especialidade com este nome"));
        }

        Especialidade novaEspecialidade = new Especialidade(nome, descricao);
        especialidadeRepositorio.salvar(novaEspecialidade);
        return novaEspecialidade; // repo deve ter setado o id
    }

    @Override
    public Especialidade cadastrarComStatusProibido(String nome, String status) {
        return cadastrar(nome, "Descrição");
    }

    @Override
    public Especialidade alterar(String nomeOriginal, String novoNome, String novaDescricao) {
        Especialidade especialidade = especialidadeRepositorio.buscarPorNome(nomeOriginal)
                .orElseThrow(() -> new RegraNegocioException("Especialidade não encontrada."));

        if (!nomeOriginal.equals(novoNome) && medicoRepositorio.contarMedicosAtivosVinculados(nomeOriginal) > 0) {
            throw new RegraNegocioException("Não é possível alterar o nome: existem médicos ativos vinculados");
        }

        if (!nomeOriginal.equals(novoNome)) {
            if (especialidadeRepositorio.existePorNome(novoNome)) {
                throw new RegraNegocioException("Já existe outra especialidade com este nome", Map.of("nome", "Já existe outra especialidade com este nome"));
            }

            // Exclui a antiga e recria para simular a alteração de chave (se necessário no seu modelo)
            // Ou apenas atualiza a existente se o repositório suportar update
            Especialidade entidadeAntiga = new Especialidade(
                nomeOriginal, 
                especialidade.getDescricao(), 
                especialidade.getStatus(), 
                especialidade.isPossuiVinculoHistorico()
            );
            especialidadeRepositorio.remover(entidadeAntiga);

            especialidade.alterarNome(novoNome);
        }

        if (novaDescricao != null) {
            especialidade.alterarDescricao(novaDescricao);
        }

        especialidadeRepositorio.salvar(especialidade);
        return especialidade;
    }

    @Override
    public Especialidade tentarAlterarComVinculo(String nomeOriginal, String novoNome) {
        return alterar(nomeOriginal, novoNome, null);
    }

    @Override
    public Especialidade alterarStatus(String nome, String status) {
        throw new RegraNegocioException("Apenas o nome e a descrição podem ser alterados");
    }

    @Override
    public void excluir(String nome) {
        Especialidade especialidade = especialidadeRepositorio.buscarPorNome(nome)
                .orElseThrow(() -> new RegraNegocioException("Especialidade não encontrada para exclusão."));

        if (medicoRepositorio.contarMedicosAtivosVinculados(nome) > 0) {
            throw new RegraNegocioException("Não é possível excluir: existem médicos ativos vinculados");
        }

        if (especialidade.isPossuiVinculoHistorico()) {
            especialidade.inativar();
            especialidadeRepositorio.salvar(especialidade);
        } else {
            especialidadeRepositorio.remover(especialidade);
        }
    }

    @Override
    public void tentarInativarDuranteExclusao(String nome, String status) {
        excluir(nome);
    }

    @Override
    public void atribuirMedico(String nomeMedico, String nomeEspecialidade) {
        Especialidade especialidade = especialidadeRepositorio.buscarPorNome(nomeEspecialidade)
                .orElseThrow(() -> new RegraNegocioException("Especialidade não encontrada para atribuição."));

        if (especialidade.getStatus() == StatusEspecialidade.INATIVA) {
            throw new RegraNegocioException("Não é possível atribuir a médicos: a especialidade está inativa");
        }

        especialidade.registrarVinculoHistorico();
        especialidadeRepositorio.salvar(especialidade);
    }

    // ========== MÉTODOS PARA A CAMADA DE APRESENTAÇÃO ==========

    @Override
    public List<Especialidade> listarTodas() {
        return especialidadeRepositorio.buscarTodos();
    }

    @Override
    public Optional<Especialidade> buscarPorNome(String nome) {
        return especialidadeRepositorio.buscarPorNome(nome);
    }

    @Override
    public Optional<Especialidade> buscarPorId(Integer id) {
        return especialidadeRepositorio.buscarPorId(id);
    }

    @Override
    public Especialidade criar(String nome, String descricao) {
        return cadastrar(nome, descricao);
    }
}