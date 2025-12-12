package br.com.medflow.apresentacao.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import br.com.medflow.dominio.referencia.especialidades.EspecialidadeRepositorio;
import br.com.medflow.dominio.referencia.especialidades.EspecialidadeServicoImpl;
import br.com.medflow.dominio.referencia.especialidades.IEspecialidadeServico;
import br.com.medflow.dominio.referencia.especialidades.EspecialidadeServicoProxy; 
import br.com.medflow.dominio.referencia.especialidades.MedicoRepositorio;

@Configuration
public class ReferenciaConfig {

    @Bean
    public IEspecialidadeServico especialidadeServico(EspecialidadeRepositorio repositorio, MedicoRepositorio medicoRepositorio) {
        // Cria a implementação real (núcleo) com as dependências necessárias
        IEspecialidadeServico original = new EspecialidadeServicoImpl(repositorio, medicoRepositorio);
        // Retorna o proxy que adiciona comportamento (auditoria/log) antes de delegar ao real
        return new EspecialidadeServicoProxy(original);
    }
}