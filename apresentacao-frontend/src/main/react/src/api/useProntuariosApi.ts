// Localização: apresentacao-frontend/src/main/react/src/api/useProntuariosApi.ts

import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { api } from "./apiClient";
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
  prontuarioId?: string | null; // ID do prontuário (quando listando todos)
  pacienteId?: string | null; // ID do paciente (quando listando todos)
}

export interface AtualizacaoItem {
  id: string;
  atendimentoId: string;
  dataHoraAtualizacao: string; // ISO 8601 format
  profissionalResponsavel: string;
  observacoes: string;
  status: "ATIVO" | "INATIVO" | "INATIVADO" | "ARQUIVADO" | "EXCLUIDO";
  prontuarioId?: string | null; // ID do prontuário (quando listando todos)
  pacienteId?: string | null; // ID do paciente (quando listando todos)
}

export interface AdicionarHistoricoPayload {
  sintomas: string;
  diagnostico: string;
  conduta: string;
  profissionalResponsavel: string;
  anexosReferenciados?: string[];
}

export interface CriarProntuarioPayload {
  pacienteId: string;
  atendimentoId?: string | null;
  profissionalResponsavel: string;
  observacoesIniciais?: string | null;
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
  const response = await api.get<ProntuarioResumo[]>(API_BASE_URL);
  return response.data;
};

const fetchProntuarioById = async (id: string): Promise<ProntuarioDetalhes> => {
  const response = await api.get<ProntuarioDetalhes>(`${API_BASE_URL}/${id}`);
  return response.data;
};

const fetchHistoricoClinico = async (prontuarioId: string): Promise<HistoricoItem[]> => {
  const response = await api.get<HistoricoItem[]>(`${API_BASE_URL}/${prontuarioId}/historico`);
  return response.data;
};

const fetchHistoricoAtualizacoes = async (prontuarioId: string): Promise<AtualizacaoItem[]> => {
  const response = await api.get<AtualizacaoItem[]>(`${API_BASE_URL}/${prontuarioId}/atualizacoes`);
  return response.data;
};

const fetchProntuariosPorPaciente = async (pacienteId: string): Promise<ProntuarioResumo[]> => {
  const response = await api.get<ProntuarioResumo[]>(`${API_BASE_URL}/paciente/${pacienteId}`);
  return response.data;
};

const adicionarHistoricoApi = async ({ prontuarioId, payload }: MutateVariables<AdicionarHistoricoPayload>) => {
  await api.post(`${API_BASE_URL}/${prontuarioId}/historico`, payload);
};

const criarProntuarioApi = async (payload: CriarProntuarioPayload): Promise<ProntuarioDetalhes> => {
  const response = await api.post<ProntuarioDetalhes>(API_BASE_URL, payload);
  return response.data;
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

export function useListarHistoricoAtualizacoes(prontuarioId: string | null) {
  return useQuery<AtualizacaoItem[]>({
    queryKey: ["prontuarios", prontuarioId, "atualizacoes"],
    queryFn: () => fetchHistoricoAtualizacoes(prontuarioId!),
    enabled: !!prontuarioId,
    refetchOnWindowFocus: false,
  });
}

export function useBuscarProntuariosPorPaciente(pacienteId: string | null) {
  return useQuery<ProntuarioResumo[]>({
    queryKey: ["prontuarios", "paciente", pacienteId],
    queryFn: () => fetchProntuariosPorPaciente(pacienteId!),
    enabled: !!pacienteId,
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
      queryClient.invalidateQueries({ queryKey: ["prontuarios"] });
      toast.success("Histórico clínico adicionado com sucesso!");
    },
    onError: (error) => {
      const mensagemErro = error instanceof Error 
        ? error.message 
        : "Erro ao adicionar histórico clínico.";
      toast.error(mensagemErro);
    },
  });
}

export function useCriarProntuario() {
  const queryClient = useQueryClient();
  return useMutation<ProntuarioDetalhes, Error, CriarProntuarioPayload>({
    mutationFn: criarProntuarioApi,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["prontuarios"] });
      toast.success("Prontuário criado com sucesso!");
    },
    onError: (error) => {
      const mensagemErro = error instanceof Error 
        ? error.message 
        : "Erro ao criar prontuário.";
      toast.error(mensagemErro);
    },
  });
}

const inativarProntuarioApi = async (prontuarioId: string, profissionalResponsavel: string): Promise<void> => {
  const url = `${API_BASE_URL}/${prontuarioId}/inativar?profissionalResponsavel=${encodeURIComponent(profissionalResponsavel)}`;
  console.log("Inativando prontuário:", url);
  const response = await api.patch(url);
  return response.data;
};

const excluirProntuarioApi = async (prontuarioId: string, profissionalResponsavel: string): Promise<void> => {
  const url = `${API_BASE_URL}/${prontuarioId}?profissionalResponsavel=${encodeURIComponent(profissionalResponsavel)}`;
  console.log("Excluindo prontuário:", url);
  const response = await api.delete(url);
  return response.data;
};

export function useInativarProntuario() {
  const queryClient = useQueryClient();
  return useMutation<void, Error, { prontuarioId: string; profissionalResponsavel: string }>({
    mutationFn: ({ prontuarioId, profissionalResponsavel }) => inativarProntuarioApi(prontuarioId, profissionalResponsavel),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["prontuarios"] });
      toast.success("Prontuário inativado com sucesso!");
    },
    onError: (error) => {
      const mensagemErro = error instanceof Error 
        ? error.message 
        : "Erro ao inativar prontuário.";
      toast.error(mensagemErro);
    },
  });
}

export function useExcluirProntuario() {
  const queryClient = useQueryClient();
  return useMutation<void, Error, { prontuarioId: string; profissionalResponsavel: string }>({
    mutationFn: ({ prontuarioId, profissionalResponsavel }) => excluirProntuarioApi(prontuarioId, profissionalResponsavel),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["prontuarios"] });
      toast.success("Prontuário excluído com sucesso!");
    },
    onError: (error) => {
      const mensagemErro = error instanceof Error 
        ? error.message 
        : "Erro ao excluir prontuário.";
      toast.error(mensagemErro);
    },
  });
}

