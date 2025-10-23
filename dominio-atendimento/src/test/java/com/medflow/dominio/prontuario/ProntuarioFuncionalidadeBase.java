package com.medflow.dominio.prontuario;

import br.com.medflow.dominio.atendimento.consultas.Paciente;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe base para funcionalidades de prontuário com mocks e utilitários.
 */
public class ProntuarioFuncionalidadeBase {
    
    protected static final DateTimeFormatter DATE_TIME_FORMATTER = 
        DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm");
    
    // Mocks de repositórios
    protected Map<String, Paciente> pacientes = new HashMap<>();
    protected Map<String, Profissional> profissionais = new HashMap<>();
    protected Map<String, Prontuario> prontuarios = new HashMap<>();
    protected Map<String, HistoricoClinico> historicos = new HashMap<>();
    protected Map<String, LogAuditoria> logsAuditoria = new HashMap<>();
    
    // Estado dos testes
    protected HistoricoClinico historicoAtual;
    protected Prontuario prontuarioAtual;
    protected List<HistoricoClinico> historicosPaciente = new ArrayList<>();
    protected List<Prontuario> prontuariosListados = new ArrayList<>();
    protected List<Prontuario> prontuariosArquivados = new ArrayList<>();
    protected LocalDateTime dataHoraRegistro;
    protected LocalDateTime dataHoraAtual;
    protected RuntimeException excecao;
    protected String ultimaMensagem;
    protected String motivoArquivamento;
    protected String motivoExclusao;
    protected String parecerJuridico;
    protected String anexosReferenciados;
    protected String atendimentoAtual;
    protected String usuarioAtual;
    protected List<HistoricoClinico> historicoConsultado;
    protected Profissional profissionalAtual;
    protected HistoricoClinico ultimoRegistroCriado;

    protected void resetContexto() {
        pacientes.clear();
        profissionais.clear();
        prontuarios.clear();
        historicos.clear();
        logsAuditoria.clear();
        historicoAtual = null;
        prontuarioAtual = null;
        historicosPaciente.clear();
        prontuariosListados.clear();
        prontuariosArquivados.clear();
        dataHoraRegistro = null;
        dataHoraAtual = null;
        excecao = null;
        ultimaMensagem = null;
        motivoArquivamento = null;
        motivoExclusao = null;
        parecerJuridico = null;
        anexosReferenciados = null;
        atendimentoAtual = null;
        usuarioAtual = null;
        historicoConsultado = null;
        profissionalAtual = null;
        ultimoRegistroCriado = null;
    }
    
    protected LocalDateTime parseDateTime(String date, String time) {
        // Se a data já contém hora, usar apenas a data
        if (date.contains(" às ")) {
            return LocalDateTime.parse(date, DATE_TIME_FORMATTER);
        }
        
        // Se a data contém apenas hora (formato "HH:mm"), adicionar data padrão
        if (date.matches("\\d{1,2}:\\d{2}")) {
            String dataAtual = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            return LocalDateTime.parse(dataAtual + " às " + date, DATE_TIME_FORMATTER);
        }
        
        // Se a data contém data e hora no formato "dd/MM/yyyy HH:mm", adicionar " às "
        if (date.matches("\\d{1,2}/\\d{1,2}/\\d{4} \\d{1,2}:\\d{2}")) {
            return LocalDateTime.parse(date.replace(" ", " às "), DATE_TIME_FORMATTER);
        }
        
        // Se não, usar hora padrão ou a hora fornecida
        if (time == null || time.isEmpty()) {
            time = "10:00";
        }
        
        // Concatenar data e hora
        return LocalDateTime.parse(date + " às " + time, DATE_TIME_FORMATTER);
    }
    
    protected void registrarHistoricoClinico(String pacienteId, String sintomas, String diagnostico, 
                                           String conduta, String profissional) throws Exception {
        // Debug: verificar parâmetros
        System.out.println("Registrando histórico clínico para paciente: " + pacienteId);
        System.out.println("Sintomas: " + sintomas);
        System.out.println("Diagnóstico: " + diagnostico);
        System.out.println("Conduta: " + conduta);
        
        // Validações de negócio
        if (pacientes.get(pacienteId) == null) {
            System.out.println("ERRO: Paciente não encontrado: " + pacienteId);
            throw new RuntimeException("paciente não encontrado");
        }
        
        if (sintomas == null || sintomas.trim().isEmpty()) {
            System.out.println("ERRO: Sintomas vazios");
            throw new IllegalArgumentException("sintomas são obrigatórios");
        }
        if (diagnostico == null || diagnostico.trim().isEmpty()) {
            System.out.println("ERRO: Diagnóstico vazio");
            throw new IllegalArgumentException("diagnóstico é obrigatório");
        }
        if (conduta == null || conduta.trim().isEmpty()) {
            System.out.println("ERRO: Conduta vazia");
            throw new IllegalArgumentException("conduta/tratamento é obrigatório");
        }
        
        // Criar histórico clínico
        String id = "HIS-" + System.currentTimeMillis();
        LocalDateTime dataHora = dataHoraRegistro != null ? dataHoraRegistro : LocalDateTime.now();
        
        List<String> anexos = anexosReferenciados != null ? 
            Arrays.asList(anexosReferenciados.split(", ")) : new ArrayList<>();
        
        historicoAtual = new HistoricoClinico(id, pacienteId, sintomas, diagnostico, conduta, 
                                            dataHora, profissional, anexos);
        
        System.out.println("Histórico clínico criado com sucesso: " + historicoAtual.getId());
        
        historicos.put(id, historicoAtual);
        historicosPaciente.add(historicoAtual);
    }
    
