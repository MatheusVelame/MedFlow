// Localização: dominio-catalogo/src/test/java/br/com/medflow/dominio/catalogo/medicamentos/MedicamentoFuncionalidadeBase.java (Atualizado)

package br.com.medflow.dominio.catalogo.medicamentos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

// Mocks de Suporte

class EventosMock {
    // Usado pela funcionalidade de teste para limpar o estado de eventos
    public List<Object> eventos = new ArrayList<>();
    public void clear() { eventos.clear(); }
}

public class MedicamentoFuncionalidadeBase {
    
    protected MedicamentoServico medicamentoServico; 
    protected MedicamentoRepositorioMemoria repositorio = new MedicamentoRepositorioMemoria();
    protected EventosMock eventos = new EventosMock(); // CAMPO "eventos" definido aqui.
    
	protected Map<String, Integer> usuarios = new HashMap<>();
	
	public MedicamentoFuncionalidadeBase() {
        // Inicializa o serviço de domínio
        this.medicamentoServico = new MedicamentoServico(repositorio);
        
		// Popula IDs simulados para os usuários do teste
		usuarios.put("Admin", 1);
		usuarios.put("Dr. Carlos", 2);
		usuarios.put("Dra. Helena", 3);
		usuarios.put("Recepção", 4);
		usuarios.put("Enfermeiro", 5);
		usuarios.put("SetupCadastro", 99);
	}
	
	protected UsuarioResponsavelId getUsuarioId(String nome) {
		return new UsuarioResponsavelId(usuarios.getOrDefault(nome, 999));
	}
	
	protected Optional<Medicamento> obterMedicamento(String nome) {
		return repositorio.obterPorNome(nome);
	}

    // MÉTODOS PROXY (Resolvem o erro 'undefined for the type MedicamentoServico')
    
    public List<Medicamento> pesquisarPadrao() {
        return repositorio.pesquisar();
    }
    
    public List<Medicamento> pesquisarComFiltroArquivado() {
        return repositorio.pesquisarComFiltroArquivado();
    }
    
	protected boolean temPermissao(String perfil, String acao) {
		// Lógica de permissão simulada
        // FIX: Incluir "Administrador Sênior" para ter todas as permissões
        if (perfil.equals("Administrador") || perfil.equals("Farmacêutico") || perfil.equals("Administrador Sênior")) {
			return true;
		}
		if (perfil.equals("Recepção") && acao.equals("arquivar")) {
			return true; 
		}
		if (perfil.equals("Farmacêutico") && acao.equals("revisar")) {
			return true;
		}
		return false;
	}
}