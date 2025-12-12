package br.com.medflow.infraestrutura.persistencia.jpa;

import br.com.medflow.dominio.catalogo.medicamentos.HistoricoEntrada;
import br.com.medflow.infraestrutura.persistencia.jpa.catalogo.HistoricoEntradaJpa;
import br.com.medflow.dominio.catalogo.medicamentos.Medicamento;
import br.com.medflow.infraestrutura.persistencia.jpa.catalogo.MedicamentoJpa;

// NOVOS IMPORTS PARA CONSULTAS
import br.com.medflow.aplicacao.atendimento.consultas.ConsultaDetalhes;
import br.com.medflow.aplicacao.atendimento.consultas.ConsultaResumo;
import br.com.medflow.infraestrutura.persistencia.jpa.atendimento.ConsultaJpa; // Assumindo o pacote

// IMPORTS PARA FUNCIONÁRIOS
import br.com.medflow.dominio.administracao.funcionarios.Funcionario;
import br.com.medflow.dominio.administracao.funcionarios.FuncionarioId;
import br.com.medflow.infraestrutura.persistencia.jpa.administracao.FuncionarioJpa;
// Não importar HistoricoEntradaJpa de administracao para evitar conflito com o de catalogo
import br.com.medflow.aplicacao.administracao.funcionarios.FuncionarioDetalhes;
import br.com.medflow.aplicacao.administracao.funcionarios.FuncionarioResumo;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class JpaMapeador extends ModelMapper {

	public JpaMapeador() {
		super();
		
        // === 1. Mapeamento de DOMÍNIO (HistoricoEntrada) para JPA ===
        createTypeMap(HistoricoEntrada.class, HistoricoEntradaJpa.class)
            .addMappings(mapper -> {
                // CORRIGIDO: Agora usa src.getResponsavel().getId()
                mapper.map(
                    src -> src.getResponsavel().getId(), 
                    HistoricoEntradaJpa::setResponsavelId
                );
                
                // Garante o mapeamento do LocalDateTime (DATA_HORA)
                mapper.map(
                    HistoricoEntrada::getDataHora, 
                    HistoricoEntradaJpa::setDataHora
                );
                
                // Ignora o mapeamento bidirecional neste sentido
                mapper.skip(HistoricoEntradaJpa::setMedicamento);
            });
            
        // === 2. Mapeamento de DOMÍNIO (Medicamento) para JPA ===
        createTypeMap(Medicamento.class, MedicamentoJpa.class)
            .addMappings(mapper -> {
                // Ignora a coleção bidirecional (a lógica está no RepositorioImpl)
                mapper.skip(MedicamentoJpa::setHistorico);
            });
            
        // Se precisar de mapeamento do ID do Medicamento (VO -> int/Integer)
        // createTypeMap(Medicamento.class, MedicamentoJpa.class)
        //     .addMappings(mapper -> {
        //         mapper.map(src -> src.getId().getId(), MedicamentoJpa::setId); 
        //     });

        // === 3. NOVOS MAPEAMENTOS JPA (ConsultaJpa) para DTOs de Aplicação (Queries) ===
        // O ModelMapper mapeará automaticamente os campos com o mesmo nome (ex: id, pacienteNome)
        
        // Mapeamento para DTO de Detalhes
        createTypeMap(ConsultaJpa.class, ConsultaDetalhes.class);
        
        // Mapeamento para DTO de Resumo
        createTypeMap(ConsultaJpa.class, ConsultaResumo.class);
        
        // === 4. MAPEAMENTOS PARA FUNCIONÁRIOS ===
        
        // Mapeamento de DOMÍNIO (HistoricoEntrada de Funcionario) para JPA
        // Usando nomes completos para evitar conflito com HistoricoEntrada de medicamentos
        createTypeMap(
            br.com.medflow.dominio.administracao.funcionarios.Funcionario.HistoricoEntrada.class, 
            br.com.medflow.infraestrutura.persistencia.jpa.administracao.HistoricoEntradaJpa.class)
            .addMappings(mapper -> {
                // Mapeia o ID do responsável
                mapper.map(
                    src -> src.getResponsavel().getId(), 
                    br.com.medflow.infraestrutura.persistencia.jpa.administracao.HistoricoEntradaJpa::setResponsavelId
                );
                
                // Garante o mapeamento do LocalDateTime (DATA_HORA)
                mapper.map(
                    br.com.medflow.dominio.administracao.funcionarios.Funcionario.HistoricoEntrada::getDataHora, 
                    br.com.medflow.infraestrutura.persistencia.jpa.administracao.HistoricoEntradaJpa::setDataHora
                );
                
                // Ignora o mapeamento bidirecional neste sentido
                mapper.skip(br.com.medflow.infraestrutura.persistencia.jpa.administracao.HistoricoEntradaJpa::setFuncionario);
            });
            
        // Mapeamento de DOMÍNIO (Funcionario) para JPA
        createTypeMap(Funcionario.class, FuncionarioJpa.class)
            .addMappings(mapper -> {
                // Ignora a coleção bidirecional (a lógica está no RepositorioImpl)
                mapper.skip(FuncionarioJpa::setHistorico);
            });
        
        // Mapeamento JPA (FuncionarioJpa) para DTOs de Aplicação (Queries)
        // Mapeamento para DTO de Detalhes
        createTypeMap(FuncionarioJpa.class, FuncionarioDetalhes.class);
        
        // Mapeamento para DTO de Resumo
        createTypeMap(FuncionarioJpa.class, FuncionarioResumo.class);
        
        // === 5. MAPEAMENTOS REVERSOS (JPA -> DOMÍNIO) PARA FUNCIONÁRIOS ===
        
        // Mapeamento reverso de JPA (HistoricoEntradaJpa de funcionários) para DOMÍNIO (HistoricoEntrada de Funcionario)
        // Nota: O ModelMapper deve usar o construtor de HistoricoEntrada que recebe os parâmetros necessários
        // O mapeamento do responsavelId para UsuarioResponsavelId será feito automaticamente se configurado corretamente
            
        // Mapeamento reverso de JPA (FuncionarioJpa) para DOMÍNIO (Funcionario)
        createTypeMap(FuncionarioJpa.class, Funcionario.class)
            .addMappings(mapper -> {
                // Mapeia o ID (Integer) para FuncionarioId (Value Object)
                mapper.map(
                    src -> src.getId() != null ? new FuncionarioId(src.getId()) : null,
                    (dest, value) -> {} // O ID será setado via construtor ou setter específico
                );
            });
	}
}