package br.com.medflow.apresentacao.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import br.com.medflow.dominio.atendimento.exames.ExameRepositorio;
import br.com.medflow.dominio.atendimento.exames.ExameServicoImpl;
import br.com.medflow.dominio.atendimento.exames.ExameServicoProxy;
import br.com.medflow.dominio.atendimento.exames.IExameServico;
import br.com.medflow.dominio.atendimento.exames.VerificadorExternoServico;
import br.com.medflow.dominio.evento.EventoBarramento;

@Configuration
public class AtendimentoConfig {

    @Bean
    public IExameServico exameServico(ExameRepositorio repositorio, 
                                      VerificadorExternoServico verificadorExterno, 
                                      EventoBarramento eventoBarramento) {
        
        // 1. Instancia a implementação real (contém a lógica de negócio RNs)
        ExameServicoImpl servicoReal = new ExameServicoImpl(repositorio, verificadorExterno, eventoBarramento);
        
        // 2. Instancia o Proxy passando o Real (adiciona comportamento de Log/Auditoria)
        // O Spring injetará este Proxy onde quer que IExameServico seja necessário
        return new ExameServicoProxy(servicoReal);
    }
}