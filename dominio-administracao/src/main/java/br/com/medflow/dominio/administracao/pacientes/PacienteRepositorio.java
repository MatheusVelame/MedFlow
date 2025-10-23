package br.com.medflow.dominio.administracao.pacientes;

import java.util.List;
import java.util.Optional;

public interface PacienteRepositorio {
    void salvar(Paciente paciente);
    Paciente obter(PacienteId id);
    Optional<Paciente> obterPorCpf(String cpf);
    List<Paciente> pesquisar();
    void remover(PacienteId id);
}