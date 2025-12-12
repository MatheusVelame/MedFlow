import { useState } from "react";
import { Stethoscope, Search, Plus, Calendar, Award } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Avatar, AvatarFallback } from "@/components/ui/avatar";
import { ProfessionalForm } from "@/components/ProfessionalForm";

const mockProfissionais = [
  {
    id: "1",
    nome: "Dr. Carlos Mendes",
    conselho: "CRM",
    numeroConselho: "12345/SP",
    especialidade: "Cardiologia",
    status: "ativo",
    consultasHoje: 8,
    proximaConsulta: "14:30"
  },
  {
    id: "2",
    nome: "Dra. Ana Paula Costa",
    conselho: "CRM",
    numeroConselho: "67890/SP",
    especialidade: "Pediatria",
    status: "ativo",
    consultasHoje: 12,
    proximaConsulta: "15:00"
  },
  {
    id: "3",
    nome: "Dr. Roberto Lima",
    conselho: "CRM",
    numeroConselho: "54321/SP",
    especialidade: "Ortopedia",
    status: "ativo",
    consultasHoje: 6,
    proximaConsulta: "16:30"
  },
  {
    id: "4",
    nome: "Enf. Juliana Santos",
    conselho: "COREN",
    numeroConselho: "98765/SP",
    especialidade: "Enfermagem",
    status: "ativo",
    consultasHoje: 0,
    proximaConsulta: "-"
  },
  {
    id: "5",
    nome: "Dra. Mariana Oliveira",
    conselho: "CRM",
    numeroConselho: "11223/SP",
    especialidade: "Dermatologia",
    status: "ferias",
    consultasHoje: 0,
    proximaConsulta: "-"
  }
];

