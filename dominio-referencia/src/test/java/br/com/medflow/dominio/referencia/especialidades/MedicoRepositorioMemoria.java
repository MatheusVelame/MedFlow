package br.com.medflow.dominio.referencia.especialidades;

import java.util.HashMap;
import java.util.Map;

/**
 * Mock simples do repositório de médicos para testes.
 * Permite configurar quantos médicos ativos estão vinculados a cada especialidade.
 */
public class MedicoRepositorioMemoria implements MedicoRepositorio {

    // Mapeia o nome da especialidade para a contagem de médicos ativos vinculados
    private final Map<String, Integer> vinculosAtivos = new HashMap<>();

    @Override
    public int contarMedicosAtivosVinculados(String nomeEspecialidade) {
        return vinculosAtivos.getOrDefault(nomeEspecialidade, 0);
    }

    /**
     * Define o número de médicos ativos vinculados a uma especialidade (para o setup do teste).
     */
    public void mockContagem(String nomeEspecialidade, int contagem) {
        vinculosAtivos.put(nomeEspecialidade, contagem);
    }
    
    /**
     * Limpa o mapa para reiniciar o estado do teste BDD (usado no @Before da classe base).
     */
    public void limpar() {
        vinculosAtivos.clear();
    }
}