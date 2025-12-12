// Localização: apresentacao-frontend/src/main/react/src/api/useMedicamentosApi.ts

import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import axios from "axios";
import { toast } from "sonner"; 

// =====================================================================
// TIPAGEM DE DADOS (BASEADO NOS DTOs DO BACKEND)
// =====================================================================

export interface MedicamentoResumo {
  id: number;
  nome: string;
  usoPrincipal: string;
  contraindicacoes: string; // <-- CORRIGIDO/ADICIONADO
  status: 'ATIVO' | 'ARQUIVADO' | 'REVISAO_PENDENTE';
}

// Payload para Cadastro (POST)
export interface CadastrarMedicamentoPayload {
  nome: string;
  usoPrincipal: string;
  contraindicacoes: string;
  responsavelId: number; 
}

// Payload para Ações Simples de Responsável (Arquivar, Aprovar, Rejeitar)
export interface AcaoResponsavelPayload {
  responsavelId: number;
}

// Interfaces de Variáveis para Mutação: combina ID + Payload (para ações de PATCH/PUT)
interface MutateVariables<T> {
    id: number;
    payload: T;
}

// =====================================================================
// CONFIGURAÇÃO DA API (FUNÇÕES AXIOS)
// =====================================================================

const API_BASE_URL = "/backend/medicamentos"; 

const fetchMedicamentos = async (): Promise<MedicamentoResumo[]> => {
  const { data } = await axios.get(API_BASE_URL);
  return data;
};

const createMedicamento = async (payload: CadastrarMedicamentoPayload) => {
  await axios.post(API_BASE_URL, payload);
};

const arquivarMedicamentoApi = async ({ id, payload }: MutateVariables<AcaoResponsavelPayload>) => {
    await axios.put(`${API_BASE_URL}/${id}/arquivar`, payload);
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

export function useArquivarMedicamento() {
    const queryClient = useQueryClient();
    return useMutation<void, Error, MutateVariables<AcaoResponsavelPayload>>({ 
        mutationFn: arquivarMedicamentoApi,
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ['medicamentos'] });
            toast.success("Medicamento arquivado com sucesso!");
        },
        onError: (error) => {
            toast.error((error as any).response?.data?.message || "Erro ao arquivar medicamento.");
        },
    });
}