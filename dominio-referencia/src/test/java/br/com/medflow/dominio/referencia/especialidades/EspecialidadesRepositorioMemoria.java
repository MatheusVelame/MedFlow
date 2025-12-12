package br.com.medflow.dominio.referencia.especialidades;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Repositório em Memória para uso exclusivo em testes BDD (Cucumber).
 * Simula a persistência e garante o isolamento dos cenários.
 */
public class EspecialidadesRepositorioMemoria implements EspecialidadeRepositorio {

    // Simula a tabela de Especialidades
    private final Map<String, Especialidade> especialidades = new HashMap<>();
    private final AtomicInteger nextId = new AtomicInteger(1);

    @Override
    public void salvar(Especialidade especialidade) {
        // Atribui id se necessário
        if (especialidade.getId() == null) {
            especialidade.setId(nextId.getAndIncrement());
        }
        // O nome é a chave única (simulando a PK ou índice de unicidade)
        especialidades.put(especialidade.getNome(), especialidade);
    }

    @Override
    public Optional<Especialidade> buscarPorNome(String nome) {
        return Optional.ofNullable(especialidades.get(nome));
    }

    @Override
    public Optional<Especialidade> buscarPorId(Integer id) {
        return especialidades.values().stream()
                .filter(e -> e.getId() != null && e.getId().equals(id))
                .findFirst();
    }

    @Override
    public boolean existePorNome(String nome) {
        return especialidades.containsKey(nome);
    }

    @Override
    public void remover(Especialidade especialidade) {
        especialidades.remove(especialidade.getNome());
    }

    @Override
    public List<Especialidade> buscarTodos() {
        return new ArrayList<>(especialidades.values());
    }

    /**
     * Limpa o mapa para reiniciar o estado do teste BDD (usado no @Before da classe base).
     */
    public void limpar() {
        especialidades.clear();
        nextId.set(1);
    }
    
    /**
     * Método para popular dados iniciais do contexto (ajuda a simular as precondições).
     */
    public void popular(String nome, String descricao, StatusEspecialidade status, boolean possuiVinculoHistorico) {
        Especialidade e = new Especialidade(nome, descricao, status, possuiVinculoHistorico);
        salvar(e);
    }
}