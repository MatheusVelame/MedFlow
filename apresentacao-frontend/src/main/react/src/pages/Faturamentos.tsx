import { useState, useMemo } from "react";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { FileText, DollarSign, Clock, CheckCircle, Edit, Trash2, Filter, Loader2, Eye, Plus } from "lucide-react";
import { FaturamentoForm } from "@/components/FaturamentoForm";
import { FaturamentoDetalhesDialog } from "@/components/FaturamentoDetalhesDialog";
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
import { useAuth } from "@/contexts/AuthContext";
import {
  useListarFaturamentos,
  useFaturamentosPorStatus,
  useRegistrarFaturamento,
  useMarcarComoPago,
  useCancelarFaturamento,
  mapStatusToDisplay,
  mapTipoProcedimentoToDisplay,
  type FaturamentoResumo,
  type RegistrarFaturamentoPayload,
} from "@/api/useFaturamentosApi";
import { useListarPacientes } from "@/api/usePacientesApi";

export default function Faturamentos() {
  const { user, isGestor, isAtendente } = useAuth();
  const [editingFaturamento, setEditingFaturamento] = useState<FaturamentoResumo | null>(null);
  const [faturamentoToCancelar, setFaturamentoToCancelar] = useState<string | null>(null);
  const [faturamentoToView, setFaturamentoToView] = useState<string | null>(null);
  const [statusFilter, setStatusFilter] = useState<string>("todos");
  const [isFormOpen, setIsFormOpen] = useState(false);

  // Queries
  const { data: faturamentos = [], isLoading: isLoadingFaturamentos, error: errorFaturamentos } = useListarFaturamentos();
  const { data: faturamentosPorStatus = [], isLoading: isLoadingPorStatus } = useFaturamentosPorStatus(
    statusFilter !== "todos" ? statusFilter : null
  );
  const { data: pacientes = [] } = useListarPacientes();

  // Mutations
  const registrarFaturamentoMutation = useRegistrarFaturamento();
  const marcarComoPagoMutation = useMarcarComoPago();
  const cancelarFaturamentoMutation = useCancelarFaturamento();

  // Mapear pacientes para lookup rápido
  const pacientesMap = useMemo(() => {
    const map = new Map<number, { nome: string }>();
    pacientes.forEach(p => {
      map.set(p.id, { nome: p.name });
    });
    return map;
  }, [pacientes]);

  // Enriquecer faturamentos com dados do paciente
  const faturamentosEnriquecidos = useMemo(() => {
    const lista = statusFilter === "todos" ? faturamentos : faturamentosPorStatus;
    return lista.map(faturamento => {
      const pacienteIdNum = parseInt(faturamento.pacienteId);
      const paciente = pacientesMap.get(pacienteIdNum);
      return {
        ...faturamento,
        pacienteNome: paciente?.nome || "Paciente não encontrado",
      };
    });
  }, [faturamentos, faturamentosPorStatus, statusFilter, pacientesMap]);

  const handleNovoFaturamento = (data: any) => {
    const payload: RegistrarFaturamentoPayload = {
      pacienteId: data.pacienteId,
      tipoProcedimento: data.procedimentoTipo.toUpperCase() as "CONSULTA" | "EXAME",
      descricaoProcedimento: data.procedimentoDescricao,
      valor: data.valor,
      metodoPagamento: data.metodoPagamento.toUpperCase().replace(/\s+/g, "_"),
      usuarioResponsavel: user?.id || "1",
      observacoes: data.observacoes,
    };

    registrarFaturamentoMutation.mutate(payload, {
      onSuccess: () => {
        setEditingFaturamento(null);
        setIsFormOpen(false);
      }
    });
  };

  const handleEdit = (faturamento: FaturamentoResumo) => {
    setEditingFaturamento(faturamento);
  };

  const handleCancelar = (id: string) => {
    cancelarFaturamentoMutation.mutate({
      id,
      payload: {
        motivo: "Cancelamento solicitado pelo usuário",
        usuarioResponsavel: user?.id || "1",
      }
    });
    setFaturamentoToCancelar(null);
  };

  const handleStatusChange = (id: string, newStatus: string) => {
    if (newStatus === "PAGO") {
      marcarComoPagoMutation.mutate({
        id,
        payload: {
          usuarioResponsavel: user?.id || "1",
        }
      });
    } else if (newStatus === "CANCELADO") {
      setFaturamentoToCancelar(id);
    }
  };

  const getStatusBadge = (status: string) => {
    const variants: Record<string, "default" | "secondary" | "destructive"> = {
      PENDENTE: "secondary",
      PAGO: "default",
      CANCELADO: "destructive",
      INVALIDO: "destructive",
      REMOVIDO: "secondary"
    };
    return <Badge variant={variants[status] || "default"}>{mapStatusToDisplay(status)}</Badge>;
  };

  const totalPendente = faturamentos
    .filter(f => f.status === "PENDENTE")
    .reduce((acc, f) => acc + Number(f.valor), 0);

  const totalPago = faturamentos
    .filter(f => f.status === "PAGO")
    .reduce((acc, f) => acc + Number(f.valor), 0);

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
              {isLoadingFaturamentos ? (
                <Loader2 className="w-6 h-6 animate-spin" />
              ) : (
                totalPendente.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })
              )}
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
              {isLoadingFaturamentos ? (
                <Loader2 className="w-6 h-6 animate-spin" />
              ) : (
                totalPago.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })
              )}
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
            <div className="text-2xl font-bold">
              {isLoadingFaturamentos ? (
                <Loader2 className="w-6 h-6 animate-spin" />
              ) : (
                faturamentos.length
              )}
            </div>
            <p className="text-xs text-muted-foreground mt-1">Registros no sistema</p>
          </CardContent>
        </Card>
        </div>

      <div className="flex justify-end mb-4">
        {(isGestor || isAtendente) && (
          <Button onClick={() => setIsFormOpen(true)} className="gap-2">
            <Plus className="h-4 w-4" />
            Novo Faturamento
          </Button>
        )}
      </div>

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
                  <SelectItem value="PENDENTE">Pendente</SelectItem>
                  <SelectItem value="PAGO">Pago</SelectItem>
                  <SelectItem value="CANCELADO">Cancelado</SelectItem>
                </SelectContent>
              </Select>
            </div>
          </div>
        </CardHeader>
            <CardContent>
              <div className="space-y-4">
                {isLoadingFaturamentos || isLoadingPorStatus ? (
                  <div className="flex items-center justify-center py-12">
                    <Loader2 className="w-8 h-8 animate-spin text-primary" />
                  </div>
                ) : errorFaturamentos ? (
                  <div className="text-center py-12 text-destructive">
                    Erro ao carregar faturamentos. Tente novamente.
                  </div>
                ) : faturamentosEnriquecidos.length === 0 ? (
                  <div className="text-center py-8 text-muted-foreground">
                    Nenhum faturamento encontrado com este filtro.
                  </div>
                ) : (
                  faturamentosEnriquecidos.map((faturamento) => (
                    <Card key={faturamento.id} className="hover:shadow-md transition-shadow">
                      <CardContent className="pt-6">
                        <div className="flex items-start justify-between">
                          <div className="space-y-2 flex-1">
                            <div className="flex items-center gap-2">
                              <h3 className="font-semibold">{faturamento.pacienteNome}</h3>
                              {getStatusBadge(faturamento.status)}
                            </div>
                            
                            <div className="text-sm text-muted-foreground space-y-1">
                              <p><strong>Procedimento:</strong> {faturamento.descricaoProcedimento}</p>
                              <p><strong>Tipo:</strong> {mapTipoProcedimentoToDisplay(faturamento.tipoProcedimento)}</p>
                              <p><strong>Pagamento:</strong> {faturamento.metodoPagamento}</p>
                              <p><strong>Data:</strong> {new Date(faturamento.dataHoraFaturamento).toLocaleString('pt-BR')}</p>
                            </div>
                          </div>
                          
                          <div className="flex flex-col items-end gap-3">
                            <div className="text-2xl font-bold text-primary">
                              {Number(faturamento.valor).toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })}
                            </div>
                            
                            {isGestor && faturamento.status === "PENDENTE" && (
                              <Select 
                                value={faturamento.status} 
                                onValueChange={(value: string) => handleStatusChange(faturamento.id, value)}
                                disabled={marcarComoPagoMutation.isPending || cancelarFaturamentoMutation.isPending}
                              >
                                <SelectTrigger className="w-[130px]">
                                  <SelectValue />
                                </SelectTrigger>
                                <SelectContent>
                                  <SelectItem value="PENDENTE">Pendente</SelectItem>
                                  <SelectItem value="PAGO">Pago</SelectItem>
                                  <SelectItem value="CANCELADO">Cancelado</SelectItem>
                                </SelectContent>
                              </Select>
                            )}

                            <div className="flex gap-2">
                              <Button 
                                variant="outline" 
                                size="sm"
                                onClick={() => setFaturamentoToView(faturamento.id)}
                              >
                                <Eye className="h-4 w-4 mr-1" />
                                Ver
                              </Button>
                              {isGestor && faturamento.status === "PENDENTE" && (
                                <Button 
                                  variant="outline" 
                                  size="sm"
                                  onClick={() => {
                                    setEditingFaturamento(faturamento);
                                    setIsFormOpen(true);
                                  }}
                                >
                                  <Edit className="h-4 w-4" />
                                </Button>
                              )}
                              {(isGestor || isAtendente) && faturamento.status === "PENDENTE" && (
                                <Button 
                                  variant="outline" 
                                  size="sm"
                                  onClick={() => setFaturamentoToCancelar(faturamento.id)}
                                >
                                  <Trash2 className="h-4 w-4" />
                                </Button>
                              )}
                            </div>
                          </div>
                        </div>
                      </CardContent>
                    </Card>
                  ))
                )}
              </div>
            </CardContent>
          </Card>

      <FaturamentoForm
        open={isFormOpen}
        onOpenChange={(open) => {
          if (!open) {
            setEditingFaturamento(null);
          }
          setIsFormOpen(open);
        }}
        onSubmit={handleNovoFaturamento}
        initialData={editingFaturamento ? {
          pacienteId: editingFaturamento.pacienteId,
          pacienteNome: faturamentosEnriquecidos.find(f => f.id === editingFaturamento.id)?.pacienteNome || "",
          procedimentoTipo: editingFaturamento.tipoProcedimento.toLowerCase() as "consulta" | "exame",
          procedimentoDescricao: editingFaturamento.descricaoProcedimento,
          valor: Number(editingFaturamento.valor),
          metodoPagamento: editingFaturamento.metodoPagamento,
          observacoes: ""
        } : undefined}
        onCancel={() => {
          setEditingFaturamento(null);
          setIsFormOpen(false);
        }}
      />

      <FaturamentoDetalhesDialog
        open={faturamentoToView !== null}
        onOpenChange={(open) => !open && setFaturamentoToView(null)}
        faturamentoId={faturamentoToView}
      />

      <AlertDialog open={!!faturamentoToCancelar} onOpenChange={() => setFaturamentoToCancelar(null)}>
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle>Confirmar cancelamento</AlertDialogTitle>
            <AlertDialogDescription>
              Tem certeza que deseja cancelar este faturamento? Esta ação não pode ser desfeita.
            </AlertDialogDescription>
          </AlertDialogHeader>
          <AlertDialogFooter>
            <AlertDialogCancel>Cancelar</AlertDialogCancel>
            <AlertDialogAction onClick={() => faturamentoToCancelar && handleCancelar(faturamentoToCancelar)}>
              Confirmar Cancelamento
            </AlertDialogAction>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>
    </div>
  );
}
