import { 
  Calendar, 
  Users, 
  DollarSign,
  TrendingUp,
  AlertTriangle,
  Package,
  Activity,
  FileText
} from "lucide-react";
import { StatCard } from "@/components/StatCard";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts';

const revenueData = [
  { day: 'Seg', value: 6500 },
  { day: 'Ter', value: 7200 },
  { day: 'Qua', value: 6800 },
  { day: 'Qui', value: 8100 },
  { day: 'Sex', value: 7500 },
  { day: 'Sáb', value: 5200 },
  { day: 'Dom', value: 3900 },
];

const upcomingAppointments = [
  { id: 1, patient: "Maria Silva", time: "09:00", doctor: "Dr. João", status: "confirmado" },
  { id: 2, patient: "Pedro Lima", time: "09:30", doctor: "Dra. Ana", status: "pendente" },
  { id: 3, patient: "Ana Costa", time: "10:00", doctor: "Dr. Carlos", status: "confirmado" },
];

const alerts = [
  { id: 1, type: "warning", message: "Estoque baixo: Paracetamol (5 unidades)", time: "5 min" },
  { id: 2, type: "urgent", message: "3 resultados de exames aguardando análise", time: "15 min" },
  { id: 3, type: "info", message: "Meta mensal atingida em 75%", time: "1 hora" },
];

const specialties = [
  { name: "Cardiologia", count: 45 },
  { name: "Pediatria", count: 38 },
  { name: "Ortopedia", count: 32 },
  { name: "Clínico Geral", count: 28 },
];

export default function GestorDashboard() {
  return (
    <div className="space-y-6 animate-fade-in">
      <div>
        <h1 className="text-3xl font-bold tracking-tight text-foreground">Dashboard - Gestão</h1>
        <p className="text-muted-foreground">Visão estratégica completa da clínica</p>
      </div>

      <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
        <StatCard
          title="Consultas Hoje"
          value="24"
          description="8 realizadas, 16 pendentes"
          icon={<Calendar className="h-4 w-4" />}
          trend={{ value: 12, isPositive: true }}
        />
        <StatCard
          title="Receita do Mês"
          value="R$ 45.280"
          description="75% da meta mensal"
          icon={<DollarSign className="h-4 w-4" />}
          trend={{ value: 22, isPositive: true }}
        />
        <StatCard
          title="Pacientes Ativos"
          value="342"
          description="Cadastrados no sistema"
          icon={<Users className="h-4 w-4" />}
          trend={{ value: 8, isPositive: true }}
        />
        <StatCard
          title="Tempo Médio de Espera"
          value="18 min"
          description="Meta: 15 minutos"
          icon={<Activity className="h-4 w-4" />}
          trend={{ value: -5, isPositive: true }}
        />
      </div>

      <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-3">
        <Card className="lg:col-span-2">
          <CardHeader>
            <CardTitle className="flex items-center gap-2">
              <TrendingUp className="h-5 w-5" />
              Receita dos Últimos 7 Dias
            </CardTitle>
          </CardHeader>
          <CardContent>
            <ResponsiveContainer width="100%" height={200}>
              <BarChart data={revenueData}>
                <CartesianGrid strokeDasharray="3 3" className="stroke-muted" />
                <XAxis dataKey="day" className="text-xs" />
                <YAxis className="text-xs" />
                <Tooltip />
                <Bar dataKey="value" fill="hsl(var(--primary))" radius={[8, 8, 0, 0]} />
              </BarChart>
            </ResponsiveContainer>
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle className="flex items-center gap-2">
              <AlertTriangle className="h-5 w-5" />
              Alertas
            </CardTitle>
          </CardHeader>
          <CardContent>
            <div className="space-y-3">
              {alerts.map((alert) => (
                <div key={alert.id} className="flex gap-3 p-3 bg-muted/50 rounded-lg">
                  <div className={`w-2 h-2 rounded-full mt-2 flex-shrink-0 ${
                    alert.type === "urgent" ? "bg-destructive" :
                    alert.type === "warning" ? "bg-warning" : "bg-primary"
                  }`} />
                  <div className="flex-1">
                    <p className="text-sm">{alert.message}</p>
                    <p className="text-xs text-muted-foreground mt-1">há {alert.time}</p>
                  </div>
                </div>
              ))}
            </div>
          </CardContent>
        </Card>
      </div>

      <div className="grid gap-6 md:grid-cols-2">
        <Card>
          <CardHeader>
            <CardTitle className="flex items-center gap-2">
              <Calendar className="h-5 w-5" />
              Próximas Consultas
            </CardTitle>
          </CardHeader>
          <CardContent>
            <div className="space-y-3">
              {upcomingAppointments.map((appointment) => (
                <div key={appointment.id} className="flex items-center justify-between p-3 bg-muted/50 rounded-lg">
                  <div className="flex items-center gap-3">
                    <div className="text-sm font-medium text-primary">
                      {appointment.time}
                    </div>
                    <div>
                      <p className="font-medium">{appointment.patient}</p>
                      <p className="text-sm text-muted-foreground">{appointment.doctor}</p>
                    </div>
                  </div>
                  <Badge variant={appointment.status === "confirmado" ? "default" : "secondary"}>
                    {appointment.status}
                  </Badge>
                </div>
              ))}
            </div>
            <Button variant="outline" className="w-full mt-4">
              Ver Todos os Agendamentos
            </Button>
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle className="flex items-center gap-2">
              <Package className="h-5 w-5" />
              Especialidades Mais Demandadas
            </CardTitle>
          </CardHeader>
          <CardContent>
            <div className="space-y-3">
              {specialties.map((specialty) => (
                <div key={specialty.name} className="flex items-center justify-between p-3 bg-muted/50 rounded-lg">
                  <span className="font-medium">{specialty.name}</span>
                  <Badge variant="outline">{specialty.count} consultas</Badge>
                </div>
              ))}
            </div>
          </CardContent>
        </Card>
      </div>

      <Card>
        <CardHeader>
          <CardTitle>Ações Rápidas</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
            <Button className="h-20 flex-col gap-2">
              <Users className="h-6 w-6" />
              Novo Paciente
            </Button>
            <Button variant="outline" className="h-20 flex-col gap-2">
              <Calendar className="h-6 w-6" />
              Agendar Consulta
            </Button>
            <Button variant="outline" className="h-20 flex-col gap-2">
              <FileText className="h-6 w-6" />
              Ver Relatórios
            </Button>
            <Button variant="outline" className="h-20 flex-col gap-2">
              <Users className="h-6 w-6" />
              Gerenciar Profissionais
            </Button>
          </div>
        </CardContent>
      </Card>
    </div>
  );
}
