import { 
  Calendar, 
  Users, 
  Clock,
  DollarSign,
  Phone,
  AlertCircle,
  ClipboardCheck,
  UserPlus
} from "lucide-react";
import { StatCard } from "@/components/StatCard";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";

const nextAppointments = [
  { id: 1, patient: "Maria Silva", time: "09:00", doctor: "Dr. João", status: "confirmado" },
  { id: 2, patient: "Pedro Lima", time: "09:15", doctor: "Dra. Ana", status: "aguardando" },
  { id: 3, patient: "Ana Costa", time: "09:30", doctor: "Dr. Carlos", status: "confirmado" },
  { id: 4, patient: "João Santos", time: "09:45", doctor: "Dr. João", status: "pendente" },
];

const operationalAlerts = [
  { id: 1, type: "waiting", message: "3 pacientes aguardando há mais de 20 minutos", time: "agora" },
  { id: 2, type: "payment", message: "5 pagamentos pendentes de confirmação", time: "10 min" },
  { id: 3, type: "confirmation", message: "8 consultas de amanhã precisam de confirmação", time: "1 hora" },
];

const waitingRoom = [
  { id: 1, name: "Maria Silva", arrival: "08:45", waitTime: "15 min", priority: "normal" },
  { id: 2, name: "Carlos Mendes", arrival: "08:30", waitTime: "30 min", priority: "urgent" },
  { id: 3, name: "Fernanda Rocha", arrival: "08:50", waitTime: "10 min", priority: "normal" },
];

const triages = [
  { id: 1, patient: "Roberto Silva", time: "08:30", status: "concluída" },
  { id: 2, patient: "Juliana Costa", time: "08:45", status: "em andamento" },
  { id: 3, patient: "Paulo Mendes", time: "09:00", status: "pendente" },
];

const toConfirm = [
  { id: 1, patient: "Ana Silva", appointment: "Amanhã, 09:00", phone: "(11) 98765-4321" },
  { id: 2, patient: "João Pedro", appointment: "Amanhã, 10:30", phone: "(11) 97654-3210" },
  { id: 3, patient: "Maria Santos", appointment: "Amanhã, 14:00", phone: "(11) 96543-2109" },
];

