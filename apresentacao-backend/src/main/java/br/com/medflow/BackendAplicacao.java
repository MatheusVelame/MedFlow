package br.com.medflow;

import static org.springframework.boot.SpringApplication.run;

import java.io.IOException;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

// Imports de Catálogo (Medicamentos)
import br.com.medflow.dominio.catalogo.medicamentos.MedicamentoServico;
import br.com.medflow.dominio.catalogo.medicamentos.MedicamentoRepositorio;
import br.com.medflow.aplicacao.catalogo.medicamentos.MedicamentoServicoAplicacao;
import br.com.medflow.aplicacao.catalogo.medicamentos.MedicamentoRepositorioAplicacao;

// Imports de Atendimento (Consultas) - NOVOS!
import br.com.medflow.aplicacao.atendimento.consultas.ConsultaRepositorioAplicacao;
import br.com.medflow.aplicacao.atendimento.consultas.ConsultaServicoAplicacao;

@SpringBootApplication
public class BackendAplicacao {
    
    // Configuração do Serviço de Domínio (Commands/Writes - Exemplo Medicamento)
    // [Se você tivesse um Serviço de Domínio para Consultas (escrita), estaria aqui]
	@Bean
	public MedicamentoServico medicamentoServico(MedicamentoRepositorio repositorio) {
		return new MedicamentoServico(repositorio);
	}

    // Configuração do Serviço de Aplicação (Queries/Reads - Medicamento)
	@Bean
	public MedicamentoServicoAplicacao medicamentoServicoAplicacao(MedicamentoRepositorioAplicacao repositorio) {
		return new MedicamentoServicoAplicacao(repositorio);
	}

    // Configuração do Serviço de Aplicação (Queries/Reads - Consulta) - NOVO!
    /**
     * Configura o bean do Serviço de Aplicação de Consultas (Queries).
     * O Spring injetará o ConsultaRepositorioAplicacao que é implementado 
     * na camada de Infraestrutura (ConsultaRepositorioAplicacaoImpl).
     */
    @Bean
    public ConsultaServicoAplicacao consultaServicoAplicacao(ConsultaRepositorioAplicacao repositorio) {
        return new ConsultaServicoAplicacao(repositorio);
    }

    // [Outros Beans de outros Contextos e Eventos seriam configurados aqui]

	public static void main(String[] args) throws IOException {
		run(BackendAplicacao.class, args);
	}
}