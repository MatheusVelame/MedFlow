package br.com.medflow.infraestrutura.persistencia.jpa.referencia;

import org.springframework.stereotype.Component;

import br.com.medflow.dominio.referencia.especialidades.MedicoRepositorio;

/**
 * Stub simples para permitir injeção de MedicoRepositorio no contexto Spring.
 * Retorna 0 por padrão para contagens, pode ser substituído por uma implementação JPA real.
 */
@Component
public class MedicoRepositorioStubImpl implements MedicoRepositorio {

    @Override
    public int contarMedicosAtivosVinculados(String nomeEspecialidade) {
        // TODO: implementar corretamente contra a camada de persistência (JPA/Query)
        return 0;
    }
}
