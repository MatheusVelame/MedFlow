package br.com.medflow.dominio.administracao.funcionarios;

import java.util.List;
import java.util.Optional;

public interface FuncionarioRepositorio {
	void salvar(Funcionario funcionario);
	Funcionario obter(FuncionarioId id);
	Optional<Funcionario> obterPorNomeEContato(String nome, String contato);
	List<Funcionario> pesquisar();
	void remover(FuncionarioId id);
}