export default function AtendenteDashboard() {
  return (
    <div className="space-y-6 animate-fade-in">
      <div>
        <h1 className="text-3xl font-bold tracking-tight text-foreground">Dashboard - Atendimento</h1>
        <p className="text-muted-foreground">Operações e fluxo de atendimento do dia</p>
      </div>

      <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
        <StatCard
          title="Agendamentos Hoje"
          value="32"
          description="28 confirmados, 4 pendentes"
          icon={<Calendar className="h-4 w-4" />}
          trend={{ value: 8, isPositive: true }}
        />
        <StatCard
          title="Pacientes na Espera"
          value="8"
          description="3 há mais de 20 min"
          icon={<Users className="h-4 w-4" />}
        />
        <StatCard
          title="Tempo Médio de Espera"
          value="18 min"
          description="Meta: 15 minutos"
          icon={<Clock className="h-4 w-4" />}
          trend={{ value: -5, isPositive: true }}
        />
        <StatCard
          title="Faturamentos Pendentes"
          value="12"
          description="R$ 3.450,00 em aberto"
          icon={<DollarSign className="h-4 w-4" />}
        />
      </div>

      <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-3">
        <Card>
          <CardHeader>
            <CardTitle className="flex items-center gap-2">
              <Calendar className="h-5 w-5" />
              Próximos Agendamentos (2h)
            </CardTitle>
          </CardHeader>
          <CardContent>
            <div className="space-y-3">
              {nextAppointments.map((appointment) => (
                <div key={appointment.id} className="flex items-center justify-between p-3 bg-muted/50 rounded-lg">
                  <div className="flex items-center gap-3">
                    <div className="text-sm font-medium text-primary">
                      {appointment.time}
                    </div>
                    <div>
                      <p className="font-medium text-sm">{appointment.patient}</p>
                      <p className="text-xs text-muted-foreground">{appointment.doctor}</p>
                    </div>
                  </div>
                  <Badge 
                    variant={
                      appointment.status === "confirmado" ? "default" :
                      appointment.status === "aguardando" ? "secondary" : "outline"
                    }
                    className="text-xs"
                  >
                    {appointment.status}
                  </Badge>
                </div>
              ))}
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle className="flex items-center gap-2">
              <Users className="h-5 w-5" />
              Sala de Espera
            </CardTitle>
          </CardHeader>
          <CardContent>
            <div className="space-y-3">
              {waitingRoom.map((patient) => (
                <div key={patient.id} className="flex items-center justify-between p-3 bg-muted/50 rounded-lg">
                  <div>
                    <p className="font-medium text-sm">{patient.name}</p>
                    <p className="text-xs text-muted-foreground">Chegada: {patient.arrival}</p>
                  </div>
                  <div className="text-right">
                    <Badge variant={patient.priority === "urgent" ? "destructive" : "outline"}>
                      {patient.waitTime}
                    </Badge>
                  </div>
                </div>
              ))}
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle className="flex items-center gap-2">
              <AlertCircle className="h-5 w-5" />
              Alertas Operacionais
            </CardTitle>
          </CardHeader>
          <CardContent>
            <div className="space-y-3">
              {operationalAlerts.map((alert) => (
                <div key={alert.id} className="flex gap-3 p-3 bg-muted/50 rounded-lg">
                  <div className={`w-2 h-2 rounded-full mt-2 flex-shrink-0 ${
                    alert.type === "waiting" ? "bg-destructive" :
                    alert.type === "payment" ? "bg-warning" : "bg-primary"
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
              <ClipboardCheck className="h-5 w-5" />
              Triagens do Dia
            </CardTitle>
          </CardHeader>
          <CardContent>
            <div className="space-y-3">
              {triages.map((triage) => (
                <div key={triage.id} className="flex items-center justify-between p-3 bg-muted/50 rounded-lg">
                  <div className="flex items-center gap-3">
                    <div className="text-sm font-medium text-primary">
                      {triage.time}
                    </div>
                    <div>
                      <p className="font-medium text-sm">{triage.patient}</p>
                    </div>
                  </div>
                  <Badge variant={
                    triage.status === "concluída" ? "default" :
                    triage.status === "em andamento" ? "secondary" : "outline"
                  }>
                    {triage.status}
                  </Badge>
                </div>
              ))}
            </div>
            <Button variant="outline" className="w-full mt-4">
              Ver Todas as Triagens
            </Button>
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle className="flex items-center gap-2">
              <Phone className="h-5 w-5" />
              Pacientes a Confirmar
            </CardTitle>
          </CardHeader>
          <CardContent>
            <div className="space-y-3">
              {toConfirm.map((item) => (
                <div key={item.id} className="flex items-center justify-between p-3 bg-muted/50 rounded-lg">
                  <div>
                    <p className="font-medium text-sm">{item.patient}</p>
                    <p className="text-xs text-muted-foreground">{item.appointment}</p>
                    <p className="text-xs text-muted-foreground">{item.phone}</p>
                  </div>
                  <Button size="sm" variant="outline">
                    <Phone className="h-4 w-4" />
                  </Button>
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
              <UserPlus className="h-6 w-6" />
              Novo Paciente
            </Button>
            <Button variant="outline" className="h-20 flex-col gap-2">
              <Calendar className="h-6 w-6" />
              Agendar Consulta
            </Button>
            <Button variant="outline" className="h-20 flex-col gap-2">
              <ClipboardCheck className="h-6 w-6" />
              Iniciar Triagem
            </Button>
            <Button variant="outline" className="h-20 flex-col gap-2">
              <DollarSign className="h-6 w-6" />
              Registrar Pagamento
            </Button>
          </div>
        </CardContent>
      </Card>
    </div>
  );
}
