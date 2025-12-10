// Localização: apresentacao-backend/src/main/java/br/com/medflow/BackendAplicacao.java

package br.com.medflow;

import static org.springframework.boot.SpringApplication.run;

import java.io.IOException;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

// Imports de Medicamentos (Originais)
import br.com.medflow.dominio.catalogo.medicamentos.MedicamentoServico;
import br.com.medflow.dominio.catalogo.medicamentos.MedicamentoRepositorio;
import br.com.medflow.aplicacao.catalogo.medicamentos.MedicamentoServicoAplicacao;
import br.com.medflow.aplicacao.catalogo.medicamentos.MedicamentoRepositorioAplicacao;

// NOVOS IMPORTS para Consultas
import br.com.medflow.dominio.atendimento.consultas.ConsultaServico;
import br.com.medflow.dominio.atendimento.consultas.ConsultaRepositorio;
import br.com.medflow.aplicacao.atendimento.consultas.ConsultaServicoAplicacao;
import br.com.medflow.aplicacao.atendimento.consultas.ConsultaRepositorioAplicacao;

@SpringBootApplication
public class BackendAplicacao {
    
    // Configuração do Serviço de Domínio Medicamento (Commands/Writes)
	@Bean
	public MedicamentoServico medicamentoServico(MedicamentoRepositorio repositorio) {
		return new MedicamentoServico(repositorio);
	}

    // Configuração do Serviço de Aplicação Medicamento (Queries/Reads)
	@Bean
	public MedicamentoServicoAplicacao medicamentoServicoAplicacao(MedicamentoRepositorioAplicacao repositorio) {
		return new MedicamentoServicoAplicacao(repositorio);
	}

    // =====================================================================
    // NOVAS CONFIGURAÇÕES DE BEANS PARA CONSULTAS
    // =====================================================================
    
    // Configuração do Serviço de Domínio Consulta (Commands/Writes)
    @Bean
    public ConsultaServico consultaServico(ConsultaRepositorio repositorio) {
        return new ConsultaServico(repositorio);
    }
    
    // Configuração do Serviço de Aplicação Consulta (Queries/Reads)
    @Bean
    public ConsultaServicoAplicacao consultaServicoAplicacao(ConsultaRepositorioAplicacao repositorio) {
        return new ConsultaServicoAplicacao(repositorio);
    }

	public static void main(String[] args) throws IOException {
		run(BackendAplicacao.class, args);
	}
}