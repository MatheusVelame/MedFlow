package br.com.medflow.dominio.catalogo.medicamentos;

import java.util.List;

public interface HistoricoRepositorio {
    void salvar(HistoricoRegistro registro);
    List<HistoricoRegistro> obterPorMedicamento(String medicamentoId);
}