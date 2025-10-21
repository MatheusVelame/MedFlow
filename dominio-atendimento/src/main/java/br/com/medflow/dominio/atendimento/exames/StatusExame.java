package br.com.medflow.dominio.atendimento.exames;

public enum StatusExame {
    AGENDADO,      // Status inicial após o agendamento (RN7)
    EM_ANDAMENTO,  // Exame em progresso
    REALIZADO,     // Exame concluído (somente pode ser cancelado)
    CANCELADO      // Exame cancelado (por paciente, médico ou clínica)
}
