package com.medflow.dominio.prontuario;

import java.util.Objects;

/**
 * Representa um profissional de saúde com permissões específicas.
 */
public class Profissional {
    private final String nome;
    private final String perfil;
    private final String registro;
    private final boolean autorizado;

    public Profissional(String nome, String perfil, String registro, boolean autorizado) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do profissional é obrigatório.");
        }
        if (perfil == null || perfil.trim().isEmpty()) {
            throw new IllegalArgumentException("Perfil do profissional é obrigatório.");
        }
        if (registro == null || registro.trim().isEmpty()) {
            throw new IllegalArgumentException("Registro do profissional é obrigatório.");
        }

        this.nome = nome;
        this.perfil = perfil;
        this.registro = registro;
        this.autorizado = autorizado;
    }

    // Getters
    public String getNome() { return nome; }
    public String getPerfil() { return perfil; }
    public String getRegistro() { return registro; }
    public boolean isAutorizado() { return autorizado; }

    public boolean possuiPerfil(String perfilDesejado) {
        return this.perfil.equalsIgnoreCase(perfilDesejado);
    }

    public boolean podeArquivarProntuario() {
        return autorizado && perfil.equalsIgnoreCase("Administrador do Sistema");
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Profissional that = (Profissional) obj;
        return Objects.equals(nome, that.nome) && Objects.equals(registro, that.registro);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nome, registro);
    }

    @Override
    public String toString() {
        return "Profissional{" +
                "nome='" + nome + '\'' +
                ", perfil='" + perfil + '\'' +
                ", registro='" + registro + '\'' +
                ", autorizado=" + autorizado +
                '}';
    }
}
