import { api, request } from "./apiClient";
import type {
  ExameResponse,
  AgendamentoExameRequest,
  AtualizacaoExameRequest,
  CancelamentoExameRequest,
} from "./types";

export const examesApi = {
  listar: async (): Promise<ExameResponse[]> =>
    request<ExameResponse[]>(api.get(`/api/exames`)),

  obter: async (id: number): Promise<ExameResponse> =>
    request<ExameResponse>(api.get(`/api/exames/${id}`)),

  agendar: async (payload: AgendamentoExameRequest): Promise<ExameResponse> =>
    request<ExameResponse>(api.post(`/api/exames`, payload)),

  atualizar: async (
    id: number,
    payload: AtualizacaoExameRequest
  ): Promise<ExameResponse> =>
    request<ExameResponse>(api.put(`/api/exames/${id}`, payload)),

  excluir: async (id: number, responsavelId: number): Promise<void> =>
    request<void>(
      api.delete(`/api/exames/${id}`, { params: { responsavelId } })
    ),

  cancelar: async (
    id: number,
    payload: CancelamentoExameRequest
  ): Promise<ExameResponse> =>
    request<ExameResponse>(
      api.patch(`/api/exames/${id}/cancelamento`, payload)
    ),
};
