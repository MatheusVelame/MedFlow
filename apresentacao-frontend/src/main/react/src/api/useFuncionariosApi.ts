// src/api/useFuncionariosApi.ts

import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import axios from "axios";
import { toast } from "sonner";

// =====================================================================
// TIPAGENS (ALINHADAS COM BACKEND)
// =====================================================================

export type StatusFuncionario = "ATIVO" | "INATIVO" | "AFASTADO" | "FERIAS";

export interface FuncionarioResumo {
  id: number;
  nome: string;
  funcao: string;
  contato: string;
  status: StatusFuncionario;
}

export interface FuncionarioDetalhes extends FuncionarioResumo {
  historico: HistoricoEntradaResumo[];
}

export interface HistoricoEntradaResumo {
  descricao: string;
  dataHora: string;
  usuarioResponsavel: string;
}

// Payloads para operações
export interface CadastrarFuncionarioPayload {
  nome: string;
  funcao: string;
  contato: string;
  responsavelId: number;
}

export interface AtualizarFuncionarioPayload {
  novoNome: string;
  novaFuncao: string;
  novoContato: string;
  responsavelId: number;
}

export interface AtualizarCompletoPayload {
  nome: string;
  funcao: string;
  contato: string;
  status: StatusFuncionario;
  responsavelId: number;
}

export interface MudarStatusPayload {
  responsavelId: number;
}

export interface ExcluirPayload {
  responsavelId: number;
}

interface MutateVariables<T> {
  id: number;
  payload: T;
  temVinculosAtivosFuncao?: boolean;
  temAtividadesFuturas?: boolean;
  possuiHistorico?: boolean;
}

interface MudarStatusVariables {
  id: number;
  novoStatus: StatusFuncionario;
  payload: MudarStatusPayload;
  temAtividadesFuturas?: boolean;
}

// =====================================================================
// CONFIGURAÇÃO API
// =====================================================================

const API_BASE_URL = "/backend/funcionarios";

// Funções de API
const fetchFuncionarios = async (): Promise<FuncionarioResumo[]> => {
  try {
    console.log("🔍 Fazendo requisição para:", API_BASE_URL);
    const response = await axios.get(API_BASE_URL);
    console.log("✅ Resposta completa:", response);
    console.log("📦 Dados retornados:", response.data);
    console.log("📊 Tipo dos dados:", Array.isArray(response.data) ? "Array" : typeof response.data);
    console.log("🔢 Quantidade:", Array.isArray(response.data) ? response.data.length : "N/A");
    
    if (!Array.isArray(response.data)) {
      console.error("❌ ERRO: A resposta não é um array!", response.data);
      return [];
    }
    
    return response.data;
  } catch (error: any) {
    console.error("❌ ERRO ao buscar funcionários:", error);
    console.error("❌ Detalhes do erro:", error.response?.data);
    console.error("❌ Status:", error.response?.status);
    throw error;
  }
};

const fetchFuncionarioById = async (id: number): Promise<FuncionarioDetalhes> => {
  const { data } = await axios.get(`${API_BASE_URL}/${id}`);
  return data;
};

const fetchFuncionariosByStatus = async (status: StatusFuncionario): Promise<FuncionarioResumo[]> => {
  const { data } = await axios.get(`${API_BASE_URL}/status/${status}`);
  return data;
};

const fetchFuncionariosByFuncao = async (funcao: string): Promise<FuncionarioResumo[]> => {
  const { data } = await axios.get(`${API_BASE_URL}/funcao/${funcao}`);
  return data;
};

const createFuncionario = async (payload: CadastrarFuncionarioPayload) => {
  await axios.post(API_BASE_URL, payload);
};

const updateFuncionario = async ({ id, payload, temVinculosAtivosFuncao = false }: MutateVariables<AtualizarFuncionarioPayload>) => {
  await axios.patch(`${API_BASE_URL}/${id}?temVinculosAtivosFuncao=${temVinculosAtivosFuncao}`, payload);
};

const updateFuncionarioCompleto = async ({ 
  id, 
  payload, 
  temVinculosAtivosFuncao = false, 
  temAtividadesFuturas = false 
}: MutateVariables<AtualizarCompletoPayload>) => {
  await axios.put(
    `${API_BASE_URL}/${id}?temVinculosAtivosFuncao=${temVinculosAtivosFuncao}&temAtividadesFuturas=${temAtividadesFuturas}`,
    payload
  );
};

