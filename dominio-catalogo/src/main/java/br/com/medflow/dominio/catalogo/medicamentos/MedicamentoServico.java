package br.com.medflow.dominio.catalogo.medicamentos;

import static org.apache.commons.lang3.Validate.notNull;

import java.util.List;
import java.util.Optional;

public class MedicamentoServico {
	private final MedicamentoRepositorio repositorio;

	public MedicamentoServico(MedicamentoRepositorio repositorio) {
		notNull(repositorio, "O repositório de medicamentos não pode ser nulo");
		this.repositorio = repositorio;
	}

	public Medicamento cadastrar(String nome, String usoPrincipal, String contraindicacoes, UsuarioResponsavelId responsavelId) {
		// Regra de Negócio: O nome do medicamento deve ser único no sistema
		var existente = repositorio.obterPorNome(nome);
		if (existente.isPresent()) {
			throw new IllegalArgumentException("O medicamento '" + nome + "' já está registrado no sistema.");
		}
		
		var novo = new Medicamento(nome, usoPrincipal, contraindicacoes, responsavelId);
		repositorio.salvar(novo);
		return novo;
	}
	
	public Medicamento obter(MedicamentoId id) {
		return repositorio.obter(id);
	}
	
	public void atualizarUsoPrincipal(MedicamentoId id, String novoUsoPrincipal, UsuarioResponsavelId responsavelId) {
		var medicamento = obter(id);
		medicamento.atualizarUsoPrincipal(novoUsoPrincipal, responsavelId);
		repositorio.salvar(medicamento);
	}
	
	public void mudarStatus(MedicamentoId id, StatusMedicamento novoStatus, UsuarioResponsavelId responsavelId, boolean temPrescricaoAtiva) {
		var medicamento = obter(id);
		
		if (temPrescricaoAtiva) {
			throw new IllegalStateException("Não é permitido alterar o status devido a prescrições ativas.");
		}
		
		medicamento.mudarStatus(novoStatus, responsavelId);
		repositorio.salvar(medicamento);
	}
	
	public void arquivar(MedicamentoId id, UsuarioResponsavelId responsavelId, boolean temPrescricaoAtiva) {
		var medicamento = obter(id);
		
		medicamento.arquivar(temPrescricaoAtiva, responsavelId);
		repositorio.salvar(medicamento);
	}
	
	public void solicitarRevisaoContraindicacoes(MedicamentoId id, String novaContraindicacao, UsuarioResponsavelId responsavelId) {
		var medicamento = obter(id);
		medicamento.solicitarRevisaoContraindicacoes(novaContraindicacao, responsavelId);
		repositorio.salvar(medicamento);
	}

	public void aprovarRevisao(MedicamentoId id, UsuarioResponsavelId revisorId) {
		var medicamento = obter(id);
		medicamento.aprovarRevisao(revisorId);
		repositorio.salvar(medicamento);
	}

	public void rejeitarRevisao(MedicamentoId id, UsuarioResponsavelId revisorId) {
		var medicamento = obter(id);
		medicamento.rejeitarRevisao(revisorId);
		repositorio.salvar(medicamento);
	}
	
	public Optional<Medicamento> pesquisar(String nome) {
		return repositorio.obterPorNome(nome);
	}

	public List<Medicamento> pesquisarComFiltroArquivado() {
		return repositorio.pesquisarComFiltroArquivado();
	}

	public List<Medicamento> pesquisarPadrao() {
		return repositorio.pesquisar();
	}
	
}