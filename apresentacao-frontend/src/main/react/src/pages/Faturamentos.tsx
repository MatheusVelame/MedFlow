import { useState } from "react";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { FileText, DollarSign, Clock, CheckCircle, Edit, Trash2, Filter } from "lucide-react";
import { FaturamentoForm } from "@/components/FaturamentoForm";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
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
import { toast } from "@/hooks/use-toast";
import { useAuth } from "@/contexts/AuthContext";

interface Faturamento {
  id: string;
  pacienteNome: string;
  pacienteId: string;
  procedimentoTipo: "consulta" | "exame";
  procedimentoDescricao: string;
  valor: number;
  metodoPagamento: string;
  dataHora: string;
  status: "Pendente" | "Pago" | "Cancelado";
  usuarioResponsavel: string;
  observacoes?: string;
  justificativaValor?: string;
}

const mockFaturamentos: Faturamento[] = [
  {
    id: "1",
    pacienteNome: "Maria Silva",
    pacienteId: "P001",
    procedimentoTipo: "consulta",
    procedimentoDescricao: "Consulta Cardiologia",
    valor: 250.00,
    metodoPagamento: "Cartão de Crédito",
    dataHora: "2025-10-15T10:30:00",
    status: "Pago",
    usuarioResponsavel: "João Atendente"
  },
  {
    id: "2",
    pacienteNome: "Carlos Santos",
    pacienteId: "P002",
    procedimentoTipo: "exame",
    procedimentoDescricao: "Raio-X Tórax",
    valor: 180.00,
    metodoPagamento: "Convênio Unimed",
    dataHora: "2025-10-15T11:00:00",
    status: "Pendente",
    usuarioResponsavel: "Ana Financeiro"
  }
];

