// src/api/useFolhaPagamentoApi.ts

import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import axios from "axios";
import { toast } from "sonner";

// =====================================================================
// TIPAGENS (ALINHADAS COM BACKEND)
// =====================================================================

export type StatusFolha = "PENDENTE" | "PAGO" | "CANCELADO";
export type TipoRegistro = "PAGAMENTO" | "AJUSTE";
export type TipoVinculo = "CLT" | "ESTAGIARIO" | "PJ";

export interface FolhaPagamentoResumo {
  id: number;
  funcionarioId: number;
  periodoReferencia: string; // formato: MM/AAAA
  valorLiquido: number;
  status: StatusFolha;
}

export interface FolhaPagamentoDetalhes {
  id: number;
  funcionarioId: number;
  periodoReferencia: string; // formato: MM/AAAA
  tipoRegistro: TipoRegistro;
  salarioBase: number;
  beneficios: number;
  metodoPagamento: string;
  status: StatusFolha;
  valorLiquido: number;
}

// Payloads para operações
export interface RegistrarFolhaPayload {
  funcionarioId: number;
  periodoReferencia: string; // formato: MM/AAAA
  tipoRegistro: TipoRegistro;
  salarioBase: number;
  beneficios: number;
  metodoPagamento: string;
  tipoVinculo: TipoVinculo;
  usuarioResponsavelId: number;
  funcionarioAtivo: boolean;
}

export interface AtualizarValoresPayload {
  novoSalarioBase: number;
  novosBeneficios: number;
  usuarioResponsavelId: number;
}

export interface AlterarStatusPayload {
  novoStatus: StatusFolha;
  usuarioResponsavelId: number;
}

interface MutateVariables<T> {
  id: number;
  payload: T;
}

interface RemoverVariables {
  id: number;
  usuarioResponsavelId: number;
}

// =====================================================================
// CONFIGURAÇÃO API
// =====================================================================

const API_BASE_URL = "/api/folhas-pagamento";

// Funções de API
const fetchFolhasPagamento = async (): Promise<FolhaPagamentoResumo[]> => {
  const { data } = await axios.get(API_BASE_URL);
  return data;
};

const fetchFolhaPagamentoById = async (id: number): Promise<FolhaPagamentoDetalhes> => {
  const { data } = await axios.get(`${API_BASE_URL}/${id}`);
  return data;
};

const fetchFolhasPorFuncionario = async (funcionarioId: number): Promise<FolhaPagamentoResumo[]> => {
  const { data } = await axios.get(`${API_BASE_URL}/funcionario/${funcionarioId}`);
  return data;
};

const fetchFolhasPorStatus = async (status: StatusFolha): Promise<FolhaPagamentoResumo[]> => {
  const { data } = await axios.get(`${API_BASE_URL}/status/${status}`);
  return data;
};

const registrarFolha = async (payload: RegistrarFolhaPayload): Promise<FolhaPagamentoDetalhes> => {
  const { data } = await axios.post(API_BASE_URL, payload);
  return data;
};

const atualizarValores = async ({ id, payload }: MutateVariables<AtualizarValoresPayload>): Promise<FolhaPagamentoDetalhes> => {
  const { data } = await axios.put(`${API_BASE_URL}/${id}/valores`, payload);
  return data;
};

const alterarStatus = async ({ id, payload }: MutateVariables<AlterarStatusPayload>): Promise<FolhaPagamentoDetalhes> => {
  const { data } = await axios.put(`${API_BASE_URL}/${id}/status`, payload);
  return data;
};

const removerFolha = async ({ id, usuarioResponsavelId }: RemoverVariables) => {
  await axios.delete(`${API_BASE_URL}/${id}?usuarioResponsavelId=${usuarioResponsavelId}`);
};

// =====================================================================
// HOOKS (REACT QUERY)
// =====================================================================

export function useListarFolhasPagamento() {
  return useQuery<FolhaPagamentoResumo[]>({
    queryKey: ["folhas-pagamento"],
    queryFn: fetchFolhasPagamento,
    refetchOnWindowFocus: false,
  });
}

export function useObterFolhaPagamento(id: number | null) {
  return useQuery<FolhaPagamentoDetalhes>({
    queryKey: ["folhas-pagamento", id],
    queryFn: () => fetchFolhaPagamentoById(id!),
    enabled: !!id,
    refetchOnWindowFocus: false,
  });
}

export function useListarFolhasPorFuncionario(funcionarioId: number | null) {
  return useQuery<FolhaPagamentoResumo[]>({
    queryKey: ["folhas-pagamento", "funcionario", funcionarioId],
    queryFn: () => fetchFolhasPorFuncionario(funcionarioId!),
    enabled: !!funcionarioId,
    refetchOnWindowFocus: false,
  });
}

export function useListarFolhasPorStatus(status: StatusFolha | null) {
  return useQuery<FolhaPagamentoResumo[]>({
    queryKey: ["folhas-pagamento", "status", status],
    queryFn: () => fetchFolhasPorStatus(status!),
    enabled: !!status,
    refetchOnWindowFocus: false,
  });
}

export function useRegistrarFolhaPagamento() {
  const queryClient = useQueryClient();
  return useMutation<FolhaPagamentoDetalhes, Error, RegistrarFolhaPayload>({
    mutationFn: registrarFolha,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["folhas-pagamento"] });
      toast.success("Folha de pagamento registrada com sucesso!");
    },
    onError: (error: any) => {
      const mensagemErro = error.response?.data?.message || "Erro ao registrar folha de pagamento.";
      toast.error(mensagemErro);
    },
  });
}

export function useAtualizarValoresFolha() {
  const queryClient = useQueryClient();
  return useMutation<FolhaPagamentoDetalhes, Error, MutateVariables<AtualizarValoresPayload>>({
    mutationFn: atualizarValores,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["folhas-pagamento"] });
      toast.success("Valores atualizados com sucesso!");
    },
    onError: (error: any) => {
      const mensagemErro = error.response?.data?.message || "Erro ao atualizar valores.";
      toast.error(mensagemErro);
    },
  });
}

export function useAlterarStatusFolha() {
  const queryClient = useQueryClient();
  return useMutation<FolhaPagamentoDetalhes, Error, MutateVariables<AlterarStatusPayload>>({
    mutationFn: alterarStatus,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["folhas-pagamento"] });
      toast.success("Status alterado com sucesso!");
    },
    onError: (error: any) => {
      const mensagemErro = error.response?.data?.message || "Erro ao alterar status.";
      toast.error(mensagemErro);
    },
  });
}

export function useRemoverFolhaPagamento() {
  const queryClient = useQueryClient();
  return useMutation<void, Error, RemoverVariables>({
    mutationFn: removerFolha,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["folhas-pagamento"] });
      toast.success("Folha de pagamento removida com sucesso!");
    },
    onError: (error: any) => {
      const mensagemErro = error.response?.data?.message || "Erro ao remover folha de pagamento.";
      toast.error(mensagemErro);
    },
  });
}