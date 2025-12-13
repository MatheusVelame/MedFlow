import { ClipboardList, Download, Calendar, TrendingUp, Users, DollarSign } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";

const relatoriosDisponiveis = [
  {
    titulo: "Relatório de Atendimentos",
    descricao: "Consultas realizadas por período, médico e especialidade",
    icone: Users,
    tipo: "operacional"
  },
  {
    titulo: "Relatório Financeiro",
    descricao: "Receitas, despesas e faturamento por convênio",
    icone: DollarSign,
    tipo: "financeiro"
  },
  {
    titulo: "Relatório de Produtividade",
    descricao: "Performance dos profissionais e taxa de ocupação",
    icone: TrendingUp,
    tipo: "gestao"
  },
  {
    titulo: "Relatório de Pacientes",
    descricao: "Cadastros, retornos e perfil demográfico",
    icone: Users,
    tipo: "operacional"
  },
  {
    titulo: "Relatório de Exames",
    descricao: "Solicitações, resultados e tempo médio de entrega",
    icone: ClipboardList,
    tipo: "operacional"
  },
  {
    titulo: "Relatório de No-Show",
    descricao: "Taxa de ausências e análise de cancelamentos",
    icone: Calendar,
    tipo: "gestao"
  }
];

export default function Relatorios() {
  const operacionais = relatoriosDisponiveis.filter(r => r.tipo === "operacional");
  const financeiros = relatoriosDisponiveis.filter(r => r.tipo === "financeiro");
  const gestao = relatoriosDisponiveis.filter(r => r.tipo === "gestao");

  const RelatorioCard = ({ relatorio }: { relatorio: typeof relatoriosDisponiveis[0] }) => (
    <Card className="shadow-card hover:shadow-medical transition-all duration-300">
      <CardContent className="p-6">
        <div className="flex items-start gap-4">
          <div className="p-3 bg-primary/10 rounded-lg">
            <relatorio.icone className="w-6 h-6 text-primary" />
          </div>
          <div className="flex-1 space-y-2">
            <h3 className="text-lg font-semibold text-foreground">{relatorio.titulo}</h3>
            <p className="text-sm text-muted-foreground">{relatorio.descricao}</p>
            <div className="flex gap-2 pt-2">
              <Button variant="outline" size="sm">
                <Calendar className="w-4 h-4 mr-2" />
                Configurar
              </Button>
              <Button size="sm" className="bg-gradient-primary text-white">
                <Download className="w-4 h-4 mr-2" />
                Gerar
              </Button>
            </div>
          </div>
        </div>
      </CardContent>
    </Card>
  );

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold text-foreground">Relatórios</h1>
          <p className="text-muted-foreground">Análises e indicadores de desempenho</p>
        </div>
      </div>

      <div className="grid gap-6 md:grid-cols-3">
        <Card className="shadow-card">
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium text-muted-foreground">
              Relatórios Gerados
            </CardTitle>
            <ClipboardList className="w-4 h-4 text-primary" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold text-foreground">142</div>
            <p className="text-xs text-muted-foreground">Este mês</p>
          </CardContent>
        </Card>

        <Card className="shadow-card">
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium text-muted-foreground">
              Relatórios Agendados
            </CardTitle>
            <Calendar className="w-4 h-4 text-primary" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold text-foreground">8</div>
            <p className="text-xs text-muted-foreground">Automáticos</p>
          </CardContent>
        </Card>

        <Card className="shadow-card">
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium text-muted-foreground">
              Tipos Disponíveis
            </CardTitle>
            <TrendingUp className="w-4 h-4 text-primary" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold text-foreground">{relatoriosDisponiveis.length}</div>
            <p className="text-xs text-muted-foreground">Modelos prontos</p>
          </CardContent>
        </Card>
      </div>

      <Tabs defaultValue="todos" className="space-y-6">
        <TabsList>
          <TabsTrigger value="todos">Todos</TabsTrigger>
          <TabsTrigger value="operacional">Operacionais</TabsTrigger>
          <TabsTrigger value="financeiro">Financeiros</TabsTrigger>
          <TabsTrigger value="gestao">Gestão</TabsTrigger>
        </TabsList>

        <TabsContent value="todos" className="space-y-4">
          <div className="grid gap-4">
            {relatoriosDisponiveis.map((relatorio, index) => (
              <RelatorioCard key={index} relatorio={relatorio} />
            ))}
          </div>
        </TabsContent>

        <TabsContent value="operacional" className="space-y-4">
          <div className="grid gap-4">
            {operacionais.map((relatorio, index) => (
              <RelatorioCard key={index} relatorio={relatorio} />
            ))}
          </div>
        </TabsContent>

        <TabsContent value="financeiro" className="space-y-4">
          <div className="grid gap-4">
            {financeiros.map((relatorio, index) => (
              <RelatorioCard key={index} relatorio={relatorio} />
            ))}
          </div>
        </TabsContent>

        <TabsContent value="gestao" className="space-y-4">
          <div className="grid gap-4">
            {gestao.map((relatorio, index) => (
              <RelatorioCard key={index} relatorio={relatorio} />
            ))}
          </div>
        </TabsContent>
      </Tabs>

      <Card className="shadow-card bg-gradient-subtle">
        <CardContent className="p-8">
          <div className="text-center space-y-4">
            <div className="inline-block p-4 bg-primary/10 rounded-full">
              <ClipboardList className="w-8 h-8 text-primary" />
            </div>
            <div>
              <h3 className="text-xl font-semibold text-foreground mb-2">
                Relatórios Personalizados
              </h3>
              <p className="text-muted-foreground mb-4">
                Configure relatórios customizados com os dados específicos que você precisa
              </p>
              <Button className="bg-gradient-primary text-white hover:opacity-90">
                Criar Relatório Personalizado
              </Button>
            </div>
          </div>
        </CardContent>
      </Card>
    </div>
  );
}
