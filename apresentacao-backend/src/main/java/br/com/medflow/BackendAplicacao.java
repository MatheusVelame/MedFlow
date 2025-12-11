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

	public static void main(String[] args) throws IOException {
		run(BackendAplicacao.class, args);
	}
}