package br.com.medflow.dominio.financeiro.faturamentos;

import java.util.Set;

public class PermissaoUsuario {
    private final UsuarioResponsavelId usuarioId;
    private final PapelUsuario papel;
    private final Set<String> permissoesEspeciais;

    public PermissaoUsuario(UsuarioResponsavelId usuarioId, PapelUsuario papel, Set<String> permissoesEspeciais) {
        if (usuarioId == null) throw new IllegalArgumentException("ID do usuário é obrigatório");
        if (papel == null) throw new IllegalArgumentException("Papel do usuário é obrigatório");
        
        this.usuarioId = usuarioId;
        this.papel = papel;
        this.permissoesEspeciais = permissoesEspeciais != null ? permissoesEspeciais : Set.of();
    }

    public UsuarioResponsavelId getUsuarioId() {
        return usuarioId;
    }

    public PapelUsuario getPapel() {
        return papel;
    }

    public boolean temPermissaoEspecial(String permissao) {
        return permissoesEspeciais.contains(permissao);
    }

    public boolean podeAlterarStatus() {
        return papel.podeAlterarStatus();
    }

    public boolean podeReverterPago() {
        return papel.podeReverterPago() || temPermissaoEspecial("PermissãoEspecialReversao");
    }

    public boolean ehAdministrativo() {
        return papel.ehAdministrativo();
    }
}
