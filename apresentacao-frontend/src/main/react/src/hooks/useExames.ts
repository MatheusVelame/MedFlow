import { useMutation, useQueryClient, useQuery } from "@tanstack/react-query";
import { examesApi } from "@/api";
import type {
  AgendamentoExameRequest,
  AtualizacaoExameRequest,
  CancelamentoExameRequest,
  ExameResponse,
} from "@/api/types";

export function useExamesList(status?: string | string[]) {
  const queryKey = Array.isArray(status) ? ["exames", ...status] : (status ? ["exames", status] : ["exames"]);

  return useQuery<ExameResponse[]>({
    queryKey,
    queryFn: async () => {
      if (!status) return examesApi.listar();
      if (Array.isArray(status)) {
        // buscar múltiplos status e concatenar (evitar duplicados)
        const results = await Promise.all(status.map(s => examesApi.listar({ status: s })).map(p => p.catch(() => [])));
        const merged = ([] as ExameResponse[]).concat(...results);
        // deduplicate by id
        const byId = new Map<number, ExameResponse>();
        merged.forEach(r => byId.set(r.id, r));
        return Array.from(byId.values());
      }
      return examesApi.listar({ status });
    },
    refetchOnWindowFocus: false,
  });
}

export function useExame(id?: number) {
  return useQuery<ExameResponse | undefined>({
    queryKey: ["exame", id],
    queryFn: () => examesApi.obter(id as number),
    enabled: !!id,
  });
}

export function useAgendarExame() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (payload: AgendamentoExameRequest) =>
      examesApi.agendar(payload),
    onSuccess: (data) => {
      qc.setQueryData<ExameResponse[] | undefined>(["exames"], (old) =>
        old ? [data, ...old] : [data]
      );
      // invalidar todas as queries que começam por 'exames' (status variantes)
      qc.invalidateQueries({ queryKey: ["exames"], exact: false });
    },
  });
}

export function useAtualizarExame() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: ({
      id,
      payload,
    }: {
      id: number;
      payload: AtualizacaoExameRequest;
    }) => examesApi.atualizar(id, payload),
    onSuccess: (updated) => {
      qc.setQueryData<ExameResponse[] | undefined>(["exames"], (old) =>
        old ? old.map((e) => (e.id === updated.id ? updated : e)) : [updated]
      );
      qc.invalidateQueries({ queryKey: ["exames"], exact: false });
    },
  });
}

export function useCancelarExame() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: ({
      id,
      payload,
    }: {
      id: number;
      payload: CancelamentoExameRequest;
    }) => examesApi.cancelar(id, payload),
    onSuccess: (updated) => {
      qc.setQueryData<ExameResponse[] | undefined>(["exames"], (old) =>
        old ? old.map((e) => (e.id === updated.id ? updated : e)) : [updated]
      );
      qc.invalidateQueries({ queryKey: ["exames"], exact: false });
    },
  });
}

export function useRegistrarResultado() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: ({ id, payload }: { id: number; payload: { descricao?: string; vincularLaudo?: boolean; vincularProntuario?: boolean; responsavelId: number } }) => examesApi.registrarResultado(id, payload),
    onSuccess: (updated) => {
      qc.setQueryData<ExameResponse[] | undefined>(["exames"], (old) =>
        old ? old.map((e) => (e.id === updated.id ? updated : e)) : [updated]
      );
      qc.invalidateQueries({ queryKey: ["exames"], exact: false });
    }
  });
}

export function useExcluirExame() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: ({ id, responsavelId }: { id: number; responsavelId: number }) => examesApi.excluir(id, responsavelId),
    onSuccess: () => {
      // invalidate exames queries so UI refreshes from server
      qc.invalidateQueries({ queryKey: ["exames"], exact: false });
    }
  });
}