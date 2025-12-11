package br.com.medflow.dominio.administracao.funcionarios;

import static org.apache.commons.lang3.Validate.notNull;
import static org.apache.commons.lang3.Validate.isTrue;

import java.util.Objects;

public final class CRM {

    private final String numero;
    private final String uf;

    // RNs: Validação do formato e notEmpty no construtor (DDD Tático)
    public CRM(String valorCompleto) {
        notNull(valorCompleto, "O CRM não pode ser nulo.");

        // CORREÇÃO: Normaliza caracteres de traço Unicode comuns para o hífen ASCII ('-')
        // para prevenir falhas de leitura de caracteres invisíveis/especiais do .feature.
        String crmNormalizado = valorCompleto.replaceAll("[\\u2010\\u2011\\u2012\\u2013\\u2014\\u2015\\u2053\\u2212]", "-");

        // CORREÇÃO: Limpeza máxima da string para garantir que a leitura do .feature não falhe por caracteres invisíveis.
        // Remove qualquer caractere que não seja letra, dígito ou hífen.
        // A sanitização agora é aplicada à string normalizada.
        String crmSanitizado = crmNormalizado.replaceAll("[^a-zA-Z0-9-]", "");

        // A validação agora usa a string sanitizada.
        isTrue(crmSanitizado.contains("-"), "O CRM deve estar no formato número-UF (ex: 12345-PE).");

        String[] partes = crmSanitizado.split("-");

        isTrue(partes.length == 2, "O CRM deve estar no formato número-UF.");
        isTrue(partes[0].matches("\\d+"), "O número do CRM deve conter apenas dígitos.");
        isTrue(partes[1].length() == 2 && partes[1].matches("[a-zA-Z]+"), "A UF do CRM deve ter 2 letras.");

        this.numero = partes[0];
        this.uf = partes[1].toUpperCase();
    }

    // Construtor vazio para frameworks de persistência (JPA)
    private CRM() {
        this.numero = null;
        this.uf = null;
    }

    public String getNumero() { return numero; }
    public String getUf() { return uf; }

    @Override
    public boolean equals(Object o) {
        // Implementação do equals para garantir que VOs são comparados por valor
        if (this == o) return true;

        // CORREÇÃO DE COMPILAÇÃO: Garante o 'return' explícito.
        if (o == null || getClass() != o.getClass()) return false;

        CRM crm = (CRM) o;
        return Objects.equals(numero, crm.numero) && Objects.equals(uf, crm.uf);
    }

    @Override
    public int hashCode() { return Objects.hash(numero, uf); }

    @Override
    public String toString() { return numero + "-" + uf; }
}