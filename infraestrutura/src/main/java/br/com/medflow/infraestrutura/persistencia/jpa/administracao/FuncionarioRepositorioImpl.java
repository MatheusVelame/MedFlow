package br.com.medflow.infraestrutura.persistencia.jpa.administracao;

import br.com.medflow.dominio.administracao.funcionarios.CRM;
import br.com.medflow.dominio.administracao.funcionarios.Funcionario;
import br.com.medflow.dominio.administracao.funcionarios.Funcionario.HistoricoEntrada;
import br.com.medflow.dominio.administracao.funcionarios.FuncionarioId;
import br.com.medflow.dominio.administracao.funcionarios.FuncionarioRepositorio;
import br.com.medflow.dominio.administracao.funcionarios.Medico;
import br.com.medflow.dominio.administracao.funcionarios.UsuarioResponsavelId;
import br.com.medflow.infraestrutura.persistencia.jpa.JpaMapeador;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// Implementa o contrato da Camada de Domínio (Commands)
@Component("funcionarioRepositorioImpl")
@Primary
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
        
        // Usa findByIdWithHistorico para garantir que o histórico seja carregado
        FuncionarioJpa jpaExistente = jpaRepository.findByIdWithHistorico(idAtualizacao)
            .orElseThrow(() -> new RuntimeException("Funcionário JPA não encontrado para atualização (ID: " + idAtualizacao + ")"));
        
        // Atualiza os campos básicos manualmente (evita substituir a coleção de histórico)
        jpaExistente.setNome(funcionario.getNome());
        jpaExistente.setFuncao(funcionario.getFuncao());
        jpaExistente.setContato(funcionario.getContato());
        jpaExistente.setStatus(funcionario.getStatus());

        // IMPORTANTE: Com orphanRemoval = true, não podemos substituir a referência da coleção.
        // Devemos modificar a coleção existente para evitar o erro "collection was no longer referenced"
        List<HistoricoEntradaJpa> historicoExistente = jpaExistente.getHistorico();
        
        // Garante que o histórico seja carregado (lazy loading)
        if (historicoExistente == null) {
            historicoExistente = new ArrayList<>();
            jpaExistente.setHistorico(historicoExistente);
        }
        
        // Cria um mapa dos históricos existentes por data/hora e descrição para identificar duplicatas
        // e preservar IDs existentes
        java.util.Map<String, HistoricoEntradaJpa> historicoPorChave = new java.util.HashMap<>();
        for (HistoricoEntradaJpa h : historicoExistente) {
            String chave = h.getDataHora().toString() + "|" + h.getDescricao() + "|" + h.getAcao();
            historicoPorChave.put(chave, h);
        }
        
        historicoExistente.clear(); // Limpa a coleção existente
        
        if (funcionario.getHistorico() != null && !funcionario.getHistorico().isEmpty()) {
            List<HistoricoEntradaJpa> historicoJpa = funcionario.getHistorico().stream()
                .map(entrada -> {
                    String chave = entrada.getDataHora().toString() + "|" + entrada.getDescricao() + "|" + entrada.getAcao();
                    HistoricoEntradaJpa historicoJpaItem = historicoPorChave.get(chave);
                    
                    // Se já existe no banco, reutiliza o objeto existente (preserva o ID)
                    if (historicoJpaItem != null) {
                        // Atualiza os campos que podem ter mudado
                        historicoJpaItem.setAcao(entrada.getAcao());
                        historicoJpaItem.setDescricao(entrada.getDescricao());
                        Integer responsavelId = Integer.parseInt(entrada.getResponsavel().getCodigo());
                        historicoJpaItem.setResponsavelId(responsavelId);
                        historicoJpaItem.setDataHora(entrada.getDataHora());
                        return historicoJpaItem;
                    }
                    
                    // Se não existe, cria um novo (será inserido no banco)
                    historicoJpaItem = new HistoricoEntradaJpa();
                    historicoJpaItem.setAcao(entrada.getAcao());
                    historicoJpaItem.setDescricao(entrada.getDescricao());
                    Integer responsavelId = Integer.parseInt(entrada.getResponsavel().getCodigo());
                    historicoJpaItem.setResponsavelId(responsavelId);
                    historicoJpaItem.setDataHora(entrada.getDataHora());
                    historicoJpaItem.setFuncionario(jpaExistente); // Configura o vínculo bidirecional
                    return historicoJpaItem;
                })
                .collect(Collectors.toList());
            historicoExistente.addAll(historicoJpa); // Adiciona os novos itens à coleção existente
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
		
		// Usa findByIdWithHistorico para garantir que o histórico seja carregado
		Optional<FuncionarioJpa> jpaOptional = jpaRepository.findByIdWithHistorico(idInteger);	
        
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
