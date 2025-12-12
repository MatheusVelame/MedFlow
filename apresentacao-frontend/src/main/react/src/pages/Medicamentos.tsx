import { useState } from "react";
import { Plus, Edit, Trash2, Pill, Search } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Input } from "@/components/ui/input";
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
import { MedicamentoForm } from "@/components/MedicamentoForm";
import { toast } from "@/hooks/use-toast";

interface Medicamento {
  id: number;
  nome: string;
  principioAtivo: string;
  fabricante: string;
  tipo: string;
  dosagem: string;
  uso: string;
  contraindicacoes: string;
  ativo: boolean;
}

const initialMedicamentos: Medicamento[] = [
  { 
    id: 1, 
    nome: "Paracetamol 500mg", 
    principioAtivo: "Paracetamol",
    fabricante: "Medley",
    tipo: "Analgésico",
    dosagem: "500mg",
    uso: "Dor e febre",
    contraindicacoes: "Doença hepática grave",
    ativo: true 
  },
  { 
    id: 2, 
    nome: "Amoxicilina 500mg", 
    principioAtivo: "Amoxicilina",
    fabricante: "EMS",
    tipo: "Antibiótico",
    dosagem: "500mg",
    uso: "Infecções bacterianas",
    contraindicacoes: "Alergia a penicilinas",
    ativo: true 
  },
  { 
    id: 3, 
    nome: "Losartana 50mg", 
    principioAtivo: "Losartana Potássica",
    fabricante: "Eurofarma",
    tipo: "Anti-hipertensivo",
    dosagem: "50mg",
    uso: "Hipertensão arterial",
    contraindicacoes: "Gravidez, hipotensão",
    ativo: true 
  },
  { 
    id: 4, 
    nome: "Omeprazol 20mg", 
    principioAtivo: "Omeprazol",
    fabricante: "Medley",
    tipo: "Antiácido",
    dosagem: "20mg",
    uso: "Úlceras e refluxo",
    contraindicacoes: "Alergia ao componente",
    ativo: true 
  },
];