export default function Faturamentos() {
  const { isGestor } = useAuth();
  const [faturamentos, setFaturamentos] = useState<Faturamento[]>(mockFaturamentos);
  const [editingFaturamento, setEditingFaturamento] = useState<Faturamento | null>(null);
  const [faturamentoToDelete, setFaturamentoToDelete] = useState<string | null>(null);
  const [statusFilter, setStatusFilter] = useState<string>("todos");

  const handleNovoFaturamento = (novoFaturamento: Omit<Faturamento, "id" | "dataHora" | "status" | "usuarioResponsavel">) => {
    if (editingFaturamento) {
      setFaturamentos(faturamentos.map(f => 
        f.id === editingFaturamento.id 
          ? { ...f, ...novoFaturamento }
          : f
      ));
      toast({
        title: "Faturamento atualizado",
        description: "As informações foram atualizadas com sucesso.",
      });
      setEditingFaturamento(null);
    } else {
      const faturamento: Faturamento = {
        ...novoFaturamento,
        id: `F${Date.now()}`,
        dataHora: new Date().toISOString(),
        status: "Pendente",
        usuarioResponsavel: "Usuário Atual"
      };
      setFaturamentos([faturamento, ...faturamentos]);
      toast({
        title: "Faturamento registrado",
        description: "O faturamento foi cadastrado com sucesso.",
      });
    }
  };

  const handleEdit = (faturamento: Faturamento) => {
    setEditingFaturamento(faturamento);
  };

  const handleDelete = (id: string) => {
    setFaturamentos(faturamentos.filter(f => f.id !== id));
    setFaturamentoToDelete(null);
    toast({
      title: "Faturamento removido",
      description: "O faturamento foi removido com sucesso.",
    });
  };

  const handleStatusChange = (id: string, newStatus: "Pendente" | "Pago" | "Cancelado") => {
    setFaturamentos(faturamentos.map(f => 
      f.id === id ? { ...f, status: newStatus } : f
    ));
    toast({
      title: "Status atualizado",
      description: `Faturamento marcado como ${newStatus}.`,
    });
  };

  const getStatusBadge = (status: string) => {
    const variants: Record<string, "default" | "secondary" | "destructive"> = {
      Pendente: "secondary",
      Pago: "default",
      Cancelado: "destructive"
    };
    return <Badge variant={variants[status] || "default"}>{status}</Badge>;
  };

  const filteredFaturamentos = statusFilter === "todos" 
    ? faturamentos 
    : faturamentos.filter(f => f.status === statusFilter);

  const totalPendente = faturamentos
    .filter(f => f.status === "Pendente")
    .reduce((acc, f) => acc + f.valor, 0);

  const totalPago = faturamentos
    .filter(f => f.status === "Pago")
    .reduce((acc, f) => acc + f.valor, 0);

  return (
    <div className="space-y-6 animate-fade-in">
      <div>
        <h1 className="text-3xl font-bold tracking-tight text-foreground">Faturamentos</h1>
        <p className="text-muted-foreground">
          Registre e acompanhe os faturamentos de consultas e exames
        </p>
      </div>

        <div className="grid gap-4 md:grid-cols-3">
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Total Pendente</CardTitle>
            <Clock className="h-4 w-4 text-warning" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold text-warning">
              {totalPendente.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })}
            </div>
            <p className="text-xs text-muted-foreground mt-1">Aguardando pagamento</p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Total Recebido</CardTitle>
            <CheckCircle className="h-4 w-4 text-success" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold text-success">
              {totalPago.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })}
            </div>
            <p className="text-xs text-muted-foreground mt-1">Pagamentos confirmados</p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Total Faturamentos</CardTitle>
            <FileText className="h-4 w-4 text-primary" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{faturamentos.length}</div>
            <p className="text-xs text-muted-foreground mt-1">Registros no sistema</p>
          </CardContent>
        </Card>
        </div>

        <Tabs defaultValue="novo" className="space-y-4">
          <TabsList>
            <TabsTrigger value="novo">Novo Faturamento</TabsTrigger>
            <TabsTrigger value="lista">Lista de Faturamentos</TabsTrigger>
          </TabsList>

        <TabsContent value="novo" className="space-y-4">
          <Card>
            <CardHeader>
              <CardTitle>
                {editingFaturamento ? "Editar Faturamento" : "Registrar Novo Faturamento"}
              </CardTitle>
              <CardDescription>
                Preencha os dados do procedimento realizado para registrar o faturamento
              </CardDescription>
            </CardHeader>
            <CardContent>
              <FaturamentoForm 
                onSubmit={handleNovoFaturamento} 
                initialData={editingFaturamento}
                onCancel={() => setEditingFaturamento(null)}
              />
            </CardContent>
          </Card>
        </TabsContent>

        <TabsContent value="lista" className="space-y-4">
          <Card>
            <CardHeader>
              <div className="flex items-center justify-between">
                <div>
                  <CardTitle>Faturamentos Registrados</CardTitle>
                  <CardDescription>
                    Histórico de todos os faturamentos da clínica
                  </CardDescription>
                </div>
                <div className="flex items-center gap-2">
                  <Filter className="h-4 w-4 text-muted-foreground" />
                  <Select value={statusFilter} onValueChange={setStatusFilter}>
                    <SelectTrigger className="w-[180px]">
                      <SelectValue placeholder="Filtrar por status" />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value="todos">Todos</SelectItem>
                      <SelectItem value="Pendente">Pendente</SelectItem>
                      <SelectItem value="Pago">Pago</SelectItem>
                      <SelectItem value="Cancelado">Cancelado</SelectItem>
                    </SelectContent>
                  </Select>
                </div>
              </div>
            </CardHeader>
            <CardContent>
              <div className="space-y-4">
                {filteredFaturamentos.length === 0 ? (
                  <div className="text-center py-8 text-muted-foreground">
                    Nenhum faturamento encontrado com este filtro.
                  </div>
                ) : (
                  filteredFaturamentos.map((faturamento) => (
                    <Card key={faturamento.id} className="hover:shadow-md transition-shadow">
                      <CardContent className="pt-6">
                        <div className="flex items-start justify-between">
                          <div className="space-y-2 flex-1">
                            <div className="flex items-center gap-2">
                              <h3 className="font-semibold">{faturamento.pacienteNome}</h3>
                              <Badge variant="outline">{faturamento.pacienteId}</Badge>
                            </div>
                            
                            <div className="text-sm text-muted-foreground space-y-1">
                              <p><strong>Procedimento:</strong> {faturamento.procedimentoDescricao}</p>
                              <p><strong>Tipo:</strong> {faturamento.procedimentoTipo === "consulta" ? "Consulta" : "Exame"}</p>
                              <p><strong>Pagamento:</strong> {faturamento.metodoPagamento}</p>
                              <p><strong>Data:</strong> {new Date(faturamento.dataHora).toLocaleString('pt-BR')}</p>
                              <p><strong>Responsável:</strong> {faturamento.usuarioResponsavel}</p>
                              {faturamento.observacoes && (
                                <p><strong>Observações:</strong> {faturamento.observacoes}</p>
                              )}
                              {faturamento.justificativaValor && (
                                <p className="text-warning"><strong>Justificativa:</strong> {faturamento.justificativaValor}</p>
                              )}
                            </div>
                          </div>
                          
                          <div className="flex flex-col items-end gap-3">
                            <div className="text-2xl font-bold text-primary">
                              {faturamento.valor.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })}
                            </div>
                            
                            <Select 
                              value={faturamento.status} 
                              onValueChange={(value: any) => handleStatusChange(faturamento.id, value)}
                            >
                              <SelectTrigger className="w-[130px]">
                                <SelectValue />
                              </SelectTrigger>
                              <SelectContent>
                                <SelectItem value="Pendente">Pendente</SelectItem>
                                <SelectItem value="Pago">Pago</SelectItem>
                                <SelectItem value="Cancelado">Cancelado</SelectItem>
                              </SelectContent>
                            </Select>

                            {isGestor && (
                              <div className="flex gap-2">
                                <Button 
                                  variant="outline" 
                                  size="sm"
                                  onClick={() => handleEdit(faturamento)}
                                >
                                  <Edit className="h-4 w-4" />
                                </Button>
                                <Button 
                                  variant="outline" 
                                  size="sm"
                                  onClick={() => setFaturamentoToDelete(faturamento.id)}
                                >
                                  <Trash2 className="h-4 w-4" />
                                </Button>
                              </div>
                            )}
                          </div>
                        </div>
                      </CardContent>
                    </Card>
                  ))
                )}
              </div>
            </CardContent>
          </Card>
        </TabsContent>
      </Tabs>

      <AlertDialog open={!!faturamentoToDelete} onOpenChange={() => setFaturamentoToDelete(null)}>
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle>Confirmar exclusão</AlertDialogTitle>
            <AlertDialogDescription>
              Tem certeza que deseja remover este faturamento? Esta ação não pode ser desfeita.
            </AlertDialogDescription>
          </AlertDialogHeader>
          <AlertDialogFooter>
            <AlertDialogCancel>Cancelar</AlertDialogCancel>
            <AlertDialogAction onClick={() => faturamentoToDelete && handleDelete(faturamentoToDelete)}>
              Confirmar
            </AlertDialogAction>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>
    </div>
  );
}
