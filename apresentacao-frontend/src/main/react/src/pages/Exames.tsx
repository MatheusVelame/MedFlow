import { useState } from "react";
import { TestTube, Search, Upload, Download, Clock, CheckCircle, XCircle, AlertCircle, Plus, Edit, Trash2, List, Loader2 } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
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

// 1. IMPORTANDO O HOOK DE EDIÇÃO (useEditarTipoExame)
import { 
  useListarTiposExames, 
  useCadastrarTipoExame, 
  useInativarTipoExame, 
  useEditarTipoExame, 
  TipoExameResumo 
} from "@/api/useTiposExamesApi";

// IMPORTS NOVOS: Exame hooks e form
import { ExameForm } from "@/components/ExameForm";
import { useAgendarExame, useAtualizarExame, useCancelarExame, useExcluirExame, useExamesList } from "@/hooks/useExames";

export default function Exames() {
  const { isGestor, isMedico } = useAuth();
  const [searchTerm, setSearchTerm] = useState("");
  const [filterStatus, setFilterStatus] = useState("todos");
  
  // ========================================================================
  // INTEGRAÇÃO COM A API (TIPOS DE EXAME)
  // ========================================================================
  
  // 1. Listagem
  const { data: tiposExame = [], isLoading: isLoadingTipos } = useListarTiposExames();

  // 2. Mutações
  const { mutate: cadastrarTipo, isPending: isCadastrando } = useCadastrarTipoExame();
  const { mutate: inativarTipo } = useInativarTipoExame();
  
  // 3. NOVO: Hook de Edição
  const { mutate: editarTipo, isPending: isEditando } = useEditarTipoExame();

  // Estados de controle da UI
  const [isTipoExameFormOpen, setIsTipoExameFormOpen] = useState(false);
  const [editingTipoExame, setEditingTipoExame] = useState<TipoExameResumo | null>(null);
  const [tipoExameToDelete, setTipoExameToDelete] = useState<number | null>(null);

  // ========================================================================
  // EXAMES: hooks e UI state
  // ========================================================================
  const { data: exames = [] } = useExamesList();
  const agendar = useAgendarExame();
  const atualizarExame = useAtualizarExame();
  const cancelarExame = useCancelarExame();
  const excluirExame = useExcluirExame();

  const [isExameFormOpen, setIsExameFormOpen] = useState(false);
  const [editingExame, setEditingExame] = useState<any | null>(null);
  const [exameToDelete, setExameToDelete] = useState<number | null>(null);
  const [exameToCancel, setExameToCancel] = useState<number | null>(null);

  // ========================================================================
  // HANDLERS
  // ========================================================================

  // LÓGICA DE SALVAR UNIFICADA (CRIAR E EDITAR)
  const handleSaveTipoExame = (data: any) => {
    const payload = {
        codigo: data.codigo,
        descricao: data.descricao,
        especialidade: data.especialidade,
        valor: Number(data.valor),
        responsavelId: 1073741824 
    };

    if (editingTipoExame) {
      // --- EDITAR ---
      editarTipo({
        id: editingTipoExame.id,
        payload: payload
      }, {
        onSuccess: () => {
           setIsTipoExameFormOpen(false);
           setEditingTipoExame(null);
           toast({
             title: "Sucesso",
             description: "Tipo de exame atualizado com sucesso.",
           });
        },
        onError: (error) => {
            console.error(error);
            toast({
                title: "Erro",
                description: "Erro ao atualizar o tipo de exame.",
                variant: "destructive"
            });
        }
      });
    } else {
      // --- CADASTRAR ---
      cadastrarTipo(payload, {
        onSuccess: () => {
           setIsTipoExameFormOpen(false);
           toast({
             title: "Sucesso",
             description: "Novo tipo de exame cadastrado.",
           });
        },
        onError: (error) => {
            console.error(error);
            toast({
                title: "Erro",
                description: "Erro ao cadastrar novo tipo.",
                variant: "destructive"
            });
        }
      });
    }
  };

  const handleEditTipoExame = (tipo: TipoExameResumo) => {
    setEditingTipoExame(tipo);
    setIsTipoExameFormOpen(true);
  };

  const handleNewTipoExame = () => {
    setEditingTipoExame(null); // Garante que o form abre vazio
    setIsTipoExameFormOpen(true);
  }

  const handleDeleteTipoExame = (id: number) => {
    inativarTipo({
        id: id,
        payload: { responsavelId: 1073741824 }
    });
    setTipoExameToDelete(null);
  };

  const handleToggleStatus = (id: number) => {
    inativarTipo({
        id: id,
        payload: { responsavelId: 1073741824 }
    });
  };

  const handleUploadResult = (exameId: string) => {
    toast({
      title: "Upload em desenvolvimento",
      description: "Funcionalidade de upload será implementada em breve.",
    });
  };

  // Exames handlers
  const handleOpenAgendar = () => {
    setEditingExame(null);
    setIsExameFormOpen(true);
  };

  const handleSaveExame = (data: any) => {
    // o backend espera dataHora como LocalDateTime (YYYY-MM-DDTHH:mm:ss)
    const ensureSeconds = (v: string) => {
      if (!v) return v;
      if (/^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}$/.test(v)) return `${v}:00`;
      if (/^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}$/.test(v)) return v;
      return v.length >= 19 ? v.slice(0, 19) : `${v}:00`;
    };

    const dataHoraComSegundos = ensureSeconds(data.dataHora);

    if (editingExame) {
      return atualizarExame.mutateAsync({ id: editingExame.id, payload: { medicoId: Number(data.medicoId), tipoExame: data.tipoExame, dataHora: dataHoraComSegundos, responsavelId: Number(data.responsavelId) } })
        .then((res) => { setEditingExame(null); return res; });
    } else {
      return agendar.mutateAsync({ pacienteId: Number(data.pacienteId), medicoId: Number(data.medicoId), tipoExame: data.tipoExame, dataHora: dataHoraComSegundos, responsavelId: Number(data.responsavelId) });
    }
  };

  const handleEditExame = (exame: any) => {
    setEditingExame(exame);
    setIsExameFormOpen(true);
  };

  const handleDeleteExame = async (id: number) => {
    try {
      await excluirExame.mutateAsync({ id, responsavelId: 1073741824 });
      toast({ title: 'Exame excluído', description: 'Exame removido.' });
      setExameToDelete(null);
    } catch (e: any) {
      toast({ title: 'Erro', description: e?.message ?? 'Erro ao excluir exame' });
    }
  };

  const handleCancelExame = async (id: number, motivo?: string) => {
    try {
      await cancelarExame.mutateAsync({ id, payload: { motivo: motivo ?? 'Cancelado pelo usuário', responsavelId: 1073741824 } });
      toast({ title: 'Exame cancelado', description: 'Exame cancelado com sucesso.' });
      setExameToCancel(null);
    } catch (e: any) {
      toast({ title: 'Erro', description: e?.message ?? 'Erro ao cancelar exame' });
    }
  };

  // ========================================================================
  // HELPERS VISUAIS
  // ========================================================================

  const getStatusIcon = (status: string) => {
    switch (status) {
      case "resultado": return <CheckCircle className="w-4 h-4" />;
      case "pendente": return <Clock className="w-4 h-4" />;
      case "aguardando": return <AlertCircle className="w-4 h-4" />;
      case "cancelado": return <XCircle className="w-4 h-4" />;
      default: return <TestTube className="w-4 h-4" />;
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
    const pacienteText = String((exame as any).paciente ?? exame.pacienteId ?? "");
    const tipoText = String((exame as any).tipo ?? exame.tipoExame ?? "");
    const matchesSearch = pacienteText.toLowerCase().includes(searchTerm.toLowerCase()) ||
      tipoText.toLowerCase().includes(searchTerm.toLowerCase());
    const matchesStatus = filterStatus === "todos" || exame.status === filterStatus;
    return matchesSearch && matchesStatus;
  });

  return (
    <div className="space-y-6 animate-fade-in">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold text-foreground">Exames</h1>
          <p className="text-muted-foreground">Gerencie e agende exames na clínica</p>
        </div>
        <div className="flex gap-2">
          {(isGestor || isMedico) && (
            <Button onClick={handleOpenAgendar} className="gap-2">
              <Plus className="h-4 w-4" />
              Agendar Exame
            </Button>
          )}
        </div>
      </div>

      <TipoExameForm
        open={isTipoExameFormOpen}
        onOpenChange={setIsTipoExameFormOpen}
        onSave={handleSaveTipoExame}
        initialData={editingTipoExame}
        existingCodes={tiposExame.map((t: any) => t.codigo)}
        especialidades={[]}
        isLoading={isCadastrando || isEditando} 
      />

      <ExameForm open={isExameFormOpen} onOpenChange={setIsExameFormOpen} initialData={editingExame ?? undefined} onSave={handleSaveExame} />

      <div className="grid gap-6 md:grid-cols-4">
        <Card className="shadow-card">
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium text-muted-foreground">Total Solicitados</CardTitle>
            <TestTube className="w-4 h-4 text-primary" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold text-foreground">156</div>
            <p className="text-xs text-muted-foreground">Este mês</p>
          </CardContent>
        </Card>
        {/* Cards adicionais podem ser inseridos aqui se necessário */}
      </div>

      <Tabs defaultValue="exames" className="space-y-4">
        <TabsList>
          <TabsTrigger value="exames">Exames</TabsTrigger>
          <TabsTrigger value="tipos">Tipos de Exames</TabsTrigger>
        </TabsList>

        <TabsContent value="exames" className="space-y-4">
          <Card>
            <CardHeader>
              <div className="flex items-center justify-between w-full">
                <div className="flex-1 pr-4">
                  <Input placeholder="Pesquisar por paciente ou tipo" value={searchTerm} onChange={(e) => setSearchTerm(e.target.value)} />
                </div>
                <div className="flex items-center gap-2">
                  <Button variant={filterStatus === 'todos' ? 'default' : 'outline'} size="sm" onClick={() => setFilterStatus('todos')}>Todos</Button>
                  <Button variant={filterStatus === 'pendente' ? 'default' : 'outline'} size="sm" onClick={() => setFilterStatus('pendente')}>Pendente</Button>
                  <Button variant={filterStatus === 'resultado' ? 'default' : 'outline'} size="sm" onClick={() => setFilterStatus('resultado')}>Resultado</Button>
                </div>
              </div>
            </CardHeader>
            <CardContent>
              {filteredExames.length === 0 ? (
                <div className="text-center py-10 text-muted-foreground">Nenhum exame encontrado</div>
              ) : (
                <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
                  {filteredExames.map((exame: any) => (
                    <Card key={exame.id} className="hover:shadow-md transition-shadow">
                      <CardContent className="pt-4">
                        <div className="flex items-start justify-between">
                          <div>
                            <h3 className="font-semibold">{exame.paciente ?? exame.pacienteId}</h3>
                            <p className="text-sm text-muted-foreground">{exame.tipo ?? exame.tipoExame}</p>
                          </div>
                          <div className="text-right">
                            <div className="text-sm text-muted-foreground">{new Date(exame.dataHora || exame.datahora || exame.data).toLocaleString()}</div>
                            <div className="mt-2">{getStatusBadge(exame.status ?? exame.situation ?? 'pendente')}</div>
                          </div>
                        </div>

                        <div className="flex gap-2 mt-4">
                          <Button size="sm" variant="outline" onClick={() => handleEditExame(exame)}>
                            <Edit className="h-4 w-4" />
                          </Button>
                          <Button size="sm" variant="outline" onClick={() => setExameToCancel(exame.id)}>
                            <XCircle className="h-4 w-4" />
                          </Button>
                          <Button size="sm" variant="outline" onClick={() => setExameToDelete(exame.id)}>
                            <Trash2 className="h-4 w-4" />
                          </Button>
                          <Button size="sm" variant="outline" onClick={() => handleUploadResult(exame.id)}>
                            <Upload className="h-4 w-4" />
                          </Button>
                        </div>
                      </CardContent>
                    </Card>
                  ))}
                </div>
              )}
            </CardContent>
          </Card>
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
                  <Button onClick={handleNewTipoExame} className="gap-2">
                    <Plus className="h-4 w-4" />
                    Novo Tipo
                  </Button>
                )}
              </div>
            </CardHeader>
            <CardContent>
              {isLoadingTipos ? (
                <div className="flex justify-center items-center py-10">
                    <Loader2 className="w-8 h-8 animate-spin text-primary" />
                </div>
              ) : (
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
                                <Badge variant={tipo.status === "ATIVO" || tipo.status === "Ativo" ? "default" : "secondary"}>
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
                                {tipo.status === "ATIVO" || tipo.status === "Ativo" ? "Inativar" : "Ativar"}
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
              )}
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