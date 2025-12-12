package br.com.medflow.infraestrutura.persistencia.jpa.administracao;

import br.com.medflow.dominio.administracao.funcionarios.CRM;
import br.com.medflow.dominio.administracao.funcionarios.Funcionario;
import br.com.medflow.dominio.administracao.funcionarios.Funcionario.HistoricoEntrada;
import br.com.medflow.dominio.administracao.funcionarios.FuncionarioId;
import br.com.medflow.dominio.administracao.funcionarios.FuncionarioRepositorio;
import br.com.medflow.dominio.administracao.funcionarios.Medico;
import br.com.medflow.dominio.administracao.funcionarios.UsuarioResponsavelId;
import br.com.medflow.infraestrutura.persistencia.jpa.JpaMapeador;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// Implementa o contrato da Camada de Domínio (Commands)
@Component("funcionarioRepositorioImpl")
public class FuncionarioRepositorioImpl implements FuncionarioRepositorio { 

	private final FuncionarioJpaRepository jpaRepository;
	private final JpaMapeador mapeador;

	public FuncionarioRepositorioImpl(FuncionarioJpaRepository jpaRepository, JpaMapeador mapeador) {
		this.jpaRepository = jpaRepository;
		this.mapeador = mapeador;
	}

    @Override
    @Transactional // Garante que a operação de persistência é atômica
	public void salvar(Funcionario funcionario) { 
        
        FuncionarioId idFuncionario = funcionario.getId();
        
        // --- 1. Lógica para NOVOS OBJETOS (INSERT) ---
        // Se não tem ID, é um POST. O Hibernate fará o INSERT.
        if (idFuncionario == null || idFuncionario.getId().isEmpty() || idFuncionario.getId().equals("0")) {
             FuncionarioJpa novaJpa = mapeador.map(funcionario, FuncionarioJpa.class);
             
             // Configura o bidirecional para as novas entradas de histórico
             if (novaJpa.getHistorico() != null) {
                novaJpa.getHistorico().forEach(h -> h.setFuncionario(novaJpa));
             }
             jpaRepository.save(novaJpa);
             return; // Finaliza o fluxo de INSERT
        }
        
        // --- 2. Lógica para ATUALIZAÇÃO (PUT/PATCH) ---
        
        // Carrega a Entidade JPA existente (gerenciada pelo Persistence Context)
        // Converte String para Integer
        Integer idAtualizacao;
        try {
            idAtualizacao = Integer.parseInt(idFuncionario.getId());
        } catch (NumberFormatException e) {
            throw new RuntimeException("ID do funcionário inválido: " + idFuncionario.getId(), e);
        }
        
        FuncionarioJpa jpaExistente = jpaRepository.findById(idAtualizacao)
            .orElseThrow(() -> new RuntimeException("Funcionário JPA não encontrado para atualização (ID: " + idAtualizacao + ")"));
        
        // Mapeia o objeto de Domínio ATUALIZADO para a Entidade JPA gerenciada.
        // O ModelMapper copia NOME, FUNCAO, CONTATO, STATUS, etc., e a nova lista de HISTORICO.
        // O ID é ignorado pelo mapeador, pois ele já está definido em jpaExistente (gerenciada).
        mapeador.map(funcionario, jpaExistente); 

        // 3. Garante que o vínculo bidirecional seja estabelecido para o novo histórico.
        if (jpaExistente.getHistorico() != null) {
            jpaExistente.getHistorico().forEach(h -> {
                // Essencial para que o Hibernate preencha a FK (funcionario_id) na tabela de histórico.
                h.setFuncionario(jpaExistente); 
            });
        }
        
		// O save() aqui faz o UPDATE/MERGE porque a Entidade jpaExistente é gerenciada e tem ID.
		jpaRepository.save(jpaExistente);
	}

	@Override
	public Funcionario obter(FuncionarioId id) {
		// id.getId() retorna String, precisa converter para Integer
		Integer idInteger;
		try {
			idInteger = Integer.parseInt(id.getId());
		} catch (NumberFormatException e) {
			throw new RuntimeException("ID do funcionário inválido: " + id.getId(), e);
		}
		
		Optional<FuncionarioJpa> jpaOptional = jpaRepository.findById(idInteger);	
        
        FuncionarioJpa jpa = jpaOptional
            .orElseThrow(() -> new RuntimeException("Funcionário não encontrado: " + id.getId()));

		// Mapeamento reverso manual (JPA -> Domínio) para evitar problema no construtor do JpaMapeador
		return mapearJpaParaDominio(jpa);
	}
    
	@Override
    public List<Funcionario> pesquisar() {	
        List<FuncionarioJpa> jpas = jpaRepository.findAll();
            
        return jpas.stream()
            .map(this::mapearJpaParaDominio)
            .collect(Collectors.toList());
    }

	@Override
	public Optional<Funcionario> obterPorNomeEContato(String nome, String contato) {	

		Optional<FuncionarioJpa> jpaOptional = jpaRepository.findByNomeIgnoreCaseAndContatoIgnoreCase(nome, contato);

        if (jpaOptional.isEmpty()) {
            return Optional.empty();
        }
        
		// Mapeamento reverso manual (JPA -> Domínio)
		return Optional.of(mapearJpaParaDominio(jpaOptional.get()));
	}
	
	/**
	 * Mapeia FuncionarioJpa para Funcionario (domínio) manualmente.
	 * Isso evita problemas com validações do ModelMapper durante a configuração.
	 */
	private Funcionario mapearJpaParaDominio(FuncionarioJpa jpa) {
		FuncionarioId funcionarioId = new FuncionarioId(jpa.getId());
		
		// Mapeia o histórico
		List<HistoricoEntrada> historico = new ArrayList<>();
		if (jpa.getHistorico() != null) {
			for (var historicoJpa : jpa.getHistorico()) {
				UsuarioResponsavelId responsavelId = new UsuarioResponsavelId(historicoJpa.getResponsavelId());
				HistoricoEntrada entrada = new HistoricoEntrada(
					historicoJpa.getAcao(),
					historicoJpa.getDescricao(),
					responsavelId,
					historicoJpa.getDataHora()
				);
				historico.add(entrada);
			}
		}
		
		// Usa o construtor de reconstrução
		return new Funcionario(
			funcionarioId,
			jpa.getNome(),
			jpa.getFuncao(),
			jpa.getContato(),
			jpa.getStatus(),
			historico
		);
	}

	@Override
	public void remover(FuncionarioId id) {
		// id.getId() retorna String, precisa converter para Integer
		Integer idInteger;
		try {
			idInteger = Integer.parseInt(id.getId());
		} catch (NumberFormatException e) {
			throw new RuntimeException("ID do funcionário inválido: " + id.getId(), e);
		}
		jpaRepository.deleteById(idInteger);
	}
	
	@Override
	public Optional<Medico> obterPorCrm(CRM crm) {
		// NOTA: A tabela funcionarios não possui coluna CRM.
		// Esta implementação retorna Optional.empty() pois não há suporte para busca por CRM no banco.
		// Se necessário, seria preciso adicionar uma coluna crm_numero e crm_uf na tabela funcionarios
		// ou criar uma tabela separada para médicos.
		// Por enquanto, retornamos empty para não quebrar a interface.
		return Optional.empty();
	}
}
