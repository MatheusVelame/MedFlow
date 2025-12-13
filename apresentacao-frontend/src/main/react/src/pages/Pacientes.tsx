import { useState } from "react";
import { User, Plus, Search, Phone, Trash2 } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Card, CardContent } from "@/components/ui/card";
import { Avatar, AvatarFallback } from "@/components/ui/avatar";
import { PatientForm } from "@/components/PatientForm";
import { AlertDialog, AlertDialogAction, AlertDialogCancel, AlertDialogContent, AlertDialogDescription, AlertDialogFooter, AlertDialogHeader, AlertDialogTitle } from "@/components/ui/alert-dialog";
import { useAuth } from "@/contexts/AuthContext";
// Importamos o novo hook useBuscarPacientePorId
import { 
  useListarPacientes, 
  useBuscarPacientePorId, 
  useExcluirPaciente, 
  useCadastrarPaciente, 
  useAtualizarPaciente, 
  PacienteView 
} from "@/api/usePacientesApi";

export default function Pacientes() {
  const { isMedico } = useAuth();
  
  // Estado local
  const [searchTerm, setSearchTerm] = useState("");
  const [isFormOpen, setIsFormOpen] = useState(false);
  
  // Guardamos o resumo do paciente selecionado (vindo da lista)
  const [selectedPatientSummary, setSelectedPatientSummary] = useState<PacienteView | undefined>();
  
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
  const [patientToDelete, setPatientToDelete] = useState<number | null>(null);

  // === HOOKS ===
  // 1. Lista Geral
  const { data: patients = [], isLoading, error } = useListarPacientes();
  
  // 2. Busca Detalhada (Só roda quando temos um ID selecionado)
  const { data: pacienteDetalhado } = useBuscarPacientePorId(selectedPatientSummary?.id);

  const excluirMutation = useExcluirPaciente();
  const cadastrarMutation = useCadastrarPaciente();
  const atualizarMutation = useAtualizarPaciente();

  const getInitials = (name: string) => {
    const names = name.split(" ");
    return names.length > 1 ? `${names[0][0]}${names[1][0]}` : names[0][0];
  };

  const formatarDataParaBackend = (dataIso: string) => {
    if (!dataIso) return "";
    if (dataIso.includes("/")) return dataIso;
    const [ano, mes, dia] = dataIso.split("-");
    return `${dia}/${mes}/${ano}`;
  };
  
  const handleSavePatient = async (data: any) => {
    try {
      const dadosParaEnviar = {
        nome: data.name,
        cpf: data.cpf.replace(/\D/g, ""),
        telefone: data.phone.replace(/\D/g, ""), // Limpa telefone
        endereco: data.address,
        dataNascimento: formatarDataParaBackend(data.birthDate),
        responsavelId: 1
      };

      if (selectedPatientSummary) {
        await atualizarMutation.mutateAsync({ 
          id: selectedPatientSummary.id, 
          payload: dadosParaEnviar 
        });
      } else {
        await cadastrarMutation.mutateAsync(dadosParaEnviar);
      }
      
      closeForm();
    } catch (error) {
      console.error("Erro ao salvar:", error);
    }
  };

  // Abre Modal de Edição
  const handleEditPatient = (patient: PacienteView) => {
    setSelectedPatientSummary(patient); // Define quem estamos editando
    setIsFormOpen(true);
  };

  // Abre Modal Novo
  const handleNewPatient = () => {
    setSelectedPatientSummary(undefined); // Limpa seleção
    setIsFormOpen(true);
  };

  const closeForm = () => {
    setIsFormOpen(false);
    setSelectedPatientSummary(undefined); // Limpa ao fechar
  };

  const handleDeletePatient = (id: number) => {
    setPatientToDelete(id);
    setDeleteDialogOpen(true);
  };

  const confirmDeletePatient = () => {
    if (patientToDelete) {
      excluirMutation.mutate(patientToDelete);
    }
    setDeleteDialogOpen(false);
    setPatientToDelete(null);
  };

  const filteredPatients = patients.filter(patient =>
    patient.name?.toLowerCase().includes(searchTerm.toLowerCase()) ||
    patient.cpf?.includes(searchTerm)
  );

  // COMBINAÇÃO DE DADOS:
  // Se 'pacienteDetalhado' (da busca nova) existir, usamos ele (tem endereço).
  // Se ainda estiver carregando, usamos 'selectedPatientSummary' (tem nome/cpf).
  const dadosParaOFormulario = pacienteDetalhado || selectedPatientSummary;

  if (isLoading) return <div className="flex justify-center h-64 items-center">Carregando pacientes...</div>;
  if (error) return <div className="flex justify-center h-64 items-center text-red-500">Erro ao carregar o sistema.</div>;

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

      {/* Passamos 'dadosParaOFormulario' que agora pode conter o endereço vindo do fetch */}
      <PatientForm
        open={isFormOpen}
        onOpenChange={(isOpen) => !isOpen && closeForm()}
        patient={dadosParaOFormulario}
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

      {filteredPatients.length === 0 ? (
        <Card>
          <CardContent className="flex flex-col items-center justify-center py-12">
            <h3 className="text-lg font-semibold">Nenhum paciente encontrado</h3>
          </CardContent>
        </Card>
      ) : (
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
                      <div className="mb-2">
                        <h3 className="font-semibold text-lg">{patient.name}</h3>
                      </div>
                      
                      <div className="flex flex-col gap-1 text-sm text-muted-foreground">
                        <div className="flex items-center gap-2">
                          <User className="w-4 h-4" />
                          <span>CPF: {patient.cpf}</span>
                        </div>
                        <div className="flex items-center gap-2">
                          <Phone className="w-4 h-4" />
                          <span>{patient.phone}</span>
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
                    <Button variant="outline" size="sm">Prontuário</Button>
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
      )}

      {/* Dialog Delete mantido */}
      <AlertDialog open={deleteDialogOpen} onOpenChange={setDeleteDialogOpen}>
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle>Excluir Paciente</AlertDialogTitle>
            <AlertDialogDescription>Esta ação é irreversível.</AlertDialogDescription>
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
