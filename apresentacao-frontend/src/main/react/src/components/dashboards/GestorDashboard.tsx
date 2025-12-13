import { useMemo } from "react";
import { useQuery } from "@tanstack/react-query"; // IMPORTANTE: Adicionado
import {
  Users,
  DollarSign,
  TrendingUp,
  TrendingDown,
  CalendarCheck,
  CalendarClock,
  AlertCircle
} from "lucide-react";
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from "@/components/ui/card";
import { Button } from "@/components/ui/button";

// Hooks existentes (Corretos)
import { useListarPacientes } from "@/api/usePacientesApi";
import { useListarFaturamentos } from "@/api/useFaturamentosApi";
import { useListarFolhasPagamento } from "@/api/useFolhaPagamentoApi";
import { useListarMedicamentos } from "@/api/useMedicamentosApi";

// Hook de Consultas (CORRIGIDO: Importamos a API e usamos useQuery aqui)
import { useConsultasApi } from "@/api/useConsultasApi";

import { useAuth } from "@/contexts/AuthContext";

export default function Dashboard() {
  const { user } = useAuth();

  // 1. Instanciando a API de consultas
  const { listarConsultas } = useConsultasApi();

  // 2. Buscando Dados Reais

  // Consultas (Correção do erro da tela branca)
  const { data: consultas = [] } = useQuery({
    queryKey: ['consultas-dashboard'],
    queryFn: listarConsultas,
    refetchOnWindowFocus: false
  });

  const { data: pacientes = [] } = useListarPacientes();
  const { data: faturamentos = [] } = useListarFaturamentos();
  const { data: folhas = [] } = useListarFolhasPagamento();
  const { data: medicamentos = [] } = useListarMedicamentos();

  // 3. Processamento dos Dados (Lógica do Dashboard)
  const stats = useMemo(() => {
    const hoje = new Date();
    const hojeString = hoje.toISOString().split('T')[0]; // YYYY-MM-DD

    // --- KPI 1: Consultas Hoje ---
    const consultasHoje = consultas.filter(c =>
      c.dataHora && c.dataHora.startsWith(hojeString) && c.status !== 'CANCELADA'
    ).length;

    // --- KPI 2 e 3: Financeiro Total ---
    const receitaTotal = faturamentos
      .filter(f => f.status === "PAGO")
      .reduce((acc, curr) => acc + Number(curr.valor), 0);

    const despesasTotal = folhas
      .filter(f => f.status === "PAGO")
      .reduce((acc, curr) => acc + Number(curr.valorLiquido), 0);

    const lucro = receitaTotal - despesasTotal;

    // --- KPI 5: Pacientes ---
    const totalPacientes = pacientes.length;

    // --- GRÁFICO: Receita Últimos 7 Dias ---
    const ultimos7Dias = Array.from({ length: 7 }, (_, i) => {
        const d = new Date();
        d.setDate(d.getDate() - i);
        return d.toISOString().split('T')[0];
    }).reverse();

    const receitaPorDia = ultimos7Dias.map(dia => {
        const totalDia = faturamentos
            .filter(f =>
                f.status === "PAGO" &&
                f.dataHoraFaturamento.startsWith(dia)
            )
            .reduce((acc, curr) => acc + Number(curr.valor), 0);

        const [ano, mes, diaNum] = dia.split('-');
        return { label: `${diaNum}/${mes}`, valor: totalDia };
    });

    // --- LISTA: Próximas Consultas ---
    const proximasConsultas = consultas
        .filter(c => c.dataHora && new Date(c.dataHora) > new Date() && c.status === 'AGENDADA')
        .sort((a, b) => new Date(a.dataHora).getTime() - new Date(b.dataHora).getTime())
        .slice(0, 5);

    // --- ALERTA: Estoque ---
    const estoqueBaixo = medicamentos.filter(m => Number(m.usoPrincipal) < 20 || false); // Ajuste provisório se qtd não existir, mas assumindo que existe no objeto real

    return {
      consultasHoje,
      receitaTotal,
      despesasTotal,
      lucro,
      totalPacientes,
      receitaPorDia,
      proximasConsultas,
      estoqueBaixo
    };
  }, [pacientes, faturamentos, folhas, medicamentos, consultas]);

  return (
    <div className="space-y-6 p-6 pb-20">

      {/* Cabeçalho */}
      <div className="flex flex-col gap-2 md:flex-row md:items-center md:justify-between">
        <div>
          <h1 className="text-3xl font-bold tracking-tight">Dashboard</h1>
          <p className="text-muted-foreground">
            Visão geral operacional e financeira.
          </p>
        </div>
        <div className="flex items-center gap-2">
           <Button variant="outline" onClick={() => window.location.reload()}>Atualizar</Button>
        </div>
      </div>

      {/* --- GRID DE 5 CARDS --- */}
      <div className="grid gap-4 grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-5">

        {/* CARD 1: Consultas Hoje */}
        <Card className="shadow-sm border-l-4 border-l-blue-500">
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Consultas Hoje</CardTitle>
            <CalendarCheck className="h-4 w-4 text-blue-500" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{stats.consultasHoje}</div>
            <p className="text-xs text-muted-foreground">Agendadas para hoje</p>
          </CardContent>
        </Card>

        {/* CARD 2: Receita */}
        <Card className="shadow-sm">
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Receita Total</CardTitle>
            <DollarSign className="h-4 w-4 text-green-600" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">
              {stats.receitaTotal.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL', maximumFractionDigits: 0 })}
            </div>
            <p className="text-xs text-muted-foreground">Acumulado Pago</p>
          </CardContent>
        </Card>

        {/* CARD 3: Despesas */}
        <Card className="shadow-sm">
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Despesas</CardTitle>
            <TrendingDown className="h-4 w-4 text-red-500" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">
              {stats.despesasTotal.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL', maximumFractionDigits: 0 })}
            </div>
            <p className="text-xs text-muted-foreground">Folha Pagamento</p>
          </CardContent>
        </Card>

        {/* CARD 4: Lucro */}
        <Card className="shadow-sm">
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Lucro Líquido</CardTitle>
            <TrendingUp className="h-4 w-4 text-emerald-600" />
          </CardHeader>
          <CardContent>
            <div className={`text-2xl font-bold ${stats.lucro >= 0 ? 'text-emerald-600' : 'text-red-600'}`}>
              {stats.lucro.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL', maximumFractionDigits: 0 })}
            </div>
            <p className="text-xs text-muted-foreground">Balanço Atual</p>
          </CardContent>
        </Card>

        {/* CARD 5: Pacientes */}
        <Card className="shadow-sm">
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Total Pacientes</CardTitle>
            <Users className="h-4 w-4 text-orange-500" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{stats.totalPacientes}</div>
            <p className="text-xs text-muted-foreground">Cadastrados na base</p>
          </CardContent>
        </Card>
      </div>

      {/* --- SEÇÃO INFERIOR --- */}
      <div className="grid gap-4 grid-cols-1 lg:grid-cols-7">

        {/* GRÁFICO */}
        <Card className="col-span-1 lg:col-span-4 shadow-sm">
          <CardHeader>
            <CardTitle>Receita - Últimos 7 Dias</CardTitle>
            <CardDescription>Valores faturados e pagos no período</CardDescription>
          </CardHeader>
          <CardContent className="pl-2">
            <div className="h-[300px] w-full flex items-end justify-between gap-2 p-4 pt-10 border rounded-md bg-slate-50/50">
               {stats.receitaPorDia.some(d => d.valor > 0) ? (
                 stats.receitaPorDia.map((dia, i) => {
                    const maxVal = Math.max(...stats.receitaPorDia.map(d => d.valor)) || 1;
                    const heightPercent = (dia.valor / maxVal) * 100;

                    return (
                     <div key={i} className="flex-1 flex flex-col justify-end items-center gap-2 group h-full">
                        <div className="mb-auto opacity-0 group-hover:opacity-100 transition-opacity text-xs font-bold text-slate-700 bg-white px-2 py-1 rounded shadow-sm border">
                            {dia.valor.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })}
                        </div>
                        <div
                          className="w-full max-w-[40px] bg-blue-500 hover:bg-blue-600 rounded-t-sm transition-all duration-500 min-h-[4px]"
                          style={{ height: `${heightPercent}%` }}
                        ></div>
                        <span className="text-xs text-muted-foreground font-medium">{dia.label}</span>
                     </div>
                    )
                 })
               ) : (
                 <div className="w-full h-full flex flex-col items-center justify-center text-muted-foreground">
                    <TrendingUp className="w-8 h-8 mb-2 opacity-20" />
                    <p>Sem movimentação nos últimos 7 dias</p>
                 </div>
               )}
            </div>
          </CardContent>
        </Card>

        {/* LISTA & ALERTAS */}
        <Card className="col-span-1 lg:col-span-3 shadow-sm flex flex-col h-[420px]">
          <CardHeader>
            <CardTitle>Agenda & Alertas</CardTitle>
            <CardDescription>Próximos compromissos</CardDescription>
          </CardHeader>
          <CardContent className="flex-1 overflow-auto pr-2">

            {/* Alertas */}
            {stats.estoqueBaixo.length > 0 && (
              <div className="mb-6">
                <h4 className="text-xs font-bold uppercase tracking-wider mb-3 flex items-center gap-2 text-red-600">
                  <AlertCircle className="w-4 h-4" /> Estoque / Revisão
                </h4>
                <div className="space-y-2">
                  {stats.estoqueBaixo.slice(0, 3).map(med => (
                    <div key={med.id} className="flex items-center justify-between text-sm p-3 bg-red-50 border border-red-100 rounded-lg">
                      <span className="font-medium text-red-900 truncate max-w-[150px]">{med.nome}</span>
                      <span className="text-xs text-red-700">{med.status}</span>
                    </div>
                  ))}
                </div>
              </div>
            )}

            {/* Próximas Consultas */}
            <div>
              <h4 className="text-xs font-bold uppercase tracking-wider mb-3 flex items-center gap-2 text-slate-500">
                <CalendarClock className="w-4 h-4" /> Próximas Consultas
              </h4>

              {stats.proximasConsultas.length > 0 ? (
                <div className="space-y-3">
                  {stats.proximasConsultas.map((cons, i) => (
                    <div key={cons.id || i} className="flex items-center gap-3 p-3 border rounded-lg hover:bg-slate-50 transition-colors">
                      <div className="h-10 w-10 rounded-full bg-blue-100 flex items-center justify-center text-blue-600 font-bold shrink-0">
                         C
                      </div>
                      <div className="flex-1 min-w-0">
                        <p className="text-sm font-medium leading-none truncate">
                            {cons.descricao || `Consulta #${cons.id}`}
                        </p>
                        <p className="text-xs text-muted-foreground mt-1 flex items-center gap-1">
                           <CalendarCheck className="w-3 h-3" />
                           {new Date(cons.dataHora).toLocaleDateString('pt-BR')} - {new Date(cons.dataHora).toLocaleTimeString('pt-BR', {hour: '2-digit', minute:'2-digit'})}
                        </p>
                      </div>
                    </div>
                  ))}
                </div>
              ) : (
                 <p className="text-sm text-muted-foreground text-center py-8 border-2 border-dashed rounded-lg">
                    Nenhuma consulta futura agendada.
                 </p>
              )}
            </div>

          </CardContent>
        </Card>
      </div>
    </div>
  );
}