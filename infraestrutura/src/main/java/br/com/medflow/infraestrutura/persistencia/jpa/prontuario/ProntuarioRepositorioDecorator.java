package br.com.medflow.infraestrutura.persistencia.jpa.prontuario;

import com.medflow.dominio.prontuario.Prontuario;
import com.medflow.dominio.prontuario.ProntuarioRepositorio;

import java.util.List;
import java.util.Optional;

/**
 * Decorator abstrato para ProntuarioRepositorio.
 * Implementa o padrão Decorator permitindo adicionar funcionalidades
 * sem modificar a implementação base.
 */
public abstract class ProntuarioRepositorioDecorator implements ProntuarioRepositorio {

    protected final ProntuarioRepositorio repositorio;

    public ProntuarioRepositorioDecorator(ProntuarioRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    @Override
    public void salvar(Prontuario prontuario) {
        repositorio.salvar(prontuario);
    }

    @Override
    public Optional<Prontuario> obterPorId(String id) {
        return repositorio.obterPorId(id);
    }

    @Override
    public List<Prontuario> buscarPorPaciente(String pacienteId) {
        return repositorio.buscarPorPaciente(pacienteId);
    }

    @Override
    public List<Prontuario> listarTodos() {
        return repositorio.listarTodos();
    }
}
