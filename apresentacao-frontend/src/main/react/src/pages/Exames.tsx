import { useState, useEffect } from "react";
import { TestTube, Search, Upload, Download, Clock, CheckCircle, XCircle, AlertCircle, Plus, Edit, Trash2, List, Loader2, Users, BarChart2 } from "lucide-react";
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
import { useAgendarExame, useAtualizarExame, useCancelarExame, useExcluirExame, useExamesList, useUploadResultadoExame, useMudarStatusExame } from "@/hooks/useExames";

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
  // HISTÓRICO GERAL: incluir também EXCLUIDOS e CANCELADOS
  const { data: examesHistorico = [] } = useExamesList(['AGENDADO','PENDENTE','REALIZADO','CANCELADO','EXCLUIDO']);
  // Fonte de exames depende do filtro atual para manter consistência entre operações (cancelar / excluir)
  let sourceExames: any[] = [];
  switch (filterStatus) {
    case 'agendados': sourceExames = examesAgendados; break;
    case 'cancelados': sourceExames = examesCancelados; break;
    case 'resultado': sourceExames = examesResultado; break;
    case 'resultado pendente': sourceExames = examesPendentes; break;
    case 'historico': sourceExames = examesHistorico; break;
    case 'todos':
    default:
      sourceExames = Array.from(new Map([...examesAgendados, ...examesPendentes, ...examesResultado].map((e:any) => [e.id, e])).values());
      break;
  }

  const agendar = useAgendarExame();
  const atualizarExame = useAtualizarExame();
  const cancelarExame = useCancelarExame();
  const excluirExame = useExcluirExame();
  const uploadResultado = useUploadResultadoExame();
  const mudarStatusExame = useMudarStatusExame();

  const { data: pacientes = [] } = useListarPacientes();
  const { data: funcionarios = [] } = useListarFuncionarios();

  const [isExameFormOpen, setIsExameFormOpen] = useState(false);
  const [editingExame, setEditingExame] = useState<any | null>(null);
  const [exameToDelete, setExameToDelete] = useState<number | null>(null);
  const [exameToCancel, setExameToCancel] = useState<number | null>(null);
  // motivo obrigatório para cancelamento
  const [cancelReason, setCancelReason] = useState<string>("");

  // Novo: per-exam histórico dialog state
  const [historicoOpenFor, setHistoricoOpenFor] = useState<number | null>(null);
  const [historicoItems, setHistoricoItems] = useState<any[] | null>(null);
  // file input ref state (controlled via DOM id)
  const [uploadingFor, setUploadingFor] = useState<number | null>(null);

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
    // abrir seletor de arquivo:** usamos input[type=file] com id baseado no exame
    const id = `exame-upload-${exameId}`;
    const input = document.getElementById(id) as HTMLInputElement | null;
    if (input) {
      setUploadingFor(Number(exameId));
      input.value = ""; // reset
      input.click();
    } else {
      toast({ title: 'Erro', description: 'Não foi possível abrir o seletor de arquivos.' });
    }
  };
  
  // Handler chamado quando o usuário escolhe um arquivo
  const onFileSelected = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files && e.target.files[0];
    const exameIdStr = e.target.getAttribute('data-exame-id');
    const exameId = exameIdStr ? Number(exameIdStr) : uploadingFor;
    if (!file || !exameId) return;
    try {
      await uploadResultado.mutateAsync({ id: exameId, file });
      toast({ title: 'Upload concluído', description: 'Resultado anexado ao exame.' });
      setUploadingFor(null);
      // refresh histórico
      examesApi.obter(exameId).then((resp:any)=> setHistoricoItems(resp.historico || []) ).catch(()=>{});
    } catch (err:any) {
      toast({ title: 'Erro no upload', description: err?.message ?? 'Falha ao enviar arquivo' , variant: 'destructive'});
    }
  };

  // Permite ao gestor marcar resultado/pendente
  const handleChangeStatus = async (exameId:number, novoStatus:string) => {
    try {
      const responsavelId = Number((window as any)._currentUserId ?? 1);
      await mudarStatusExame.mutateAsync({ id: exameId, novoStatus, responsavelId, descricao: `Alterado via UI para ${novoStatus}` });
      toast({ title: 'Status atualizado', description: `Status atualizado para ${novoStatus}` });
    } catch (err:any) {
      toast({ title: 'Erro', description: err?.message ?? 'Erro ao alterar status', variant: 'destructive' });
    }
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
      return agendar.mutateAsync(payloadCreate)
        .catch((e: any) => {
          // Já temos o erro amigável mapeado no apiClient, então mostrar toast com e.message
          toast({ title: 'Erro ao agendar', description: e?.message ?? 'Erro desconhecido', variant: 'destructive' });
          throw e;
        });
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

  // Novo: carregar histórico por exame quando solicitado
  useEffect(() => {
    let mounted = true;
    if (historicoOpenFor != null) {
      examesApi.obter(historicoOpenFor)
        .then((resp: any) => { if (mounted) setHistoricoItems(resp.historico || []); })
        .catch(() => { if (mounted) setHistoricoItems([]); });
    } else {
      setHistoricoItems(null);
    }
    return () => { mounted = false; };
  }, [historicoOpenFor]);

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

  // Total correto de solicitações: buscar todos os exames (sem filtro) e deduplicar por id
  const { data: examesTodos = [] } = useExamesList();
  const { data: examesExcluidos = [] } = useExamesList('EXCLUIDO');

  // garantir deduplicação por id caso múltiplas queries retornem dados sobre as mesmas entradas
  const uniqueIds = new Set<number>();
  examesTodos.forEach((e: any) => e && e.id && uniqueIds.add(e.id));
  const totalSolicitados = uniqueIds.size;

  const totalAgendados = examesAgendados.length;
  const totalPendentes = examesPendentes.length;
  const totalResultado = examesResultado.length;
  const totalCancelados = examesCancelados.length;
  const totalExcluidos = examesExcluidos.length || 0;

  // Estatísticas: tipos mais solicitados (top 5)
  const tipoCounts: Record<string, number> = {};
  examesTodos.forEach((e: any) => {
    // Evita erro do parser: parentizar a expressão que usa nullish coalescing junto com ||
    const tipoKeySource = (e.tipoExame ?? e.tipo);
    const key = String(tipoKeySource || 'Desconhecido');
    tipoCounts[key] = (tipoCounts[key] || 0) + 1;
  });
  const tiposMaisSolicitados = Object.entries(tipoCounts)
    .sort((a,b) => b[1] - a[1])
    .slice(0,5)
    .map(([tipo, count]) => ({ tipo, count }));

  // Estatísticas: pacientes mais frequentes (top 5)
  const pacienteCounts: Record<string, number> = {};
  examesTodos.forEach((e: any) => {
    const pid = String(e.pacienteId ?? '');
    pacienteCounts[pid] = (pacienteCounts[pid] || 0) + 1;
  });
  const pacientesMaisFrequentes = Object.entries(pacienteCounts)
    .sort((a,b) => b[1] - a[1])
    .slice(0,5)
    .map(([pid, count]) => ({
      id: pid,
      name: (pacientes.find((p: any) => String(p.id) === pid)?.name) || `Paciente ${pid}`,
      count
    }));

  // resultados pendentes (contagem) — já temos examesPendentes
  const resultadosPendentes = totalPendentes;

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
      case 'historico': matchesStatus = true; break;
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
            <div className="text-2xl font-bold text-foreground">{totalSolicitados}</div>
            <p className="text-xs text-muted-foreground">Total (todos os status)</p>
          </CardContent>
        </Card>

        <Card className="shadow-card">
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium text-muted-foreground">Resultados Pendentes</CardTitle>
            <Clock className="w-4 h-4 text-amber-600" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold text-foreground">{resultadosPendentes}</div>
            <p className="text-xs text-muted-foreground">Exames sem resultado</p>
          </CardContent>
        </Card>

        <Card className="shadow-card">
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium text-muted-foreground">Cancelados</CardTitle>
            <XCircle className="w-4 h-4 text-destructive" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold text-foreground">{totalCancelados}</div>
            <p className="text-xs text-muted-foreground">Agendamentos cancelados</p>
          </CardContent>
        </Card>

        <Card className="shadow-card">
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium text-muted-foreground">Excluídos</CardTitle>
            <AlertCircle className="w-4 h-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold text-foreground">{totalExcluidos}</div>
            <p className="text-xs text-muted-foreground">Registros excluídos</p>
          </CardContent>
        </Card>

        <Card className="shadow-card">
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium text-muted-foreground">Tipos mais solicitados</CardTitle>
            <BarChart2 className="w-4 h-4 text-primary" />
          </CardHeader>
          <CardContent>
            {tiposMaisSolicitados.length === 0 ? (
              <div className="text-sm text-muted-foreground">Sem dados</div>
            ) : (
              <ul className="text-sm space-y-1">
                {tiposMaisSolicitados.slice(0,3).map((t: any, idx: number) => (
                  <li key={idx} className="flex justify-between">
                    <span className="truncate">{t.tipo}</span>
                    <span className="font-medium">{t.count}</span>
                  </li>
                ))}
              </ul>
            )}
          </CardContent>
        </Card>

        <Card className="shadow-card">
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium text-muted-foreground">Pacientes mais frequentes</CardTitle>
            <Users className="w-4 h-4 text-primary" />
          </CardHeader>
          <CardContent>
            {pacientesMaisFrequentes.length === 0 ? (
              <div className="text-sm text-muted-foreground">Sem dados</div>
            ) : (
              <ul className="text-sm space-y-1">
                {pacientesMaisFrequentes.slice(0,3).map((p: any, idx: number) => (
                  <li key={idx} className="flex justify-between">
                    <span className="truncate">{p.name}</span>
                    <span className="font-medium">{p.count}</span>
                  </li>
                ))}
              </ul>
            )}
          </CardContent>
        </Card>
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
                  <Button variant={filterStatus === 'historico' ? 'default' : 'outline'} size="sm" onClick={() => setFilterStatus('historico')}>Histórico</Button>
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
                          {/* excluir só se agendado e não vinculado a laudo/prontuário */}
                          <Button size="sm" variant="outline" onClick={() => setExameToDelete(exame.id)} disabled={!(exame.statusNormalized === 'agendado') || !!(exame.temLaudo || exame.possuiLaudo || exame.laudoId)}>
                            {excluirExame.isLoading && exameToDelete === exame.id ? (
                              <Loader2 className="h-4 w-4 animate-spin" />
                            ) : (
                              <Trash2 className="h-4 w-4" />
                            )}
                          </Button>
                          
                          {/* upload: abre seletor de arquivo */}
                          <input id={`exame-upload-${exame.id}`} data-exame-id={String(exame.id)} type="file" accept="*/*" className="hidden" onChange={onFileSelected} />
                          <Button size="sm" variant="outline" onClick={() => handleUploadResult(String(exame.id))} title="Anexar resultado">
                            <Upload className="h-4 w-4" />
                          </Button>

                          {/* Histórico compacto dentro do card */}
                          <Button size="sm" variant="outline" onClick={() => setHistoricoOpenFor(exame.id)} title="Histórico">
                            <List className="h-4 w-4" />
                          </Button>

                          {/* Se for gestor, permitir alterar status de pendente->resultado ou marcar pendente */}
                          {isGestor && (
                            <div className="ml-2">
                              <select
                                value={exame.statusNormalized}
                                onChange={(ev) => handleChangeStatus(exame.id, ev.target.value)}
                                className="text-sm p-1 border rounded"
                              >
                                <option value="agendado">Agendado</option>
                                <option value="pendente">Pendente</option>
                                <option value="resultado">Resultado</option>
                              </select>
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
      <AlertDialog open={!!exameToCancel} onOpenChange={(open) => { if (!open) { setExameToCancel(null); setCancelReason(""); } }}>
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle>Confirmar cancelamento do exame</AlertDialogTitle>
            <AlertDialogDescription>
              Informe o motivo do cancelamento (obrigatório). O exame será marcado como cancelado e o motivo ficará registrado no histórico.
            </AlertDialogDescription>
          </AlertDialogHeader>

          <div className="px-6 pb-4">
            <label htmlFor="cancelReason" className="text-sm font-medium">Motivo do cancelamento</label>
            <textarea
              id="cancelReason"
              value={cancelReason}
              onChange={(e) => setCancelReason(e.target.value)}
              className="w-full mt-2 p-2 border rounded-md min-h-[80px]"
              placeholder="Descreva o motivo do cancelamento..."
            />
            {cancelReason.trim().length === 0 && (
              <p className="text-sm text-destructive mt-1">É obrigatório informar o motivo do cancelamento.</p>
            )}
          </div>

          <AlertDialogFooter>
            <AlertDialogCancel onClick={() => { setExameToCancel(null); setCancelReason(""); }}>Cancelar</AlertDialogCancel>
            <AlertDialogAction
              onClick={() => exameToCancel && handleCancelExame(exameToCancel, cancelReason.trim())}
              disabled={cancelReason.trim().length === 0 || (cancelarExame.isLoading && exameToCancel !== null)}
            >
              {cancelarExame.isLoading && exameToCancel ? <Loader2 className="w-4 h-4 animate-spin" /> : 'Confirmar'}
            </AlertDialogAction>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>

      {/* Histórico modal (por exame) */}
      <AlertDialog open={historicoOpenFor !== null} onOpenChange={(open) => { if (!open) setHistoricoOpenFor(null); }}>
        <AlertDialogContent>
          <AlertDialogHeader>
            <div className="flex items-center justify-between w-full">
              <div>
                <AlertDialogTitle>Histórico do Exame</AlertDialogTitle>
                <AlertDialogDescription>Registros de alterações e ações realizadas neste exame.</AlertDialogDescription>
              </div>
              <div>
                <Button variant="ghost" size="sm" onClick={() => setHistoricoOpenFor(null)} aria-label="Fechar histórico">✕</Button>
              </div>
            </div>
          </AlertDialogHeader>
          <div className="px-6 pb-4">
            {historicoItems === null ? (
              <div className="py-6">Carregando...</div>
            ) : historicoItems.length === 0 ? (
              <div className="py-6">Nenhum histórico encontrado para este exame.</div>
            ) : (
              <ul className="space-y-2">
                {historicoItems.map((h, idx) => (
                  <li key={idx} className="p-2 rounded bg-muted/5">
                    <div className="text-xs text-muted-foreground">{new Date(h.dataHora).toLocaleString()} — <span className="font-medium">{h.acao}</span></div>
                    <div className="mt-1">{h.descricao}</div>
                    {h.responsavelId && <div className="text-xs text-muted-foreground mt-1">Responsável: {h.responsavelId}</div>}
                  </li>
                ))}
              </ul>
            )}
          </div>
        </AlertDialogContent>
      </AlertDialog>

    </div>
  );
}
