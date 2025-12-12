import { useState } from "react";
import { format } from "date-fns";
import { DollarSign, TrendingUp, TrendingDown, CreditCard, FileText, Clock, Plus, UserCheck, Edit, Trash2 } from "lucide-react";
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

const mockTransacoes = [
  {
    id: "1",
    tipo: "receita",
    descricao: "Consulta - Maria Silva Santos",
    valor: 250.00,
    formaPagamento: "PIX",
    data: "2024-01-10",
    status: "pago"
  },
  {
    id: "2",
    tipo: "receita",
    descricao: "Consulta - João Pedro Oliveira",
    valor: 180.00,
    formaPagamento: "Cartão de Crédito",
    data: "2024-01-10",
    status: "pago"
  },
  {
    id: "3",
    tipo: "receita",
    descricao: "Exame - Ana Costa Ferreira",
    valor: 350.00,
    formaPagamento: "Convênio",
    data: "2024-01-09",
    status: "pendente"
  },
  {
    id: "4",
    tipo: "despesa",
    descricao: "Material Médico",
    valor: 1200.00,
    formaPagamento: "Boleto",
    data: "2024-01-08",
    status: "pago"
  }
];

const mockConvenios = [
  { nome: "Unimed", pendente: 15420.00, recebido: 48750.00 },
  { nome: "Bradesco Saúde", pendente: 8900.00, recebido: 32100.00 },
  { nome: "Amil", pendente: 12300.00, recebido: 29800.00 }
];

const mockProfissionais = [
  { id: "1", nome: "Dr. João Silva", especialidade: "Cardiologia", salario: 8500.00 },
  { id: "2", nome: "Dra. Ana Santos", especialidade: "Pediatria", salario: 7800.00 },
  { id: "3", nome: "Dr. Carlos Mendes", especialidade: "Ortopedia", salario: 9200.00 },
  { id: "4", nome: "Dra. Maria Costa", especialidade: "Dermatologia", salario: 7500.00 },
];

interface Pagamento {
  id: string;
  profissionalId: string;
  profissionalNome: string;
  mesReferencia: Date;
  salarioBase: number;
  bonus: number;
  descontos: number;
  valorLiquido: number;
  metodoPagamento: string;
  status: "Pendente" | "Pago";
  dataPagamento?: string;
  observacoes?: string;
}

const mockPagamentos: Pagamento[] = [
  {
    id: "1",
    profissionalId: "1",
    profissionalNome: "Dr. João Silva",
    mesReferencia: new Date(2025, 9, 1),
    salarioBase: 8500.00,
    bonus: 500.00,
    descontos: 300.00,
    valorLiquido: 8700.00,
    metodoPagamento: "Transferência Bancária",
    status: "Pago",
    dataPagamento: "2025-10-05"
  },
  {
    id: "2",
    profissionalId: "2",
    profissionalNome: "Dra. Ana Santos",
    mesReferencia: new Date(2025, 9, 1),
    salarioBase: 7800.00,
    bonus: 0,
    descontos: 250.00,
    valorLiquido: 7550.00,
    metodoPagamento: "PIX",
    status: "Pendente"
  }
];

