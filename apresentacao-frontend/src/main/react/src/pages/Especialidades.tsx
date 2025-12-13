import { useState } from "react";
import { Plus, Edit, Trash2, Stethoscope } from "lucide-react";
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
import { useEspecialidades, useCriarEspecialidade, useAtualizarEspecialidade, useExcluirEspecialidade } from "@/hooks/useEspecialidades";
import { useAuth } from "@/contexts/AuthContext";

interface Especialidade {
  id: number;
  nome: string;
  descricao: string;
  ativa?: boolean;
  medicosVinculados?: number;
}

export default function Especialidades() {
  const { data: especialidadesData, isLoading, isError } = useEspecialidades();
  const criar = useCriarEspecialidade();
  const atualizar = useAtualizarEspecialidade();
  const excluir = useExcluirEspecialidade();
  const { isGestor, isMedico } = useAuth();

  const [isFormOpen, setIsFormOpen] = useState(false);
  const [editingEspecialidade, setEditingEspecialidade] = useState<Especialidade | null>(null);
  const [especialidadeToDelete, setEspecialidadeToDelete] = useState<number | null>(null);

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

      {isLoading && <div>Carregando especialidades...</div>}
      {isError && <div>Erro ao carregar especialidades</div>}

      <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
        {especialidadesData?.map((especialidade) => (
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
              </div>
            </CardContent>
          </Card>
        ))}
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
    </div>
  );
}