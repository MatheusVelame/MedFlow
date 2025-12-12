import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { especialidadesApi } from '@/api';
import type { EspecialidadeResumo } from '@/api/types';

export function useEspecialidades() {
  return useQuery(['especialidades'], () => especialidadesApi.listar());
}

export function useEspecialidade(id?: number) {
  return useQuery(['especialidade', id], () => especialidadesApi.obter(id as number), { enabled: !!id });
}

export function useCriarEspecialidade() {
  const qc = useQueryClient();
  return useMutation((payload: { nome: string; descricao?: string | null }) => especialidadesApi.criar(payload), {
    onSuccess: () => qc.invalidateQueries(['especialidades']),
  });
}

export function useAtualizarEspecialidade() {
  const qc = useQueryClient();
  return useMutation(({ id, payload }: { id: number; payload: { novoNome?: string | null; novaDescricao?: string | null } }) => especialidadesApi.atualizar(id, payload), {
    onSuccess: () => qc.invalidateQueries(['especialidades']),
  });
}

export function useExcluirEspecialidade() {
  const qc = useQueryClient();
  return useMutation((id: number) => especialidadesApi.excluir(id), {
    onSuccess: () => qc.invalidateQueries(['especialidades']),
  });
}