export default function Financeiro() {
  const { isGestor } = useAuth();
  const [pagamentos, setPagamentos] = useState<Pagamento[]>(mockPagamentos);
  const [isFormOpen, setIsFormOpen] = useState(false);
  const [editingPagamento, setEditingPagamento] = useState<Pagamento | null>(null);
  const [pagamentoToDelete, setPagamentoToDelete] = useState<string | null>(null);

  const handleSavePagamento = (data: any) => {
    const valorLiquido = data.salarioBase + data.bonus - data.descontos;
    
    if (editingPagamento) {
      setPagamentos(pagamentos.map(p => 
        p.id === editingPagamento.id 
          ? { ...p, ...data, valorLiquido }
          : p
      ));
      toast({
        title: "Pagamento atualizado",
        description: "As informações foram atualizadas com sucesso.",
      });
      setEditingPagamento(null);
    } else {
      const novoPagamento: Pagamento = {
        ...data,
        id: `P${Date.now()}`,
        valorLiquido,
        status: "Pendente",
      };
      setPagamentos([novoPagamento, ...pagamentos]);
      toast({
        title: "Pagamento registrado",
        description: "O pagamento foi cadastrado com sucesso.",
      });
    }
    setIsFormOpen(false);
  };

  const handleEdit = (pagamento: Pagamento) => {
    setEditingPagamento(pagamento);
    setIsFormOpen(true);
  };

  const handleDelete = (id: string) => {
    setPagamentos(pagamentos.filter(p => p.id !== id));
    setPagamentoToDelete(null);
    toast({
      title: "Pagamento removido",
      description: "O registro foi removido com sucesso.",
    });
  };

  const handleMarkAsPaid = (id: string) => {
    setPagamentos(pagamentos.map(p => 
      p.id === id 
        ? { ...p, status: "Pago" as const, dataPagamento: new Date().toISOString() }
        : p
    ));
    toast({
      title: "Pagamento confirmado",
      description: "O pagamento foi marcado como pago.",
    });
  };

  const totalFolhaPendente = pagamentos
    .filter(p => p.status === "Pendente")
    .reduce((acc, p) => acc + p.valorLiquido, 0);

  const totalFolhaPago = pagamentos
    .filter(p => p.status === "Pago")
    .reduce((acc, p) => acc + p.valorLiquido, 0);
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
              Receita Mensal
            </CardTitle>
            <TrendingUp className="w-4 h-4 text-success" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold text-foreground">R$ 145.750</div>
            <p className="text-xs text-success mt-1">+18% vs mês anterior</p>
          </CardContent>
        </Card>

        <Card className="shadow-card">
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium text-muted-foreground">
              Despesas Mensais
            </CardTitle>
            <TrendingDown className="w-4 h-4 text-destructive" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold text-foreground">R$ 42.300</div>
            <p className="text-xs text-muted-foreground mt-1">29% da receita</p>
          </CardContent>
        </Card>

        <Card className="shadow-card">
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium text-muted-foreground">
              A Receber (Convênios)
            </CardTitle>
            <Clock className="w-4 h-4 text-warning" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold text-foreground">R$ 36.620</div>
            <p className="text-xs text-muted-foreground mt-1">3 convênios</p>
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
            <div className="text-2xl font-bold text-foreground">R$ 103.450</div>
            <p className="text-xs text-success mt-1">+12% vs mês anterior</p>
          </CardContent>
        </Card>
      </div>

      <Tabs defaultValue="transacoes" className="space-y-4">
        <TabsList>
          <TabsTrigger value="transacoes">Transações</TabsTrigger>
          <TabsTrigger value="convenios">Convênios</TabsTrigger>
          <TabsTrigger value="folha">Folha de Pagamento</TabsTrigger>
          <TabsTrigger value="relatorios">Relatórios</TabsTrigger>
        </TabsList>

        <TabsContent value="transacoes" className="space-y-4">
          <Card className="shadow-card">
            <CardHeader>
              <CardTitle>Transações Recentes</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="space-y-4">
                {mockTransacoes.map((transacao) => (
                  <div
                    key={transacao.id}
                    className="flex items-center justify-between p-4 border border-border rounded-lg hover:bg-muted/50 transition-colors"
                  >
                    <div className="flex items-center gap-4">
                      <div className={`p-2 rounded-lg ${
                        transacao.tipo === "receita" 
                          ? "bg-success/10" 
                          : "bg-destructive/10"
                      }`}>
                        {transacao.tipo === "receita" ? (
                          <TrendingUp className="w-4 h-4 text-success" />
                        ) : (
                          <TrendingDown className="w-4 h-4 text-destructive" />
                        )}
                      </div>
                      <div>
                        <p className="font-medium text-foreground">{transacao.descricao}</p>
                        <div className="flex items-center gap-2 mt-1">
                          <span className="text-sm text-muted-foreground">
                            {new Date(transacao.data).toLocaleDateString('pt-BR')}
                          </span>
                          <span className="text-sm text-muted-foreground">•</span>
                          <span className="text-sm text-muted-foreground">
                            {transacao.formaPagamento}
                          </span>
                        </div>
                      </div>
                    </div>
                    <div className="text-right">
                      <p className={`font-bold ${
                        transacao.tipo === "receita" 
                          ? "text-success" 
                          : "text-destructive"
                      }`}>
                        {transacao.tipo === "receita" ? "+" : "-"}
                        R$ {transacao.valor.toFixed(2)}
                      </p>
                      <Badge 
                        variant={transacao.status === "pago" ? "default" : "secondary"}
                        className="mt-1"
                      >
                        {transacao.status === "pago" ? "Pago" : "Pendente"}
                      </Badge>
                    </div>
                  </div>
                ))}
              </div>
            </CardContent>
          </Card>
        </TabsContent>

        <TabsContent value="convenios" className="space-y-4">
          <div className="grid gap-4">
            {mockConvenios.map((convenio, index) => (
              <Card key={index} className="shadow-card">
                <CardContent className="p-6">
                  <div className="flex items-center justify-between mb-4">
                    <h3 className="text-lg font-semibold text-foreground">{convenio.nome}</h3>
                    <CreditCard className="w-5 h-5 text-primary" />
                  </div>
                  <div className="grid grid-cols-2 gap-4">
                    <div>
                      <p className="text-sm text-muted-foreground mb-1">Valores Recebidos</p>
                      <p className="text-xl font-bold text-success">
                        R$ {convenio.recebido.toLocaleString('pt-BR', { minimumFractionDigits: 2 })}
                      </p>
                    </div>
                    <div>
                      <p className="text-sm text-muted-foreground mb-1">Pendente de Recebimento</p>
                      <p className="text-xl font-bold text-warning">
                        R$ {convenio.pendente.toLocaleString('pt-BR', { minimumFractionDigits: 2 })}
                      </p>
                    </div>
                  </div>
                </CardContent>
              </Card>
            ))}
          </div>
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

              <div className="space-y-4">
                {pagamentos.length === 0 ? (
                  <div className="text-center py-12 text-muted-foreground">
                    <UserCheck className="h-12 w-12 mx-auto mb-4 opacity-50" />
                    <p>Nenhum pagamento registrado ainda.</p>
                  </div>
                ) : (
                  pagamentos.map((pagamento) => (
                    <Card key={pagamento.id} className="hover:shadow-md transition-shadow">
                      <CardContent className="pt-6">
                        <div className="flex items-start justify-between">
                          <div className="space-y-2 flex-1">
                            <div className="flex items-center gap-2">
                              <h3 className="font-semibold">{pagamento.profissionalNome}</h3>
                              <Badge variant={pagamento.status === "Pago" ? "default" : "secondary"}>
                                {pagamento.status}
                              </Badge>
                            </div>
                            
                            <div className="text-sm text-muted-foreground space-y-1">
                              <p><strong>Referência:</strong> {format(new Date(pagamento.mesReferencia), "MMMM 'de' yyyy")}</p>
                              <p><strong>Salário Base:</strong> {pagamento.salarioBase.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })}</p>
                              {pagamento.bonus > 0 && (
                                <p className="text-success"><strong>Bônus:</strong> +{pagamento.bonus.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })}</p>
                              )}
                              {pagamento.descontos > 0 && (
                                <p className="text-destructive"><strong>Descontos:</strong> -{pagamento.descontos.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })}</p>
                              )}
                              <p><strong>Método:</strong> {pagamento.metodoPagamento}</p>
                              {pagamento.dataPagamento && (
                                <p><strong>Data do Pagamento:</strong> {new Date(pagamento.dataPagamento).toLocaleDateString('pt-BR')}</p>
                              )}
                              {pagamento.observacoes && (
                                <p><strong>Observações:</strong> {pagamento.observacoes}</p>
                              )}
                            </div>
                          </div>
                          
                          <div className="flex flex-col items-end gap-3">
                            <div className="text-right">
                              <p className="text-sm text-muted-foreground">Valor Líquido</p>
                              <p className="text-2xl font-bold text-primary">
                                {pagamento.valorLiquido.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })}
                              </p>
                            </div>

                            {isGestor && (
                              <div className="flex flex-col gap-2 w-full">
                                {pagamento.status === "Pendente" && (
                                  <Button 
                                    size="sm" 
                                    className="w-full"
                                    onClick={() => handleMarkAsPaid(pagamento.id)}
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
                                    onClick={() => handleEdit(pagamento)}
                                  >
                                    <Edit className="h-4 w-4" />
                                  </Button>
                                  <Button 
                                    variant="outline" 
                                    size="sm"
                                    onClick={() => setPagamentoToDelete(pagamento.id)}
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
                  ))
                )}
              </div>
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
        profissionais={mockProfissionais}
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
            <AlertDialogAction onClick={() => pagamentoToDelete && handleDelete(pagamentoToDelete)}>
              Confirmar
            </AlertDialogAction>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>
    </div>
  );
}
