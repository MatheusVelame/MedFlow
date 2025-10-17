package br.com.medflow.dominio.catalogo.medicamentos;

import java.util.List;
import java.util.Optional;

public interface MedicamentoRepositorio {
    String salvar(Medicamento medicamento);
    Optional<Medicamento> obterPorNome(String nome);
    Optional<Medicamento> obterPorId(String id);
    List<Medicamento> listarTodos();
    HistoricoRepositorio getHistoricoRepositorio();
}