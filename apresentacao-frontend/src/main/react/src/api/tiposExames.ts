import { api, request } from "./apiClient";
import type { TipoExameResumo } from "./types";

export const tiposExamesApi = {
  listar: async (): Promise<TipoExameResumo[]> =>
    request<TipoExameResumo[]>(api.get(`/backend/tipos-exames`)),

  obter: async (id: number): Promise<TipoExameResumo> =>
    request<TipoExameResumo>(api.get(`/backend/tipos-exames/${id}`)),

  listarInativos: async (): Promise<TipoExameResumo[]> =>
    request<TipoExameResumo[]>(api.get(`/backend/tipos-exames/inativos`)),
};
