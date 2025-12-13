import { useState, useMemo, useEffect, useRef } from "react";
import { Plus, Edit, Trash2, Stethoscope, List, History } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
} from "@/components/ui/alert-dialog";
import { EspecialidadeForm } from "@/components/EspecialidadeForm";
import { toast } from "@/hooks/use-toast";
import { useEspecialidades, useCriarEspecialidade, useAtualizarEspecialidade, useExcluirEspecialidade, useHistoricoEspecialidade, useToggleStatusEspecialidade } from "@/hooks/useEspecialidades";
import { useAuth } from "@/contexts/AuthContext";

interface Especialidade {
  id: number;
  nome: string;
  descricao: string;
  ativa?: boolean;
  medicosVinculados?: number;
  status?: string;
}

export default function Especialidades() {
  const { data: especialidadesData, isLoading, isError } = useEspecialidades();
  const criar = useCriarEspecialidade();
  const atualizar = useAtualizarEspecialidade();
  const excluir = useExcluirEspecialidade();
  const toggleStatus = useToggleStatusEspecialidade();
  const { isGestor, isMedico } = useAuth();

  const [isFormOpen, setIsFormOpen] = useState(false);
  const [editingEspecialidade, setEditingEspecialidade] = useState<Especialidade | null>(null);
  const [especialidadeToDelete, setEspecialidadeToDelete] = useState<number | null>(null);
  const [historicoOpenFor, setHistoricoOpenFor] = useState<number | null>(null);
  const [togglingId, setTogglingId] = useState<number | null>(null);
  const closeHistoricoBtnRef = useRef<HTMLButtonElement | null>(null);

  const { data: historicoItems, refetch: refetchHistorico } = useHistoricoEspecialidade(historicoOpenFor ?? undefined);

  useEffect(() => {
    if (historicoOpenFor != null) {
      refetchHistorico();
      // give modal time to render then focus close button to avoid aria-hidden focus issues
      setTimeout(() => { closeHistoricoBtnRef.current?.focus(); }, 100);
    }
  }, [historicoOpenFor, refetchHistorico]);

  // UI state for filtering, sorting and pagination
  const [statusFilter, setStatusFilter] = useState<"ATIVA" | "INATIVA" | "TODAS">("ATIVA"); // default: only active
  const [sortAsc, setSortAsc] = useState(true);
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(9);

  const handleSave = (data: any) => {
    if (editingEspecialidade) {
      return atualizar.mutateAsync({ id: editingEspecialidade.id, payload: { novoNome: data.nome, novaDescricao: data.descricao } })
        .then(() => { setIsFormOpen(false); setEditingEspecialidade(null); });
    }

    return criar.mutateAsync({ nome: data.nome, descricao: data.descricao })
      .then(() => { setIsFormOpen(false); });
  };

  const handleEdit = (especialidade: Especialidade) => {
    setEditingEspecialidade(especialidade);
    setIsFormOpen(true);
  };

  const handleDelete = async (id: number) => {
    try {
      await excluir.mutateAsync(id);
      setEspecialidadeToDelete(null);
      toast({ title: "Especialidade removida", description: "A especialidade foi removida com sucesso." });
    } catch (e: any) {
      toast({ title: "Erro ao remover", description: e?.message ?? "Não foi possível remover a especialidade" });
    }
  };

  const openHistorico = (id: number | null) => {
    try {
      const active = document.activeElement as HTMLElement | null;
      if (active && typeof active.blur === 'function') {
        active.blur();
      }
    } catch (e) {
      // ignore
    }
    setHistoricoOpenFor(id);
  };

  // Updated: use togglingId to disable per-button while mutation runs
  const handleToggleStatus = async (id: number) => {
    try {
      setTogglingId(id);
      const responsavelId = Number((window as any)._currentUserId ?? 1);
      await toggleStatus.mutateAsync({ id, payload: { responsavelId } });
      toast({ title: 'Status atualizado', description: 'Status da especialidade atualizado.' });
    } catch (err: any) {
      const serverMsg = err?.response?.data?.message || err?.message;
      toast({ title: 'Erro ao alterar status', description: serverMsg ?? 'Não foi possível alterar o status', variant: 'destructive' });
    } finally {
      setTogglingId(null);
    }
  };

  // Derived list: filter -> sort -> paginate
  const processedEspecialidades = useMemo(() => {
    if (!especialidadesData) return [] as Especialidade[];

    let items = [...especialidadesData] as Especialidade[];

    // filter by status
    if (statusFilter === "ATIVA") {
      items = items.filter((s) => (s.status ?? "").toUpperCase() === "ATIVA");
    } else if (statusFilter === "INATIVA") {
      items = items.filter((s) => (s.status ?? "").toUpperCase() === "INATIVA");
    }

    // sort by name alphabetically
    items.sort((a, b) => {
      const an = (a.nome ?? "").toLowerCase();
      const bn = (b.nome ?? "").toLowerCase();
      return sortAsc ? an.localeCompare(bn) : bn.localeCompare(an);
    });

    return items;
  }, [especialidadesData, statusFilter, sortAsc]);

  const totalItems = processedEspecialidades.length;
  const totalPages = Math.max(1, Math.ceil(totalItems / pageSize));

  // ensure page is within bounds when data / pageSize / filter change
  if (page > totalPages) setPage(totalPages);

  const pagedEspecialidades = useMemo(() => {
    const start = (page - 1) * pageSize;
    return processedEspecialidades.slice(start, start + pageSize);
  }, [processedEspecialidades, page, pageSize]);

  // reset page when filters or pageSize change
  const handleStatusChange = (value: "ATIVA" | "INATIVA" | "TODAS") => { setStatusFilter(value); setPage(1); };
  const handleSortToggle = () => { setSortAsc((s) => !s); setPage(1); };
  const handlePageSizeChange = (n: number) => { setPageSize(n); setPage(1); };

  return (
    <div className="space-y-6 animate-fade-in">
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-3xl font-bold tracking-tight text-foreground">Especialidades Médicas</h1>
          <p className="text-muted-foreground">Gerencie as especialidades disponíveis na clínica</p>
        </div>
        {isGestor && (
          <Button onClick={() => { setIsFormOpen(true); setEditingEspecialidade(null); }} className="gap-2">
            <Plus className="h-4 w-4" />
            Nova Especialidade
          </Button>
        )}
      </div>

      {/* Filters / sorting / pagination controls */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-3">
        <div className="flex items-center gap-2">
          <label className="text-sm text-muted-foreground">Status:</label>
          <select
            value={statusFilter}
            onChange={(e) => handleStatusChange(e.target.value as any)}
            className="border rounded-md px-2 py-1 text-sm"
          >
            <option value="ATIVA">Ativas</option>
            <option value="INATIVA">Inativas</option>
            <option value="TODAS">Todas</option>
          </select>

          <button
            onClick={handleSortToggle}
            className="ml-3 inline-flex items-center gap-2 text-sm px-3 py-1 rounded-md border"
            title={sortAsc ? "Ordenar A → Z" : "Ordenar Z → A"}
          >
            Ordenar: {sortAsc ? "A → Z" : "Z → A"}
          </button>
        </div>

        <div className="flex items-center gap-2">
          <label className="text-sm text-muted-foreground">Itens por página:</label>
          <select
            value={pageSize}
            onChange={(e) => handlePageSizeChange(Number(e.target.value))}
            className="border rounded-md px-2 py-1 text-sm"
          >
            <option value={6}>6</option>
            <option value={9}>9</option>
            <option value={12}>12</option>
          </select>

          <div className="text-sm text-muted-foreground ml-4">
            {totalItems} resultado{totalItems !== 1 ? "s" : ""}
          </div>
        </div>
      </div>

      {isLoading && <div>Carregando especialidades...</div>}
      {isError && <div>Erro ao carregar especialidades</div>}

      <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
        {pagedEspecialidades?.map((especialidade) => (
          <Card key={especialidade.id} className="hover:shadow-lg transition-shadow">
            <CardHeader className="pb-3">
              <div className="flex items-start justify-between">
                <div className="flex items-center gap-3">
                  <div className="p-2 bg-primary/10 rounded-lg">
                    <Stethoscope className="h-5 w-5 text-primary" />
                  </div>
                  <div>
                    <CardTitle className="text-lg">{especialidade.nome}</CardTitle>
                    <Badge variant={especialidade.status === 'ATIVA' ? "default" : "secondary"} className="mt-1">
                      {especialidade.status}
                    </Badge>
                  </div>
                </div>
              </div>
            </CardHeader>
            <CardContent>
              <p className="text-sm text-muted-foreground mb-4">{especialidade.descricao}</p>

              <div className="flex items-center justify-between mb-4">
                <span className="text-sm text-muted-foreground">Médicos vinculados:</span>
                <Badge variant="outline">{especialidade.medicosVinculados ?? 0}</Badge>
              </div>

              <div className="flex gap-2">
                {(isGestor || isMedico) && (
                  <Button
                    variant="outline"
                    size="sm"
                    className="flex-1"
                    onClick={() => handleEdit(especialidade as Especialidade)}
                  >
                    <Edit className="h-4 w-4 mr-1" />
                    Editar
                  </Button>
                )}
                {isGestor && (
                  <Button
                    variant="outline"
                    size="sm"
                    onClick={() => setEspecialidadeToDelete(especialidade.id)}
                  >
                    <Trash2 className="h-4 w-4" />
                  </Button>
                )}
                {isGestor && (
                  <Button
                    variant="outline"
                    size="sm"
                    className="flex-1"
                    onClick={() => openHistorico(especialidade.id)}
                  >
                    <History className="h-4 w-4" />
                  </Button>
                )}
                {/* New: toggle status button visible to gestor */}
                {isGestor && (
                  <Button
                    variant="outline"
                    size="sm"
                    className="flex-1"
                    onClick={() => handleToggleStatus(especialidade.id)}
                    disabled={togglingId === especialidade.id}
                  >
                    <List className="h-4 w-4 mr-1" />
                    { (especialidade.status === "ATIVA" || especialidade.status === "Ativa") ? "Inativar" : "Ativar" }
                  </Button>
                )}
              </div>
            </CardContent>
          </Card>
        ))}
      </div>

      {/* Pagination controls */}
      <div className="flex items-center justify-between mt-4">
        <div className="flex items-center gap-2">
          <Button variant="outline" size="sm" onClick={() => setPage((p) => Math.max(1, p - 1))} disabled={page <= 1}>
            Anterior
          </Button>
          <div className="text-sm text-muted-foreground">Página {page} de {totalPages}</div>
          <Button variant="outline" size="sm" onClick={() => setPage((p) => Math.min(totalPages, p + 1))} disabled={page >= totalPages}>
            Próxima
          </Button>
        </div>

        <div className="text-sm text-muted-foreground">Exibindo {(page - 1) * pageSize + 1}–{Math.min(page * pageSize, totalItems)} de {totalItems}</div>
      </div>

      <EspecialidadeForm
        open={isFormOpen}
        onOpenChange={setIsFormOpen}
        onSave={handleSave}
        initialData={editingEspecialidade ?? undefined}
      />

      <AlertDialog open={!!especialidadeToDelete} onOpenChange={() => setEspecialidadeToDelete(null)}>
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle>Confirmar exclusão</AlertDialogTitle>
            <AlertDialogDescription>
              Tem certeza que deseja remover esta especialidade? Esta ação não pode ser desfeita.
            </AlertDialogDescription>
          </AlertDialogHeader>
          <AlertDialogFooter>
            <AlertDialogCancel>Cancelar</AlertDialogCancel>
            <AlertDialogAction onClick={() => especialidadeToDelete && handleDelete(especialidadeToDelete)}>
              Confirmar
            </AlertDialogAction>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>

      <AlertDialog open={historicoOpenFor !== null} onOpenChange={(open) => { if (!open) setHistoricoOpenFor(null); }}>
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle>Histórico da Especialidade</AlertDialogTitle>
            <AlertDialogDescription>Registros de alterações e operações sobre esta especialidade.</AlertDialogDescription>
          </AlertDialogHeader>
          <div className="px-6 pb-4">
            {historicoItems == null ? (
              <div className="py-6"></div>
            ) : !Array.isArray(historicoItems) ? (
              <div className="py-6 text-sm text-muted-foreground">Histórico indisponível.</div>
            ) : historicoItems.length === 0 ? (
              <div className="py-6">Nenhum histórico encontrado para esta especialidade.</div>
            ) : (
              <ul className="space-y-2">
                {historicoItems.map((h: any, idx: number) => (
                  <li key={idx} className="p-2 rounded bg-muted/5">
                    <div className="text-xs text-muted-foreground">{new Date(h.dataHora).toLocaleString()} — <span className="font-medium">{h.tipo || h.acao || h.campo}</span></div>
                    <div className="mt-1">{h.campo ? `${h.campo}: ${h.valorAnterior} -> ${h.novoValor}` : h.descricao || JSON.stringify(h)}</div>
                  </li>
                ))}
              </ul>
            )}
            <div className="pt-4 text-right">
              <Button ref={closeHistoricoBtnRef} size="sm" onClick={() => setHistoricoOpenFor(null)}>Fechar</Button>
            </div>
          </div>
        </AlertDialogContent>
      </AlertDialog>
    </div>
  );
}
