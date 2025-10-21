package br.com.medflow.dominio.financeiro.faturamentos;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TabelaPrecosServico {
    
    private final Map<String, BigDecimal> precosConfigurados = new ConcurrentHashMap<>();

    public void configurarPreco(String procedimento, BigDecimal valor) {
        precosConfigurados.put(procedimento.toLowerCase(), valor);
    }

    public Valor obterValorPadrao(TipoProcedimento tipoProcedimento, String descricaoProcedimento) {
        // Primeiro verifica se há um preço configurado dinamicamente
        BigDecimal precoConfigurado = precosConfigurados.get(descricaoProcedimento.toLowerCase());
        if (precoConfigurado != null) {
            return new Valor(precoConfigurado);
        }
        
        // Senão, usa os valores padrão
        switch (tipoProcedimento) {
            case CONSULTA:
                return obterValorPadraoConsulta(descricaoProcedimento);
            case EXAME:
                return obterValorPadraoExame(descricaoProcedimento);
            default:
                throw new IllegalArgumentException("Tipo de procedimento não suportado: " + tipoProcedimento);
        }
    }

    private Valor obterValorPadraoConsulta(String descricaoProcedimento) {
        String descricao = descricaoProcedimento.toLowerCase();
        
        if (descricao.contains("consulta clínica") || descricao.contains("clinica")) {
            return new Valor(new BigDecimal("200.00"));
        } else if (descricao.contains("cardiologia") || descricao.contains("cardio")) {
            return new Valor(new BigDecimal("150.00"));
        } else if (descricao.contains("pediatria") || descricao.contains("pediatrica")) {
            return new Valor(new BigDecimal("120.00"));
        } else if (descricao.contains("ortopedia") || descricao.contains("ortopedica")) {
            return new Valor(new BigDecimal("180.00"));
        } else if (descricao.contains("dermatologia") || descricao.contains("dermatologica")) {
            return new Valor(new BigDecimal("140.00"));
        } else if (descricao.contains("ginecologia") || descricao.contains("ginecologica")) {
            return new Valor(new BigDecimal("130.00"));
        } else if (descricao.contains("clínico geral") || descricao.contains("clinico geral")) {
            return new Valor(new BigDecimal("100.00"));
        } else {
            return new Valor(new BigDecimal("120.00"));
        }
    }

    private Valor obterValorPadraoExame(String descricaoProcedimento) {
        String descricao = descricaoProcedimento.toLowerCase();
        
        if (descricao.contains("exame hemograma") || descricao.contains("hemograma")) {
            return new Valor(new BigDecimal("45.00"));
        } else if (descricao.contains("ultrassom abdominal") || descricao.contains("ultrassom")) {
            return new Valor(new BigDecimal("180.00"));
        } else if (descricao.contains("raio-x tórax") || descricao.contains("raio-x") || descricao.contains("radiografia")) {
            return new Valor(new BigDecimal("95.00"));
        } else if (descricao.contains("exame glicemia") || descricao.contains("glicemia")) {
            return new Valor(new BigDecimal("30.00"));
        } else if (descricao.contains("tomografia") || descricao.contains("ct")) {
            return new Valor(new BigDecimal("300.00"));
        } else if (descricao.contains("ressonância") || descricao.contains("rm")) {
            return new Valor(new BigDecimal("500.00"));
        } else if (descricao.contains("ecg") || descricao.contains("eletrocardiograma")) {
            return new Valor(new BigDecimal("50.00"));
        } else if (descricao.contains("endoscopia")) {
            return new Valor(new BigDecimal("200.00"));
        } else if (descricao.contains("colonoscopia")) {
            return new Valor(new BigDecimal("250.00"));
        } else {
            return new Valor(new BigDecimal("100.00"));
        }
    }

    public boolean valorEhCompativel(Valor valorInformado, TipoProcedimento tipoProcedimento, String descricaoProcedimento) {
        Valor valorPadrao = obterValorPadrao(tipoProcedimento, descricaoProcedimento);
        // Considera compatível se o valor for exatamente igual ao padrão
        return valorInformado.getValor().compareTo(valorPadrao.getValor()) == 0;
    }
}
