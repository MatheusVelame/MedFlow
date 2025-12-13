// Localização: apresentacao-frontend/src/main/react/src/api/useFaturamentosApi.ts

import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import axios, { AxiosError } from "axios";
import { toast } from "sonner";

// =====================================================================
// TIPAGEM DE DADOS E PAYLOADS
// =====================================================================

export interface FaturamentoResumo {
  id: string;
  pacienteId: string;
  tipoProcedimento: "CONSULTA" | "EXAME";
  descricaoProcedimento: string;
  valor: number;
  metodoPagamento: string;
  status: "PENDENTE" | "PAGO" | "CANCELADO" | "INVALIDO" | "REMOVIDO";
  dataHoraFaturamento: string; // ISO 8601 format
}

export interface FaturamentoDetalhes {
  id: string;
  pacienteId: string;
  tipoProcedimento: "CONSULTA" | "EXAME";
  descricaoProcedimento: string;
  valor: number;
  metodoPagamento: string;
  status: "PENDENTE" | "PAGO" | "CANCELADO" | "INVALIDO" | "REMOVIDO";
  dataHoraFaturamento: string;
  usuarioResponsavel: string;
  observacoes: string | null;
  valorPadrao: number | null;
  justificativaValorDiferente: string | null;
  historico: HistoricoFaturamento[];
}

export interface HistoricoFaturamento {
  acao: string;
  descricao: string;
  responsavel: string;
  dataHora: string;
}

export interface RegistrarFaturamentoPayload {
  pacienteId: string; // String no backend (mas Integer no banco)
  tipoProcedimento: "CONSULTA" | "EXAME";
  descricaoProcedimento: string;
  valor: number;
  metodoPagamento: string;
  usuarioResponsavel: string;
  observacoes?: string;
}

export interface MarcarComoPagoPayload {
  usuarioResponsavel: string;
}

export interface CancelarFaturamentoPayload {
  motivo: string;
  usuarioResponsavel: string;
}

interface MutateVariables<T> {
  id: string;
  payload: T;
}

// =====================================================================
// CONFIGURAÇÃO DA API (FUNÇÕES AXIOS)
// =====================================================================

const API_BASE_URL = "/backend/faturamentos";

const fetchFaturamentos = async (): Promise<FaturamentoResumo[]> => {
  const { data } = await axios.get<FaturamentoResumo[]>(API_BASE_URL);
  return data;
};

const fetchFaturamentoById = async (id: string): Promise<FaturamentoDetalhes> => {
  const { data } = await axios.get<FaturamentoDetalhes>(`${API_BASE_URL}/${id}`);
  return data;
};

const fetchFaturamentosPorStatus = async (status: string): Promise<FaturamentoResumo[]> => {
  const { data } = await axios.get<FaturamentoResumo[]>(`${API_BASE_URL}/status/${status}`);
  return data;
};

const createFaturamentoApi = async (payload: RegistrarFaturamentoPayload): Promise<FaturamentoResumo> => {
  const { data } = await axios.post<FaturamentoResumo>(API_BASE_URL, payload);
  return data;
};

const marcarComoPagoApi = async ({ id, payload }: MutateVariables<MarcarComoPagoPayload>) => {
  await axios.put(`${API_BASE_URL}/${id}/pago`, payload);
};

const cancelarFaturamentoApi = async ({ id, payload }: MutateVariables<CancelarFaturamentoPayload>) => {
  await axios.put(`${API_BASE_URL}/${id}/cancelar`, payload);
};

// =====================================================================
// HOOKS (REACT QUERY)
// =====================================================================

export function useListarFaturamentos() {
  return useQuery<FaturamentoResumo[]>({
    queryKey: ["faturamentos"],
    queryFn: fetchFaturamentos,
    refetchOnWindowFocus: false,
  });
}

export function useObterFaturamento(id: string | null) {
  return useQuery<FaturamentoDetalhes>({
    queryKey: ["faturamentos", id],
    queryFn: () => fetchFaturamentoById(id!),
    enabled: !!id,
    refetchOnWindowFocus: false,
  });
}

export function useFaturamentosPorStatus(status: string | null) {
  return useQuery<FaturamentoResumo[]>({
    queryKey: ["faturamentos", "status", status],
    queryFn: () => fetchFaturamentosPorStatus(status!),
    enabled: !!status,
    refetchOnWindowFocus: false,
  });
}

export function useRegistrarFaturamento() {
  const queryClient = useQueryClient();
  return useMutation<FaturamentoResumo, Error, RegistrarFaturamentoPayload>({
    mutationFn: createFaturamentoApi,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["faturamentos"] });
      toast.success("Faturamento registrado com sucesso!");
    },
    onError: (error) => {
      const mensagemErro = (error as AxiosError).response?.data
        ? ((error as AxiosError).response?.data as any)?.mensagem || ((error as AxiosError).response?.data as any)?.message
        : "Erro ao registrar faturamento.";
      toast.error(mensagemErro || "Erro ao registrar faturamento.");
    },
  });
}

export function useMarcarComoPago() {
  const queryClient = useQueryClient();
  return useMutation<void, Error, MutateVariables<MarcarComoPagoPayload>>({
    mutationFn: marcarComoPagoApi,
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({ queryKey: ["faturamentos"] });
      queryClient.invalidateQueries({ queryKey: ["faturamentos", variables.id] });
      toast.success("Faturamento marcado como pago com sucesso!");
    },
    onError: (error) => {
      const mensagemErro = (error as AxiosError).response?.data
        ? ((error as AxiosError).response?.data as any)?.mensagem || ((error as AxiosError).response?.data as any)?.message
        : "Erro ao marcar faturamento como pago.";
      toast.error(mensagemErro || "Erro ao marcar faturamento como pago.");
    },
  });
}

export function useCancelarFaturamento() {
  const queryClient = useQueryClient();
  return useMutation<void, Error, MutateVariables<CancelarFaturamentoPayload>>({
    mutationFn: cancelarFaturamentoApi,
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({ queryKey: ["faturamentos"] });
      queryClient.invalidateQueries({ queryKey: ["faturamentos", variables.id] });
      toast.success("Faturamento cancelado com sucesso!");
    },
    onError: (error) => {
      const mensagemErro = (error as AxiosError).response?.data
        ? ((error as AxiosError).response?.data as any)?.mensagem || ((error as AxiosError).response?.data as any)?.message
        : "Erro ao cancelar faturamento.";
      toast.error(mensagemErro || "Erro ao cancelar faturamento.");
    },
  });
}

// =====================================================================
// FUNÇÕES AUXILIARES PARA MAPEAMENTO
// =====================================================================

export function mapStatusToDisplay(status: string): string {
  const statusMap: Record<string, string> = {
    PENDENTE: "Pendente",
    PAGO: "Pago",
    CANCELADO: "Cancelado",
    INVALIDO: "Inválido",
    REMOVIDO: "Removido",
  };
  return statusMap[status] || status;
}

export function mapTipoProcedimentoToDisplay(tipo: string): string {
  const tipoMap: Record<string, string> = {
    CONSULTA: "Consulta",
    EXAME: "Exame",
  };
  return tipoMap[tipo] || tipo;
}

