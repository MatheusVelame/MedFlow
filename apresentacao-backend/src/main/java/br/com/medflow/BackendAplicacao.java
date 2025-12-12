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

// Imports de Prontuário
import com.medflow.dominio.prontuario.ProntuarioRepositorio;
import br.com.medflow.aplicacao.prontuario.ProntuarioServicoAplicacao;
import br.com.medflow.aplicacao.prontuario.ProntuarioRepositorioAplicacao;
import br.com.medflow.aplicacao.prontuario.usecase.*;
import br.com.medflow.infraestrutura.persistencia.jpa.prontuario.*;

// Imports de Faturamento
import br.com.medflow.dominio.financeiro.faturamentos.FaturamentoServico;
import br.com.medflow.dominio.financeiro.faturamentos.FaturamentoRepositorio;
import br.com.medflow.dominio.financeiro.faturamentos.TabelaPrecosServico;
import br.com.medflow.aplicacao.financeiro.faturamentos.FaturamentoServicoAplicacao;
import br.com.medflow.aplicacao.financeiro.faturamentos.FaturamentoRepositorioAplicacao;
import br.com.medflow.aplicacao.financeiro.faturamentos.usecase.*;
import br.com.medflow.infraestrutura.persistencia.jpa.financeiro.*;

// Imports de Convenios
import br.com.medflow.dominio.financeiro.convenios.ConvenioServico;
import br.com.medflow.dominio.financeiro.convenios.ConvenioRepositorio;
import br.com.medflow.aplicacao.financeiro.convenios.ConvenioServicoAplicacao;
import br.com.medflow.aplicacao.financeiro.convenios.ConvenioRepositorioAplicacao;
import br.com.medflow.dominio.financeiro.evento.EventoBarramento;
import br.com.medflow.infraestrutura.evento.EventoBarramentoImpl;
import br.com.medflow.aplicacao.financeiro.convenios.ConvenioAuditoriaObservador;

//NOVOS IMPORTS para TiposExames
import br.com.medflow.dominio.referencia.tiposExames.TipoExameServico;
import br.com.medflow.dominio.referencia.tiposExames.TipoExameRepositorio;
import br.com.medflow.aplicacao.referencia.tiposExames.TipoExameServicoAplicacao;
import br.com.medflow.aplicacao.referencia.tiposExames.TipoExameRepositorioAplicacao;

// Imports de FolhaPagamento
import br.com.medflow.dominio.financeiro.folhapagamento.FolhaPagamentoServico;
import br.com.medflow.dominio.financeiro.folhapagamento.FolhaPagamentoRepositorio;
import br.com.medflow.aplicacao.financeiro.folhapagamento.FolhaPagamentoServicoAplicacao;
import br.com.medflow.aplicacao.financeiro.folhapagamento.FolhaPagamentoRepositorioAplicacao;


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

    // =====================================================================
    // CONFIGURAÇÕES DE BEANS PARA PRONTUÁRIO
    // =====================================================================
    
    @Bean
    public ProntuarioServicoAplicacao prontuarioServicoAplicacao(ProntuarioRepositorioAplicacao repositorio) {
        return new ProntuarioServicoAplicacao(repositorio);
    }
    
    @Bean
    public ObterProntuarioQuery obterProntuarioQuery(ProntuarioServicoAplicacao servicoAplicacao) {
        return new ObterProntuarioQuery(servicoAplicacao);
    }
    
    @Bean
    public ListarHistoricoQuery listarHistoricoQuery(ProntuarioServicoAplicacao servicoAplicacao) {
        return new ListarHistoricoQuery(servicoAplicacao);
    }
    
    @Bean
    public ProntuarioRepositorioBase prontuarioRepositorioBase(
            ProntuarioJpaRepository jpaRepository,
            HistoricoClinicoJpaRepository historicoClinicoJpaRepository,
            HistoricoAtualizacaoJpaRepository historicoAtualizacaoJpaRepository) {
        return new ProntuarioRepositorioBase(jpaRepository, historicoClinicoJpaRepository, historicoAtualizacaoJpaRepository);
    }
    
    @Bean
    public AdicionarHistoricoClinicoUseCase adicionarHistoricoClinicoUseCase(
            ProntuarioRepositorioImpl repositorio) {
        return new AdicionarHistoricoClinicoUseCase(repositorio);
    }

    // =====================================================================
    // CONFIGURAÇÕES DE BEANS PARA FATURAMENTO
    // =====================================================================
    
    @Bean
    public TabelaPrecosServico tabelaPrecosServico() {
        return new TabelaPrecosServico();
    }
    
    @Bean
    public FaturamentoRepositorioBase faturamentoRepositorioBase(
            FaturamentoJpaRepository jpaRepository,
            HistoricoFaturamentoJpaRepository historicoJpaRepository) {
        return new FaturamentoRepositorioBase(jpaRepository, historicoJpaRepository);
    }
    
    // Bean para FaturamentoRepositorio (injeta o FaturamentoRepositorioImpl que já tem os decorators)
    @Bean
    public FaturamentoServico faturamentoServico(
            FaturamentoRepositorioImpl repositorio, 
            TabelaPrecosServico tabelaPrecosServico) {
        return new FaturamentoServico(repositorio, tabelaPrecosServico);
    }
    
    @Bean
    public FaturamentoServicoAplicacao faturamentoServicoAplicacao(FaturamentoRepositorioAplicacao repositorio) {
        return new FaturamentoServicoAplicacao(repositorio);
    }
    
    @Bean
    public RegistrarFaturamentoUseCase registrarFaturamentoUseCase(FaturamentoServico faturamentoServico) {
        return new RegistrarFaturamentoUseCase(faturamentoServico);
    }
    
    @Bean
    public MarcarComoPagoUseCase marcarComoPagoUseCase(FaturamentoServico faturamentoServico) {
        return new MarcarComoPagoUseCase(faturamentoServico);
    }
    
    @Bean
    public CancelarFaturamentoUseCase cancelarFaturamentoUseCase(FaturamentoServico faturamentoServico) {
        return new CancelarFaturamentoUseCase(faturamentoServico);
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

	// Configuração do Serviço de Domínio de Folha de Pagamento
	@Bean
	public FolhaPagamentoServico folhaPagamentoServico(FolhaPagamentoRepositorio repositorio) {
		return new FolhaPagamentoServico(repositorio);
	}

	// Configuração do Serviço de Aplicação de Folha de Pagamento
	@Bean
	public FolhaPagamentoServicoAplicacao folhaPagamentoServicoAplicacao(
			FolhaPagamentoServico servicoDominio,
			FolhaPagamentoRepositorioAplicacao repositorioAplicacao) {
		return new FolhaPagamentoServicoAplicacao(servicoDominio, repositorioAplicacao);
	}


    // [Outros Beans de outros Contextos e Eventos seriam configurados aqui]

	// =====================================================================
    // NOVAS CONFIGURAÇÕES DE BEANS PARA TIPOS DE EXAMES
    // =====================================================================

    // Configuração do Serviço de Domínio TipoExame (Commands/Writes)
    @Bean
    public TipoExameServico tipoExameServico(TipoExameRepositorio repositorio) {
        return new TipoExameServico(repositorio);
    }

    // Configuração do Serviço de Aplicação TipoExame (Queries/Reads)
    @Bean
    public TipoExameServicoAplicacao tipoExameServicoAplicacao(TipoExameRepositorioAplicacao repositorio) {
        return new TipoExameServicoAplicacao(repositorio);
    }

	public static void main(String[] args) throws IOException {
		run(BackendAplicacao.class, args);
	}
}