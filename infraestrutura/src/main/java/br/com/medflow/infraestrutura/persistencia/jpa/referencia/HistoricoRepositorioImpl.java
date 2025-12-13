package br.com.medflow.infraestrutura.persistencia.jpa.referencia;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import br.com.medflow.dominio.referencia.especialidades.EspecialidadeHistorico;
import br.com.medflow.dominio.referencia.especialidades.HistoricoRepositorio;

@Component
public class HistoricoRepositorioImpl implements HistoricoRepositorio {

    private final EspecialidadeHistoricoJpaRepository jpaRepository;
    private final ModelMapper mapper;

    public HistoricoRepositorioImpl(EspecialidadeHistoricoJpaRepository jpaRepository, @Qualifier("jpaMapeador") ModelMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public void salvar(EspecialidadeHistorico historico) {
        EspecialidadeHistoricoJpa jpa = mapper.map(historico, EspecialidadeHistoricoJpa.class);
        jpa.setDataHora(historico.getDataHora());
        jpaRepository.saveAndFlush(jpa);
    }
}