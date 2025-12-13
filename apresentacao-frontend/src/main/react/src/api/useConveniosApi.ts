// src/api/useConveniosApi.ts

import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import axios from "axios";
import { toast } from "sonner";

// =====================================================================
// TIPAGENS (ALINHADAS COM BACKEND)
// =====================================================================

export type StatusConvenio = "ATIVO" | "INATIVO";

export interface ConvenioResumo {
  id: number;
  nome: string;
  codigoIdentificacao: string;
  status: StatusConvenio;
}

export interface ConvenioDetalhes extends ConvenioResumo {
  historico: HistoricoEntradaResumo[];
}

export interface HistoricoEntradaResumo {
  descricao: string;
  dataHora: string;
  usuarioResponsavel: string;
}

// Payloads para operações
export interface CadastrarConvenioPayload {
  nome: string;
  codigoIdentificacao: string;
  responsavelId: number;
}

export interface AlterarNomePayload {
  novoNome: string;
  responsavelId: number;
}

export interface MudarStatusPayload {
  status: StatusConvenio;
  responsavelId: number;
}

export interface ExcluirPayload {
  responsavelId: number;
}

interface MutateVariables<T> {
  id: number;
  payload: T;
  temProcedimentoAtivo?: boolean;
}

interface MudarStatusVariables {
  id: number;
  payload: MudarStatusPayload;
  temProcedimentoAtivo?: boolean;
}

interface ExcluirVariables {
  codigoIdentificacao: string;
  payload: ExcluirPayload;
  temProcedimentoAtivo?: boolean;
}

// =====================================================================
// CONFIGURAÇÃO API
// =====================================================================

const API_BASE_URL = "/backend/convenios";

// Funções de API
const fetchConvenios = async (): Promise<ConvenioResumo[]> => {
  const { data } = await axios.get(API_BASE_URL);
  return data;
};

const fetchConvenioById = async (id: number): Promise<ConvenioDetalhes> => {
  const { data } = await axios.get(`${API_BASE_URL}/${id}`);
  return data;
};

const fetchConveniosByCodigo = async (codigoIdentificacao: string): Promise<ConvenioResumo[]> => {
  const { data } = await axios.get(`${API_BASE_URL}/codigo/${codigoIdentificacao}`);
  return data;
};

const fetchConveniosByStatus = async (status: StatusConvenio): Promise<ConvenioResumo[]> => {
  const { data } = await axios.get(`${API_BASE_URL}/status/${status}`);
  return data;
};

const createConvenio = async (payload: CadastrarConvenioPayload) => {
  await axios.post(API_BASE_URL, payload);
};

const alterarNomeConvenio = async ({ id, payload }: MutateVariables<AlterarNomePayload>) => {
  await axios.patch(`${API_BASE_URL}/${id}/nome`, payload);
};

const mudarStatusConvenio = async ({ 
  id, 
  payload, 
  temProcedimentoAtivo = false 
}: MudarStatusVariables) => {
  await axios.put(
    `${API_BASE_URL}/${id}?temProcedimentoAtivo=${temProcedimentoAtivo}`,
    payload
  );
};

const deleteConvenio = async ({ 
  codigoIdentificacao, 
  payload, 
  temProcedimentoAtivo = false 
}: ExcluirVariables) => {
  await axios.delete(
    `${API_BASE_URL}/${codigoIdentificacao}?temProcedimentoAtivo=${temProcedimentoAtivo}`,
    { data: payload }
  );
};

// =====================================================================
// HOOKS (REACT QUERY)
// =====================================================================

export function useListarConvenios() {
  return useQuery<ConvenioResumo[]>({
    queryKey: ["convenios"],
    queryFn: fetchConvenios,
    refetchOnWindowFocus: false,
  });
}

export function useObterConvenio(id: number | null) {
  return useQuery<ConvenioDetalhes>({
    queryKey: ["convenios", id],
    queryFn: () => fetchConvenioById(id!),
    enabled: !!id,
    refetchOnWindowFocus: false,
  });
}

export function useListarConveniosPorCodigo(codigoIdentificacao: string | null) {
  return useQuery<ConvenioResumo[]>({
    queryKey: ["convenios", "codigo", codigoIdentificacao],
    queryFn: () => fetchConveniosByCodigo(codigoIdentificacao!),
    enabled: !!codigoIdentificacao,
    refetchOnWindowFocus: false,
  });
}

export function useListarConveniosPorStatus(status: StatusConvenio | null) {
  return useQuery<ConvenioResumo[]>({
    queryKey: ["convenios", "status", status],
    queryFn: () => fetchConveniosByStatus(status!),
    enabled: !!status,
    refetchOnWindowFocus: false,
  });
}

export function useCadastrarConvenio() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: createConvenio,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["convenios"] });
      toast.success("Convênio cadastrado com sucesso!");
    },
    onError: (error: any) => {
      const mensagemErro = error.response?.data?.message || "Erro ao cadastrar convênio.";
      toast.error(mensagemErro);
    },
  });
}

export function useAlterarNomeConvenio() {
  const queryClient = useQueryClient();
  return useMutation<void, Error, MutateVariables<AlterarNomePayload>>({
    mutationFn: alterarNomeConvenio,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["convenios"] });
      toast.success("Nome do convênio alterado com sucesso!");
    },
    onError: (error: any) => {
      const mensagemErro = error.response?.data?.message || "Erro ao alterar nome do convênio.";
      toast.error(mensagemErro);
    },
  });
}

export function useMudarStatusConvenio() {
  const queryClient = useQueryClient();
  return useMutation<void, Error, MudarStatusVariables>({
    mutationFn: mudarStatusConvenio,
    onSuccess: (data, variables) => {
      queryClient.invalidateQueries({ queryKey: ["convenios"] });
      toast.success(`Status alterado para ${variables.payload.status.toLowerCase()} com sucesso!`);
    },
    onError: (error: any) => {
      const mensagemErro = error.response?.data?.message || "Erro ao mudar status do convênio.";
      toast.error(mensagemErro);
    },
  });
}

export function useExcluirConvenio() {
  const queryClient = useQueryClient();
  return useMutation<void, Error, ExcluirVariables>({
    mutationFn: deleteConvenio,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["convenios"] });
      toast.success("Convênio excluído com sucesso!");
    },
    onError: (error: any) => {
      const mensagemErro = error.response?.data?.message || "Erro ao excluir convênio.";
      toast.error(mensagemErro);
    },
  });
}
