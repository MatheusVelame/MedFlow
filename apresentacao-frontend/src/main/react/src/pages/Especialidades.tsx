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

interface Especialidade {
  id: number;
  nome: string;
  descricao: string;
  ativa: boolean;
  medicosVinculados: number;
}

const initialEspecialidades: Especialidade[] = [
  { id: 1, nome: "Cardiologia", descricao: "Especialidade médica que trata do coração", ativa: true, medicosVinculados: 3 },
  { id: 2, nome: "Pediatria", descricao: "Especialidade médica dedicada à saúde infantil", ativa: true, medicosVinculados: 2 },
  { id: 3, nome: "Ortopedia", descricao: "Especialidade médica que trata do sistema musculoesquelético", ativa: true, medicosVinculados: 2 },
  { id: 4, nome: "Dermatologia", descricao: "Especialidade médica que trata da pele", ativa: true, medicosVinculados: 1 },
  { id: 5, nome: "Clínico Geral", descricao: "Atendimento médico generalista", ativa: true, medicosVinculados: 4 },
];

export default function Especialidades() {
  const [especialidades, setEspecialidades] = useState<Especialidade[]>(initialEspecialidades);
  const [isFormOpen, setIsFormOpen] = useState(false);
  const [editingEspecialidade, setEditingEspecialidade] = useState<Especialidade | null>(null);
  const [especialidadeToDelete, setEspecialidadeToDelete] = useState<number | null>(null);

  const handleSave = (data: any) => {
    if (editingEspecialidade) {
      setEspecialidades(especialidades.map(e => 
        e.id === editingEspecialidade.id ? { ...e, ...data } : e
      ));
      toast({
        title: "Especialidade atualizada",
        description: "As informações foram atualizadas com sucesso.",
      });
    } else {
      const newEspecialidade = {
        id: Math.max(...especialidades.map(e => e.id)) + 1,
        ...data,
        medicosVinculados: 0,
      };
      setEspecialidades([...especialidades, newEspecialidade]);
      toast({
        title: "Especialidade cadastrada",
        description: "Nova especialidade foi adicionada com sucesso.",
      });
    }
    setIsFormOpen(false);
    setEditingEspecialidade(null);
  };

  const handleEdit = (especialidade: Especialidade) => {
    setEditingEspecialidade(especialidade);
    setIsFormOpen(true);
  };

  const handleDelete = (id: number) => {
    setEspecialidades(especialidades.filter(e => e.id !== id));
    setEspecialidadeToDelete(null);
    toast({
      title: "Especialidade removida",
      description: "A especialidade foi removida com sucesso.",
    });
  };

  return (
    <div className="space-y-6 animate-fade-in">
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-3xl font-bold tracking-tight text-foreground">Especialidades Médicas</h1>
          <p className="text-muted-foreground">Gerencie as especialidades disponíveis na clínica</p>
        </div>
        <Button onClick={() => setIsFormOpen(true)} className="gap-2">
          <Plus className="h-4 w-4" />
          Nova Especialidade
        </Button>
      </div>

      <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
        {especialidades.map((especialidade) => (
          <Card key={especialidade.id} className="hover:shadow-lg transition-shadow">
            <CardHeader className="pb-3">
              <div className="flex items-start justify-between">
                <div className="flex items-center gap-3">
                  <div className="p-2 bg-primary/10 rounded-lg">
                    <Stethoscope className="h-5 w-5 text-primary" />
                  </div>
                  <div>
                    <CardTitle className="text-lg">{especialidade.nome}</CardTitle>
                    <Badge variant={especialidade.ativa ? "default" : "secondary"} className="mt-1">
                      {especialidade.ativa ? "Ativa" : "Inativa"}
                    </Badge>
                  </div>
                </div>
              </div>
            </CardHeader>
            <CardContent>
              <p className="text-sm text-muted-foreground mb-4">{especialidade.descricao}</p>
              
              <div className="flex items-center justify-between mb-4">
                <span className="text-sm text-muted-foreground">Médicos vinculados:</span>
                <Badge variant="outline">{especialidade.medicosVinculados}</Badge>
              </div>

              <div className="flex gap-2">
                <Button 
                  variant="outline" 
                  size="sm" 
                  className="flex-1"
                  onClick={() => handleEdit(especialidade)}
                >
                  <Edit className="h-4 w-4 mr-1" />
                  Editar
                </Button>
                <Button 
                  variant="outline" 
                  size="sm"
                  onClick={() => setEspecialidadeToDelete(especialidade.id)}
                >
                  <Trash2 className="h-4 w-4" />
                </Button>
              </div>
            </CardContent>
          </Card>
        ))}
      </div>

      <EspecialidadeForm
        open={isFormOpen}
        onOpenChange={setIsFormOpen}
        onSave={handleSave}
        initialData={editingEspecialidade}
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
