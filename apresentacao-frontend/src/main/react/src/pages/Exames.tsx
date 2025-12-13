import { useState, useEffect } from "react";
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

// Novo: Patients list for displaying names
import { useListarPacientes } from "@/api/usePacientesApi";
// Novo: Funcionários (médicos)
import { useListarFuncionarios } from "@/api/useFuncionariosApi";
import { examesApi } from "@/api";

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
  // Usar hooks com filtros adequados: por padrão mostramos agendados e futuros
  const { data: examesAgendados = [] } = useExamesList('AGENDADO');
  const { data: examesCancelados = [] } = useExamesList('CANCELADO');
  const { data: examesResultado = [] } = useExamesList('REALIZADO');
  // Para pendentes (status PENDENTE)
  const { data: examesPendentes = [] } = useExamesList('PENDENTE');
  // Fonte de exames depende do filtro atual para manter consistência entre operações (cancelar / excluir)
  let sourceExames: any[] = [];
  switch (filterStatus) {
    case 'agendados': sourceExames = examesAgendados; break;
    case 'cancelados': sourceExames = examesCancelados; break;
    case 'resultado': sourceExames = examesResultado; break;
    case 'resultado pendente': sourceExames = examesPendentes; break;
    case 'todos':
    default:
      sourceExames = Array.from(new Map([...examesAgendados, ...examesPendentes, ...examesResultado].map((e:any) => [e.id, e])).values());
      break;
  }

  const agendar = useAgendarExame();
  const atualizarExame = useAtualizarExame();
  const cancelarExame = useCancelarExame();
  const excluirExame = useExcluirExame();

  const { data: pacientes = [] } = useListarPacientes();
  const { data: funcionarios = [] } = useListarFuncionarios();

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

    // Sanitização defensiva: alguns valores podem, por causa do componente Select, vir como array.
    const firstValue = (v: any) => Array.isArray(v) ? v[0] : v;
    const pacienteId = Number(firstValue(data.pacienteId));
    const medicoId = Number(firstValue(data.medicoId));
    const tipoExame = String(firstValue(data.tipoExame));
    const responsavelId = Number(firstValue((data.responsavelId ?? (window as any)._currentUserId ?? 1)));

    const payloadCreate = {
      pacienteId,
      medicoId,
      tipoExame,
      dataHora: dataHoraComSegundos,
      responsavelId,
    };

    // Log do payload para facilitar diagnóstico no browser
    console.debug("[Exames] Payload para agendamento/atualização:", payloadCreate);

    if (editingExame) {
      const payloadUpdate = { medicoId, tipoExame, dataHora: dataHoraComSegundos, responsavelId, observacoes: (data as any).observacoes };
      return atualizarExame.mutateAsync({ id: editingExame.id, payload: payloadUpdate })
        .then((res) => { setEditingExame(null); return res; });
    } else {
      return agendar.mutateAsync(payloadCreate);
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
      case "agendado": return <Clock className="w-4 h-4" />;
      default: return <TestTube className="w-4 h-4" />;
    }
  };

  const getStatusBadge = (status: string) => {
    const variants: Record<string, { variant: "default" | "secondary" | "destructive" | "outline"; label: string; className?: string }> = {
      resultado: { variant: "default", label: "Resultado Disponível", className: "bg-success/10 text-success border-success/20" },
      pendente: { variant: "outline", label: "Pendente", className: "bg-warning/10 text-warning border-warning/20" },
      aguardando: { variant: "secondary", label: "Aguardando Autorização", className: "bg-muted text-muted-foreground" },
      cancelado: { variant: "destructive", label: "Cancelado", className: "bg-destructive/10 text-destructive border-destructive/20" },
      agendado: { variant: "outline", label: "Agendado", className: "bg-blue-50 text-blue-600 border-blue-100" }
    };
    
    const config = variants[status] || variants.pendente;
    return <Badge variant={config.variant} className={config.className}>{config.label}</Badge>;
  };

  const getPrioridadeBadge = (prioridade: string) => {
    return prioridade === "urgente" ? (
      <Badge variant="destructive" className="ml-2">Urgente</Badge>
    ) : null;
  };

  const normalizeStatus = (s: any) => {
    if (!s) return '';
    const v = String(s).toLowerCase();
    if (v.includes('pend')) return 'pendente';
    if (v.includes('agend')) return 'agendado';
    if (v.includes('result') || v.includes('resultado')) return 'resultado';
    if (v.includes('cancel')) return 'cancelado';
    if (v.includes('aguard')) return 'aguardando';
    return v;
  };

  // Map exames para incluir nomes legíveis para paciente, tipo e médico
  const mappedExames = sourceExames.map((exame: any) => {
    const paciente = pacientes.find((p: any) => p.id === Number(exame.pacienteId));
    const pacienteName = paciente ? paciente.name : String(exame.pacienteId ?? '');

    // medico
    const medico = funcionarios.find((f: any) => f.id === Number(exame.medicoId));
    const medicoName = medico ? medico.nome || medico.name : String(exame.medicoId ?? '');

    // tipoExame in the API is usually the codigo; try to resolve to a more friendly label
    const tipo = tiposExame.find((t: any) => (t.codigo && String(t.codigo) === String(exame.tipoExame)) || String(t.id) === String(exame.tipoExame));
    const tipoLabel = tipo ? ((tipo.codigo ? tipo.codigo + ' - ' : '') + (tipo.descricao || tipo.codigo)) : String(exame.tipoExame ?? '');

    const statusNormalized = normalizeStatus(exame.status || exame.situation || exame.statusName || '');

    return { ...exame, pacienteName, tipoLabel, medicoName, statusNormalized };
  });

  const totalAgendados = examesAgendados.length;

  // ========================================================================
  // EFFECTS
  // ========================================================================

  // Para os filtros principais não há necessidade de efeitos extras aqui.
  useEffect(() => {
    // efeito reservado caso precisemos reagir a mudanças de filtro no futuro
  }, [filterStatus]);

  // ========================================================================
  // FILTRO
  // ========================================================================

  const filteredExames = mappedExames.filter((exame: any) => {
    const pacienteText = String(exame.pacienteName ?? exame.pacienteId ?? "");
    const tipoText = String(exame.tipoLabel ?? exame.tipoExame ?? "");
    const matchesSearch = pacienteText.toLowerCase().includes(searchTerm.toLowerCase()) ||
      tipoText.toLowerCase().includes(searchTerm.toLowerCase()) ||
      String(exame.medicoName ?? '').toLowerCase().includes(searchTerm.toLowerCase());

    const now = new Date();
    const exameDate = exame.dataHora ? new Date(exame.dataHora) : (exame.datahora ? new Date(exame.datahora) : null);

    let matchesStatus = true;
    switch (filterStatus) {
      case 'todos': matchesStatus = true; break;
      case 'agendados': matchesStatus = exame.statusNormalized === 'agendado'; break;
      case 'cancelados': matchesStatus = exame.statusNormalized === 'cancelado'; break;
      case 'resultado pendente':
        // Unifica pendentes e pendentes de resultado: qualquer PENDENTE
        matchesStatus = exame.statusNormalized === 'pendente'; break;
      case 'resultado': matchesStatus = exame.statusNormalized === 'resultado' || exame.statusNormalized === 'realizado'; break;
      default: matchesStatus = true;
    }

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
            <div className="text-2xl font-bold text-foreground">{totalAgendados}</div>
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
                  <Input placeholder="Pesquisar por paciente, tipo ou médico" value={searchTerm} onChange={(e) => setSearchTerm(e.target.value)} />
                </div>
                <div className="flex items-center gap-2 overflow-auto">
                  
                  <Button variant={filterStatus === 'agendados' ? 'default' : 'outline'} size="sm" onClick={() => setFilterStatus('agendados')}>Agendados</Button>
                  <Button variant={filterStatus === 'cancelados' ? 'default' : 'outline'} size="sm" onClick={() => setFilterStatus('cancelados')}>Cancelados</Button>
                  <Button variant={filterStatus === 'resultado pendente' ? 'default' : 'outline'} size="sm" onClick={() => setFilterStatus('resultado pendente')}>Resultado Pendente</Button>
                  <Button variant={filterStatus === 'resultado' ? 'default' : 'outline'} size="sm" onClick={() => setFilterStatus('resultado')}>Resultado</Button>
				  <Button variant={filterStatus === 'todos' ? 'default' : 'outline'} size="sm" onClick={() => setFilterStatus('todos')}>Todos</Button>
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
                            <h3 className="font-semibold">{exame.pacienteName}</h3>
                            <p className="text-sm text-muted-foreground">{exame.tipoLabel}</p>
                            <p className="text-sm text-muted-foreground">Médico: <span className="font-medium text-foreground">{exame.medicoName}</span></p>
                          </div>
                          <div className="text-right">
                            <div className="text-sm text-muted-foreground">{new Date(exame.dataHora || exame.datahora || exame.data).toLocaleString()}</div>
                            <div className="mt-2">{getStatusBadge(exame.statusNormalized ?? 'pendente')}</div>
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
                            {excluirExame.isLoading && exameToDelete === exame.id ? (
                              <Loader2 className="h-4 w-4 animate-spin" />
                            ) : (
                              <Trash2 className="h-4 w-4" />
                            )}
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

      {/* Confirm dialog para exclusão de exame */}
      <AlertDialog open={!!exameToDelete} onOpenChange={() => setExameToDelete(null)}>
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle>Confirmar exclusão do exame</AlertDialogTitle>
            <AlertDialogDescription>
              Tem certeza que deseja excluir este exame? Esta ação irá remover o agendamento.
            </AlertDialogDescription>
          </AlertDialogHeader>
          <AlertDialogFooter>
            <AlertDialogCancel>Cancelar</AlertDialogCancel>
            <AlertDialogAction onClick={() => exameToDelete && handleDeleteExame(exameToDelete)}>
              {excluirExame.isLoading && exameToDelete ? <Loader2 className="w-4 h-4 animate-spin" /> : 'Confirmar'}
            </AlertDialogAction>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>

      {/* Confirm dialog para cancelamento de exame */}
      <AlertDialog open={!!exameToCancel} onOpenChange={() => setExameToCancel(null)}>
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle>Confirmar cancelamento do exame</AlertDialogTitle>
            <AlertDialogDescription>
              Tem certeza que deseja cancelar este exame? O exame será marcado como cancelado.
            </AlertDialogDescription>
          </AlertDialogHeader>
          <AlertDialogFooter>
            <AlertDialogCancel>Cancelar</AlertDialogCancel>
            <AlertDialogAction onClick={() => exameToCancel && handleCancelExame(exameToCancel)}>
              {cancelarExame.isLoading && exameToCancel ? <Loader2 className="w-4 h-4 animate-spin" /> : 'Confirmar'}
            </AlertDialogAction>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>
    </div>
  );
}