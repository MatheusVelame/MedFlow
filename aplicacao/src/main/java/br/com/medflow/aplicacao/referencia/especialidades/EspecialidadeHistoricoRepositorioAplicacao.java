package br.com.medflow.aplicacao.referencia.especialidades;

import java.util.List;

public interface EspecialidadeHistoricoRepositorioAplicacao {
    List<EspecialidadeHistoricoDto> findByEspecialidadeIdOrderByDataHoraDesc(Integer especialidadeId);
}
