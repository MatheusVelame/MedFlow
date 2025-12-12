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
  contraindicacoes: string;
  status: 'ATIVO' | 'ARQUIVADO' | 'REVISAO_PENDENTE';
}

// Payload para Cadastro (POST)
export interface CadastrarMedicamentoPayload {
  nome: string;
  usoPrincipal: string;
  contraindicacoes: string;
  responsavelId: number; 
}

// Payload para Atualizar Uso Principal (PATCH)
export interface UsoPrincipalPayload {
  novoUsoPrincipal: string;
  responsavelId: number;
}

// Payload para Solicitar Revisão (PUT /revisao/solicitar)
export interface SolicitarRevisaoPayload {
  novaContraindicacao: string;
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

// --- QUERIES (Leitura) ---

const fetchMedicamentos = async (): Promise<MedicamentoResumo[]> => {
  // GET /backend/medicamentos
  const { data } = await axios.get(API_BASE_URL);
  return data;
};

// --- MUTATIONS (Escrita/Ação) ---

const createMedicamento = async (payload: CadastrarMedicamentoPayload) => {
  // POST /backend/medicamentos
  await axios.post(API_BASE_URL, payload);
};

const updateUsoPrincipalApi = async ({ id, payload }: MutateVariables<UsoPrincipalPayload>) => {
    // PATCH /backend/medicamentos/{id}/uso-principal
    await axios.patch(`${API_BASE_URL}/${id}/uso-principal`, payload);
};

const arquivarMedicamentoApi = async ({ id, payload }: MutateVariables<AcaoResponsavelPayload>) => {
    // PUT /backend/medicamentos/{id}/arquivar
    await axios.put(`${API_BASE_URL}/${id}/arquivar`, payload);
};

const solicitarRevisaoApi = async ({ id, payload }: MutateVariables<SolicitarRevisaoPayload>) => {
    // PUT /backend/medicamentos/{id}/revisao/solicitar
    await axios.put(`${API_BASE_URL}/${id}/revisao/solicitar`, payload);
};

const aprovarRevisaoApi = async ({ id, payload }: MutateVariables<AcaoResponsavelPayload>) => {
    // PUT /backend/medicamentos/{id}/revisao/aprovar
    await axios.put(`${API_BASE_URL}/${id}/revisao/aprovar`, payload);
};

const rejeitarRevisaoApi = async ({ id, payload }: MutateVariables<AcaoResponsavelPayload>) => {
    // PUT /backend/medicamentos/{id}/revisao/rejeitar
    await axios.put(`${API_BASE_URL}/${id}/revisao/rejeitar`, payload);
};


// =====================================================================
// HOOKS (REACT QUERY)
// =====================================================================

// Hook 1: Listagem de Medicamentos
export function useListarMedicamentos() {
  return useQuery<MedicamentoResumo[]>({
    queryKey: ['medicamentos'],
    queryFn: fetchMedicamentos,
    refetchOnWindowFocus: false,
  });
}

// Hook 2: Cadastro de Medicamentos
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

// Hook 4: Arquivar Medicamento
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

// [Outros hooks de mutação (useAtualizarUsoPrincipal, useSolicitarRevisao, useAprovarRevisao, useRejeitarRevisao) seriam incluídos aqui, seguindo o padrão useMutation<void, Error, MutateVariables<...>>]