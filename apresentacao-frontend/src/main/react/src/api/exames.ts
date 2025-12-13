import { api, request } from "./apiClient";
import type {
  ExameResponse,
  ExameDetalheResponse,
  AgendamentoExameRequest,
  AtualizacaoExameRequest,
  CancelamentoExameRequest,
} from "./types";

export const examesApi = {
  // listar aceita opcionalmente um filtro `status` (ex: 'AGENDADO', 'CANCELADO', 'PENDENTE', 'REALIZADO')
  listar: async (opts?: { status?: string }): Promise<ExameResponse[]> => {
    const config = opts && opts.status ? { params: { status: opts.status } } : undefined;
    return request<ExameResponse[]>(api.get(`/api/exames`, config));
  },

  obter: async (id: number): Promise<ExameDetalheResponse> =>
    request<ExameDetalheResponse>(api.get(`/api/exames/${id}`)),

  agendar: async (payload: AgendamentoExameRequest): Promise<ExameResponse> => {
    // Sanitização defensiva: converte arrays para primeiro elemento e garante IDs numéricos.
    const sanitize = (p: any) => {
      if (!p) return p;
      const out: any = {};
      Object.entries(p).forEach(([k, v]) => {
        let val = v;
        if (Array.isArray(val)) val = val[0];
        // valores de id podem chegar como string -> converter para number
        if (
          (k === "pacienteId" ||
            k === "medicoId" ||
            k === "responsavelId") &&
          val != null
        ) {
          const n = Number(val);
          val = Number.isNaN(n) ? val : n;
        }
        out[k] = val;
      });
      return out;
    };

    const sanitized = sanitize(payload);
    // Log para facilitar diagnóstico no browser (remova em produção se necessário)
    console.debug(
      "[examesApi] POST /api/exames payload (sanitized):",
      sanitized,
      " raw:",
      payload
    );

    return request<ExameResponse>(api.post(`/api/exames`, sanitized));
  },

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

  // Upload de resultado / anexo para um exame (multipart/form-data)
  uploadResultado: async (id: number, file: File): Promise<void> => {
    const form = new FormData();
    form.append('file', file);
    // endpoint esperado: POST /api/exames/{id}/anexos
    return request<void>(api.post(`/api/exames/${id}/anexos`, form, { headers: { 'Content-Type': 'multipart/form-data' } }));
  },

  // Alterar status do exame (ex.: PENDENTE, REALIZADO)
  mudarStatus: async (id: number, novoStatus: string, responsavelId: number, descricao?: string): Promise<ExameResponse> => {
    const payload = { novoStatus, responsavelId, descricao };
    // endpoint esperado: PATCH /api/exames/{id}/status
    return request<ExameResponse>(api.patch(`/api/exames/${id}/status`, payload));
  },
};