package br.com.medflow.infraestrutura.persistencia.jpa.referencia;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import br.com.medflow.aplicacao.referencia.especialidades.EspecialidadeHistoricoDto;
import br.com.medflow.aplicacao.referencia.especialidades.EspecialidadeHistoricoRepositorioAplicacao;

@Component
public class EspecialidadeHistoricoRepositorioAplicacaoImpl implements EspecialidadeHistoricoRepositorioAplicacao {

    private final EspecialidadeHistoricoJpaRepository jpaRepository;

    public EspecialidadeHistoricoRepositorioAplicacaoImpl(EspecialidadeHistoricoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<EspecialidadeHistoricoDto> findByEspecialidadeIdOrderByDataHoraDesc(Integer especialidadeId) {
        return jpaRepository.findByEspecialidadeIdOrderByDataHoraDesc(especialidadeId)
                .stream()
                .map(j -> new EspecialidadeHistoricoDto(
                        j.getId(),
                        j.getEspecialidadeId(),
                        j.getCampo(),
                        j.getValorAnterior(),
                        j.getNovoValor(),
                        j.getDataHora(),
                        j.getTipo() != null ? j.getTipo().name() : null
                ))
                .collect(Collectors.toList());
    }
}
