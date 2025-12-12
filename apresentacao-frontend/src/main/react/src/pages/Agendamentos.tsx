import { useState } from "react";
import { Calendar, Clock, User, Plus, Search, X } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { AppointmentForm } from "@/components/AppointmentForm";
import { AlertDialog, AlertDialogAction, AlertDialogCancel, AlertDialogContent, AlertDialogDescription, AlertDialogFooter, AlertDialogHeader, AlertDialogTitle } from "@/components/ui/alert-dialog";
import { toast } from "sonner";
import { useAuth } from "@/contexts/AuthContext";

const initialAppointments = [
  {
    id: 1,
    patient: "Maria Silva",
    doctor: "Dr. João Santos",
    date: "2024-09-01",
    time: "09:00",
    type: "Consulta" as const,
    status: "confirmado" as const,
    specialty: "Cardiologia"
  },
  {
    id: 2,
    patient: "Pedro Costa",
    doctor: "Dra. Ana Lima",
    date: "2024-09-01",
    time: "09:30",
    type: "Retorno" as const,
    status: "agendado" as const,
    specialty: "Ortopedia"
  },
  {
    id: 3,
    patient: "João Santos",
    doctor: "Dr. Carlos Mendes",
    date: "2024-09-01",
    time: "10:00",
    type: "Exame" as const,
    status: "em-atendimento" as const,
    specialty: "Neurologia"
  },
] as Array<{
  id: number;
  patient: string;
  doctor: string;
  date: string;
  time: string;
  type: "Consulta" | "Retorno" | "Exame";
  status: "confirmado" | "agendado" | "em-atendimento" | "concluido" | "cancelado";
  specialty: string;
}>;

export default function Agendamentos() {
  const { isGestor } = useAuth();
  const [searchTerm, setSearchTerm] = useState("");
  const [appointments, setAppointments] = useState(initialAppointments);
  const [isFormOpen, setIsFormOpen] = useState(false);
  const [editingAppointment, setEditingAppointment] = useState<typeof appointments[0] | undefined>();
  const [cancelDialogOpen, setCancelDialogOpen] = useState(false);
  const [appointmentToCancel, setAppointmentToCancel] = useState<number | null>(null);

  const handleSaveAppointment = (data: any) => {
    if (editingAppointment) {
      setAppointments(appointments.map(a => 
        a.id === editingAppointment.id 
          ? { ...a, ...data }
          : a
      ));
      setEditingAppointment(undefined);
    } else {
      const newAppointment = {
        ...data,
        id: Math.max(...appointments.map(a => a.id)) + 1,
      };
      setAppointments([...appointments, newAppointment]);
    }
  };

  const handleEditAppointment = (appointment: typeof appointments[0]) => {
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
      setAppointments(appointments.map(a => 
        a.id === appointmentToCancel 
          ? { ...a, status: "cancelado" as const }
          : a
      ));
      toast.success("Agendamento cancelado com sucesso!");
    }
    setCancelDialogOpen(false);
    setAppointmentToCancel(null);
  };

  const filteredAppointments = appointments.filter(appointment =>
    appointment.patient.toLowerCase().includes(searchTerm.toLowerCase()) ||
    appointment.doctor.toLowerCase().includes(searchTerm.toLowerCase())
  );

  const getStatusBadge = (status: string) => {
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

    return (
      <Badge 
        variant={variants[status as keyof typeof variants] || "secondary"}
        className={colors[status as keyof typeof colors] || ""}
      >
        {status.replace("-", " ")}
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
          <div className="grid gap-4">
            {filteredAppointments.map((appointment) => (
              <Card key={appointment.id} className="hover:shadow-medical transition-all duration-300">
                <CardContent className="p-4">
                  <div className="flex items-center justify-between">
                    <div className="flex items-center gap-4">
                      <div className="text-center">
                        <div className="text-lg font-bold text-primary">{appointment.time}</div>
                        <div className="text-xs text-muted-foreground">
                          {new Date(appointment.date).toLocaleDateString('pt-BR', { 
                            day: '2-digit', 
                            month: 'short' 
                          })}
                        </div>
                      </div>
                      
                      <div className="flex-1">
                        <div className="flex items-center gap-2 mb-1">
                          <User className="w-4 h-4 text-muted-foreground" />
                          <span className="font-medium">{appointment.patient}</span>
                        </div>
                        <div className="text-sm text-muted-foreground">
                          {appointment.doctor} • {appointment.specialty}
                        </div>
                        <div className="text-sm text-muted-foreground">
                          {appointment.type}
                        </div>
                      </div>
                    </div>

                    <div className="flex items-center gap-3">
                      {getStatusBadge(appointment.status)}
                      {!isGestor && (
                        <>
                          <Button variant="outline" size="sm" onClick={() => handleEditAppointment(appointment)}>
                            Remarcar
                          </Button>
                          {appointment.status !== "cancelado" && (
                            <Button 
                              variant="outline" 
                              size="sm" 
                              onClick={() => handleCancelAppointment(appointment.id)}
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
            ))}
          </div>
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