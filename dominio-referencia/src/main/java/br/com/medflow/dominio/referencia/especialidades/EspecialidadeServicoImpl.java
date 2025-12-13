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
    private final HistoricoRepositorio historicoRepositorio;

    public EspecialidadeServicoImpl(EspecialidadeRepositorio especialidadeRepositorio, MedicoRepositorio medicoRepositorio) {
        this(especialidadeRepositorio, medicoRepositorio, null);
    }

    // novo construtor para injeção do historico (infraestrutura)
    public EspecialidadeServicoImpl(EspecialidadeRepositorio especialidadeRepositorio, MedicoRepositorio medicoRepositorio, HistoricoRepositorio historicoRepositorio) {
        this.especialidadeRepositorio = especialidadeRepositorio;
        this.medicoRepositorio = medicoRepositorio;
        this.historicoRepositorio = historicoRepositorio;
    }

    private String normalize(String s) {
        return s == null ? null : s.trim();
    }

    @Override
    public Especialidade cadastrar(String nome, String descricao) {
        nome = normalize(nome);
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
        nomeOriginal = normalize(nomeOriginal);
        novoNome = normalize(novoNome);

        Especialidade especialidade = especialidadeRepositorio.buscarPorNome(nomeOriginal)
                .orElseThrow(() -> new RegraNegocioException("Especialidade não encontrada."));

        // RN-01: Se houver médicos ATIVOS vinculados, bloquear alteração de nome
        boolean temMedicosAtivos = medicoRepositorio.contarMedicosAtivosVinculados(nomeOriginal) > 0;

        if (!nomeOriginal.equals(novoNome) && temMedicosAtivos) {
            throw new RegraNegocioException("Não é possível alterar o nome: existem médicos ativos vinculados");
        }

        if (!nomeOriginal.equals(novoNome)) {
            if (especialidadeRepositorio.existePorNome(novoNome)) {
                throw new RegraNegocioException("Já existe outra especialidade com este nome", Map.of("nome", "Já existe outra especialidade com este nome"));
            }

            String antigo = especialidade.getNome();
            especialidade.alterarNome(novoNome);

            especialidadeRepositorio.salvar(especialidade);

            // RN-02: registrar historico de alteração de nome
            if (historicoRepositorio != null) {
                EspecialidadeHistorico h = new EspecialidadeHistorico(especialidade.getId(), "nome", antigo, novoNome, TipoOperacaoHistorico.UPDATE);
                historicoRepositorio.salvar(h);
            }
        }

        if (novaDescricao != null) {
            String antigoDesc = especialidade.getDescricao();
            especialidade.alterarDescricao(novaDescricao);
            especialidadeRepositorio.salvar(especialidade);

            if (historicoRepositorio != null) {
                EspecialidadeHistorico h = new EspecialidadeHistorico(especialidade.getId(), "descricao", antigoDesc, novaDescricao, TipoOperacaoHistorico.UPDATE);
                historicoRepositorio.salvar(h);
            }
        }

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
        nome = normalize(nome);
        Especialidade especialidade = especialidadeRepositorio.buscarPorNome(nome)
                .orElseThrow(() -> new RegraNegocioException("Especialidade não encontrada para exclusão."));

        // RN-03: bloquear exclusão se existirem médicos ATIVOS vinculados
        if (medicoRepositorio.contarMedicosAtivosVinculados(nome) > 0) {
            throw new RegraNegocioException("Não é possível excluir: existem médicos ativos vinculados");
        }

        // RN-04 e RN-05: Se já teve vínculo histórico (possuiVinculoHistorico == true), não excluir fisicamente, apenas inativar
        if (especialidade.isPossuiVinculoHistorico()) {
            String antigo = especialidade.getStatus() == null ? null : especialidade.getStatus().name();
            especialidade.inativar();
            especialidadeRepositorio.salvar(especialidade);

            if (historicoRepositorio != null) {
                EspecialidadeHistorico h = new EspecialidadeHistorico(especialidade.getId(), "status", antigo, especialidade.getStatus().name(), TipoOperacaoHistorico.INATIVACAO);
                historicoRepositorio.salvar(h);
            }
        } else {
            // RN-04: Permitir exclusão física apenas se nunca teve vínculo historico
            especialidadeRepositorio.remover(especialidade);
            if (historicoRepositorio != null) {
                EspecialidadeHistorico h = new EspecialidadeHistorico(especialidade.getId(), "exclusao", null, null, TipoOperacaoHistorico.DELETE_FISICO);
                historicoRepositorio.salvar(h);
            }
        }
    }

    @Override
    public void tentarInativarDuranteExclusao(String nome, String status) {
        excluir(nome);
    }

    @Override
    public void atribuirMedico(String nomeMedico, String nomeEspecialidade) {
        nomeEspecialidade = normalize(nomeEspecialidade);
        Especialidade especialidade = especialidadeRepositorio.buscarPorNome(nomeEspecialidade)
                .orElseThrow(() -> new RegraNegocioException("Especialidade não encontrada para atribuição."));

        // RN-07: Especialidades INATIVAS não podem ser atribuídas a novos médicos
        if (especialidade.getStatus() == StatusEspecialidade.INATIVA) {
            throw new RegraNegocioException("Não é possível atribuir a médicos: a especialidade está inativa");
        }

        // marca vínculo histórico e salva
        especialidade.registrarVinculoHistorico();
        especialidadeRepositorio.salvar(especialidade);

        if (historicoRepositorio != null) {
            EspecialidadeHistorico h = new EspecialidadeHistorico(especialidade.getId(), "vinculo", "false", "true", TipoOperacaoHistorico.ATRIBUICAO);
            historicoRepositorio.salvar(h);
        }
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