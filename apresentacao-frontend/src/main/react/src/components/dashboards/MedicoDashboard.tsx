import { 
  Calendar, 
  Users, 
  Clock,
  FileText,
  Activity,
  AlertCircle,
  ClipboardList,
  CheckCircle
} from "lucide-react";
import { StatCard } from "@/components/StatCard";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
// Import do hook para listar medicamentos com revisão pendente
import { useListarMedicamentosComRevisaoPendente } from "@/api/useMedicamentosApi"; 

const myAppointments = [
  { id: 1, patient: "Maria Silva", time: "09:00", type: "Consulta", room: "Sala 1" },
  { id: 2, patient: "João Santos", time: "09:30", type: "Retorno", room: "Sala 1" },
  { id: 3, patient: "Ana Costa", time: "10:00", type: "Consulta", room: "Sala 1" },
  { id: 4, patient: "Pedro Lima", time: "10:30", type: "Retorno", room: "Sala 1" },
];

// Removendo os alertas clínicos mockados, pois o cliente deseja exibir apenas as revisões.
const clinicalAlerts = [
  { id: 1, type: "exam", message: "Resultado de hemograma de Maria Silva disponível", time: "10 min" },
  { id: 2, type: "evolution", message: "2 evoluções pendentes de registro", time: "30 min" },
  { id: 3, type: "followup", message: "João Santos: retorno agendado para amanhã", time: "1 hora" },
];

const recentRecords = [
  { id: 1, patient: "Carlos Mendes", date: "Hoje, 08:30", diagnosis: "Hipertensão" },
  { id: 2, patient: "Fernanda Rocha", date: "Ontem, 15:45", diagnosis: "Gripe" },
  { id: 3, patient: "Roberto Silva", date: "Ontem, 14:20", diagnosis: "Diabetes tipo 2" },
];

const followUpPatients = [
  { id: 1, name: "Maria Silva", condition: "Pós-operatório", nextVisit: "Amanhã" },
  { id: 2, name: "João Santos", condition: "Hipertensão", nextVisit: "Em 3 dias" },
  { id: 3, name: "Ana Costa", condition: "Diabetes", nextVisit: "Próxima semana" },
];

