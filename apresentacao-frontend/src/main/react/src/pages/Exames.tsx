import { useState } from "react";
import { TestTube, Search, Upload, Download, Clock, CheckCircle, XCircle, AlertCircle, Plus, Edit, Trash2, List } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { ExameForm } from "@/components/ExameForm";
import { TipoExameForm } from "@/components/TipoExameForm";
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

const mockExames = [
  {
    id: "1",
    paciente: "Maria Silva Santos",
    tipo: "Hemograma Completo",
    solicitante: "Dr. Carlos Mendes",
    dataSolicitacao: "2024-01-10",
    status: "resultado",
    prioridade: "normal",
    laboratorio: "Lab Central"
  },
  {
    id: "2",
    paciente: "João Pedro Oliveira",
    tipo: "Ressonância Magnética",
    solicitante: "Dra. Ana Paula",
    dataSolicitacao: "2024-01-09",
    status: "pendente",
    prioridade: "urgente",
    laboratorio: "Clínica de Imagem"
  },
  {
    id: "3",
    paciente: "Ana Costa Ferreira",
    tipo: "Raio-X Tórax",
    solicitante: "Dr. Roberto Lima",
    dataSolicitacao: "2024-01-08",
    status: "aguardando",
    prioridade: "normal",
    laboratorio: "Lab Central"
  },
  {
    id: "4",
    paciente: "Pedro Henrique Santos",
    tipo: "Ultrassom Abdominal",
    solicitante: "Dra. Juliana Costa",
    dataSolicitacao: "2024-01-05",
    status: "cancelado",
    prioridade: "normal",
    laboratorio: "Clínica de Imagem"
  }
];

const mockEspecialidades = [
  { id: "1", nome: "Cardiologia" },
  { id: "2", nome: "Clínico Geral" },
  { id: "3", nome: "Radiologia" },
  { id: "4", nome: "Laboratório" },
  { id: "5", nome: "Imagem" },
];

interface TipoExame {
  id: string;
  codigo: string;
  descricao: string;
  especialidade: string;
  valor: number;
  status: "Ativo" | "Inativo";
  observacoes?: string;
}

const mockTiposExame: TipoExame[] = [
  {
    id: "1",
    codigo: "HEMOG-001",
    descricao: "Hemograma Completo",
    especialidade: "Laboratório",
    valor: 80.00,
    status: "Ativo",
  },
  {
    id: "2",
    codigo: "RESM-001",
    descricao: "Ressonância Magnética",
    especialidade: "Imagem",
    valor: 800.00,
    status: "Ativo",
  },
  {
    id: "3",
    codigo: "RAIO-001",
    descricao: "Raio-X Tórax",
    especialidade: "Radiologia",
    valor: 150.00,
    status: "Ativo",
  },
];

