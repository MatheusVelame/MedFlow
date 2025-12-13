import { api, request } from "./apiClient";
import type { EspecialidadeResumo, EspecialidadeDetalhes } from "./types";

export const especialidadesApi = {
  listar: async (): Promise<EspecialidadeResumo[]> =>
    request<EspecialidadeResumo[]>(api.get(`/api/referencia/especialidades`)),

  obter: async (id: number): Promise<EspecialidadeDetalhes> =>
    request<EspecialidadeDetalhes>(api.get(`/api/referencia/especialidades/${id}`)),

  criar: async (payload: { nome: string; descricao?: string | null }): Promise<EspecialidadeResumo> =>
    request<EspecialidadeResumo>(api.post(`/api/referencia/especialidades`, payload)),

  atualizar: async (
    id: number,
    payload: { novoNome?: string | null; novaDescricao?: string | null }
  ): Promise<EspecialidadeResumo> =>
    request<EspecialidadeResumo>(api.patch(`/api/referencia/especialidades/${id}`, payload)),

  excluir: async (id: number): Promise<void> =>
    request<void>(api.delete(`/api/referencia/especialidades/${id}`)),

  historico: async (id: number): Promise<any[]> =>
    request<any[]>(api.get(`/referencia/especialidades/${id}/historico`)),

  // New: toggle status (activate / inactivate)
  toggleStatus: async (id: number, payload: { responsavelId?: number }) =>
    request(api.put(`/api/referencia/especialidades/${id}/inativar`, payload)),
};