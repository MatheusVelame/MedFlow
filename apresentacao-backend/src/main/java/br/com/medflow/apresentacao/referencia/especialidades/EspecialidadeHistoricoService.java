package br.com.medflow.apresentacao.referencia.especialidades;

import org.springframework.stereotype.Service;

import br.com.medflow.infraestrutura.persistencia.jpa.referencia.EspecialidadeHistoricoJpaRepository;
import br.com.medflow.infraestrutura.persistencia.jpa.referencia.EspecialidadeHistoricoJpa;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EspecialidadeHistoricoService {

    private final EspecialidadeHistoricoJpaRepository repo;

    public EspecialidadeHistoricoService(EspecialidadeHistoricoJpaRepository repo) {
        this.repo = repo;
    }

    public List<EspecialidadeHistoricoDto> listarPorEspecialidade(Integer especialidadeId) {
        List<EspecialidadeHistoricoJpa> jpas = repo.findByEspecialidadeIdOrderByDataHoraDesc(especialidadeId);
        return jpas.stream().map(this::toDto).collect(Collectors.toList());
    }

    private EspecialidadeHistoricoDto toDto(EspecialidadeHistoricoJpa j) {
        return new EspecialidadeHistoricoDto(
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
