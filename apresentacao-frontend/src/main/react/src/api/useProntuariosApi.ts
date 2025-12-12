// Localização: apresentacao-frontend/src/main/react/src/api/useProntuariosApi.ts

import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import axios, { AxiosError } from "axios";
import { toast } from "sonner";

// =====================================================================
// TIPAGEM DE DADOS E PAYLOADS
// =====================================================================

export interface ProntuarioResumo {
  id: string;
  pacienteId: string;
  atendimentoId: string | null;
  status: "ATIVO" | "INATIVO" | "INATIVADO" | "ARQUIVADO" | "EXCLUIDO";
  dataHoraCriacao: string; // ISO 8601 format
  profissionalResponsavel: string;
}

export interface ProntuarioDetalhes {
  id: string;
  pacienteId: string;
  atendimentoId: string | null;
  status: "ATIVO" | "INATIVO" | "INATIVADO" | "ARQUIVADO" | "EXCLUIDO";
  dataHoraCriacao: string;
  profissionalResponsavel: string;
  observacoesIniciais: string | null;
}

export interface HistoricoItem {
  id: string;
  sintomas: string;
  diagnostico: string;
  conduta: string;
  dataHoraRegistro: string; // ISO 8601 format
  profissionalResponsavel: string;
  anexosReferenciados: string[];
}

export interface AdicionarHistoricoPayload {
  sintomas: string;
  diagnostico: string;
  conduta: string;
  profissionalResponsavel: string;
  anexosReferenciados?: string[];
}

interface MutateVariables<T> {
  prontuarioId: string;
  payload: T;
}

// =====================================================================
// CONFIGURAÇÃO DA API (FUNÇÕES AXIOS)
// =====================================================================

const API_BASE_URL = "/backend/prontuarios";

const fetchProntuarios = async (): Promise<ProntuarioResumo[]> => {
  const { data } = await axios.get<ProntuarioResumo[]>(API_BASE_URL);
  return data;
};

const fetchProntuarioById = async (id: string): Promise<ProntuarioDetalhes> => {
  const { data } = await axios.get<ProntuarioDetalhes>(`${API_BASE_URL}/${id}`);
  return data;
};

const fetchHistoricoClinico = async (prontuarioId: string): Promise<HistoricoItem[]> => {
  const { data } = await axios.get<HistoricoItem[]>(`${API_BASE_URL}/${prontuarioId}/historico`);
  return data;
};

const adicionarHistoricoApi = async ({ prontuarioId, payload }: MutateVariables<AdicionarHistoricoPayload>) => {
  await axios.post(`${API_BASE_URL}/${prontuarioId}/historico`, payload);
};

// =====================================================================
// HOOKS (REACT QUERY)
// =====================================================================

export function useListarProntuarios() {
  return useQuery<ProntuarioResumo[]>({
    queryKey: ["prontuarios"],
    queryFn: fetchProntuarios,
    refetchOnWindowFocus: false,
  });
}

export function useObterProntuario(id: string | null) {
  return useQuery<ProntuarioDetalhes>({
    queryKey: ["prontuarios", id],
    queryFn: () => fetchProntuarioById(id!),
    enabled: !!id,
    refetchOnWindowFocus: false,
  });
}

export function useListarHistoricoClinico(prontuarioId: string | null) {
  return useQuery<HistoricoItem[]>({
    queryKey: ["prontuarios", prontuarioId, "historico"],
    queryFn: () => fetchHistoricoClinico(prontuarioId!),
    enabled: !!prontuarioId,
    refetchOnWindowFocus: false,
  });
}

export function useAdicionarHistoricoClinico() {
  const queryClient = useQueryClient();
  return useMutation<void, Error, MutateVariables<AdicionarHistoricoPayload>>({
    mutationFn: adicionarHistoricoApi,
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({ queryKey: ["prontuarios", variables.prontuarioId] });
      queryClient.invalidateQueries({ queryKey: ["prontuarios", variables.prontuarioId, "historico"] });
      toast.success("Histórico clínico adicionado com sucesso!");
    },
    onError: (error) => {
      const mensagemErro = (error as AxiosError).response?.data
        ? ((error as AxiosError).response?.data as any)?.mensagem || ((error as AxiosError).response?.data as any)?.message
        : "Erro ao adicionar histórico clínico.";
      toast.error(mensagemErro || "Erro ao adicionar histórico clínico.");
    },
  });
}

