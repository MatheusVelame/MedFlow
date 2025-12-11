package br.com.medflow.dominio.administracao.funcionarios;

import static org.apache.commons.lang3.Validate.notNull;
import static org.apache.commons.lang3.Validate.isTrue;

import java.util.Objects;

public final class CRM {

    private final String numero;
    private final String uf;

    public CRM(String valorCompleto) {
        notNull(valorCompleto, "O CRM não pode ser nulo.");

        String crmNormalizado = valorCompleto.replaceAll("[\\u2010\\u2011\\u2012\\u2013\\u2014\\u2015\\u2053\\u2212]", "-");

        String crmSanitizado = crmNormalizado.replaceAll("[^a-zA-Z0-9-]", "");

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

        if (o == null || getClass() != o.getClass()) return false;

        CRM crm = (CRM) o;
        return Objects.equals(numero, crm.numero) && Objects.equals(uf, crm.uf);
    }

    @Override
    public int hashCode() { return Objects.hash(numero, uf); }

    @Override
    public String toString() { return numero + "-" + uf; }
}