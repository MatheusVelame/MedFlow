package br.com.medflow.dominio.financeiro.faturamentos;

public class UsuarioContext {
    private static String usuarioAtual;
    private static String papelUsuario;
    private static String permissoesEspeciais;
    private static RuntimeException excecaoCompartilhada;
    
    public static void setUsuario(String usuario, String papel) {
        usuarioAtual = usuario;
        papelUsuario = papel;
        permissoesEspeciais = null;
    }
    
    public static void setUsuario(String usuario, String papel, String permissoes) {
        usuarioAtual = usuario;
        papelUsuario = papel;
        permissoesEspeciais = permissoes;
    }
    
    public static String getUsuarioAtual() {
        return usuarioAtual;
    }
    
    public static String getPapelUsuario() {
        return papelUsuario;
    }
    
    public static String getPermissoesEspeciais() {
        return permissoesEspeciais;
    }
    
    public static boolean temPermissaoParaAlterarStatus() {
        return "Administrador Financeiro".equals(papelUsuario) || 
               "Administrador do Sistema".equals(papelUsuario) ||
               (permissoesEspeciais != null && permissoesEspeciais.contains("PermissãoEspecialReversao"));
    }

    public static void clear() {
        usuarioAtual = null;
        papelUsuario = null;
        permissoesEspeciais = null;
        excecaoCompartilhada = null;
    }

    public static void setExcecao(RuntimeException excecao) {
        excecaoCompartilhada = excecao;
    }

    public static RuntimeException getExcecao() {
        return excecaoCompartilhada;
    }
}
