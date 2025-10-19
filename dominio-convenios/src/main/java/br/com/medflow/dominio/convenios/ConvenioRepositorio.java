package br.com.medflow.dominio.convenios;

import java.util.List;
import java.util.Optional;

public interface ConvenioRepositorio {
	void salvar(Convenio convenio);
	Convenio obter(ConvenioId id);
	Optional<Convenio> obterPorNome(String nome);
	Optional<Convenio> obterPorCodigoIdentificacao(String codigoIdentificacao);
	List<Convenio> pesquisar();
	List<Convenio> pesquisarComFiltroArquivado();
}