export default function Profissionais() {
  const [searchTerm, setSearchTerm] = useState("");
  const [profissionais, setProfissionais] = useState(mockProfissionais);
  const [isFormOpen, setIsFormOpen] = useState(false);
  const [editingProfessional, setEditingProfessional] = useState<typeof profissionais[0] | undefined>();

  const handleSaveProfessional = (data: any) => {
    if (editingProfessional) {
      setProfissionais(profissionais.map(p => 
        p.id === editingProfessional.id 
          ? { ...p, ...data }
          : p
      ));
      setEditingProfessional(undefined);
    } else {
      const newProfessional = {
        ...data,
        id: String(Math.max(...profissionais.map(p => parseInt(p.id))) + 1),
        consultasHoje: 0,
        proximaConsulta: "-"
      };
      setProfissionais([...profissionais, newProfessional]);
    }
  };

  const handleEditProfessional = (professional: typeof profissionais[0]) => {
    setEditingProfessional(professional);
    setIsFormOpen(true);
  };

  const handleNewProfessional = () => {
    setEditingProfessional(undefined);
    setIsFormOpen(true);
  };

  const getInitials = (nome: string) => {
    return nome
      .split(" ")
      .map(n => n[0])
      .join("")
      .substring(0, 2)
      .toUpperCase();
  };

  const getStatusBadge = (status: string) => {
    const configs: Record<string, { variant: "default" | "secondary" | "destructive" | "outline"; label: string; className?: string }> = {
      ativo: { variant: "default", label: "Ativo", className: "bg-success/10 text-success border-success/20" },
      ferias: { variant: "secondary", label: "Férias", className: "bg-muted text-muted-foreground" },
      afastado: { variant: "outline", label: "Afastado", className: "bg-warning/10 text-warning border-warning/20" }
    };
    const config = configs[status] || configs.ativo;
    return <Badge variant={config.variant} className={config.className}>{config.label}</Badge>;
  };

  const filteredProfissionais = mockProfissionais.filter(prof =>
    prof.nome.toLowerCase().includes(searchTerm.toLowerCase()) ||
    prof.especialidade.toLowerCase().includes(searchTerm.toLowerCase()) ||
    prof.numeroConselho.includes(searchTerm)
  );

  const profissionaisAtivos = mockProfissionais.filter(p => p.status === "ativo").length;
  const totalConsultasHoje = mockProfissionais.reduce((sum, p) => sum + p.consultasHoje, 0);

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold text-foreground">Profissionais</h1>
          <p className="text-muted-foreground">Gestão da equipe médica e de enfermagem</p>
        </div>
        <Button className="bg-gradient-primary text-white hover:opacity-90" onClick={handleNewProfessional}>
          <Plus className="w-4 h-4 mr-2" />
          Adicionar Profissional
        </Button>
      </div>

      <ProfessionalForm
        open={isFormOpen}
        onOpenChange={setIsFormOpen}
        professional={editingProfessional}
        onSave={handleSaveProfessional}
      />

      <div className="grid gap-6 md:grid-cols-4">
        <Card className="shadow-card">
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium text-muted-foreground">
              Total de Profissionais
            </CardTitle>
            <Stethoscope className="w-4 h-4 text-primary" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold text-foreground">{mockProfissionais.length}</div>
            <p className="text-xs text-muted-foreground">Médicos e enfermeiros</p>
          </CardContent>
        </Card>

        <Card className="shadow-card">
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium text-muted-foreground">
              Ativos Hoje
            </CardTitle>
            <Award className="w-4 h-4 text-success" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold text-foreground">{profissionaisAtivos}</div>
            <p className="text-xs text-muted-foreground">Em atendimento</p>
          </CardContent>
        </Card>

        <Card className="shadow-card">
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium text-muted-foreground">
              Consultas Hoje
            </CardTitle>
            <Calendar className="w-4 h-4 text-primary" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold text-foreground">{totalConsultasHoje}</div>
            <p className="text-xs text-muted-foreground">Agendadas</p>
          </CardContent>
        </Card>

        <Card className="shadow-card">
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium text-muted-foreground">
              Especialidades
            </CardTitle>
            <Stethoscope className="w-4 h-4 text-primary" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold text-foreground">8</div>
            <p className="text-xs text-muted-foreground">Diferentes áreas</p>
          </CardContent>
        </Card>
      </div>

      <div className="relative">
        <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-muted-foreground w-4 h-4" />
        <Input
          placeholder="Buscar por nome, especialidade ou conselho..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          className="pl-10"
        />
      </div>

      <div className="grid gap-4 md:grid-cols-2">
        {filteredProfissionais.map((profissional) => (
          <Card
            key={profissional.id}
            className="shadow-card hover:shadow-medical transition-all duration-300"
          >
            <CardContent className="p-6">
              <div className="flex items-start gap-4">
                <Avatar className="w-16 h-16">
                  <AvatarFallback className="bg-gradient-primary text-white text-lg">
                    {getInitials(profissional.nome)}
                  </AvatarFallback>
                </Avatar>
                
                <div className="flex-1 space-y-3">
                  <div>
                    <div className="flex items-center justify-between mb-1">
                      <h3 className="text-lg font-semibold text-foreground">
                        {profissional.nome}
                      </h3>
                      {getStatusBadge(profissional.status)}
                    </div>
                    <p className="text-sm text-muted-foreground">{profissional.especialidade}</p>
                  </div>

                  <div className="grid grid-cols-2 gap-3 text-sm">
                    <div>
                      <span className="text-muted-foreground">Conselho: </span>
                      <span className="text-foreground font-medium">
                        {profissional.conselho} {profissional.numeroConselho}
                      </span>
                    </div>
                    <div>
                      <span className="text-muted-foreground">Consultas Hoje: </span>
                      <span className="text-foreground font-medium">
                        {profissional.consultasHoje}
                      </span>
                    </div>
                  </div>

                  {profissional.proximaConsulta !== "-" && (
                    <div className="flex items-center gap-2 text-sm">
                      <Calendar className="w-4 h-4 text-primary" />
                      <span className="text-muted-foreground">Próxima consulta: </span>
                      <span className="text-foreground font-medium">
                        {profissional.proximaConsulta}
                      </span>
                    </div>
                  )}
                </div>

                <div className="flex flex-col gap-2">
                  <Button variant="outline" size="sm">Ver Agenda</Button>
                  <Button variant="outline" size="sm" onClick={() => handleEditProfessional(profissional)}>
                    Editar
                  </Button>
                </div>
              </div>
            </CardContent>
          </Card>
        ))}
      </div>
    </div>
  );
}