const mudarStatusFuncionario = async ({ 
  id, 
  novoStatus, 
  payload, 
  temAtividadesFuturas = false 
}: MudarStatusVariables) => {
  await axios.put(
    `${API_BASE_URL}/${id}/status/${novoStatus}?temAtividadesFuturas=${temAtividadesFuturas}`,
    payload
  );
};

const deleteFuncionario = async ({ id, payload, possuiHistorico = false }: MutateVariables<ExcluirPayload>) => {
  await axios.delete(`${API_BASE_URL}/${id}?possuiHistorico=${possuiHistorico}`, {
    data: payload
  });
};

// =====================================================================
// HOOKS (REACT QUERY)
// =====================================================================

export function useListarFuncionarios() {
  return useQuery<FuncionarioResumo[]>({
    queryKey: ["funcionarios"],
    queryFn: fetchFuncionarios,
    refetchOnWindowFocus: true,
    staleTime: 0, // Sempre considera os dados como "stale" para forçar refetch
  });
}

export function useObterFuncionario(id: number | null) {
  return useQuery<FuncionarioDetalhes>({
    queryKey: ["funcionarios", id],
    queryFn: () => fetchFuncionarioById(id!),
    enabled: !!id,
    refetchOnWindowFocus: false,
  });
}

export function useListarFuncionariosPorStatus(status: StatusFuncionario | null) {
  return useQuery<FuncionarioResumo[]>({
    queryKey: ["funcionarios", "status", status],
    queryFn: () => fetchFuncionariosByStatus(status!),
    enabled: !!status,
    refetchOnWindowFocus: false,
  });
}

export function useListarFuncionariosPorFuncao(funcao: string | null) {
  return useQuery<FuncionarioResumo[]>({
    queryKey: ["funcionarios", "funcao", funcao],
    queryFn: () => fetchFuncionariosByFuncao(funcao!),
    enabled: !!funcao,
    refetchOnWindowFocus: false,
  });
}

export function useCadastrarFuncionario() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: createFuncionario,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["funcionarios"] });
      toast.success("Funcionário cadastrado com sucesso!");
    },
    onError: (error: any) => {
      const mensagemErro = error.response?.data?.message || "Erro ao cadastrar funcionário.";
      toast.error(mensagemErro);
    },
  });
}

export function useAtualizarFuncionario() {
  const queryClient = useQueryClient();
  return useMutation<void, Error, MutateVariables<AtualizarFuncionarioPayload>>({
    mutationFn: updateFuncionario,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["funcionarios"] });
      toast.success("Funcionário atualizado com sucesso!");
    },
    onError: (error: any) => {
      const mensagemErro = error.response?.data?.message || "Erro ao atualizar funcionário.";
      toast.error(mensagemErro);
    },
  });
}

export function useAtualizarFuncionarioCompleto() {
  const queryClient = useQueryClient();
  return useMutation<void, Error, MutateVariables<AtualizarCompletoPayload>>({
    mutationFn: updateFuncionarioCompleto,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["funcionarios"] });
      toast.success("Funcionário atualizado com sucesso!");
    },
    onError: (error: any) => {
      const mensagemErro = error.response?.data?.message || "Erro ao atualizar funcionário.";
      toast.error(mensagemErro);
    },
  });
}

export function useMudarStatusFuncionario() {
  const queryClient = useQueryClient();
  return useMutation<void, Error, MudarStatusVariables>({
    mutationFn: mudarStatusFuncionario,
    onSuccess: (data, variables) => {
      queryClient.invalidateQueries({ queryKey: ["funcionarios"] });
      toast.success(`Status alterado para ${variables.novoStatus.toLowerCase()} com sucesso!`);
    },
    onError: (error: any) => {
      const mensagemErro = error.response?.data?.message || "Erro ao mudar status do funcionário.";
      toast.error(mensagemErro);
    },
  });
}

export function useExcluirFuncionario() {
  const queryClient = useQueryClient();
  return useMutation<void, Error, MutateVariables<ExcluirPayload>>({
    mutationFn: deleteFuncionario,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["funcionarios"] });
      toast.success("Funcionário excluído com sucesso!");
    },
    onError: (error: any) => {
      const mensagemErro = error.response?.data?.message || "Erro ao excluir funcionário.";
      toast.error(mensagemErro);
    },
  });
}