export default function Medicamentos() {
  const [medicamentos, setMedicamentos] = useState<Medicamento[]>(initialMedicamentos);
  const [isFormOpen, setIsFormOpen] = useState(false);
  const [editingMedicamento, setEditingMedicamento] = useState<Medicamento | null>(null);
  const [medicamentoToDelete, setMedicamentoToDelete] = useState<number | null>(null);
  const [searchTerm, setSearchTerm] = useState("");

  const filteredMedicamentos = medicamentos.filter(med =>
    med.nome.toLowerCase().includes(searchTerm.toLowerCase()) ||
    med.principioAtivo.toLowerCase().includes(searchTerm.toLowerCase()) ||
    med.tipo.toLowerCase().includes(searchTerm.toLowerCase())
  );

  const handleSave = (data: any) => {
    if (editingMedicamento) {
      setMedicamentos(medicamentos.map(m => 
        m.id === editingMedicamento.id ? { ...m, ...data } : m
      ));
      toast({
        title: "Medicamento atualizado",
        description: "As informações foram atualizadas com sucesso.",
      });
    } else {
      const newMedicamento = {
        id: Math.max(...medicamentos.map(m => m.id)) + 1,
        ...data,
      };
      setMedicamentos([...medicamentos, newMedicamento]);
      toast({
        title: "Medicamento cadastrado",
        description: "Novo medicamento foi adicionado com sucesso.",
      });
    }
    setIsFormOpen(false);
    setEditingMedicamento(null);
  };

  const handleEdit = (medicamento: Medicamento) => {
    setEditingMedicamento(medicamento);
    setIsFormOpen(true);
  };

  const handleDelete = (id: number) => {
    setMedicamentos(medicamentos.filter(m => m.id !== id));
    setMedicamentoToDelete(null);
    toast({
      title: "Medicamento removido",
      description: "O medicamento foi removido com sucesso.",
    });
  };

  return (
    <div className="space-y-6 animate-fade-in">
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-3xl font-bold tracking-tight text-foreground">Medicamentos</h1>
          <p className="text-muted-foreground">Gerencie o catálogo de medicamentos para prescrições</p>
        </div>
        <Button onClick={() => setIsFormOpen(true)} className="gap-2">
          <Plus className="h-4 w-4" />
          Novo Medicamento
        </Button>
      </div>

      <div className="relative">
        <Search className="absolute left-3 top-3 h-4 w-4 text-muted-foreground" />
        <Input
          placeholder="Buscar por nome, princípio ativo ou tipo..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          className="pl-10"
        />
      </div>

      <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
        {filteredMedicamentos.map((medicamento) => (
          <Card key={medicamento.id} className="hover:shadow-lg transition-shadow">
            <CardHeader className="pb-3">
              <div className="flex items-start justify-between">
                <div className="flex items-center gap-3">
                  <div className="p-2 bg-primary/10 rounded-lg">
                    <Pill className="h-5 w-5 text-primary" />
                  </div>
                  <div>
                    <CardTitle className="text-base">{medicamento.nome}</CardTitle>
                    <p className="text-xs text-muted-foreground mt-1">{medicamento.principioAtivo}</p>
                  </div>
                </div>
                <Badge variant={medicamento.ativo ? "default" : "secondary"}>
                  {medicamento.ativo ? "Ativo" : "Inativo"}
                </Badge>
              </div>
            </CardHeader>
            <CardContent>
              <div className="space-y-2 mb-4">
                <div className="flex justify-between text-sm">
                  <span className="text-muted-foreground">Tipo:</span>
                  <Badge variant="outline">{medicamento.tipo}</Badge>
                </div>
                <div className="flex justify-between text-sm">
                  <span className="text-muted-foreground">Dosagem:</span>
                  <span className="font-medium">{medicamento.dosagem}</span>
                </div>
                <div className="flex justify-between text-sm">
                  <span className="text-muted-foreground">Fabricante:</span>
                  <span className="font-medium">{medicamento.fabricante}</span>
                </div>
                <div className="pt-2 border-t">
                  <p className="text-xs text-muted-foreground mb-1">Uso:</p>
                  <p className="text-sm">{medicamento.uso}</p>
                </div>
                <div className="pt-2 border-t">
                  <p className="text-xs text-muted-foreground mb-1">Contraindicações:</p>
                  <p className="text-sm">{medicamento.contraindicacoes}</p>
                </div>
              </div>

              <div className="flex gap-2">
                <Button 
                  variant="outline" 
                  size="sm" 
                  className="flex-1"
                  onClick={() => handleEdit(medicamento)}
                >
                  <Edit className="h-4 w-4 mr-1" />
                  Editar
                </Button>
                <Button 
                  variant="outline" 
                  size="sm"
                  onClick={() => setMedicamentoToDelete(medicamento.id)}
                >
                  <Trash2 className="h-4 w-4" />
                </Button>
              </div>
            </CardContent>
          </Card>
        ))}
      </div>

      <MedicamentoForm
        open={isFormOpen}
        onOpenChange={setIsFormOpen}
        onSave={handleSave}
        initialData={editingMedicamento}
      />

      <AlertDialog open={!!medicamentoToDelete} onOpenChange={() => setMedicamentoToDelete(null)}>
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle>Confirmar exclusão</AlertDialogTitle>
            <AlertDialogDescription>
              Tem certeza que deseja remover este medicamento? Esta ação não pode ser desfeita.
            </AlertDialogDescription>
          </AlertDialogHeader>
          <AlertDialogFooter>
            <AlertDialogCancel>Cancelar</AlertDialogCancel>
            <AlertDialogAction onClick={() => medicamentoToDelete && handleDelete(medicamentoToDelete)}>
              Confirmar
            </AlertDialogAction>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>
    </div>
  );
}
