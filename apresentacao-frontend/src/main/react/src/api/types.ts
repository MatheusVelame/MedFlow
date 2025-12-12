export interface EspecialidadeResumo {
  id: number;
  nome: string;
  descricao?: string | null;
  status: string;
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

export interface ExameResponse {
  id: number;
  pacienteId: number;
  medicoId: number;
  tipoExame: string;
  dataHora: string; // ISO local
  status: string;
}

export interface AgendamentoExameRequest {
  pacienteId: number;
  medicoId: number;
  tipoExame: string;
  dataHora: string; // ISO local format: YYYY-MM-DDTHH:mm:ss
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
