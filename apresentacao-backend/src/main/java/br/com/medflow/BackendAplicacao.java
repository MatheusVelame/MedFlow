package br.com.medflow;

import static org.springframework.boot.SpringApplication.run;

import java.io.IOException;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

// Imports de Medicamentos
import br.com.medflow.dominio.catalogo.medicamentos.MedicamentoServico;
import br.com.medflow.dominio.catalogo.medicamentos.MedicamentoRepositorio;
import br.com.medflow.aplicacao.catalogo.medicamentos.MedicamentoServicoAplicacao;
import br.com.medflow.aplicacao.catalogo.medicamentos.MedicamentoRepositorioAplicacao;

// Imports de Convenios
import br.com.medflow.dominio.financeiro.convenios.ConvenioServico;
import br.com.medflow.dominio.financeiro.convenios.ConvenioRepositorio;
import br.com.medflow.aplicacao.financeiro.convenios.ConvenioServicoAplicacao;
import br.com.medflow.aplicacao.financeiro.convenios.ConvenioRepositorioAplicacao;
import br.com.medflow.dominio.financeiro.evento.EventoBarramento;
import br.com.medflow.infraestrutura.evento.EventoBarramentoImpl;
import br.com.medflow.aplicacao.financeiro.convenios.ConvenioAuditoriaObservador;

@SpringBootApplication
public class BackendAplicacao {
    
    // Configuração do Serviço de Domínio (Commands/Writes)
	@Bean
	public MedicamentoServico medicamentoServico(MedicamentoRepositorio repositorio) {
		return new MedicamentoServico(repositorio);
	}

    // Configuração do Serviço de Aplicação (Queries/Reads)
	@Bean
	public MedicamentoServicoAplicacao medicamentoServicoAplicacao(MedicamentoRepositorioAplicacao repositorio) {
		return new MedicamentoServicoAplicacao(repositorio);
	}

	// =====================================================================
	// Configuração de Convenios com Observer
	// =====================================================================

	// Configuração do Barramento de Eventos
	@Bean
	public EventoBarramento eventoBarramento() {
		EventoBarramentoImpl barramento = new EventoBarramentoImpl();
		
		// Registrar observadores
		ConvenioAuditoriaObservador observadorAuditoria = new ConvenioAuditoriaObservador();
		barramento.adicionar(observadorAuditoria);
		
		return barramento;
	}

	// Configuração do Serviço de Domínio de Convenios (com EventoBarramento)
	@Bean
	public ConvenioServico convenioServico(ConvenioRepositorio repositorio, EventoBarramento barramento) {
		return new ConvenioServico(repositorio, barramento);
	}

	// Configuração do Serviço de Aplicação de Convenios (Queries/Reads)
	@Bean
	public ConvenioServicoAplicacao convenioServicoAplicacao(ConvenioRepositorioAplicacao repositorio) {
		return new ConvenioServicoAplicacao(repositorio);
	}

	public static void main(String[] args) throws IOException {
		run(BackendAplicacao.class, args);
	}
}