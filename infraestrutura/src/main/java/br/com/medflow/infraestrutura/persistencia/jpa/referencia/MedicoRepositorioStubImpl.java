package br.com.medflow.infraestrutura.persistencia.jpa.referencia;

import org.springframework.stereotype.Component;

import br.com.medflow.dominio.referencia.especialidades.MedicoRepositorio;
import br.com.medflow.infraestrutura.persistencia.jpa.referencia.EspecialidadeJpaRepository;
import br.com.medflow.infraestrutura.persistencia.jpa.administracao.MedicoJpaRepository;

@Component
public class MedicoRepositorioStubImpl implements MedicoRepositorio {

    private final EspecialidadeJpaRepository especialidadeJpaRepository;
    private final MedicoJpaRepository medicoJpaRepository;

    public MedicoRepositorioStubImpl(EspecialidadeJpaRepository especialidadeJpaRepository, MedicoJpaRepository medicoJpaRepository) {
        this.especialidadeJpaRepository = especialidadeJpaRepository;
        this.medicoJpaRepository = medicoJpaRepository;
    }

    @Override
    public int contarMedicosAtivosVinculados(String nomeEspecialidade) {
        if (nomeEspecialidade == null) return 0;
        var opt = especialidadeJpaRepository.findByNomeIgnoreCase(nomeEspecialidade.trim());
        if (opt.isEmpty()) return 0;
        Integer especialidadeId = opt.get().getId();
        Long count = medicoJpaRepository.contarAtivosPorEspecialidade(especialidadeId);
        return count == null ? 0 : count.intValue();
    }
}