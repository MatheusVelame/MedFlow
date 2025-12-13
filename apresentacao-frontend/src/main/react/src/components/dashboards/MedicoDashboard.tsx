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
          value="4"
          description="2 Agendadas, 2 Em andamento"
          icon={<Calendar className="h-4 w-4" />}
        />
        <StatCard
          title="Pacientes Atendidos no Mês"
          value="87"
          description="Meta: 100 pacientes"
          icon={<Users className="h-4 w-4" />}
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

        {/* Card MODIFICADO: Exibe APENAS os Alertas de Revisões Pendentes */}
        <Card className="lg:col-start-2">
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

      </div>

    </div>
  );
}