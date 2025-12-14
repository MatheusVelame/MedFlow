// src/api/useMedicosApi.ts
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import axios from "axios";
import { toast } from "sonner";

// ============================
// Tipagens (backend / swagger)
// ============================

export type StatusMedico = "ATIVO" | "INATIVO";

export interface Disponibilidade {
diaSemana: string; // pode vir "SEGUNDA" etc
horaInicio: string; // "08:00"
horaFim: string; // "12:00"
}

export interface HistoricoEntradaMedico {
acao: string;
descricao: string;
responsavel: string;
dataHora: string; // ISO
}

export interface MedicoResumo {
id: string; // ✅ no backend o id vem como String no DTO
nome: string;
funcao: string;
contato: string;
status: StatusMedico;
crm: string;
especialidade: string;
consultasHoje: number;
proximaConsulta: string | null;
}

export interface MedicoDetalhes {
id: string;
nome: string;
funcao: string;
contato: string;
status: StatusMedico;
crm: string;
especialidade: string;

historico: HistoricoEntradaMedico[];
horariosDisponiveis: Disponibilidade[];

// ✅ “exists” no banco (não só hoje)
temConsultas: boolean;
temExames: boolean;
temProntuarios: boolean;

// (o backend também manda dataNascimento em alguns fluxos)
dataNascimento?: string | null;
}

// Payloads
export interface CadastrarMedicoPayload {
nome: string;
contato: string;
crmNumero: string;
crmUf: string;
especialidadeId: number;
dataNascimento?: string;
disponibilidades?: Disponibilidade[];
}

export interface AtualizarMedicoPayload {
nome?: string;
contato?: string;
dataNascimento?: string;
disponibilidades?: Disponibilidade[];
}

export interface DeleteMedicoResponse {
status: number;
mensagem: string;
}

interface MutateVariables<T> {
id: string;
payload: T;
}

// ============================
// API base
// ============================
const API_BASE_URL = "/api/medicos";

// ============================
// Calls
// ============================

const fetchMedicos = async (): Promise<MedicoResumo[]> => {
const { data } = await axios.get(API_BASE_URL);
return data;
};

const fetchMedicoById = async (id: string): Promise<MedicoDetalhes> => {
const { data } = await axios.get(`${API_BASE_URL}/${id}`);
return data;
};

const fetchMedicosByStatus = async (status: StatusMedico): Promise<MedicoResumo[]> => {
const { data } = await axios.get(`${API_BASE_URL}/status/${status}`);
return data;
};

const fetchMedicosByEspecialidade = async (especialidadeId: number): Promise<MedicoResumo[]> => {
const { data } = await axios.get(`${API_BASE_URL}/especialidade/${especialidadeId}`);
return data;
};

const fetchMedicoByCrm = async (crm: string): Promise<MedicoDetalhes> => {
const { data } = await axios.get(`${API_BASE_URL}/crm/${encodeURIComponent(crm)}`);
return data;
};

const buscarMedicos = async (termo: string): Promise<MedicoResumo[]> => {
const { data } = await axios.get(`${API_BASE_URL}/buscar`, { params: { termo } });
return data;
};

const createMedico = async (payload: CadastrarMedicoPayload) => {
await axios.post(API_BASE_URL, payload);
};

const updateMedico = async ({ id, payload }: MutateVariables<AtualizarMedicoPayload>) => {
  await axios.put(`${API_BASE_URL}/${id}`, payload);
};

const deleteMedico = async (id: string): Promise<DeleteMedicoResponse> => {
  const { data } = await axios.delete(`${API_BASE_URL}/${id}`);
  return data;
};

// ============================
// Hooks
// ============================

export function useListarMedicos() {
  return useQuery<MedicoResumo[]>({
    queryKey: ["medicos"],
    queryFn: fetchMedicos,
    refetchOnWindowFocus: true,
    staleTime: 0,
  });
}

export function useObterMedico(id: string | null) {
  return useQuery<MedicoDetalhes>({
    queryKey: ["medicos", "detalhes", id],
    queryFn: () => fetchMedicoById(id!),
    enabled: !!id,
    refetchOnWindowFocus: false,
  });
}

export function useListarMedicosPorStatus(status: StatusMedico | null) {
  return useQuery<MedicoResumo[]>({
    queryKey: ["medicos", "status", status],
    queryFn: () => fetchMedicosByStatus(status!),
    enabled: !!status,
    refetchOnWindowFocus: false,
  });
}

export function useListarMedicosPorEspecialidade(especialidadeId: number | null) {
  return useQuery<MedicoResumo[]>({
    queryKey: ["medicos", "especialidade", especialidadeId],
    queryFn: () => fetchMedicosByEspecialidade(especialidadeId!),
    enabled: especialidadeId !== null && especialidadeId !== undefined,
    refetchOnWindowFocus: false,
  });
}

export function useBuscarMedicoPorCrm(crm: string | null) {
  return useQuery<MedicoDetalhes>({
    queryKey: ["medicos", "crm", crm],
    queryFn: () => fetchMedicoByCrm(crm!),
    enabled: !!crm,
    refetchOnWindowFocus: false,
  });
}

export function useBuscarMedicos(termo: string) {
  const t = termo.trim();
  return useQuery<MedicoResumo[]>({
    queryKey: ["medicos", "buscar", t],
    queryFn: () => buscarMedicos(t),
    enabled: t.length > 0,
    refetchOnWindowFocus: false,
  });
}

// ============================
// Mutations
// ============================

export function useCadastrarMedico() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: createMedico,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["medicos"] });
      toast.success("Médico cadastrado com sucesso!");
    },
    onError: (error: any) => {
      toast.error(error?.response?.data?.message || "Erro ao cadastrar médico.");
    },
  });
}

export function useAtualizarMedico() {
  const queryClient = useQueryClient();
  return useMutation<void, Error, MutateVariables<AtualizarMedicoPayload>>({
    mutationFn: updateMedico,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["medicos"] });
      toast.success("Médico atualizado com sucesso!");
    },
    onError: (error: any) => {
      toast.error(error?.response?.data?.message || "Erro ao atualizar médico.");
    },
  });
}

export function useExcluirMedico() {
  const queryClient = useQueryClient();
  return useMutation<DeleteMedicoResponse, Error, string>({
    mutationFn: deleteMedico,
    onSuccess: (data) => {
      queryClient.invalidateQueries({ queryKey: ["medicos"] });
      toast.success(data?.mensagem || "Médico excluído com sucesso!");
    },
    onError: (error: any) => {
      toast.error(error?.response?.data?.message || "Erro ao excluir médico.");
    },
  });
}
