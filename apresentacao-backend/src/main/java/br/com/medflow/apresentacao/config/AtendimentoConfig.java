package br.com.medflow.apresentacao.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import br.com.medflow.dominio.atendimento.exames.ExameRepositorio;
import br.com.medflow.dominio.atendimento.exames.ExameServicoImpl;
import br.com.medflow.dominio.atendimento.exames.ExameServicoProxy;
import br.com.medflow.dominio.atendimento.exames.IExameServico;
import br.com.medflow.dominio.atendimento.exames.VerificadorExternoServico;
import br.com.medflow.dominio.evento.EventoBarramento;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

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

    // Bean stub para VerificadorExternoServico (configurável via properties)
    @Bean
    public VerificadorExternoServico verificadorExternoServico(Environment env) {
        // Leitura de propriedades simples:
        // medflow.stub.pacientes.invalid = csv de ids (ex: 999,1000)
        // medflow.stub.medicos.inativos = csv
        // medflow.stub.tipos.invalid = csv
        // medflow.stub.medicos.indisponiveis = csv (medicoId sempre indisponível)

        String pacientesInvalidos = env.getProperty("medflow.stub.pacientes.invalid", "");
        String medicosInativos = env.getProperty("medflow.stub.medicos.inativos", "");
        String tiposInvalidos = env.getProperty("medflow.stub.tipos.invalid", "");
        String medicosIndisponiveis = env.getProperty("medflow.stub.medicos.indisponiveis", "");

        Set<Long> pacientesInvalidosSet = parseLongSet(pacientesInvalidos);
        Set<Long> medicosInativosSet = parseLongSet(medicosInativos);
        Set<String> tiposInvalidosSet = parseStringSet(tiposInvalidos);
        Set<Long> medicosIndisponiveisSet = parseLongSet(medicosIndisponiveis);

        return new VerificadorExternoServico() {
            @Override
            public boolean pacienteEstaCadastrado(Long pacienteId) {
                if (pacienteId == null) return false;
                return !pacientesInvalidosSet.contains(pacienteId);
            }

            @Override
            public boolean medicoEstaCadastrado(Long medicoId) {
                if (medicoId == null) return false;
                // Se estiver inativo ou indisponível, consideramos cadastrado (se quiser testar cadastro verifique medicosInativosSet)
                return true;
            }

            @Override
            public boolean medicoEstaAtivo(Long medicoId) {
                if (medicoId == null) return false;
                return !medicosInativosSet.contains(medicoId);
            }

            @Override
            public boolean tipoExameEstaCadastrado(String tipoExame) {
                if (tipoExame == null) return false;
                return !tiposInvalidosSet.contains(tipoExame.toLowerCase());
            }

            @Override
            public boolean medicoEstaDisponivel(Long medicoId, java.time.LocalDateTime dataHora) {
                if (medicoId == null || dataHora == null) return false;
                // Simples: se o médico estiver na lista de indisponíveis, retorna false para qualquer horário
                return !medicosIndisponiveisSet.contains(medicoId);
            }
        };
    }

    private static Set<Long> parseLongSet(String csv) {
        if (csv == null || csv.trim().isEmpty()) return new HashSet<>();
        return Arrays.stream(csv.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Long::valueOf)
                .collect(Collectors.toSet());
    }

    private static Set<String> parseStringSet(String csv) {
        if (csv == null || csv.trim().isEmpty()) return new HashSet<>();
        return Arrays.stream(csv.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(String::toLowerCase)
                .collect(Collectors.toSet());
    }

}