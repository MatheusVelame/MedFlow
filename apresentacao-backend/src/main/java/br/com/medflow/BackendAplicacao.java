// Localização: apresentacao-backend/src/main/java/br/com/medflow/BackendAplicacao.java

package br.com.medflow;

import static org.springframework.boot.SpringApplication.run;

import java.io.IOException;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

// Imports de Consultas (Módulo 1)
import br.com.medflow.dominio.atendimento.consultas.ConsultaServico;
import br.com.medflow.dominio.atendimento.consultas.ConsultaRepositorio;
import br.com.medflow.aplicacao.atendimento.consultas.ConsultaServicoAplicacao;
import br.com.medflow.aplicacao.atendimento.consultas.ConsultaRepositorioAplicacao;

// Imports de Medicamentos (Módulo 2)
import br.com.medflow.dominio.catalogo.medicamentos.MedicamentoServico;
import br.com.medflow.dominio.catalogo.medicamentos.MedicamentoRepositorio;
import br.com.medflow.aplicacao.catalogo.medicamentos.MedicamentoServicoAplicacao;
import br.com.medflow.aplicacao.catalogo.medicamentos.MedicamentoRepositorioAplicacao;

@SpringBootApplication
public class BackendAplicacao {
    
    // =====================================================================
    // CONFIGURAÇÕES DE BEANS PARA CONSULTAS
    // =====================================================================
    @Bean
    public ConsultaServico consultaServico(ConsultaRepositorio repositorio) {
        return new ConsultaServico(repositorio);
    }
    
    @Bean
    public ConsultaServicoAplicacao consultaServicoAplicacao(ConsultaRepositorioAplicacao repositorio) {
        return new ConsultaServicoAplicacao(repositorio);
    }
    
    // =====================================================================
    // NOVAS CONFIGURAÇÕES DE BEANS PARA MEDICAMENTOS
    // =====================================================================
    
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

	public static void main(String[] args) throws IOException {
		run(BackendAplicacao.class, args);
	}
}