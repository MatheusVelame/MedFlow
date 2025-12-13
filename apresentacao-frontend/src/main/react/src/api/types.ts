export type StatusConsulta = 'AGENDADA' | 'EM_ANDAMENTO' | 'REALIZADA' | 'CANCELADA';

/**
 * Representa os dados resumidos de uma consulta.
 * Baseado em br.com.medflow.aplicacao.atendimento.consultas.ConsultaResumo
 */
export interface ConsultaResumo {
    id: number;
    dataHora: string; // Mapeado de LocalDateTime no Java
    descricao: string;
    pacienteId: number | string; // Ajustado para ser mais flexível, se necessário
    medicoId: number | string;   // Ajustado para ser mais flexível, se necessário
    status: StatusConsulta;
}

/**
 * Interface para a entrada de histórico da consulta.
 * Baseado em br.com.medflow.dominio.atendimento.consultas.HistoricoConsultaEntrada
 */
export interface HistoricoConsultaEntrada {
    acao: string; 
    descricao: string;
    responsavelId: number;
    dataRegistro: string; 
}

/**
 * Representa os detalhes completos de uma consulta.
 * Baseado em br.com.medflow.aplicacao.atendimento.consultas.ConsultaDetalhes
 */
export interface ConsultaDetalhes extends ConsultaResumo {
    // Campos que vêm na resposta de detalhes, mas não no resumo:
    idPaciente: number | string;
    nomeCompletoPaciente: string;
    idMedico: number | string;
    nomeCompletoMedico: string;
    historico: HistoricoConsultaEntrada[];
}

export interface EspecialidadeResumo {
  id: number;
  nome: string;
  descricao?: string | null;
  status: string;
  medicosVinculados?: number;
}

export interface EspecialidadeDetalhes {
  id: number;
  nome: string;
  descricao?: string | null;
  status: string;
  // adicione campos extras se necessário
}

export interface TipoExameResumo {
  id: number;
  codigo?: string;
  descricao?: string;
  especialidade?: string;
  valor?: number;
}

// ExameResponse mapeado diretamente do record Java em backend:
// public record ExameResponse(Long id, Long pacienteId, Long medicoId, String tipoExame, LocalDateTime dataHora, String status)
export interface ExameResponse {
  id: number;
  pacienteId: number;
  medicoId: number;
  tipoExame: string;
  // LocalDateTime do backend é serializado como string no formato ISO local (YYYY-MM-DDTHH:mm:ss)
  dataHora: string;
  status: string;
}

export interface AgendamentoExameRequest {
  pacienteId: number;
  medicoId: number;
  tipoExame: string;
  dataHora: string; // ISO local format: YYYY-MM-DDTHH:mm:ss (sem timezone)
  responsavelId: number;
}

export interface AtualizacaoExameRequest {
  medicoId: number;
  tipoExame: string;
  dataHora: string;
  responsavelId: number;
}

export interface CancelamentoExameRequest {
  motivo: string;
  responsavelId: number;
}
