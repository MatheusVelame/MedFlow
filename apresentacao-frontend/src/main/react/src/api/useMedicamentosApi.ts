// Localização: apresentacao-frontend/src/main/react/src/api/useMedicamentosApi.ts

import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import axios, { AxiosError } from "axios"; // Adicionado AxiosError para tipagem correta
import { toast } from "sonner"; 

// =====================================================================
// TIPAGEM DE DADOS E PAYLOADS
// =====================================================================

export interface MedicamentoResumo {
  id: number;
  nome: string;
  usoPrincipal: string;
  contraindicacoes: string;
  // MODIFICAÇÃO: Adicionar 'INATIVO' ao tipo Status
  status: 'ATIVO' | 'INATIVO' | 'ARQUIVADO' | 'REVISAO_PENDENTE';
  // CORREÇÃO: Adicionar a propriedade booleana retornada pelo backend
  possuiRevisaoPendente: boolean; 
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

// NOVO TIPO: Variáveis para o comando de Mudar Status
interface MudarStatusMutateVariables {
    id: number; 
    payload: AcaoResponsavelPayload; // { responsavelId: number }
    // Status alvo pode ser ATIVO, INATIVO ou ARQUIVADO (o endpoint permite)
    novoStatus: 'ATIVO' | 'INATIVO' | 'ARQUIVADO'; 
    temPrescricaoAtiva: boolean; // Parâmetro de query exigido pelo endpoint Java
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

// NOVO: Função para buscar medicamentos com revisão pendente
const fetchMedicamentosComRevisaoPendente = async (): Promise<MedicamentoResumo[]> => {
  const { data } = await axios.get(`${API_BASE_URL}/revisao-pendente`); 
  return data;
};

const createMedicamento = async (payload: CadastrarMedicamentoPayload) => {
  return axios.post(API_BASE_URL, payload);
};

// CORREÇÃO: O endpoint /arquivar é um atalho que assume temPrescricaoAtiva=false por padrão
const arquivarMedicamentoApi = async ({ id, payload }: MutateVariables<AcaoResponsavelPayload>) => {
    // Definimos temPrescricaoAtiva=false como padrão no backend, mas é bom passar explicitamente
    await axios.put(`${API_BASE_URL}/${id}/arquivar?temPrescricaoAtiva=false`, payload);
};

// NOVO: Função para Mudar o Status (PUT /status/{novoStatus})
const mudarStatusApi = async ({ id, payload, novoStatus, temPrescricaoAtiva }: MudarStatusMutateVariables) => {
    // Chama o endpoint PUT /backend/medicamentos/{id}/status/{novoStatus}?temPrescricaoAtiva=...
    const url = `${API_BASE_URL}/${id}/status/${novoStatus}?temPrescricaoAtiva=${temPrescricaoAtiva}`;
    await axios.put(url, payload); 
};

const updateUsoPrincipalApi = async ({ id, payload }: MutateVariables<UsoPrincipalPayload>) => {
    // Mapeia para PATCH /backend/medicamentos/{id}/uso-principal
    await axios.patch(`${API_BASE_URL}/${id}/uso-principal`, payload);
};

const solicitarRevisaoApi = async ({ id, payload }: MutateVariables<SolicitarRevisaoPayload>) => {
    try {
        // Tenta fazer a requisição PUT. Se o backend retornar 204 (status de sucesso), resolve.
        await axios.put(`${API_BASE_URL}/${id}/revisao/solicitar`, payload);
    } catch (error) {
        const axiosError = error as AxiosError;
        
        // Verifica se é o erro esperado de "Sucesso Funcional" (400 Bad Request)
        if (axiosError.response && axiosError.response.status === 400) {
            const errorMessage = (axiosError.response.data as any)?.mensagem || (axiosError.response.data as any)?.message;
            
            // Verifica a mensagem de exceção de domínio (RevisaoPendenteException)
            if (errorMessage === "Alteração crítica exige revisão.") {
                // Se for o 400 esperado, não re-lançamos o erro. 
                // Isso faz com que a Promise resolva com sucesso (e o Mutation onSuccess será chamado).
                return; 
            }
        }
        
        // Se for um erro real (rede, 5xx, ou 4xx inesperado), re-lança
        throw error;
    }
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

// NOVO HOOK: Para listar medicamentos com revisão pendente
export function useListarMedicamentosComRevisaoPendente() {
  return useQuery<MedicamentoResumo[]>({
    queryKey: ['medicamentos', 'revisao-pendente'],
    queryFn: fetchMedicamentosComRevisaoPendente,
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

// NOVO HOOK: Para mudar status entre ATIVO e INATIVO
export function useMudarStatus() {
    const queryClient = useQueryClient();
    return useMutation<void, Error, MudarStatusMutateVariables>({
        mutationFn: mudarStatusApi,
        onSuccess: (data, variables) => {
            queryClient.invalidateQueries({ queryKey: ['medicamentos'] });
            toast.success(`Status alterado para ${variables.novoStatus.toLowerCase()} com sucesso.`);
        },
        onError: (error) => {
            toast.error((error as any).response?.data?.message || "Erro ao mudar o status do medicamento.");
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
            // Este onSuccess é chamado para 204 ou para o 400 que foi tratado como sucesso funcional na API.
            queryClient.invalidateQueries({ queryKey: ['medicamentos'] });
            // Mensagem atualizada para refletir o fluxo de revisão
            toast.success("Revisão de contraindicações solicitada e pendente de aprovação."); 
        },
        onError: (error) => {
            // Este onError agora só trata erros reais.
            toast.error((error as any).response?.data?.message || "Erro ao solicitar revisão. Verifique os dados.");
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