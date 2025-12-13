import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import axios from "axios";
import { toast } from "sonner";

// ================= HELPERS =================
const limparFormatacao = (valor: string) => valor.replace(/\D/g, "");

const formatarDataParaJava = (dataIso: string) => {
  if (!dataIso) return "";
  if (dataIso.includes("/")) return dataIso; 
  const [ano, mes, dia] = dataIso.split("-");
  return `${dia}/${mes}/${ano}`;
};


// ================= TIPAGENS =================
export interface PacienteApi {
  id: number;
  nome: string; 
  cpf: string;
  telefone: string;
  dataNascimento?: string; 
  endereco?: string;
}

export interface PacienteView {
  id: number;
  name: string;
  cpf: string;
  phone: string;
  birthDate?: string; 
  address?: string;   
}

export interface SalvarPacientePayload {
  nome: string;
  cpf: string;
  telefone: string;
  endereco: string;
  dataNascimento: string;
  responsavelId: number;
}

interface PacienteIdPayload {
  id: number;
  payload: SalvarPacientePayload;
}

// ================= CONFIGURAÇÃO =================
const API_BASE_URL = "/api/pacientes";

function mapApiToView(p: PacienteApi): PacienteView {
  return {
    id: p.id,
    name: p.nome,
    cpf: p.cpf,
    phone: p.telefone,
    birthDate: p.dataNascimento, 
    address: p.endereco
  };
}

// ================= QUERIES =================

// 1. LISTA (Resumo)
const fetchPacientes = async (): Promise<PacienteView[]> => {
  const response = await axios.get<PacienteApi[]>(API_BASE_URL);
  return response.data.map(mapApiToView);
};

export function useListarPacientes() {
  return useQuery({
    queryKey: ["pacientes"],
    queryFn: fetchPacientes,
    refetchOnWindowFocus: false,
  });
}

// 2. DETALHE (Completo) - ADICIONADO AGORA
const fetchPacientePorId = async (id: number): Promise<PacienteView> => {
  const response = await axios.get<PacienteApi>(`${API_BASE_URL}/${id}`);
  return mapApiToView(response.data);
};

export function useBuscarPacientePorId(id?: number) {
  return useQuery({
    queryKey: ["paciente", id],
    queryFn: () => fetchPacientePorId(id!),
    enabled: !!id, // Só busca se tiver ID selecionado
    staleTime: 0,  // Sempre pega dados frescos ao abrir edição
  });
}

// ================= MUTATIONS =================
const createPacienteApi = async (payload: SalvarPacientePayload) => {
  const payloadParaJava = {
    nome: payload.nome,
    cpf: limparFormatacao(payload.cpf),
    telefone: payload.telefone,
    responsavelId: payload.responsavelId,
    endereco: payload.endereco, 
    dataNascimento: formatarDataParaJava(payload.dataNascimento)
  };
  const response = await axios.post(API_BASE_URL, payloadParaJava);
  return response.data;
};

const updatePacienteApi = async ({ id, payload }: PacienteIdPayload) => {
  const payloadParaJava = {
    nome: payload.nome,
    cpf: limparFormatacao(payload.cpf),
    telefone: payload.telefone,
    responsavelId: payload.responsavelId,
    endereco: payload.endereco,
    dataNascimento: formatarDataParaJava(payload.dataNascimento)
  };
  const response = await axios.put(`${API_BASE_URL}/${id}`, payloadParaJava);
  return response.data;
};

const deletePacienteApi = async (id: number) => {
  await axios.delete(`${API_BASE_URL}/${id}?responsavelId=1&temProntuario=false&temConsulta=false&temExame=false`);
};

// --- HOOKS ---
export function useCadastrarPaciente() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: createPacienteApi,
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ["pacientes"] });
      toast.success("Paciente cadastrado!");
    },
    onError: (error: any) => {
      const msg = error.response?.data?.message || "Erro ao cadastrar.";
      toast.error(msg);
    },
  });
}

export function useAtualizarPaciente() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: updatePacienteApi,
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ["pacientes"] });
      // Invalida também o detalhe específico para atualizar o cache
      qc.invalidateQueries({ queryKey: ["paciente"] }); 
      toast.success("Atualizado!");
    },
    onError: (error: any) => {
      const msg = error.response?.data?.message || "Erro ao atualizar.";
      toast.error(msg);
    },
  });
}

export function useExcluirPaciente() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: deletePacienteApi,
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ["pacientes"] });
      toast.success("Excluído!");
    },
    onError: (error: any) => {
      const msg = error.response?.data?.message || "Erro ao excluir.";
      toast.error(msg);
    },
  });
}
