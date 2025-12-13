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
        
        // 1. Instancia a implementação real
        ExameServicoImpl servicoReal = new ExameServicoImpl(repositorio, verificadorExterno, eventoBarramento);
        
        // 2. Instancia o Proxy passando o Real
        return new ExameServicoProxy(servicoReal);
    }

    /**
     * ADAPTADOR: Cria um Bean do tipo 'dominio.evento.EventoBarramento' (esperado pelo Exame)
     * reutilizando o bean 'dominio.financeiro.evento.EventoBarramento' (global).
     * * O nome do método é 'eventoBarramentoAtendimento' para NÃO conflitar com o 
     * 'eventoBarramento' do BackendAplicacao.
     */
    @Bean
    public EventoBarramento eventoBarramentoAtendimento(
            br.com.medflow.dominio.financeiro.evento.EventoBarramento barramentoGlobal) {
        
        return new EventoBarramento() {
            @Override
            public <E> void adicionar(br.com.medflow.dominio.evento.EventoObservador<E> observador) {
                // Cria um adaptador para o observador
                br.com.medflow.dominio.financeiro.evento.EventoObservador<Object> adaptador = 
                    new br.com.medflow.dominio.financeiro.evento.EventoObservador<Object>() {
                        @Override
                        public void observarEvento(Object evento) {
                            try {
                                @SuppressWarnings("unchecked")
                                E eventoTipado = (E) evento;
                                observador.observarEvento(eventoTipado);
                            } catch (ClassCastException e) {
                                // Ignora eventos de tipos incompatíveis
                            }
                        }
                    };
                barramentoGlobal.adicionar(adaptador);
            }

            @Override
            public <E> void postar(E evento) {
                // Repassa o evento para o barramento global
                barramentoGlobal.postar(evento);
            }
        };
    }

    // Bean stub para VerificadorExternoServico
    @Bean
    public VerificadorExternoServico verificadorExternoServico() {
        return new VerificadorExternoServico() {
            @Override
            public boolean pacienteEstaCadastrado(Long pacienteId) { return true; }
            @Override
            public boolean medicoEstaCadastrado(Long medicoId) { return true; }
            @Override
            public boolean medicoEstaAtivo(Long medicoId) { return true; }
            @Override
            public boolean tipoExameEstaCadastrado(String tipoExame) { return true; }
            @Override
            public boolean medicoEstaDisponivel(Long medicoId, java.time.LocalDateTime dataHora) { return true; }
        };
    }
}