    protected void atualizarProntuario(String prontuarioId, String atendimentoId, String observacoes, String profissional) throws Exception {
        Prontuario prontuario = prontuarios.get(prontuarioId);
        if (prontuario == null) {
            throw new RuntimeException("Prontuário não encontrado");
        }
        
        // Verificar se há atendimento ativo vinculado
        if (atendimentoId == null) {
            throw new RuntimeException("Cada atualização deve estar vinculada a um atendimento válido");
        }
        
        // Criar histórico de atualização
        String id = "ATU-" + System.currentTimeMillis();
        LocalDateTime dataHora = dataHoraRegistro != null ? dataHoraRegistro : LocalDateTime.now();
        HistoricoAtualizacao atualizacao = new HistoricoAtualizacao(id, prontuarioId, atendimentoId, 
            dataHora, profissional, observacoes, StatusProntuario.ATIVO);
        
        // Adicionar atualização
        prontuario.adicionarAtualizacao(atualizacao);
        prontuarioAtual = prontuario;
    }

    protected void finalizarAtendimento(String atendimentoId) throws Exception {
        // Simular finalização do atendimento
        // Inativar atualizações vinculadas ao atendimento
        for (Prontuario prontuario : prontuarios.values()) {
            if (prontuario.getAtendimentoId() != null && prontuario.getAtendimentoId().equals(atendimentoId)) {
                prontuario.inativar();
                prontuarioAtual = prontuario;
            }
        }
    }

    protected void consultarHistoricoProntuario(String prontuarioId) throws Exception {
        Prontuario prontuario = prontuarios.get(prontuarioId);
        if (prontuario == null) {
            throw new RuntimeException("Prontuário não encontrado");
        }
        
        // Simular consulta do histórico
        prontuariosListados = new ArrayList<>();
        prontuariosListados.add(prontuario);
    }
    
    protected void arquivarProntuario(String prontuarioId, String usuario, String motivo) throws Exception {
        Prontuario prontuario = prontuarios.get(prontuarioId);
        if (prontuario == null) {
            throw new IllegalArgumentException("prontuário não encontrado");
        }
        
        Profissional prof = profissionais.get(usuario);
        if (prof == null || !prof.podeArquivarProntuario()) {
            throw new SecurityException("permissão administrativa necessária");
        }
        
        if (motivo == null || motivo.trim().isEmpty()) {
            throw new IllegalArgumentException("motivo é obrigatório");
        }
        
        // Debug: verificar status antes do arquivamento
        System.out.println("Status antes do arquivamento: " + prontuario.getStatus());
        
        // Arquivar prontuário
        prontuario.arquivar();
        
        // Debug: verificar status após arquivamento
        System.out.println("Status após arquivamento: " + prontuario.getStatus());
        
        prontuarios.put(prontuarioId, prontuario); // Atualizar no mapa
        prontuarioAtual = prontuario;
        prontuariosArquivados.add(prontuario);
        
    }
    
    protected void excluirProntuario(String prontuarioId, String usuario, String motivo) throws Exception {
        Prontuario prontuario = prontuarios.get(prontuarioId);
        if (prontuario == null) {
            throw new IllegalArgumentException("prontuário não encontrado");
        }
        
        Profissional prof = profissionais.get(usuario);
        if (prof == null || !prof.podeArquivarProntuario()) {
            throw new SecurityException("permissão administrativa necessária");
        }
        
        if (parecerJuridico == null) {
            throw new SecurityException("exclusão somente com autorização legal");
        }
        
        if (motivo == null || motivo.trim().isEmpty()) {
            throw new IllegalArgumentException("motivo é obrigatório");
        }
        
        // Excluir prontuário
        prontuario.excluirLogicamente();
        prontuarioAtual = prontuario;
        
    }
}
