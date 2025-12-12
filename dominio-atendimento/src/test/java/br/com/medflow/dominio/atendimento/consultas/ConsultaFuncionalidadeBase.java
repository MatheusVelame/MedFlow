// Localização: dominio-atendimento/src/test/java/br/com/medflow/dominio/atendimento/consultas/ConsultaFuncionalidadeBase.java

package br.com.medflow.dominio.atendimento.consultas;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException; 



// ====================================================================================
// CLASSES DE SUPORTE (Mocks necessários para o Cucumber)
// OBS: Agora usa as classes de produção Medico e Paciente
// ====================================================================================

class PacienteIdMock {
    private final Integer valor;
    public PacienteIdMock(Integer valor) { this.valor = valor; }
    public Integer getValor() { return valor; }
}

class NotificacaoServicoMock {
    public List<String> notificacoesEnviadas = new ArrayList<>();
    
    public void enviarNotificacao(String destinatario, String tipo, String mensagem) {
        notificacoesEnviadas.add(String.format("%s:%s", destinatario, tipo));
    }
    public void clear() { notificacoesEnviadas.clear(); }
}


public class ConsultaFuncionalidadeBase {
    protected ConsultaRepositorioMemoria repositorio = new ConsultaRepositorioMemoria(); 
    protected Map<String, Medico> medicos = new HashMap<>(); 
    protected Map<String, Paciente> pacientes = new HashMap<>(); 
    protected NotificacaoServicoMock notificacaoServico = new NotificacaoServicoMock();
    
    // NOVO: Mapa para rastrear o estado mockado do número de remarcações (Chave: Paciente/Teste)
    protected Map<String, Integer> remarcacoesCount = new HashMap<>(); 

    protected RuntimeException excecao;
    protected String ultimaMensagem;
    protected Consulta consultaAtual; // Classe de produção
    protected String usuarioAtual;
    
    protected Consulta consultaJoao; // Classe de produção
    
    protected LocalDateTime dataHoraAtual;
    protected LocalDateTime dataHoraConsulta;
    protected LocalDateTime dataHoraNovaConsulta;
    
    protected static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm");
    protected static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public ConsultaFuncionalidadeBase() {
        // Inicialização dos mocks usando o construtor de Paciente e Medico de produção
        // Medico.java
        // Paciente.java (usa o construtor Paciente(nome, prefNotificacao))
        medicos.put("Dr. Eduardo", new Medico("Dr. Eduardo", "Cardiologia", "E-mail"));
        medicos.put("Dra. Helena", new Medico("Dra. Helena", "Dermatologia", "E-mail"));
        medicos.put("Dr. Bruno", new Medico("Dr. Bruno", "Ortopedia", "E-mail"));
        medicos.put("Dr. House", new Medico("Dr. House", "Diagnóstico", "E-mail"));

        pacientes.put("Ana Silva", new Paciente("Ana Silva", "E-mail"));
        pacientes.put("Pedro Alves", new Paciente("Pedro Alves", "SMS"));
        pacientes.put("Joana Lima", new Paciente("Joana Lima", "E-mail"));
        pacientes.put("João", new Paciente("João", "E-mail")); 
        pacientes.put("Gabriel", new Paciente("Gabriel", "E-mail")); 
        pacientes.put("João Costa", new Paciente("João Costa", "E-mail"));
        pacientes.put("Maria Lima", new Paciente("Maria Lima", "E-mail"));
    }

    protected void resetContexto() {
        repositorio.clear();
        notificacaoServico.clear();
        excecao = null;
        ultimaMensagem = null;
        consultaAtual = null;
        usuarioAtual = null;
        dataHoraAtual = null;
        dataHoraConsulta = null;
        dataHoraNovaConsulta = null;
        consultaJoao = null;
        remarcacoesCount.clear(); // NOVO: Reset do contador
        
        pacientes.values().forEach(p -> p.setCancelamentosRecentes(0)); 
    }
    
    protected void simularPermissao(String usuario) {
        if (usuario.equals("Julia") || usuario.equals("Maria") || usuario.equals("João") || usuario.equals("Gabriel")) {
            usuarioAtual = "Recepcionista";
        } else if (usuario.contains("Dr.") || usuario.contains("Dra.")) {
            usuarioAtual = "Medico";
        } else {
            usuarioAtual = "UsuarioComum";
        }
    }
    
