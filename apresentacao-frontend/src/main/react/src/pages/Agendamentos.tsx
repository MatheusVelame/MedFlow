import { useState } from "react";
import { Calendar, Clock, User, Plus, Search, X, Loader2 } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { AppointmentForm } from "@/components/AppointmentForm";
import { AlertDialog, AlertDialogAction, AlertDialogCancel, AlertDialogContent, AlertDialogDescription, AlertDialogFooter, AlertDialogHeader, AlertDialogTitle } from "@/components/ui/alert-dialog";
import { toast } from "sonner";
import { useAuth } from "@/contexts/AuthContext";
import {
  useListarConsultas,
  useAgendarConsulta,
  useMudarStatusConsulta,
  mapStatusToFrontend,
  mapStatusToBackend,
  type ConsultaResumo,
  type StatusConsulta
} from "@/api/useConsultasApi";

export default function Agendamentos() {
  const { isGestor, user } = useAuth();
  const [searchTerm, setSearchTerm] = useState("");
  const [isFormOpen, setIsFormOpen] = useState(false);
  const [editingAppointment, setEditingAppointment] = useState<ConsultaResumo | undefined>();
  const [cancelDialogOpen, setCancelDialogOpen] = useState(false);
  const [appointmentToCancel, setAppointmentToCancel] = useState<number | null>(null);

  // Queries e Mutations
  const { data: consultas = [], isLoading, error } = useListarConsultas();
  const agendarMutation = useAgendarConsulta();
  const mudarStatusMutation = useMudarStatusConsulta();

  const handleSaveAppointment = (data: any) => {
    const usuarioId = parseInt(user?.id || "1");
    
    if (editingAppointment) {
      // Atualizar consulta (remarcar)
      toast.info("Funcionalidade de remarcar em desenvolvimento");
    } else {
      // Agendar nova consulta
      const dataHoraISO = `${data.date}T${data.time}:00`;
      agendarMutation.mutate({
        dataHora: dataHoraISO,
        descricao: data.type || "Consulta",
        pacienteId: data.patientId,
        medicoId: data.doctorId,
        usuarioId: usuarioId
      }, {
        onSuccess: () => {
          setIsFormOpen(false);
          setEditingAppointment(undefined);
        }
      });
    }
  };

  const handleEditAppointment = (appointment: ConsultaResumo) => {
    setEditingAppointment(appointment);
    setIsFormOpen(true);
  };

  const handleNewAppointment = () => {
    setEditingAppointment(undefined);
    setIsFormOpen(true);
  };

  const handleCancelAppointment = (id: number) => {
    setAppointmentToCancel(id);
    setCancelDialogOpen(true);
  };

  const confirmCancelAppointment = () => {
    if (appointmentToCancel) {
      const usuarioId = parseInt(user?.id || "1");
      mudarStatusMutation.mutate({
        id: appointmentToCancel,
        payload: {
          novoStatus: "CANCELADA" as StatusConsulta,
          usuarioId: usuarioId
        }
      }, {
        onSuccess: () => {
          setCancelDialogOpen(false);
          setAppointmentToCancel(null);
        }
      });
    }
  };

  const filteredAppointments = consultas.filter(consulta =>
    consulta.nomePaciente.toLowerCase().includes(searchTerm.toLowerCase()) ||
    consulta.nomeMedico.toLowerCase().includes(searchTerm.toLowerCase())
  );

  const getStatusBadge = (status: StatusConsulta) => {
    const statusFrontend = mapStatusToFrontend(status);
    const variants = {
      confirmado: "default",
      agendado: "secondary",
      "em-atendimento": "outline",
      concluido: "default",
      cancelado: "destructive"
    } as const;

    const colors = {
      confirmado: "bg-success text-success-foreground",
      agendado: "",
      "em-atendimento": "bg-warning text-warning-foreground",
      concluido: "bg-success text-success-foreground",
      cancelado: ""
    };

    const labels: Record<string, string> = {
      confirmado: "Confirmado",
      agendado: "Agendado",
      "em-atendimento": "Em Atendimento",
      concluido: "Concluído",
      cancelado: "Cancelado"
    };

    return (
      <Badge 
        variant={variants[statusFrontend as keyof typeof variants] || "secondary"}
        className={colors[statusFrontend as keyof typeof colors] || ""}
      >
        {labels[statusFrontend] || statusFrontend}
      </Badge>
    );
  };

  return (
    <div className="space-y-6 animate-fade-in">
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-3xl font-bold tracking-tight">Agendamentos</h1>
          <p className="text-muted-foreground">Gerencie consultas e procedimentos</p>
        </div>
        {!isGestor && (
          <Button className="bg-gradient-primary hover:bg-primary-hover" onClick={handleNewAppointment}>
            <Plus className="w-4 h-4 mr-2" />
            Nova Consulta
          </Button>
        )}
      </div>

      <AppointmentForm
        open={isFormOpen}
        onOpenChange={setIsFormOpen}
        appointment={editingAppointment}
        onSave={handleSaveAppointment}
      />

      <div className="flex gap-4">
        <div className="relative flex-1 max-w-md">
          <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-muted-foreground w-4 h-4" />
          <Input
            placeholder="Buscar por paciente, médico..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="pl-10"
          />
        </div>
      </div>

      <Tabs defaultValue="hoje" className="space-y-4">
        <TabsList>
          <TabsTrigger value="hoje">Hoje</TabsTrigger>
          <TabsTrigger value="semana">Esta Semana</TabsTrigger>
          <TabsTrigger value="mes">Este Mês</TabsTrigger>
        </TabsList>

        <TabsContent value="hoje" className="space-y-4">
          {isLoading ? (
            <div className="flex items-center justify-center py-12">
              <Loader2 className="w-8 h-8 animate-spin text-primary" />
            </div>
          ) : error ? (
            <div className="text-center py-12 text-destructive">
              Erro ao carregar agendamentos. Tente novamente.
            </div>
          ) : filteredAppointments.length === 0 ? (
            <div className="text-center py-12 text-muted-foreground">
              {searchTerm ? "Nenhum agendamento encontrado com os filtros aplicados." : "Nenhum agendamento cadastrado."}
            </div>
          ) : (
            <div className="grid gap-4">
              {filteredAppointments.map((consulta) => {
                const dataHora = new Date(consulta.dataHora);
                const time = dataHora.toLocaleTimeString('pt-BR', { hour: '2-digit', minute: '2-digit' });
                const date = dataHora.toLocaleDateString('pt-BR', { day: '2-digit', month: 'short' });
                
                return (
                  <Card key={consulta.id} className="hover:shadow-medical transition-all duration-300">
                    <CardContent className="p-4">
                      <div className="flex items-center justify-between">
                        <div className="flex items-center gap-4">
                          <div className="text-center">
                            <div className="text-lg font-bold text-primary">{time}</div>
                            <div className="text-xs text-muted-foreground">{date}</div>
                          </div>
                          
                          <div className="flex-1">
                            <div className="flex items-center gap-2 mb-1">
                              <User className="w-4 h-4 text-muted-foreground" />
                              <span className="font-medium">{consulta.nomePaciente}</span>
                            </div>
                            <div className="text-sm text-muted-foreground">
                              {consulta.nomeMedico}
                            </div>
                          </div>
                        </div>

                        <div className="flex items-center gap-3">
                          {getStatusBadge(consulta.status)}
                          {!isGestor && (
                            <>
                              <Button variant="outline" size="sm" onClick={() => handleEditAppointment(consulta)}>
                                Remarcar
                              </Button>
                              {consulta.status !== "CANCELADA" && (
                                <Button 
                                  variant="outline" 
                                  size="sm" 
                                  onClick={() => handleCancelAppointment(consulta.id)}
                                  className="text-destructive hover:text-destructive"
                                >
                                  <X className="w-4 h-4" />
                                </Button>
                              )}
                            </>
                          )}
                        </div>
                      </div>
                    </CardContent>
                  </Card>
                );
              })}
            </div>
          )}
        </TabsContent>

        <TabsContent value="semana">
          <Card>
            <CardContent className="p-6">
              <p className="text-center text-muted-foreground">
                Visualização semanal em desenvolvimento
              </p>
            </CardContent>
          </Card>
        </TabsContent>

        <TabsContent value="mes">
          <Card>
            <CardContent className="p-6">
              <p className="text-center text-muted-foreground">
                Visualização mensal em desenvolvimento
              </p>
            </CardContent>
          </Card>
        </TabsContent>
      </Tabs>

      <AlertDialog open={cancelDialogOpen} onOpenChange={setCancelDialogOpen}>
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle>Cancelar Agendamento</AlertDialogTitle>
            <AlertDialogDescription>
              Tem certeza que deseja cancelar este agendamento? Esta ação não pode ser desfeita.
            </AlertDialogDescription>
          </AlertDialogHeader>
          <AlertDialogFooter>
            <AlertDialogCancel>Não</AlertDialogCancel>
            <AlertDialogAction onClick={confirmCancelAppointment} className="bg-destructive hover:bg-destructive/90">
              Sim, cancelar
            </AlertDialogAction>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>
    </div>
  );
}