// Localização: apresentacao-frontend/src/main/react/src/api/useMedicamentosApi.ts

import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import axios from "axios";
import { toast } from "sonner"; 

// =====================================================================
// TIPAGEM DE DADOS E PAYLOADS
// =====================================================================

export interface MedicamentoResumo {
  id: number;
  nome: string;
  usoPrincipal: string;
  contraindicacoes: string;
  status: 'ATIVO' | 'ARQUIVADO' | 'REVISAO_PENDENTE';
}

// Payloads para DTOs do Backend
export interface CadastrarMedicamentoPayload {
  nome: string;
  usoPrincipal: string;
  contraindicacoes: string;
  responsavelId: number; 
}
export interface AcaoResponsavelPayload { responsavelId: number; }
export interface UsoPrincipalPayload { 
  novoUsoPrincipal: string; 
  responsavelId: number; 
}
export interface SolicitarRevisaoPayload {
  novaContraindicacao: string;
  responsavelId: number;
}
interface MutateVariables<T> { id: number; payload: T; }

// =====================================================================
// CONFIGURAÇÃO DA API (FUNÇÕES AXIOS)
// =====================================================================

const API_BASE_URL = "/backend/medicamentos"; 

// CORREÇÃO: Garante o retorno da Promise<MedicamentoResumo[]>
const fetchMedicamentos = async (): Promise<MedicamentoResumo[]> => {
  const { data } = await axios.get(API_BASE_URL);
  return data;
};

const createMedicamento = async (payload: CadastrarMedicamentoPayload) => {
  return axios.post(API_BASE_URL, payload);
};

// CORREÇÃO: Usamos 'await' e omitimos 'return' para tipar como Promise<void>
const arquivarMedicamentoApi = async ({ id, payload }: MutateVariables<AcaoResponsavelPayload>) => {
    await axios.put(`${API_BASE_URL}/${id}/arquivar`, payload);
};

const updateUsoPrincipalApi = async ({ id, payload }: MutateVariables<UsoPrincipalPayload>) => {
    // Mapeia para PATCH /backend/medicamentos/{id}/uso-principal
    await axios.patch(`${API_BASE_URL}/${id}/uso-principal`, payload);
};

const solicitarRevisaoApi = async ({ id, payload }: MutateVariables<SolicitarRevisaoPayload>) => {
    await axios.put(`${API_BASE_URL}/${id}/revisao/solicitar`, payload);
};

const aprovarRevisaoApi = async ({ id, payload }: MutateVariables<AcaoResponsavelPayload>) => {
    await axios.put(`${API_BASE_URL}/${id}/revisao/aprovar`, payload);
};

const rejeitarRevisaoApi = async ({ id, payload }: MutateVariables<AcaoResponsavelPayload>) => {
    await axios.put(`${API_BASE_URL}/${id}/revisao/rejeitar`, payload);
};


// =====================================================================
// HOOKS (REACT QUERY)
// =====================================================================

export function useListarMedicamentos() {
  return useQuery<MedicamentoResumo[]>({
    queryKey: ['medicamentos'],
    queryFn: fetchMedicamentos,
    refetchOnWindowFocus: false,
  });
}

export function useCadastrarMedicamento() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: createMedicamento,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['medicamentos'] });
      toast.success("Medicamento cadastrado com sucesso!");
    },
    onError: (error) => {
      const mensagemErro = (error as any).response?.data?.message || "Erro ao cadastrar medicamento.";
      toast.error(mensagemErro);
    },
  });
}

// HOOK DE EDIÇÃO DE USO PRINCIPAL (PATCH)
export function useAtualizarUsoPrincipal() {
    const queryClient = useQueryClient();
    return useMutation<void, Error, MutateVariables<UsoPrincipalPayload>>({ 
        mutationFn: updateUsoPrincipalApi,
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ['medicamentos'] });
            toast.success("Uso principal atualizado com sucesso.");
        },
        onError: (error) => {
            toast.error((error as any).response?.data?.message || "Erro ao atualizar uso principal.");
        },
    });
}

export function useArquivarMedicamento() {
    const queryClient = useQueryClient();
    return useMutation<void, Error, MutateVariables<AcaoResponsavelPayload>>({ 
        mutationFn: arquivarMedicamentoApi,
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ['medicamentos'] });
            toast.success("Medicamento arquivado com sucesso.");
        },
        onError: (error) => {
            toast.error((error as any).response?.data?.message || "Erro ao arquivar medicamento.");
        },
    });
}

export function useSolicitarRevisao() {
    const queryClient = useQueryClient();
    return useMutation<void, Error, MutateVariables<SolicitarRevisaoPayload>>({ 
        mutationFn: solicitarRevisaoApi,
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ['medicamentos'] });
            toast.success("Revisão de contraindicações solicitada.");
        },
        onError: (error) => {
            toast.error((error as any).response?.data?.message || "Erro ao solicitar revisão.");
        },
    });
}

export function useAprovarRevisao() {
    const queryClient = useQueryClient();
    return useMutation<void, Error, MutateVariables<AcaoResponsavelPayload>>({
        mutationFn: aprovarRevisaoApi,
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ['medicamentos'] });
            toast.success("Revisão aprovada e medicamento reativado.");
        },
        onError: (error) => {
            toast.error((error as any).response?.data?.message || "Erro ao aprovar revisão.");
        },
    });
}

export function useRejeitarRevisao() {
    const queryClient = useQueryClient();
    return useMutation<void, Error, MutateVariables<AcaoResponsavelPayload>>({
        mutationFn: rejeitarRevisaoApi,
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ['medicamentos'] });
            toast.success("Revisão rejeitada. Status mantido.");
        },
        onError: (error) => {
            toast.error((error as any).response?.data?.message || "Erro ao rejeitar revisão.");
        },
    });
}