// Localização: apresentacao-frontend/src/main/react/src/api/useTiposExamesApi.ts

import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import axios from "axios";
import { toast } from "sonner"; 

// =====================================================================
// TIPAGEM DE DADOS 
// =====================================================================

export interface TipoExameResumo {
  id: number;
  codigo: string;
  descricao: string;
  especialidade: string;
  valor: number;
  status: 'ATIVO' | 'INATIVO'; 
}

// Payload para Cadastro (POST) e Edição (PUT)
export interface CadastrarTipoExamePayload {
  codigo: string;
  descricao: string;
  especialidade: string;
  valor: number;
  responsavelId: number; 
}

// Payload para Ações de Responsável (Ex: Inativar)
export interface AcaoResponsavelPayload {
  responsavelId: number;
}

// Interfaces de Variáveis para Mutação (ID + Payload)
interface MutateVariables<T> {
    id: number;
    payload: T;
}

// =====================================================================
// CONFIGURAÇÃO DA API (FUNÇÕES AXIOS)
// =====================================================================

const API_BASE_URL = "/backend/tipos-exames"; 

const fetchTiposExames = async (): Promise<TipoExameResumo[]> => {
  const { data } = await axios.get(API_BASE_URL);
  return data;
};

// --- NOVO: Função para buscar Inativos ---
const fetchTiposInativos = async (): Promise<TipoExameResumo[]> => {
  const { data } = await axios.get(`${API_BASE_URL}/inativos`);
  return data;
};

const createTipoExame = async (payload: CadastrarTipoExamePayload) => {
  await axios.post(API_BASE_URL, payload);
};

// --- Função para Editar (Ajustada para o seu Swagger) ---
const updateTipoExame = async ({ id, payload }: MutateVariables<CadastrarTipoExamePayload>) => {
    
    // 1. TRATAMENTO DO VALOR (Garante que é número 15.00)
    let valorTratado = payload.valor;
    if (typeof payload.valor === 'string') {
       const apenasNumeros = (payload.valor as string).replace(/[^\d,]/g, '');
       valorTratado = parseFloat(apenasNumeros.replace(',', '.'));
    }

    // Definindo um ID de responsável padrão caso não venha no payload
    const idResponsavel = payload.responsavelId || 1; 

    console.log("Enviando atualização com Responsável ID:", idResponsavel);

    await Promise.all([
        // 1. DESCRIÇÃO
        axios.patch(`${API_BASE_URL}/${id}/descricao`, 
            { 
                novaDescricao: payload.descricao,
                responsavelId: idResponsavel
            }, 
            { headers: { 'Content-Type': 'application/json' } }
        ),
        
        // 2. ESPECIALIDADE
        axios.patch(`${API_BASE_URL}/${id}/especialidade`, 
            { 
                novaEspecialidade: payload.especialidade,
                responsavelId: idResponsavel
            },
            { headers: { 'Content-Type': 'application/json' } }
        ),

        // 3. VALOR
        axios.patch(`${API_BASE_URL}/${id}/valor`, 
            { 
                novoValor: valorTratado,
                responsavelId: idResponsavel
            },
            { headers: { 'Content-Type': 'application/json' } }
        )
    ]);
};

const inativarTipoExameApi = async ({ id, payload }: MutateVariables<AcaoResponsavelPayload>) => {
    await axios.put(`${API_BASE_URL}/${id}/inativar`, payload);
};

// =====================================================================
// HOOKS (REACT QUERY)
// =====================================================================

export function useListarTiposExames() {
  return useQuery<TipoExameResumo[]>({
    queryKey: ['tipos-exames'], 
    queryFn: fetchTiposExames,
    refetchOnWindowFocus: false,
  });
}

// --- NOVO: Hook para listar Inativos ---
export function useListarTiposInativos() {
  return useQuery<TipoExameResumo[]>({
    queryKey: ['tipos-exames-inativos'],
    queryFn: fetchTiposInativos,
    refetchOnWindowFocus: false,
  });
}

export function useCadastrarTipoExame() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: createTipoExame,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['tipos-exames'] });
      toast.success("Tipo de exame cadastrado com sucesso!");
    },
    onError: (error) => {
      const mensagemErro = (error as any).response?.data?.message || "Erro ao cadastrar tipo de exame.";
      toast.error(mensagemErro);
    },
  });
}

export function useEditarTipoExame() {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: updateTipoExame,
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ['tipos-exames'] });
            toast.success("Tipo de exame atualizado com sucesso!");
        },
        onError: (error) => {
            const mensagemErro = (error as any).response?.data?.message || "Erro ao atualizar tipo de exame.";
            toast.error(mensagemErro);
        },
    });
}

export function useInativarTipoExame() {
    const queryClient = useQueryClient();
    return useMutation<void, Error, MutateVariables<AcaoResponsavelPayload>>({ 
        mutationFn: inativarTipoExameApi,
        onSuccess: () => {
            // Invalida as duas listas para atualizar tudo
            queryClient.invalidateQueries({ queryKey: ['tipos-exames'] });
            queryClient.invalidateQueries({ queryKey: ['tipos-exames-inativos'] });
            toast.success("Tipo de exame inativado com sucesso!");
        },
        onError: (error) => {
            toast.error((error as any).response?.data?.message || "Erro ao inativar exame.");
        },
    });
}