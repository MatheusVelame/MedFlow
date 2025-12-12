import { useState } from "react";
import { Plus, Edit, Trash2, CreditCard } from "lucide-react";
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
import { ConvenioForm } from "@/components/ConvenioForm";
import { toast } from "@/hooks/use-toast";
import { useAuth } from "@/contexts/AuthContext";

interface Convenio {
  id: number;
  nome: string;
  cnpj: string;
  telefone: string;
  email: string;
  valorConsulta: number;
  ativo: boolean;
  pacientesVinculados: number;
}

const initialConvenios: Convenio[] = [
  { 
    id: 1, 
    nome: "Unimed", 
    cnpj: "12.345.678/0001-90", 
    telefone: "(11) 3000-0000",
    email: "atendimento@unimed.com.br",
    valorConsulta: 150.00,
    ativo: true, 
    pacientesVinculados: 45 
  },
  { 
    id: 2, 
    nome: "Bradesco Saúde", 
    cnpj: "23.456.789/0001-01", 
    telefone: "(11) 3001-0000",
    email: "saude@bradesco.com.br",
    valorConsulta: 180.00,
    ativo: true, 
    pacientesVinculados: 32 
  },
  { 
    id: 3, 
    nome: "Amil", 
    cnpj: "34.567.890/0001-12", 
    telefone: "(11) 3002-0000",
    email: "contato@amil.com.br",
    valorConsulta: 200.00,
    ativo: true, 
    pacientesVinculados: 28 
  },
  { 
    id: 4, 
    nome: "SulAmérica", 
    cnpj: "45.678.901/0001-23", 
    telefone: "(11) 3003-0000",
    email: "saude@sulamerica.com.br",
    valorConsulta: 170.00,
    ativo: true, 
    pacientesVinculados: 21 
  },
];

export default function Convenios() {
  const { isGestor } = useAuth();
  const [convenios, setConvenios] = useState<Convenio[]>(initialConvenios);
  const [isFormOpen, setIsFormOpen] = useState(false);
  const [editingConvenio, setEditingConvenio] = useState<Convenio | null>(null);
  const [convenioToDelete, setConvenioToDelete] = useState<number | null>(null);

  const handleSave = (data: any) => {
    if (editingConvenio) {
      setConvenios(convenios.map(c => 
        c.id === editingConvenio.id ? { ...c, ...data } : c
      ));
      toast({
        title: "Convênio atualizado",
        description: "As informações foram atualizadas com sucesso.",
      });
    } else {
      const newConvenio = {
        id: Math.max(...convenios.map(c => c.id)) + 1,
        ...data,
        pacientesVinculados: 0,
      };
      setConvenios([...convenios, newConvenio]);
      toast({
        title: "Convênio cadastrado",
        description: "Novo convênio foi adicionado com sucesso.",
      });
    }
    setIsFormOpen(false);
    setEditingConvenio(null);
  };

  const handleEdit = (convenio: Convenio) => {
    setEditingConvenio(convenio);
    setIsFormOpen(true);
  };

  const handleDelete = (id: number) => {
    setConvenios(convenios.filter(c => c.id !== id));
    setConvenioToDelete(null);
    toast({
      title: "Convênio removido",
      description: "O convênio foi removido com sucesso.",
    });
  };

  return (
    <div className="space-y-6 animate-fade-in">
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-3xl font-bold tracking-tight text-foreground">Convênios</h1>
          <p className="text-muted-foreground">Gerencie os planos de saúde aceitos pela clínica</p>
        </div>
        {isGestor && (
          <Button onClick={() => setIsFormOpen(true)} className="gap-2">
            <Plus className="h-4 w-4" />
            Novo Convênio
          </Button>
        )}
      </div>

      <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
        {convenios.map((convenio) => (
          <Card key={convenio.id} className="hover:shadow-lg transition-shadow">
            <CardHeader className="pb-3">
              <div className="flex items-start justify-between">
                <div className="flex items-center gap-3">
                  <div className="p-2 bg-primary/10 rounded-lg">
                    <CreditCard className="h-5 w-5 text-primary" />
                  </div>
                  <div>
                    <CardTitle className="text-lg">{convenio.nome}</CardTitle>
                    <Badge variant={convenio.ativo ? "default" : "secondary"} className="mt-1">
                      {convenio.ativo ? "Ativo" : "Inativo"}
                    </Badge>
                  </div>
                </div>
              </div>
            </CardHeader>
            <CardContent>
              <div className="space-y-2 mb-4">
                <div className="flex justify-between text-sm">
                  <span className="text-muted-foreground">CNPJ:</span>
                  <span className="font-medium">{convenio.cnpj}</span>
                </div>
                <div className="flex justify-between text-sm">
                  <span className="text-muted-foreground">Telefone:</span>
                  <span className="font-medium">{convenio.telefone}</span>
                </div>
                <div className="flex justify-between text-sm">
                  <span className="text-muted-foreground">Email:</span>
                  <span className="font-medium text-xs">{convenio.email}</span>
                </div>
                <div className="flex justify-between text-sm pt-2 border-t">
                  <span className="text-muted-foreground">Valor Consulta:</span>
                  <span className="font-bold text-primary">
                    {convenio.valorConsulta.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })}
                  </span>
                </div>
                <div className="flex justify-between text-sm">
                  <span className="text-muted-foreground">Pacientes:</span>
                  <Badge variant="outline">{convenio.pacientesVinculados}</Badge>
                </div>
              </div>

              {isGestor && (
                <div className="flex gap-2">
                  <Button 
                    variant="outline" 
                    size="sm" 
                    className="flex-1"
                    onClick={() => handleEdit(convenio)}
                  >
                    <Edit className="h-4 w-4 mr-1" />
                    Editar
                  </Button>
                  <Button 
                    variant="outline" 
                    size="sm"
                    onClick={() => setConvenioToDelete(convenio.id)}
                  >
                    <Trash2 className="h-4 w-4" />
                  </Button>
                </div>
              )}
            </CardContent>
          </Card>
        ))}
      </div>

      <ConvenioForm
        open={isFormOpen}
        onOpenChange={setIsFormOpen}
        onSave={handleSave}
        initialData={editingConvenio}
      />

      <AlertDialog open={!!convenioToDelete} onOpenChange={() => setConvenioToDelete(null)}>
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle>Confirmar exclusão</AlertDialogTitle>
            <AlertDialogDescription>
              Tem certeza que deseja remover este convênio? Esta ação não pode ser desfeita.
            </AlertDialogDescription>
          </AlertDialogHeader>
          <AlertDialogFooter>
            <AlertDialogCancel>Cancelar</AlertDialogCancel>
            <AlertDialogAction onClick={() => convenioToDelete && handleDelete(convenioToDelete)}>
              Confirmar
            </AlertDialogAction>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>
    </div>
  );
}
