// src/api/usePacientesApi.ts

import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import axios from "axios";
import { toast } from "sonner";

axios.defaults.baseURL = '';

// =====================================================================
// TIPAGENS (ALINHADAS COM SEUS DTOs REAIS)
// =====================================================================

// O que o backend retorna (PacienteResumoDTO)
export interface PacienteApi {
  id: number;
  nomeCompleto: string;
  cpf: string;
  dataNascimento: string;
  telefone: string;
  email: string;
  status: string;
  dataUltimaConsulta?: string | null;
}

// O que o frontend usa na tela
export interface PacienteView {
  id: number;
  name: string;
  cpf: string;
  birthDate: string;
  phone: string;
  email: string;
  status: string;
  lastVisit: string;
}

// Payload esperado pelo backend (CadastrarPacienteRequest / AtualizarPacienteRequest)
export interface SalvarPacientePayload {
  nomeCompleto: string;
  cpf: string;
  dataNascimento: string;
  telefone: string;
  email: string;
  status: string;
  responsavelId: number; // OBRIGATÓRIO no backend
}

interface PacienteIdPayload {
  id: number;
  payload: SalvarPacientePayload;
}

// =====================================================================
// CONFIGURAÇÃO API (USANDO PROXY DO VITE)
// =====================================================================

const API_BASE_URL = "/api/pacientes"; // Proxy redireciona para http://localhost:8080/api/pacientes

function mapApiToView(p: PacienteApi): PacienteView {
  return {
    id: p.id,
    name: p.nomeCompleto,
    cpf: p.cpf,
    birthDate: p.dataNascimento,
    phone: p.telefone,
    email: p.email,
    status: p.status,
    lastVisit: p.dataUltimaConsulta ?? "-", // caso venha null
  };
}

// =====================================================================
// QUERIES
// =====================================================================

const fetchPacientes = async (): Promise<PacienteView[]> => {
  console.log('🔍 Buscando pacientes em:', API_BASE_URL);
  const response = await axios.get<PacienteApi[]>(API_BASE_URL);
  console.log('✅ Resposta do backend:', response.data);
  return response.data.map(mapApiToView);
};

export function useListarPacientes() {
  return useQuery({
    queryKey: ["pacientes"],
    queryFn: fetchPacientes,
    refetchOnWindowFocus: false,
  });
}

// =====================================================================
// MUTATIONS
// =====================================================================

const createPacienteApi = async (payload: SalvarPacientePayload) => {
  await axios.post(API_BASE_URL, payload);
};

const updatePacienteApi = async ({ id, payload }: PacienteIdPayload) => {
  await axios.put(`${API_BASE_URL}/${id}`, payload); // CORRIGIDO: template literal
};

const deletePacienteApi = async (id: number) => {
  await axios.delete(`${API_BASE_URL}/${id}`); // CORRIGIDO: template literal
};

export function useCadastrarPaciente() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: createPacienteApi,
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ["pacientes"] });
      toast.success("Paciente cadastrado com sucesso!");
    },
    onError: () => {
      toast.error("Erro ao cadastrar paciente.");
    },
  });
}

export function useAtualizarPaciente() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: updatePacienteApi,
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ["pacientes"] });
      toast.success("Paciente atualizado com sucesso!");
    },
    onError: () => {
      toast.error("Erro ao atualizar paciente.");
    },
  });
}

export function useExcluirPaciente() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: deletePacienteApi,
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ["pacientes"] });
      toast.success("Paciente excluído com sucesso!");
    },
    onError: () => {
      toast.error("Erro ao excluir paciente.");
    },
  });
}
