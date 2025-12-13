package br.com.medflow.aplicacao.referencia.especialidades;

import java.util.List;

import br.com.medflow.aplicacao.referencia.especialidades.EspecialidadeHistoricoDto;

public interface IEspecialidadeHistoricoAppService {
    List<EspecialidadeHistoricoDto> listarPorEspecialidade(Integer especialidadeId);
}