package br.com.medflow.apresentacao.referencia.especialidades;

import org.springframework.stereotype.Service;

import br.com.medflow.aplicacao.referencia.especialidades.IEspecialidadeHistoricoAppService;
import br.com.medflow.aplicacao.referencia.especialidades.EspecialidadeHistoricoRepositorioAplicacao;

import java.util.List;

@Service
public class EspecialidadeHistoricoService implements IEspecialidadeHistoricoAppService {

    private final EspecialidadeHistoricoRepositorioAplicacao repo;

    public EspecialidadeHistoricoService(EspecialidadeHistoricoRepositorioAplicacao repo) {
        this.repo = repo;
    }

    @Override
    public List<br.com.medflow.aplicacao.referencia.especialidades.EspecialidadeHistoricoDto> listarPorEspecialidade(Integer especialidadeId) {
        return repo.findByEspecialidadeIdOrderByDataHoraDesc(especialidadeId);
    }
}