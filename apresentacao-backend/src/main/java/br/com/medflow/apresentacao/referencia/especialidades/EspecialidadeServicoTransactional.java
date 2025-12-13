package br.com.medflow.apresentacao.referencia.especialidades;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.medflow.dominio.referencia.especialidades.Especialidade;
import br.com.medflow.dominio.referencia.especialidades.EspecialidadeRepositorio;
import br.com.medflow.dominio.referencia.especialidades.HistoricoRepositorio;
import br.com.medflow.dominio.referencia.especialidades.IEspecialidadeServico;
import br.com.medflow.dominio.referencia.especialidades.MedicoRepositorio;
import br.com.medflow.dominio.referencia.especialidades.EspecialidadeServicoImpl;
import br.com.medflow.dominio.referencia.especialidades.EspecialidadeServicoProxy;

import java.util.List;
import java.util.Optional;

/**
 * Transactional adapter that composes the domain implementation and proxy,
 * and exposes a transactional, primary bean to the application.
 */
@Service
@Primary
@Transactional
public class EspecialidadeServicoTransactional implements IEspecialidadeServico {

    private final IEspecialidadeServico delegate;

    public EspecialidadeServicoTransactional(EspecialidadeRepositorio repositorio, MedicoRepositorio medicoRepositorio, HistoricoRepositorio historicoRepositorio) {
        EspecialidadeServicoImpl impl = new EspecialidadeServicoImpl(repositorio, medicoRepositorio, historicoRepositorio);
        this.delegate = new EspecialidadeServicoProxy(impl);
    }

    @Override
    public Especialidade cadastrar(String nome, String descricao) {
        return delegate.cadastrar(nome, descricao);
    }

    @Override
    public Especialidade cadastrarComStatusProibido(String nome, String status) {
        return delegate.cadastrarComStatusProibido(nome, status);
    }

    @Override
    public Especialidade alterar(String nomeOriginal, String novoNome, String novaDescricao) {
        return delegate.alterar(nomeOriginal, novoNome, novaDescricao);
    }

    @Override
    public Especialidade tentarAlterarComVinculo(String nomeOriginal, String novoNome) {
        return delegate.tentarAlterarComVinculo(nomeOriginal, novoNome);
    }

    @Override
    public Especialidade alterarStatus(String nome, String status) {
        return delegate.alterarStatus(nome, status);
    }

    @Override
    public void excluir(String nome) {
        delegate.excluir(nome);
    }

    @Override
    public void tentarInativarDuranteExclusao(String nome, String status) {
        delegate.tentarInativarDuranteExclusao(nome, status);
    }

    @Override
    public void atribuirMedico(String nomeMedico, String nomeEspecialidade) {
        delegate.atribuirMedico(nomeMedico, nomeEspecialidade);
    }

    @Override
    public List<Especialidade> listarTodas() {
        return delegate.listarTodas();
    }

    @Override
    public Optional<Especialidade> buscarPorNome(String nome) {
        return delegate.buscarPorNome(nome);
    }

    @Override
    public Optional<Especialidade> buscarPorId(Integer id) {
        return delegate.buscarPorId(id);
    }

    @Override
    public Especialidade criar(String nome, String descricao) {
        return delegate.criar(nome, descricao);
    }
}
