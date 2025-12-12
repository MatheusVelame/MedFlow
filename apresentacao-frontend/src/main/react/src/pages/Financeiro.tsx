import { useState, useMemo } from "react";
import { format } from "date-fns";
import { DollarSign, TrendingUp, TrendingDown, CreditCard, FileText, Clock, Plus, UserCheck, Edit, Trash2, Loader2 } from "lucide-react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { FolhaPagamentoForm } from "@/components/FolhaPagamentoForm";
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
import { useListarFaturamentos } from "@/api/useFaturamentosApi";
import { useListarConvenios } from "@/api/useConveniosApi";
import { useListarFuncionarios } from "@/api/useFuncionariosApi";
import {
  useListarFolhasPagamento,
  useRegistrarFolhaPagamento,
  useAlterarStatusFolha,
  useRemoverFolhaPagamento,
  type FolhaPagamentoResumo,
  type StatusFolha,
  type TipoRegistro
} from "@/api/useFolhaPagamentoApi";

export default function Financeiro() {
  const { isGestor, user } = useAuth();
  const [isFormOpen, setIsFormOpen] = useState(false);
  const [editingPagamento, setEditingPagamento] = useState<FolhaPagamentoResumo | null>(null);
  const [pagamentoToDelete, setPagamentoToDelete] = useState<number | null>(null);

  // Queries
  const { data: faturamentos = [], isLoading: isLoadingFaturamentos } = useListarFaturamentos();
  const { data: convenios = [], isLoading: isLoadingConvenios } = useListarConvenios();
  const { data: funcionarios = [], isLoading: isLoadingFuncionarios } = useListarFuncionarios();
  const { data: folhasPagamento = [], isLoading: isLoadingFolhas } = useListarFolhasPagamento();

  // Mutations
  const registrarFolhaMutation = useRegistrarFolhaPagamento();
  const alterarStatusMutation = useAlterarStatusFolha();
  const removerFolhaMutation = useRemoverFolhaPagamento();

  // Cálculos baseados em dados reais
  const totalFaturamentoPendente = useMemo(() => 
    faturamentos
      .filter(f => f.status === "PENDENTE")
      .reduce((acc, f) => acc + Number(f.valor), 0),
    [faturamentos]
  );

  const totalFaturamentoPago = useMemo(() =>
    faturamentos
      .filter(f => f.status === "PAGO")
      .reduce((acc, f) => acc + Number(f.valor), 0),
    [faturamentos]
  );

  const totalFolhaPendente = useMemo(() =>
    folhasPagamento
      .filter(f => f.status === "PENDENTE")
      .reduce((acc, f) => acc + Number(f.valorLiquido), 0),
    [folhasPagamento]
  );

  const totalFolhaPago = useMemo(() =>
    folhasPagamento
      .filter(f => f.status === "PAGO")
      .reduce((acc, f) => acc + Number(f.valorLiquido), 0),
    [folhasPagamento]
  );

  const handleSavePagamento = (data: any) => {
    const usuarioResponsavelId = parseInt(user?.id || "1");
    
    if (editingPagamento) {
      toast.info("Funcionalidade de edição em desenvolvimento");
      setEditingPagamento(null);
      setIsFormOpen(false);
    } else {
      registrarFolhaMutation.mutate({
        funcionarioId: parseInt(data.profissionalId),
        periodoReferencia: format(data.mesReferencia, "yyyy-MM"),
        tipoRegistro: "MENSALISTA" as TipoRegistro,
        salarioBase: data.salarioBase,
        beneficios: data.bonus || 0,
        metodoPagamento: data.metodoPagamento,
        usuarioResponsavelId: usuarioResponsavelId,
        funcionarioAtivo: true
      }, {
        onSuccess: () => {
          setIsFormOpen(false);
        }
      });
    }
  };

  const handleEdit = (pagamento: FolhaPagamentoResumo) => {
    setEditingPagamento(pagamento);
    setIsFormOpen(true);
  };

  const handleDelete = () => {
    if (pagamentoToDelete) {
      const usuarioResponsavelId = parseInt(user?.id || "1");
      removerFolhaMutation.mutate({
        id: pagamentoToDelete,
        usuarioResponsavelId: usuarioResponsavelId
      }, {
        onSuccess: () => {
          setPagamentoToDelete(null);
        }
      });
    }
  };

  const handleMarkAsPaid = (id: number) => {
    const usuarioResponsavelId = parseInt(user?.id || "1");
    alterarStatusMutation.mutate({
      id: id,
      payload: {
        novoStatus: "PAGO" as StatusFolha,
        usuarioResponsavelId: usuarioResponsavelId
      }
    });
  };
  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold text-foreground">Financeiro</h1>
          <p className="text-muted-foreground">Controle de receitas, despesas e convênios</p>
        </div>
        <Button className="bg-gradient-primary text-white hover:opacity-90">
          <FileText className="w-4 h-4 mr-2" />
          Gerar Relatório
        </Button>
      </div>

      <div className="grid gap-6 md:grid-cols-4">
        <Card className="shadow-card">
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium text-muted-foreground">
              Receita Paga
            </CardTitle>
            <TrendingUp className="w-4 h-4 text-success" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold text-foreground">
              {isLoadingFaturamentos ? <Loader2 className="w-6 h-6 animate-spin" /> : totalFaturamentoPago.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })}
            </div>
            <p className="text-xs text-success mt-1">Faturamentos pagos</p>
          </CardContent>
        </Card>

        <Card className="shadow-card">
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium text-muted-foreground">
              Despesas (Folha)
            </CardTitle>
            <TrendingDown className="w-4 h-4 text-destructive" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold text-foreground">
              {isLoadingFolhas ? <Loader2 className="w-6 h-6 animate-spin" /> : totalFolhaPago.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })}
            </div>
            <p className="text-xs text-muted-foreground mt-1">Folha de pagamento</p>
          </CardContent>
        </Card>

        <Card className="shadow-card">
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium text-muted-foreground">
              A Receber
            </CardTitle>
            <Clock className="w-4 h-4 text-warning" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold text-foreground">
              {isLoadingFaturamentos ? <Loader2 className="w-6 h-6 animate-spin" /> : totalFaturamentoPendente.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })}
            </div>
            <p className="text-xs text-muted-foreground mt-1">Faturamentos pendentes</p>
          </CardContent>
        </Card>

        <Card className="shadow-card">
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium text-muted-foreground">
              Saldo Líquido
            </CardTitle>
            <DollarSign className="w-4 h-4 text-primary" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold text-foreground">
              {(isLoadingFaturamentos || isLoadingFolhas) ? <Loader2 className="w-6 h-6 animate-spin" /> : (totalFaturamentoPago - totalFolhaPago).toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })}
            </div>
            <p className="text-xs text-success mt-1">Receitas - Despesas</p>
          </CardContent>
        </Card>
      </div>

      <Tabs defaultValue="faturamentos" className="space-y-6">
        <TabsList>
          <TabsTrigger value="faturamentos">Faturamentos</TabsTrigger>
          <TabsTrigger value="convenios">Convênios</TabsTrigger>
          <TabsTrigger value="folha">Folha de Pagamento</TabsTrigger>
        </TabsList>

        <TabsContent value="faturamentos" className="space-y-4">
          <Card className="shadow-card">
            <CardHeader>
              <CardTitle>Últimos Faturamentos</CardTitle>
            </CardHeader>
            <CardContent>
              {isLoadingFaturamentos ? (
                <div className="flex items-center justify-center py-12">
                  <Loader2 className="w-8 h-8 animate-spin text-primary" />
                </div>
              ) : faturamentos.length === 0 ? (
                <div className="text-center py-12 text-muted-foreground">
                  Nenhum faturamento registrado.
                </div>
              ) : (
                <div className="space-y-4">
                  {faturamentos.slice(0, 10).map((faturamento) => (
                    <div
                      key={faturamento.id}
                      className="flex items-center justify-between p-4 border border-border rounded-lg hover:bg-accent transition-colors"
                    >
                      <div className="flex items-center gap-4">
                        <div className="p-3 rounded-full bg-success/10">
                          <TrendingUp className="w-5 h-5 text-success" />
                        </div>
                        <div>
                          <p className="font-medium text-foreground">{faturamento.descricaoProcedimento}</p>
                          <div className="flex items-center gap-2 mt-1 text-sm text-muted-foreground">
                            <span>{new Date(faturamento.dataHoraFaturamento).toLocaleDateString('pt-BR')}</span>
                            <span>•</span>
                            <span className="flex items-center gap-1">
                              <CreditCard className="w-3 h-3" />
                              {faturamento.metodoPagamento}
                            </span>
                          </div>
                        </div>
                      </div>
                      <div className="text-right">
                        <p className="font-bold text-success">
                          {Number(faturamento.valor).toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })}
                        </p>
                        <Badge 
                          variant={faturamento.status === "PAGO" ? "default" : "secondary"}
                          className="mt-1"
                        >
                          {faturamento.status === "PAGO" ? "Pago" : faturamento.status === "PENDENTE" ? "Pendente" : faturamento.status}
                        </Badge>
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </CardContent>
          </Card>
        </TabsContent>

        <TabsContent value="convenios" className="space-y-4">
          {isLoadingConvenios ? (
            <div className="flex items-center justify-center py-12">
              <Loader2 className="w-8 h-8 animate-spin text-primary" />
            </div>
          ) : convenios.length === 0 ? (
            <div className="text-center py-12 text-muted-foreground">
              Nenhum convênio cadastrado.
            </div>
          ) : (
            <div className="grid gap-4">
              {convenios.map((convenio) => (
                <Card key={convenio.id} className="shadow-card">
                  <CardContent className="p-6">
                    <div className="flex items-center justify-between mb-4">
                      <h3 className="text-lg font-semibold text-foreground">{convenio.nome}</h3>
                      <div className="flex items-center gap-2">
                        <CreditCard className="w-5 h-5 text-primary" />
                        <Badge variant={convenio.status === "ATIVO" ? "default" : "secondary"}>
                          {convenio.status}
                        </Badge>
                      </div>
                    </div>
                    <div className="text-sm text-muted-foreground">
                      <p>Código: {convenio.codigoIdentificacao}</p>
                    </div>
                  </CardContent>
                </Card>
              ))}
            </div>
          )}
        </TabsContent>

        <TabsContent value="folha" className="space-y-4">
          <Card className="shadow-card">
            <CardHeader>
              <div className="flex items-center justify-between">
                <div>
                  <CardTitle>Folha de Pagamento</CardTitle>
                  <p className="text-sm text-muted-foreground mt-1">
                    Gerencie os pagamentos dos profissionais da clínica
                  </p>
                </div>
                {isGestor && (
                  <Button onClick={() => setIsFormOpen(true)} className="gap-2">
                    <Plus className="h-4 w-4" />
                    Novo Pagamento
                  </Button>
                )}
              </div>
            </CardHeader>
            <CardContent>
              <div className="grid gap-4 md:grid-cols-2 mb-6">
                <Card className="bg-warning/10 border-warning">
                  <CardContent className="pt-6">
                    <div className="flex items-center justify-between">
                      <div>
                        <p className="text-sm text-muted-foreground">Pagamentos Pendentes</p>
                        <p className="text-2xl font-bold text-warning mt-1">
                          {totalFolhaPendente.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })}
                        </p>
                      </div>
                      <Clock className="h-8 w-8 text-warning" />
                    </div>
                  </CardContent>
                </Card>
                
                <Card className="bg-success/10 border-success">
                  <CardContent className="pt-6">
                    <div className="flex items-center justify-between">
                      <div>
                        <p className="text-sm text-muted-foreground">Pagamentos Realizados</p>
                        <p className="text-2xl font-bold text-success mt-1">
                          {totalFolhaPago.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })}
                        </p>
                      </div>
                      <UserCheck className="h-8 w-8 text-success" />
                    </div>
                  </CardContent>
                </Card>
              </div>

              {isLoadingFolhas ? (
                <div className="flex items-center justify-center py-12">
                  <Loader2 className="w-8 h-8 animate-spin text-primary" />
                </div>
              ) : folhasPagamento.length === 0 ? (
                <div className="text-center py-12 text-muted-foreground">
                  <UserCheck className="h-12 w-12 mx-auto mb-4 opacity-50" />
                  <p>Nenhum pagamento registrado ainda.</p>
                </div>
              ) : (
                <div className="space-y-4">
                  {folhasPagamento.map((folha) => {
                    const funcionario = funcionarios.find(f => f.id === folha.funcionarioId);
                    return (
                      <Card key={folha.id} className="hover:shadow-md transition-shadow">
                        <CardContent className="pt-6">
                          <div className="flex items-start justify-between">
                            <div className="space-y-2 flex-1">
                              <div className="flex items-center gap-2">
                                <h3 className="font-semibold">{funcionario?.nome || `Funcionário ID: ${folha.funcionarioId}`}</h3>
                                <Badge variant={folha.status === "PAGO" ? "default" : folha.status === "PENDENTE" ? "secondary" : "destructive"}>
                                  {folha.status === "PAGO" ? "Pago" : folha.status === "PENDENTE" ? "Pendente" : "Cancelado"}
                                </Badge>
                              </div>
                              
                              <div className="text-sm text-muted-foreground space-y-1">
                                <p><strong>Referência:</strong> {folha.periodoReferencia}</p>
                                <p><strong>Função:</strong> {funcionario?.funcao || "N/A"}</p>
                              </div>
                            </div>
                            
                            <div className="flex flex-col items-end gap-3">
                              <div className="text-right">
                                <p className="text-sm text-muted-foreground">Valor Líquido</p>
                                <p className="text-2xl font-bold text-primary">
                                  {Number(folha.valorLiquido).toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })}
                                </p>
                              </div>

                              {isGestor && (
                                <div className="flex flex-col gap-2 w-full">
                                  {folha.status === "PENDENTE" && (
                                    <Button 
                                      size="sm" 
                                      className="w-full"
                                      onClick={() => handleMarkAsPaid(folha.id)}
                                    >
                                      <UserCheck className="h-4 w-4 mr-1" />
                                      Confirmar Pagamento
                                    </Button>
                                  )}
                                  <div className="flex gap-2">
                                    <Button 
                                      variant="outline" 
                                      size="sm"
                                      className="flex-1"
                                      onClick={() => handleEdit(folha)}
                                    >
                                      <Edit className="h-4 w-4" />
                                    </Button>
                                    <Button 
                                      variant="outline" 
                                      size="sm"
                                      onClick={() => setPagamentoToDelete(folha.id)}
                                    >
                                      <Trash2 className="h-4 w-4" />
                                    </Button>
                                  </div>
                                </div>
                              )}
                            </div>
                          </div>
                        </CardContent>
                      </Card>
                    );
                  })}
                </div>
              )}
            </CardContent>
          </Card>
        </TabsContent>

        <TabsContent value="relatorios" className="space-y-4">
          <Card className="shadow-card">
            <CardContent className="p-12 text-center">
              <FileText className="w-12 h-12 mx-auto text-muted-foreground mb-4" />
              <h3 className="text-lg font-semibold text-foreground mb-2">Relatórios Financeiros</h3>
              <p className="text-muted-foreground mb-6">
                Gere relatórios detalhados de receitas, despesas e convênios
              </p>
              <div className="flex gap-3 justify-center">
                <Button variant="outline">Relatório Mensal</Button>
                <Button variant="outline">Relatório Anual</Button>
                <Button className="bg-gradient-primary text-white">Personalizado</Button>
              </div>
            </CardContent>
          </Card>
        </TabsContent>
      </Tabs>

      <FolhaPagamentoForm
        open={isFormOpen}
        onOpenChange={setIsFormOpen}
        onSave={handleSavePagamento}
        initialData={editingPagamento}
        profissionais={funcionarios.map(f => ({ id: String(f.id), nome: f.nome, especialidade: f.funcao, salario: 0 }))}
      />

      <AlertDialog open={!!pagamentoToDelete} onOpenChange={() => setPagamentoToDelete(null)}>
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle>Confirmar exclusão</AlertDialogTitle>
            <AlertDialogDescription>
              Tem certeza que deseja remover este registro de pagamento? Esta ação não pode ser desfeita.
            </AlertDialogDescription>
          </AlertDialogHeader>
          <AlertDialogFooter>
            <AlertDialogCancel>Cancelar</AlertDialogCancel>
            <AlertDialogAction onClick={handleDelete}>
              Confirmar
            </AlertDialogAction>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>
    </div>
  );
}
