package br.com.medflow.dominio.atendimento.exames;

import java.util.*;

public class ExameRepositorioMemoria {

    private final Map<Long, Exame> exames = new HashMap<>();
    private long sequencia = 1L;

    public Exame salvar(Exame exame) {
        if (exame == null) throw new IllegalArgumentException("Exame não pode ser nulo");
        if (exame.getId() == null) {
            exame = new Exame(sequencia++, exame.getPaciente(), exame.getMedico(),
                    exame.getTipo(), exame.getData(), exame.getHora());
        }
        exames.put(exame.getId(), exame);
        return exame;
    }

    public Optional<Exame> obterPorId(Long id) {
        return Optional.ofNullable(exames.get(id));
    }

    public void remover(Long id) {
        exames.remove(id);
    }

    public List<Exame> listar() {
        return new ArrayList<>(exames.values());
    }

    public void limpar() {
        exames.clear();
        sequencia = 1L;
    }
}
