import { useState } from "react";
import { Package, Search, AlertTriangle, Plus, TrendingDown, TrendingUp } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { MovimentoEstoqueForm } from "@/components/MovimentoEstoqueForm";

const mockEstoque = [
  {
    id: "1",
    nome: "Luvas de Procedimento",
    categoria: "Material de Consumo",
    quantidade: 450,
    unidade: "Caixa",
    minimo: 100,
    validade: "2025-06-30",
    status: "normal"
  },
  {
    id: "2",
    nome: "Seringas 10ml",
    categoria: "Material de Consumo",
    quantidade: 85,
    unidade: "Unidade",
    minimo: 200,
    validade: "2024-12-31",
    status: "baixo"
  },
  {
    id: "3",
    nome: "Dipirona 500mg",
    categoria: "Medicamento",
    quantidade: 30,
    unidade: "Caixa",
    minimo: 50,
    validade: "2024-03-15",
    status: "critico"
  },
  {
    id: "4",
    nome: "Álcool 70%",
    categoria: "Material de Limpeza",
    quantidade: 180,
    unidade: "Litro",
    minimo: 50,
    validade: "2026-01-30",
    status: "normal"
  }
];

const mockMovimentos = [
  {
    id: "1",
    item: "Luvas de Procedimento",
    tipo: "saida",
    quantidade: 50,
    data: "2024-01-10",
    motivo: "Uso clínico"
  },
  {
    id: "2",
    item: "Seringas 10ml",
    tipo: "entrada",
    quantidade: 100,
    data: "2024-01-09",
    motivo: "Compra"
  },
  {
    id: "3",
    item: "Dipirona 500mg",
    tipo: "saida",
    quantidade: 20,
    data: "2024-01-08",
    motivo: "Uso clínico"
  }
];

