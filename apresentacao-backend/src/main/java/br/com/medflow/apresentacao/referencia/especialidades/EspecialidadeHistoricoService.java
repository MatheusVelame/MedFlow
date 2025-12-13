package br.com.medflow.apresentacao.referencia.especialidades;

import org.springframework.stereotype.Service;

import br.com.medflow.infraestrutura.persistencia.jpa.referencia.EspecialidadeHistoricoJpaRepository;
import br.com.medflow.infraestrutura.persistencia.jpa.referencia.EspecialidadeHistoricoJpa;
import br.com.medflow.aplicacao.referencia.especialidades.IEspecialidadeHistoricoAppService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EspecialidadeHistoricoService implements IEspecialidadeHistoricoAppService {

    private final EspecialidadeHistoricoJpaRepository repo;

    public EspecialidadeHistoricoService(EspecialidadeHistoricoJpaRepository repo) {
        this.repo = repo;
    }

    @Override
    public List<br.com.medflow.aplicacao.referencia.especialidades.EspecialidadeHistoricoDto> listarPorEspecialidade(Integer especialidadeId) {
        List<EspecialidadeHistoricoJpa> jpas = repo.findByEspecialidadeIdOrderByDataHoraDesc(especialidadeId);
        return jpas.stream().map(this::toAppDto).collect(Collectors.toList());
    }

    private br.com.medflow.aplicacao.referencia.especialidades.EspecialidadeHistoricoDto toAppDto(EspecialidadeHistoricoJpa j) {
        return new br.com.medflow.aplicacao.referencia.especialidades.EspecialidadeHistoricoDto(
                j.getId(),
                j.getEspecialidadeId(),
                j.getCampo(),
                j.getValorAnterior(),
                j.getNovoValor(),
                j.getDataHora(),
                j.getTipo() != null ? j.getTipo().name() : null
        );
    }
}