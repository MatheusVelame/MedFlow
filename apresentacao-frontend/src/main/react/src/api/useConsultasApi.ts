// src/api/useConsultasApi.ts

import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import axios from "axios";
import { toast } from "sonner";

// =====================================================================
// TIPAGENS (ALINHADAS COM BACKEND)
// =====================================================================

export type StatusConsulta = "AGENDADA" | "CONFIRMADA" | "EM_ATENDIMENTO" | "CONCLUIDA" | "CANCELADA";

export interface ConsultaResumo {
  id: number;
  dataHora: string;
  nomePaciente: string;
  nomeMedico: string;
  status: StatusConsulta;
}

export interface HistoricoConsultaDetalhes {
  data: string;
  acao: string;
  responsavel: string;
}

export interface ConsultaDetalhes {
  id: number;
  dataHora: string;
  descricao: string;
  status: StatusConsulta;
  idPaciente: string;
  nomeCompletoPaciente: string;
  idMedico: string;
  nomeCompletoMedico: string;
  historico: HistoricoConsultaDetalhes[];
}

// Payloads para operações
export interface AgendarConsultaPayload {
  dataHora: string; // ISO 8601 format
  descricao: string;
  pacienteId: string;
  medicoId: string;
  usuarioId: number;
}

export interface MudarStatusPayload {
  novoStatus: StatusConsulta;
  usuarioId: number;
}

interface MudarStatusVariables {
  id: number;
  payload: MudarStatusPayload;
}

// =====================================================================
// CONFIGURAÇÃO API
// =====================================================================

const API_BASE_URL = "/backend/consultas";

// Funções de API
const fetchConsultas = async (): Promise<ConsultaResumo[]> => {
  const { data } = await axios.get(API_BASE_URL);
  return data;
};

const fetchConsultaById = async (id: number): Promise<ConsultaDetalhes> => {
  const { data } = await axios.get(`${API_BASE_URL}/${id}`);
  return data;
};

const fetchConsultasAgendadas = async (): Promise<ConsultaResumo[]> => {
  const { data } = await axios.get(`${API_BASE_URL}/agendadas`);
  return data;
};

const agendarConsulta = async (payload: AgendarConsultaPayload) => {
  await axios.post(API_BASE_URL, payload);
};

const mudarStatusConsulta = async ({ id, payload }: MudarStatusVariables) => {
  await axios.put(`${API_BASE_URL}/${id}/status`, payload);
};

// =====================================================================
// HOOKS (REACT QUERY)
// =====================================================================

export function useListarConsultas() {
  return useQuery<ConsultaResumo[]>({
    queryKey: ["consultas"],
    queryFn: fetchConsultas,
    refetchOnWindowFocus: false,
  });
}

export function useObterConsulta(id: number | null) {
  return useQuery<ConsultaDetalhes>({
    queryKey: ["consultas", id],
    queryFn: () => fetchConsultaById(id!),
    enabled: !!id,
    refetchOnWindowFocus: false,
  });
}

export function useListarConsultasAgendadas() {
  return useQuery<ConsultaResumo[]>({
    queryKey: ["consultas", "agendadas"],
    queryFn: fetchConsultasAgendadas,
    refetchOnWindowFocus: false,
  });
}

export function useAgendarConsulta() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: agendarConsulta,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["consultas"] });
      toast.success("Consulta agendada com sucesso!");
    },
    onError: (error: any) => {
      const mensagemErro = error.response?.data?.message || "Erro ao agendar consulta.";
      toast.error(mensagemErro);
    },
  });
}

export function useMudarStatusConsulta() {
  const queryClient = useQueryClient();
  return useMutation<void, Error, MudarStatusVariables>({
    mutationFn: mudarStatusConsulta,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["consultas"] });
      toast.success("Status da consulta atualizado com sucesso!");
    },
    onError: (error: any) => {
      const mensagemErro = error.response?.data?.message || "Erro ao atualizar status da consulta.";
      toast.error(mensagemErro);
    },
  });
}

// Função auxiliar para mapear status do frontend para backend
export function mapStatusToBackend(status: string): StatusConsulta {
  const statusMap: Record<string, StatusConsulta> = {
    "agendado": "AGENDADA",
    "confirmado": "CONFIRMADA",
    "em-atendimento": "EM_ATENDIMENTO",
    "concluido": "CONCLUIDA",
    "cancelado": "CANCELADA"
  };
  return statusMap[status] || "AGENDADA";
}

// Função auxiliar para mapear status do backend para frontend
export function mapStatusToFrontend(status: StatusConsulta): string {
  const statusMap: Record<StatusConsulta, string> = {
    "AGENDADA": "agendado",
    "CONFIRMADA": "confirmado",
    "EM_ATENDIMENTO": "em-atendimento",
    "CONCLUIDA": "concluido",
    "CANCELADA": "cancelado"
  };
  return statusMap[status] || "agendado";
}

// Função auxiliar para mapear status para display
export function mapStatusToDisplay(status: StatusConsulta | string): string {
  const statusMap: Record<string, string> = {
    "AGENDADA": "Agendado",
    "CONFIRMADA": "Confirmado",
    "EM_ATENDIMENTO": "Em Atendimento",
    "CONCLUIDA": "Concluído",
    "CANCELADA": "Cancelado",
    "agendado": "Agendado",
    "confirmado": "Confirmado",
    "em-atendimento": "Em Atendimento",
    "concluido": "Concluído",
    "cancelado": "Cancelado"
  };
  return statusMap[status] || status;
}
