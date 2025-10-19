package br.com.medflow.dominio.atendimento.consultas;

import java.util.Collections;
import java.util.Set;

public class Medico {
    private final String nome;
    private final Set<String> especialidades;
    private final String prefNotificacao;

    public Medico(String nome, String especialidadePrincipal, String prefNotificacao) {
        this.nome = nome;
        this.especialidades = Collections.singleton(especialidadePrincipal);
        this.prefNotificacao = prefNotificacao;
    }

    public String getNome() { return nome; }
    public boolean atendeEspecialidade(String especialidade) { return especialidades.contains(especialidade); }
    public String getPrefNotificacao() { return prefNotificacao; }
    public Set<String> getEspecialidades() { return especialidades; }

    @Override
    public int hashCode() { return nome.hashCode(); }
    @Override
    public boolean equals(Object obj) { return (obj instanceof Medico) && nome.equals(((Medico) obj).nome); }
}