    protected void setSystemDate(String date) {
        this.dataHoraAtual = LocalDateTime.parse(date + " às 12:00", DATE_TIME_FORMATTER); 
    }
    
    protected LocalDateTime parseDateTime(String date, String time) {
        return LocalDateTime.parse(date + " às " + time, DATE_TIME_FORMATTER);
    }
    
    // MÉTODO AUXILIAR CORRIGIDO: Extrai o nome do médico da descrição (Visibilidade alterada para protected)
    protected String getMedicoNomeAtual() {
        if (consultaAtual == null) return "Dr. Eduardo";
        String desc = consultaAtual.getDescricao();
        if (desc != null && desc.contains("Consulta com ")) {
             try {
                 return desc.substring(desc.indexOf("Consulta com ") + 13, desc.indexOf(" (Paciente:"));
             } catch (IndexOutOfBoundsException e) {
                 return "Dr. Eduardo"; // Fallback
             }
        }
        return "Dr. Eduardo"; 
    }
    
    // MÉTODO AUXILIAR CORRIGIDO: Extrai o nome do paciente da descrição (Visibilidade alterada para protected)
    protected String getPacienteNomeAtual() {
        if (consultaAtual == null) return "Ana Silva";
        String desc = consultaAtual.getDescricao();
        if (desc != null && desc.contains("(Paciente: ")) {
             try {
                 return desc.substring(desc.indexOf("(Paciente: ") + 11, desc.length() - 1);
             } catch (IndexOutOfBoundsException e) {
                 return "Ana Silva"; // Fallback
             }
        }
        return "Ana Silva"; 
    }
    
    // MÉTODO CORRIGIDO para usar o construtor e IDs da classe de produção (ADICIONANDO RESPONSAVEL)
    protected void cadastrarConsulta(String medicoNome, String pacienteNome, LocalDateTime dataHora) {
        // Cria uma descrição que permite ao repositório mock buscar os nomes.
        String descricao = String.format("Consulta com %s (Paciente: %s)", medicoNome, pacienteNome);
        
        // CORREÇÃO: Paciente.getId() retorna String. Convertemos para Integer conforme o construtor de Consulta espera.
        String pacienteIdStr = pacientes.get(pacienteNome) != null ? pacientes.get(pacienteNome).getId() : "999";
        Integer pacienteIdSimulado = 999;
        try {
            pacienteIdSimulado = Integer.parseInt(pacienteIdStr.replaceAll("\\D", "")); // Tenta extrair um número
        } catch (NumberFormatException ignored) {}
        
        Integer medicoIdSimulado = medicoNome.contains("Eduardo") ? 1 : 2; 

        // NOVO: Simula o ID do usuário que está cadastrando a consulta (Recepcionista/Sistema)
        UsuarioResponsavelId responsavelId = new UsuarioResponsavelId(99); // Recepcionista

        // Usa o construtor de CRIAÇÃO da classe de domínio Consulta (NOVA ASSINATURA)
        Consulta nova = new Consulta(dataHora, descricao, pacienteIdSimulado, medicoIdSimulado, responsavelId); 
        
        String chave = medicoNome + dataHora.toString(); 
        repositorio.salvar(chave, nova); 
        consultaAtual = nova;
    }
    
    protected void marcarConsulta(String medicoNome, String pacienteNome, String especialidade, LocalDateTime dataHora, String usuario) throws Exception {
        if (!usuarioAtual.equals("Recepcionista")) {
            throw new SecurityException("Usuário não tem permissão de recepcionista.");
        }
        
        if (!repositorio.isHorarioLivre(medicoNome, dataHora)) {
            throw new IllegalArgumentException("Horário já está ocupado.");
        }

        if (pacientes.get(pacienteNome) == null) {
            throw new IllegalArgumentException("Paciente não encontrado.");
        }
        
        if (medicos.get(medicoNome) == null) {
            throw new IllegalArgumentException("Médico não encontrado.");
        }
        
        Medico medico = medicos.get(medicoNome);
        if (!medico.atendeEspecialidade(especialidade)) {
            throw new IllegalArgumentException(medico.getNome() + " não atende " + especialidade + ".");
        }
        
        if (dataHora.isBefore(dataHoraAtual.minusDays(1))) { 
            throw new IllegalArgumentException("Não é possível agendar consultas para datas passadas.");
        }

        cadastrarConsulta(medicoNome, pacienteNome, dataHora);

    }