export default function Estoque() {
  const [searchTerm, setSearchTerm] = useState("");
  const [isMovimentoFormOpen, setIsMovimentoFormOpen] = useState(false);
  const [movimentoTipo, setMovimentoTipo] = useState<"entrada" | "saida">("entrada");
  const [selectedItem, setSelectedItem] = useState<string>("");

  const handleOpenMovimento = (tipo: "entrada" | "saida", itemNome: string) => {
    setMovimentoTipo(tipo);
    setSelectedItem(itemNome);
    setIsMovimentoFormOpen(true);
  };

  const handleSaveMovimento = (data: any) => {
    console.log("Movimento registrado:", data);
  };

  const getStatusBadge = (status: string) => {
    const configs: Record<string, { variant: "default" | "secondary" | "destructive" | "outline"; label: string; className?: string }> = {
      normal: { variant: "default", label: "Normal", className: "bg-success/10 text-success border-success/20" },
      baixo: { variant: "outline", label: "Estoque Baixo", className: "bg-warning/10 text-warning border-warning/20" },
      critico: { variant: "destructive", label: "Crítico", className: "bg-destructive/10 text-destructive border-destructive/20" }
    };
    const config = configs[status] || configs.normal;
    return <Badge variant={config.variant} className={config.className}>{config.label}</Badge>;
  };

  const filteredEstoque = mockEstoque.filter(item =>
    item.nome.toLowerCase().includes(searchTerm.toLowerCase()) ||
    item.categoria.toLowerCase().includes(searchTerm.toLowerCase())
  );

  const itensCriticos = mockEstoque.filter(item => item.status === "critico").length;
  const itensBaixos = mockEstoque.filter(item => item.status === "baixo").length;

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold text-foreground">Estoque</h1>
          <p className="text-muted-foreground">Controle de medicamentos e materiais</p>
        </div>
        <Button className="bg-gradient-primary text-white hover:opacity-90">
          <Plus className="w-4 h-4 mr-2" />
          Adicionar Item
        </Button>
      </div>

      <MovimentoEstoqueForm
        open={isMovimentoFormOpen}
        onOpenChange={setIsMovimentoFormOpen}
        itemNome={selectedItem}
        tipo={movimentoTipo}
        onSave={handleSaveMovimento}
      />

      <div className="grid gap-6 md:grid-cols-4">
        <Card className="shadow-card">
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium text-muted-foreground">
              Total de Itens
            </CardTitle>
            <Package className="w-4 h-4 text-primary" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold text-foreground">248</div>
            <p className="text-xs text-muted-foreground">Em 12 categorias</p>
          </CardContent>
        </Card>

        <Card className="shadow-card">
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium text-muted-foreground">
              Estoque Crítico
            </CardTitle>
            <AlertTriangle className="w-4 h-4 text-destructive" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold text-foreground">{itensCriticos}</div>
            <p className="text-xs text-destructive">Requer atenção imediata</p>
          </CardContent>
        </Card>

        <Card className="shadow-card">
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium text-muted-foreground">
              Estoque Baixo
            </CardTitle>
            <TrendingDown className="w-4 h-4 text-warning" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold text-foreground">{itensBaixos}</div>
            <p className="text-xs text-warning">Abaixo do mínimo</p>
          </CardContent>
        </Card>

        <Card className="shadow-card">
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium text-muted-foreground">
              Valor Total
            </CardTitle>
            <TrendingUp className="w-4 h-4 text-success" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold text-foreground">R$ 45.320</div>
            <p className="text-xs text-muted-foreground">Custo de estoque</p>
          </CardContent>
        </Card>
      </div>

      <Tabs defaultValue="itens" className="space-y-4">
        <TabsList>
          <TabsTrigger value="itens">Itens em Estoque</TabsTrigger>
          <TabsTrigger value="movimentos">Movimentações</TabsTrigger>
          <TabsTrigger value="alertas">Alertas</TabsTrigger>
        </TabsList>

        <TabsContent value="itens" className="space-y-4">
          <div className="relative">
            <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-muted-foreground w-4 h-4" />
            <Input
              placeholder="Buscar item por nome ou categoria..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="pl-10"
            />
          </div>

          <div className="grid gap-4">
            {filteredEstoque.map((item) => (
              <Card
                key={item.id}
                className="shadow-card hover:shadow-medical transition-all duration-300"
              >
                <CardContent className="p-6">
                  <div className="flex items-start justify-between">
                    <div className="flex-1 space-y-3">
                      <div className="flex items-center gap-3">
                        <div className="p-2 bg-primary/10 rounded-lg">
                          <Package className="w-4 h-4 text-primary" />
                        </div>
                        <div>
                          <h3 className="text-lg font-semibold text-foreground">{item.nome}</h3>
                          <p className="text-sm text-muted-foreground">{item.categoria}</p>
                        </div>
                      </div>
                      
                      <div className="grid grid-cols-4 gap-4 text-sm">
                        <div>
                          <span className="text-muted-foreground">Quantidade: </span>
                          <span className="text-foreground font-medium">
                            {item.quantidade} {item.unidade}
                          </span>
                        </div>
                        <div>
                          <span className="text-muted-foreground">Mínimo: </span>
                          <span className="text-foreground font-medium">
                            {item.minimo} {item.unidade}
                          </span>
                        </div>
                        <div>
                          <span className="text-muted-foreground">Validade: </span>
                          <span className="text-foreground font-medium">
                            {new Date(item.validade).toLocaleDateString('pt-BR')}
                          </span>
                        </div>
                        <div>
                          {getStatusBadge(item.status)}
                        </div>
                      </div>
                    </div>

                    <div className="flex gap-2">
                      <Button variant="outline" size="sm" onClick={() => handleOpenMovimento("entrada", item.nome)}>
                        Entrada
                      </Button>
                      <Button variant="outline" size="sm" onClick={() => handleOpenMovimento("saida", item.nome)}>
                        Saída
                      </Button>
                    </div>
                  </div>
                </CardContent>
              </Card>
            ))}
          </div>
        </TabsContent>

        <TabsContent value="movimentos" className="space-y-4">
          <Card className="shadow-card">
            <CardHeader>
              <CardTitle>Movimentações Recentes</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="space-y-4">
                {mockMovimentos.map((movimento) => (
                  <div
                    key={movimento.id}
                    className="flex items-center justify-between p-4 border border-border rounded-lg"
                  >
                    <div className="flex items-center gap-4">
                      <div className={`p-2 rounded-lg ${
                        movimento.tipo === "entrada" 
                          ? "bg-success/10" 
                          : "bg-destructive/10"
                      }`}>
                        {movimento.tipo === "entrada" ? (
                          <TrendingUp className="w-4 h-4 text-success" />
                        ) : (
                          <TrendingDown className="w-4 h-4 text-destructive" />
                        )}
                      </div>
                      <div>
                        <p className="font-medium text-foreground">{movimento.item}</p>
                        <p className="text-sm text-muted-foreground">{movimento.motivo}</p>
                      </div>
                    </div>
                    <div className="text-right">
                      <p className={`font-bold ${
                        movimento.tipo === "entrada" 
                          ? "text-success" 
                          : "text-destructive"
                      }`}>
                        {movimento.tipo === "entrada" ? "+" : "-"}
                        {movimento.quantidade}
                      </p>
                      <p className="text-sm text-muted-foreground">
                        {new Date(movimento.data).toLocaleDateString('pt-BR')}
                      </p>
                    </div>
                  </div>
                ))}
              </div>
            </CardContent>
          </Card>
        </TabsContent>

        <TabsContent value="alertas" className="space-y-4">
          <Card className="shadow-card border-destructive/20">
            <CardContent className="p-6">
              <div className="flex items-start gap-4">
                <AlertTriangle className="w-6 h-6 text-destructive flex-shrink-0 mt-1" />
                <div className="space-y-2">
                  <h3 className="font-semibold text-foreground">Itens Críticos</h3>
                  <p className="text-sm text-muted-foreground">
                    {itensCriticos} {itensCriticos === 1 ? 'item está' : 'itens estão'} com estoque crítico e {itensBaixos} com estoque baixo.
                    Recomenda-se realizar pedido de compra urgente.
                  </p>
                  <Button variant="destructive" size="sm" className="mt-2">
                    Gerar Pedido de Compra
                  </Button>
                </div>
              </div>
            </CardContent>
          </Card>
        </TabsContent>
      </Tabs>
    </div>
  );
}
