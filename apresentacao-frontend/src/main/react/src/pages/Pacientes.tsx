import { useState } from "react";
import { User, Plus, Search, Phone, Mail, Calendar, Trash2 } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Card, CardContent } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Avatar, AvatarFallback } from "@/components/ui/avatar";
import { PatientForm } from "@/components/PatientForm";
import { AlertDialog, AlertDialogAction, AlertDialogCancel, AlertDialogContent, AlertDialogDescription, AlertDialogFooter, AlertDialogHeader, AlertDialogTitle } from "@/components/ui/alert-dialog";
import { toast } from "sonner";
import { useAuth } from "@/contexts/AuthContext";

// Dados mockados removidos - aguardando integração com backend
const initialPatients: any[] = [];

export default function Pacientes() {
  const { isMedico } = useAuth();
  const [searchTerm, setSearchTerm] = useState("");
  const [patients, setPatients] = useState(initialPatients);
  const [isFormOpen, setIsFormOpen] = useState(false);
  const [editingPatient, setEditingPatient] = useState<typeof patients[0] | undefined>();
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
  const [patientToDelete, setPatientToDelete] = useState<number | null>(null);

  const getInitials = (name: string) => {
    const names = name.split(" ");
    return names.length > 1 ? `${names[0][0]}${names[1][0]}` : names[0][0];
  };

  const calculateAge = (birthDate: string) => {
    const today = new Date();
    const birth = new Date(birthDate);
    let age = today.getFullYear() - birth.getFullYear();
    const monthDiff = today.getMonth() - birth.getMonth();
    if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birth.getDate())) {
      age--;
    }
    return age;
  };

  const handleSavePatient = (data: any) => {
    if (editingPatient) {
      setPatients(patients.map(p => 
        p.id === editingPatient.id 
          ? { ...p, ...data, lastVisit: p.lastVisit }
          : p
      ));
      setEditingPatient(undefined);
    } else {
      const newPatient = {
        ...data,
        id: Math.max(...patients.map(p => p.id)) + 1,
        lastVisit: new Date().toISOString().split('T')[0],
      };
      setPatients([...patients, newPatient]);
    }
  };

  const handleEditPatient = (patient: typeof patients[0]) => {
    setEditingPatient(patient);
    setIsFormOpen(true);
  };

  const handleNewPatient = () => {
    setEditingPatient(undefined);
    setIsFormOpen(true);
  };

  const handleDeletePatient = (id: number) => {
    setPatientToDelete(id);
    setDeleteDialogOpen(true);
  };

  const confirmDeletePatient = () => {
    if (patientToDelete) {
      setPatients(patients.filter(p => p.id !== patientToDelete));
      toast.success("Paciente excluído com sucesso!");
    }
    setDeleteDialogOpen(false);
    setPatientToDelete(null);
  };

  const filteredPatients = patients.filter(patient =>
    patient.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
    patient.cpf.includes(searchTerm)
  );

  return (
    <div className="space-y-6 animate-fade-in">
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-3xl font-bold tracking-tight">Pacientes</h1>
          <p className="text-muted-foreground">Gerencie o cadastro de pacientes</p>
        </div>
        {!isMedico && (
          <Button className="bg-gradient-primary hover:bg-primary-hover" onClick={handleNewPatient}>
            <Plus className="w-4 h-4 mr-2" />
            Novo Paciente
          </Button>
        )}
      </div>

      <PatientForm
        open={isFormOpen}
        onOpenChange={setIsFormOpen}
        patient={editingPatient}
        onSave={handleSavePatient}
      />

      <div className="flex gap-4">
        <div className="relative flex-1 max-w-md">
          <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-muted-foreground w-4 h-4" />
          <Input
            placeholder="Buscar por nome, CPF..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="pl-10"
          />
        </div>
      </div>

      <div className="grid gap-4">
        {filteredPatients.map((patient) => (
          <Card key={patient.id} className="hover:shadow-medical transition-all duration-300">
            <CardContent className="p-6">
              <div className="flex items-start justify-between">
                <div className="flex items-start gap-4">
                  <Avatar className="w-12 h-12">
                    <AvatarFallback className="bg-gradient-primary text-white font-medium">
                      {getInitials(patient.name)}
                    </AvatarFallback>
                  </Avatar>
                  
                  <div className="flex-1">
                    <div className="flex items-center gap-2 mb-2">
                      <h3 className="font-semibold text-lg">{patient.name}</h3>
                      <Badge variant={patient.status === "ativo" ? "default" : "secondary"}>
                        {patient.status}
                      </Badge>
                    </div>
                    
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-2 text-sm text-muted-foreground">
                      <div className="flex items-center gap-2">
                        <User className="w-4 h-4" />
                        CPF: {patient.cpf}
                      </div>
                      <div className="flex items-center gap-2">
                        <Calendar className="w-4 h-4" />
                        {calculateAge(patient.birthDate)} anos
                      </div>
                      <div className="flex items-center gap-2">
                        <Phone className="w-4 h-4" />
                        {patient.phone}
                      </div>
                      <div className="flex items-center gap-2">
                        <Mail className="w-4 h-4" />
                        {patient.email}
                      </div>
                    </div>
                    
                    <div className="mt-3 pt-3 border-t">
                      <div className="flex items-center justify-between text-sm">
                        <div>
                          <span className="text-muted-foreground">Convênio: </span>
                          <span className="font-medium">{patient.insurance}</span>
                          {patient.plan !== "-" && (
                            <span className="text-muted-foreground"> • {patient.plan}</span>
                          )}
                        </div>
                        <div>
                          <span className="text-muted-foreground">Última consulta: </span>
                          <span className="font-medium">
                            {new Date(patient.lastVisit).toLocaleDateString('pt-BR')}
                          </span>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>

                <div className="flex gap-2">
                  {!isMedico && (
                    <Button variant="outline" size="sm" onClick={() => handleEditPatient(patient)}>
                      Editar
                    </Button>
                  )}
                  <Button variant="outline" size="sm">
                    Prontuário
                  </Button>
                  {!isMedico && (
                    <Button 
                      variant="outline" 
                      size="sm" 
                      onClick={() => handleDeletePatient(patient.id)}
                      className="text-destructive hover:text-destructive"
                    >
                      <Trash2 className="w-4 h-4" />
                    </Button>
                  )}
                </div>
              </div>
            </CardContent>
          </Card>
        ))}
      </div>

      <AlertDialog open={deleteDialogOpen} onOpenChange={setDeleteDialogOpen}>
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle>Excluir Paciente</AlertDialogTitle>
            <AlertDialogDescription>
              Tem certeza que deseja excluir este paciente? Esta ação não pode ser desfeita e todos os dados relacionados serão perdidos.
            </AlertDialogDescription>
          </AlertDialogHeader>
          <AlertDialogFooter>
            <AlertDialogCancel>Cancelar</AlertDialogCancel>
            <AlertDialogAction onClick={confirmDeletePatient} className="bg-destructive hover:bg-destructive/90">
              Excluir
            </AlertDialogAction>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>
    </div>
  );
}