    // MÉTODO CORRIGIDO para usar a nova ação de domínio Consulta.remarcar()
    protected void remarcarConsulta(String pacienteNome, LocalDateTime novaDataHora, String usuario) throws Exception {
        // Regra do limite de remarcação (Mock)
        String patientKey = "Paciente Teste"; // O paciente usado nos cenários de limite
        int currentCount = remarcacoesCount.getOrDefault(patientKey, 0);

        if (currentCount >= 2) {
             throw new IllegalStateException("Limite máximo de 2 remarcações foi atingido.");
        }
        
        if (!repositorio.isHorarioLivre(getMedicoNomeAtual(), novaDataHora)) { 
            throw new IllegalArgumentException("Já existe uma consulta marcada para o dia e o horário escolhido.");
        }
        
        // Checagem de 24h
        if (dataHoraAtual.plusHours(24).isAfter(consultaAtual.getDataHora())) {
            throw new IllegalStateException("A remarcação só é possível com mais de 24h de antecedência");
        }
        
        // NOVO: Simula o ID do usuário responsável pela ação
        UsuarioResponsavelId responsavelId = new UsuarioResponsavelId(1); // Usuario/Sistema
        
        // FIX: Chama o método de domínio correto para remarcação
        consultaAtual.remarcar(novaDataHora, responsavelId); 
        
        this.dataHoraConsulta = novaDataHora; // Atualiza a data de referência para o THEN
        
        // Atualiza o contador de remarcações no mock
        remarcacoesCount.put(patientKey, currentCount + 1); 
    }
    
    // MÉTODO CORRIGIDO para usar a nova assinatura de mudarStatus
    protected void cancelarConsulta(String motivo, String usuario) throws Exception {
        if (motivo == null || motivo.trim().isEmpty()) {
            throw new IllegalArgumentException("O motivo é obrigatório");
        }
        
        if (dataHoraAtual.plusDays(1).isAfter(consultaAtual.getDataHora())) {
            throw new IllegalStateException("O prazo limite de 24 horas foi excedido");
        }
        
        // Lógica simplificada de penalidade
        Paciente paciente = pacientes.get(getPacienteNomeAtual()); 
        
        if (paciente != null && paciente.getCancelamentosRecentes() >= 2) {
            throw new IllegalStateException("Penalidade: restrição de agendamento por 30 dias");
        }
        
        // NOVO: Simula o ID do usuário responsável pela ação
        UsuarioResponsavelId responsavelId = new UsuarioResponsavelId(1); // Usuario/Sistema
        
        // Chama mudarStatus com a nova assinatura
        consultaAtual.mudarStatus(StatusConsulta.CANCELADA, responsavelId); 
    }
    
    // ====================================================================================
    // MÉTODOS AUXILIARES PARA VALIDAÇÃO DO ITERATOR (inalterados)
    // ====================================================================================

    /** Retorna o número de entradas no histórico usando o Iterator. */
    protected int contarEntradasHistorico() {
        if (consultaAtual == null) return 0;
        int count = 0;
        IteradorHistorico<HistoricoConsultaEntrada> iterador = consultaAtual.criarIterador();
        while (iterador.temProximo()) {
            iterador.proximo();
            count++;
        }
        return count;
    }

    /** Verifica se o histórico contém uma entrada com uma ação específica usando o Iterator. */
    protected boolean historicoContemAcao(AcaoHistorico acao) {
        if (consultaAtual == null) return false;
        
        IteradorHistorico<HistoricoConsultaEntrada> iterador = consultaAtual.criarIterador();
        try {
             while (iterador.temProximo()) {
                HistoricoConsultaEntrada entrada = iterador.proximo();
                if (entrada.getAcao() == acao) {
                    return true;
                }
            }
        } catch (NoSuchElementException e) {
            // Ignorar, apenas para segurança em caso de uso indevido no teste
        }
        return false;
    }

    /** Verifica se o histórico contém uma entrada com uma descrição específica usando o Iterator. */
    protected boolean historicoContemDescricao(String parteDescricao) {
        if (consultaAtual == null) return false;
        
        IteradorHistorico<HistoricoConsultaEntrada> iterador = consultaAtual.criarIterador();
        try {
            while (iterador.temProximo()) {
                HistoricoConsultaEntrada entrada = iterador.proximo();
                if (entrada.getDescricao().contains(parteDescricao)) {
                    return true;
                }
            }
        } catch (NoSuchElementException e) {
            // Ignorar
        }
        return false;
    }
}