export default function MedicoDashboard() {
  // Busca os dados de revisões pendentes
  const { data: medicamentosPendentes, isLoading: isLoadingMedicamentos } = useListarMedicamentosComRevisaoPendente();

  // Mapeia os medicamentos com revisão pendente para o formato de alerta
  const medicamentosAlerts = medicamentosPendentes?.map(med => ({
      id: `med-${med.id}`, 
      type: "medicamento-review", 
      message: `Revisão pendente: ${med.nome}`, 
      time: "Aguardando Aprovação" // Texto mais contextualizado
  })) || [];

  // OBS: Não precisamos mais do allClinicalAlerts se o objetivo é mostrar APENAS as revisões

  return (
    <div className="space-y-6 animate-fade-in">
      <div>
        <h1 className="text-3xl font-bold tracking-tight text-foreground">Dashboard - Médico</h1>
        <p className="text-muted-foreground">Sua agenda e atividades clínicas do dia</p>
      </div>

      <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
        <StatCard
          title="Minhas Consultas Hoje"
          value="12"
          description="4 realizadas, 8 pendentes"
          icon={<Calendar className="h-4 w-4" />}
          trend={{ value: 15, isPositive: true }}
        />
        <StatCard
          title="Pacientes Atendidos no Mês"
          value="87"
          description="Meta: 100 pacientes"
          icon={<Users className="h-4 w-4" />}
          trend={{ value: 10, isPositive: true }}
        />
        {/* Card de Revisões de Medicamentos Pendentes (Estatística) */}
        <StatCard
          title="Revisões Medicamento"
          value={isLoadingMedicamentos ? '...' : (medicamentosPendentes?.length ?? 0).toString()}
          description="Medicamentos com alteração crítica"
          icon={<AlertCircle className="h-4 w-4" />}
        />
        <StatCard
          title="Exames Pendentes"
          value="5"
          description="Aguardando análise"
          icon={<FileText className="h-4 w-4" />}
        />
      </div>

      <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-3">
        <Card className="lg:col-span-2">
          <CardHeader>
            <CardTitle className="flex items-center gap-2">
              <Calendar className="h-5 w-5" />
              Minha Agenda do Dia
            </CardTitle>
          </CardHeader>
          <CardContent>
            <div className="space-y-3">
              {myAppointments.map((appointment) => (
                <div key={appointment.id} className="flex items-center justify-between p-3 bg-muted/50 rounded-lg hover:bg-muted transition-colors">
                  <div className="flex items-center gap-3">
                    <div className="text-sm font-medium text-primary">
                      {appointment.time}
                    </div>
                    <div>
                      <p className="font-medium">{appointment.patient}</p>
                      <p className="text-sm text-muted-foreground">{appointment.type} • {appointment.room}</p>
                    </div>
                  </div>
                  <Button size="sm" variant="outline">Iniciar</Button>
                </div>
              ))}
            </div>
            <Button variant="outline" className="w-full mt-4">
              Ver Agenda Completa
            </Button>
          </CardContent>
        </Card>

        {/* Card MODIFICADO: Exibe APENAS os Alertas de Revisões Pendentes */}
        <Card>
          <CardHeader>
            <CardTitle className="flex items-center gap-2">
              {/* Ícone de Alerta mantido, mas com foco no processo crítico */}
              <AlertCircle className="h-5 w-5 text-destructive" /> 
              Alertas de Revisões Pendentes
            </CardTitle>
          </CardHeader>
          <CardContent>
            <div className="space-y-3">
              {medicamentosAlerts.length > 0 ? (
                medicamentosAlerts.map((alert) => (
                  <div key={alert.id} className="flex gap-3 p-3 bg-destructive/10 rounded-lg">
                    <div className="w-2 h-2 rounded-full mt-2 flex-shrink-0 bg-destructive" />
                    <div className="flex-1">
                      <p className="text-sm font-medium">{alert.message}</p>
                      <p className="text-xs text-muted-foreground mt-1">{alert.time}</p>
                    </div>
                  </div>
                ))
              ) : (
                <p className="text-sm text-muted-foreground">Nenhuma revisão crítica pendente no momento.</p>
              )}
            </div>
          </CardContent>
        </Card>
      </div>

      <div className="grid gap-6 md:grid-cols-2">
        <Card>
          <CardHeader>
            <CardTitle className="flex items-center gap-2">
              <ClipboardList className="h-5 w-5" />
              Últimos Prontuários Atualizados
            </CardTitle>
          </CardHeader>
          <CardContent>
            <div className="space-y-3">
              {recentRecords.map((record) => (
                <div key={record.id} className="flex items-center justify-between p-3 bg-muted/50 rounded-lg">
                  <div>
                    <p className="font-medium">{record.patient}</p>
                    <p className="text-sm text-muted-foreground">{record.diagnosis}</p>
                    <p className="text-xs text-muted-foreground mt-1">{record.date}</p>
                  </div>
                  <Button size="sm" variant="ghost">Ver</Button>
                </div>
              ))}
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle className="flex items-center gap-2">
              <Activity className="h-5 w-5" />
              Pacientes em Acompanhamento
            </CardTitle>
          </CardHeader>
          <CardContent>
            <div className="space-y-3">
              {followUpPatients.map((patient) => (
                <div key={patient.id} className="flex items-center justify-between p-3 bg-muted/50 rounded-lg">
                  <div>
                    <p className="font-medium">{patient.name}</p>
                    <p className="text-sm text-muted-foreground">{patient.condition}</p>
                  </div>
                  <Badge variant="outline">{patient.nextVisit}</Badge>
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
              <CheckCircle className="h-6 w-6" />
              Iniciar Consulta
            </Button>
            <Button variant="outline" className="h-20 flex-col gap-2">
              <ClipboardList className="h-6 w-6" />
              Registrar Evolução
            </Button>
            <Button variant="outline" className="h-20 flex-col gap-2">
              <FileText className="h-6 w-6" />
              Solicitar Exame
            </Button>
            <Button variant="outline" className="h-20 flex-col gap-2">
              <Activity className="h-6 w-6" />
              Ver Prontuários
            </Button>
          </div>
        </CardContent>
      </Card>
    </div>
  );
}