export default function Exames() {
  const { isGestor } = useAuth();
  const [searchTerm, setSearchTerm] = useState("");
  const [filterStatus, setFilterStatus] = useState("todos");
  const [isExameFormOpen, setIsExameFormOpen] = useState(false);
  const [exames, setExames] = useState(mockExames);
  
  const [tiposExame, setTiposExame] = useState<TipoExame[]>(mockTiposExame);
  const [isTipoExameFormOpen, setIsTipoExameFormOpen] = useState(false);
  const [editingTipoExame, setEditingTipoExame] = useState<TipoExame | null>(null);
  const [tipoExameToDelete, setTipoExameToDelete] = useState<string | null>(null);

  const handleSaveExame = (data: any) => {
    const newExame = {
      ...data,
      id: String(Math.max(...exames.map(e => parseInt(e.id))) + 1),
      status: "pendente" as const
    };
    setExames([...exames, newExame]);
  };

  const handleSaveTipoExame = (data: any) => {
    if (editingTipoExame) {
      setTiposExame(tiposExame.map(t => 
        t.id === editingTipoExame.id ? { ...t, ...data } : t
      ));
      toast({
        title: "Tipo de exame atualizado",
        description: "As informações foram atualizadas com sucesso.",
      });
      setEditingTipoExame(null);
    } else {
      const novoTipo: TipoExame = {
        ...data,
        id: String(Math.max(...tiposExame.map(t => parseInt(t.id))) + 1),
        status: "Ativo" as const, // RN: Status inicial sempre Ativo
      };
      setTiposExame([...tiposExame, novoTipo]);
      toast({
        title: "Tipo de exame cadastrado",
        description: `${data.codigo} - ${data.descricao} foi cadastrado com sucesso.`,
      });
    }
    setIsTipoExameFormOpen(false);
  };

  const handleEditTipoExame = (tipo: TipoExame) => {
    setEditingTipoExame(tipo);
    setIsTipoExameFormOpen(true);
  };

  const handleDeleteTipoExame = (id: string) => {
    setTiposExame(tiposExame.filter(t => t.id !== id));
    setTipoExameToDelete(null);
    toast({
      title: "Tipo de exame removido",
      description: "O tipo de exame foi removido com sucesso.",
    });
  };

  const handleToggleStatus = (id: string) => {
    setTiposExame(tiposExame.map(t => 
      t.id === id 
        ? { ...t, status: t.status === "Ativo" ? "Inativo" as const : "Ativo" as const }
        : t
    ));
    toast({
      title: "Status atualizado",
      description: "O status do tipo de exame foi alterado.",
    });
  };

  const handleUploadResult = (exameId: string) => {
    toast({
      title: "Upload em desenvolvimento",
      description: "Funcionalidade de upload será implementada em breve.",
    });
  };

  const getStatusIcon = (status: string) => {
    switch (status) {
      case "resultado":
        return <CheckCircle className="w-4 h-4" />;
      case "pendente":
        return <Clock className="w-4 h-4" />;
      case "aguardando":
        return <AlertCircle className="w-4 h-4" />;
      case "cancelado":
        return <XCircle className="w-4 h-4" />;
      default:
        return <TestTube className="w-4 h-4" />;
    }
  };

  const getStatusBadge = (status: string) => {
    const variants: Record<string, { variant: "default" | "secondary" | "destructive" | "outline"; label: string; className?: string }> = {
      resultado: { variant: "default", label: "Resultado Disponível", className: "bg-success/10 text-success border-success/20" },
      pendente: { variant: "outline", label: "Pendente", className: "bg-warning/10 text-warning border-warning/20" },
      aguardando: { variant: "secondary", label: "Aguardando Autorização", className: "bg-muted text-muted-foreground" },
      cancelado: { variant: "destructive", label: "Cancelado", className: "bg-destructive/10 text-destructive border-destructive/20" }
    };
    
    const config = variants[status] || variants.pendente;
    return <Badge variant={config.variant} className={config.className}>{config.label}</Badge>;
  };

  const getPrioridadeBadge = (prioridade: string) => {
    return prioridade === "urgente" ? (
      <Badge variant="destructive" className="ml-2">Urgente</Badge>
    ) : null;
  };

  const filteredExames = exames.filter(exame => {
    const matchesSearch = exame.paciente.toLowerCase().includes(searchTerm.toLowerCase()) ||
      exame.tipo.toLowerCase().includes(searchTerm.toLowerCase());
    const matchesStatus = filterStatus === "todos" || exame.status === filterStatus;
    return matchesSearch && matchesStatus;
  });

  return (
    <div className="space-y-6 animate-fade-in">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold text-foreground">Exames</h1>
          <p className="text-muted-foreground">Solicitações, resultados e tipos de exames</p>
        </div>
        {!isGestor && (
          <Button className="bg-gradient-primary text-white hover:opacity-90" onClick={() => setIsExameFormOpen(true)}>
            <TestTube className="w-4 h-4 mr-2" />
            Nova Solicitação
          </Button>
        )}
      </div>

      <ExameForm
        open={isExameFormOpen}
        onOpenChange={setIsExameFormOpen}
        onSave={handleSaveExame}
      />

      <TipoExameForm
        open={isTipoExameFormOpen}
        onOpenChange={setIsTipoExameFormOpen}
        onSave={handleSaveTipoExame}
        initialData={editingTipoExame}
        existingCodes={tiposExame.map(t => t.codigo)}
        especialidades={mockEspecialidades}
      />

      <div className="grid gap-6 md:grid-cols-4">
        <Card className="shadow-card">
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium text-muted-foreground">
              Total Solicitados
            </CardTitle>
            <TestTube className="w-4 h-4 text-primary" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold text-foreground">156</div>
            <p className="text-xs text-muted-foreground">Este mês</p>
          </CardContent>
        </Card>

        <Card className="shadow-card">
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium text-muted-foreground">
              Resultados Disponíveis
            </CardTitle>
            <CheckCircle className="w-4 h-4 text-success" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold text-foreground">89</div>
            <p className="text-xs text-muted-foreground">57% concluídos</p>
          </CardContent>
        </Card>

        <Card className="shadow-card">
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium text-muted-foreground">
              Pendentes
            </CardTitle>
            <Clock className="w-4 h-4 text-warning" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold text-foreground">52</div>
            <p className="text-xs text-muted-foreground">Em processamento</p>
          </CardContent>
        </Card>

        <Card className="shadow-card">
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium text-muted-foreground">
              Aguardando Autorização
            </CardTitle>
            <AlertCircle className="w-4 h-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold text-foreground">15</div>
            <p className="text-xs text-muted-foreground">Convênios</p>
          </CardContent>
        </Card>
      </div>

      <Tabs defaultValue="solicitacoes" className="space-y-4">
        <TabsList>
          <TabsTrigger value="solicitacoes">Solicitações</TabsTrigger>
          <TabsTrigger value="tipos">Tipos de Exames</TabsTrigger>
        </TabsList>

        <TabsContent value="solicitacoes" className="space-y-4">
          <Tabs defaultValue="todos" onValueChange={setFilterStatus}>
            <div className="flex items-center justify-between">
              <TabsList>
                <TabsTrigger value="todos">Todos</TabsTrigger>
                <TabsTrigger value="resultado">Resultados</TabsTrigger>
                <TabsTrigger value="pendente">Pendentes</TabsTrigger>
                <TabsTrigger value="aguardando">Aguardando</TabsTrigger>
              </TabsList>

              <div className="relative w-64">
                <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-muted-foreground w-4 h-4" />
                <Input
                  placeholder="Buscar exames..."
                  value={searchTerm}
                  onChange={(e) => setSearchTerm(e.target.value)}
                  className="pl-10"
                />
              </div>
            </div>

            <TabsContent value={filterStatus} className="space-y-4">
              <div className="grid gap-4">
                {filteredExames.map((exame) => (
                  <Card
                    key={exame.id}
                    className="shadow-card hover:shadow-medical transition-all duration-300"
                  >
                    <CardContent className="p-6">
                      <div className="flex items-start justify-between">
                        <div className="flex-1 space-y-3">
                          <div className="flex items-center gap-3">
                            <div className="p-2 bg-primary/10 rounded-lg">
                              {getStatusIcon(exame.status)}
                            </div>
                            <div>
                              <h3 className="text-lg font-semibold text-foreground">
                                {exame.tipo}
                                {getPrioridadeBadge(exame.prioridade)}
                              </h3>
                              <p className="text-sm text-muted-foreground">{exame.paciente}</p>
                            </div>
                          </div>
                          
                          <div className="grid grid-cols-3 gap-4 text-sm">
                            <div>
                              <span className="text-muted-foreground">Solicitante: </span>
                              <span className="text-foreground font-medium">{exame.solicitante}</span>
                            </div>
                            <div>
                              <span className="text-muted-foreground">Laboratório: </span>
                              <span className="text-foreground font-medium">{exame.laboratorio}</span>
                            </div>
                            <div>
                              <span className="text-muted-foreground">Data: </span>
                              <span className="text-foreground font-medium">
                                {new Date(exame.dataSolicitacao).toLocaleDateString('pt-BR')}
                              </span>
                            </div>
                          </div>

                          <div className="flex items-center gap-2">
                            {getStatusBadge(exame.status)}
                          </div>
                        </div>

                        <div className="flex gap-2">
                          {exame.status === "resultado" && (
                            <Button variant="outline" size="sm">
                              <Download className="w-4 h-4 mr-2" />
                              Baixar
                            </Button>
                          )}
                          {exame.status !== "resultado" && (
                            <Button variant="outline" size="sm" onClick={() => handleUploadResult(exame.id)}>
                              <Upload className="w-4 h-4 mr-2" />
                              Anexar
                            </Button>
                          )}
                        </div>
                      </div>
                    </CardContent>
                  </Card>
                ))}
              </div>
            </TabsContent>
          </Tabs>
        </TabsContent>

        <TabsContent value="tipos" className="space-y-4">
          <Card>
            <CardHeader>
              <div className="flex items-center justify-between">
                <div>
                  <CardTitle>Tipos de Exames</CardTitle>
                  <CardDescription>
                    Gerencie os tipos de exames disponíveis para agendamento e faturamento
                  </CardDescription>
                </div>
                {isGestor && (
                  <Button onClick={() => setIsTipoExameFormOpen(true)} className="gap-2">
                    <Plus className="h-4 w-4" />
                    Novo Tipo
                  </Button>
                )}
              </div>
            </CardHeader>
            <CardContent>
              <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
                {tiposExame.map((tipo) => (
                  <Card key={tipo.id} className="hover:shadow-md transition-shadow">
                    <CardContent className="pt-6">
                      <div className="space-y-3">
                        <div className="flex items-start justify-between">
                          <div className="flex items-center gap-2">
                            <div className="p-2 bg-primary/10 rounded-lg">
                              <TestTube className="h-4 w-4 text-primary" />
                            </div>
                            <div>
                              <h3 className="font-semibold">{tipo.codigo}</h3>
                              <Badge variant={tipo.status === "Ativo" ? "default" : "secondary"}>
                                {tipo.status}
                              </Badge>
                            </div>
                          </div>
                        </div>

                        <div className="space-y-2 text-sm">
                          <p className="font-medium">{tipo.descricao}</p>
                          <div className="flex justify-between">
                            <span className="text-muted-foreground">Especialidade:</span>
                            <Badge variant="outline">{tipo.especialidade}</Badge>
                          </div>
                          <div className="flex justify-between">
                            <span className="text-muted-foreground">Valor:</span>
                            <span className="font-bold text-primary">
                              {tipo.valor.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })}
                            </span>
                          </div>
                          {tipo.observacoes && (
                            <p className="text-xs text-muted-foreground pt-2 border-t">
                              {tipo.observacoes}
                            </p>
                          )}
                        </div>

                        {isGestor && (
                          <div className="flex gap-2 pt-2">
                            <Button 
                              variant="outline" 
                              size="sm" 
                              className="flex-1"
                              onClick={() => handleToggleStatus(tipo.id)}
                            >
                              <List className="h-4 w-4 mr-1" />
                              {tipo.status === "Ativo" ? "Inativar" : "Ativar"}
                            </Button>
                            <Button 
                              variant="outline" 
                              size="sm"
                              onClick={() => handleEditTipoExame(tipo)}
                            >
                              <Edit className="h-4 w-4" />
                            </Button>
                            <Button 
                              variant="outline" 
                              size="sm"
                              onClick={() => setTipoExameToDelete(tipo.id)}
                            >
                              <Trash2 className="h-4 w-4" />
                            </Button>
                          </div>
                        )}
                      </div>
                    </CardContent>
                  </Card>
                ))}
              </div>
            </CardContent>
          </Card>
        </TabsContent>
      </Tabs>

      <AlertDialog open={!!tipoExameToDelete} onOpenChange={() => setTipoExameToDelete(null)}>
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle>Confirmar exclusão</AlertDialogTitle>
            <AlertDialogDescription>
              Tem certeza que deseja remover este tipo de exame? Esta ação não pode ser desfeita.
            </AlertDialogDescription>
          </AlertDialogHeader>
          <AlertDialogFooter>
            <AlertDialogCancel>Cancelar</AlertDialogCancel>
            <AlertDialogAction onClick={() => tipoExameToDelete && handleDeleteTipoExame(tipoExameToDelete)}>
              Confirmar
            </AlertDialogAction>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>
    </div>
  );
}
