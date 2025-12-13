import { useMutation, useQueryClient, useQuery } from "@tanstack/react-query";
import { examesApi } from "@/api";
import type { AgendamentoExameRequest, AtualizacaoExameRequest, CancelamentoExameRequest, ExameResponse } from "@/api/types";

export function useExamesList() {
  return useQuery<ExameResponse[]>(['exames'], () => examesApi.listar(), { refetchOnWindowFocus: false });
}

export function useExame(id?: number) {
  return useQuery<ExameResponse | undefined>(['exame', id], () => examesApi.obter(id as number), { enabled: !!id });
}

export function useAgendarExame() {
  const qc = useQueryClient();
  return useMutation((payload: AgendamentoExameRequest) => examesApi.agendar(payload), {
    onSuccess: (data) => {
      qc.setQueryData<ExameResponse[] | undefined>(['exames'], (old) => old ? [data, ...old] : [data]);
      qc.invalidateQueries({ queryKey: ['exames'] });
    },
  });
}

export function useAtualizarExame() {
  const qc = useQueryClient();
  return useMutation(({ id, payload }: { id: number; payload: AtualizacaoExameRequest }) => examesApi.atualizar(id, payload), {
    onSuccess: (updated) => {
      qc.setQueryData<ExameResponse[] | undefined>(['exames'], (old) => old ? old.map(e => e.id === updated.id ? updated : e) : [updated]);
      qc.invalidateQueries({ queryKey: ['exames'] });
    }
  });
}

export function useCancelarExame() {
  const qc = useQueryClient();
  return useMutation(({ id, payload }: { id: number; payload: CancelamentoExameRequest }) => examesApi.cancelar(id, payload), {
    onSuccess: (updated) => {
      qc.setQueryData<ExameResponse[] | undefined>(['exames'], (old) => old ? old.map(e => e.id === updated.id ? updated : e) : [updated]);
      qc.invalidateQueries({ queryKey: ['exames'] });
    }
  });
}

export function useExcluirExame() {
  const qc = useQueryClient();
  return useMutation(({ id, responsavelId }: { id: number; responsavelId: number }) => examesApi.excluir(id, responsavelId), {
    onSuccess: (_data, variables) => {
      qc.setQueryData<ExameResponse[] | undefined>(['exames'], (old) => old ? old.filter(e => e.id !== variables.id) : []);
      qc.invalidateQueries({ queryKey: ['exames'] });
    }
  });
}