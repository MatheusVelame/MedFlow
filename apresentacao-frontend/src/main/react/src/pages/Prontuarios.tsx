import { useState } from "react";
import { FileText, Search, Eye, Plus, Download, Clock } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { EvolucaoForm } from "@/components/EvolucaoForm";
import { useAuth } from "@/contexts/AuthContext";

const mockProntuarios = [
  {
    id: "1",
    paciente: "Maria Silva Santos",
    cpf: "123.456.789-00",
    ultimaConsulta: "2024-01-10",
    totalConsultas: 8,
    status: "active",
    especialidade: "Cardiologia"
  },
  {
    id: "2",
    paciente: "João Pedro Oliveira",
    cpf: "987.654.321-00",
    ultimaConsulta: "2024-01-08",
    totalConsultas: 3,
    status: "active",
    especialidade: "Ortopedia"
  },
  {
    id: "3",
    paciente: "Ana Costa Ferreira",
    cpf: "456.789.123-00",
    ultimaConsulta: "2023-12-20",
    totalConsultas: 15,
    status: "inactive",
    especialidade: "Pediatria"
  }
];

const mockEvolucoes = [
  {
    id: "1",
    data: "2024-01-10",
    medico: "Dr. Carlos Mendes",
    especialidade: "Cardiologia",
    queixa: "Dor no peito",
    diagnostico: "Angina estável",
    conduta: "Medicação e acompanhamento"
  },
  {
    id: "2",
    data: "2023-12-15",
    medico: "Dr. Carlos Mendes",
    especialidade: "Cardiologia",
    queixa: "Fadiga e palpitações",
    diagnostico: "Arritmia cardíaca",
    conduta: "Holter 24h e retorno em 15 dias"
  }
];

export default function Prontuarios() {
  const { isGestor } = useAuth();
  const [searchTerm, setSearchTerm] = useState("");
  const [selectedProntuario, setSelectedProntuario] = useState<string | null>(null);
  const [isEvolucaoFormOpen, setIsEvolucaoFormOpen] = useState(false);

  const handleSaveEvolucao = (data: any) => {
    console.log("Nova evolução:", data);
  };

  const filteredProntuarios = mockProntuarios.filter(p =>
    p.paciente.toLowerCase().includes(searchTerm.toLowerCase()) ||
    p.cpf.includes(searchTerm)
  );

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold text-foreground">Prontuários</h1>
          <p className="text-muted-foreground">Histórico médico completo dos pacientes</p>
        </div>
        {!isGestor && (
          <Button className="bg-gradient-primary text-white hover:opacity-90" onClick={() => setIsEvolucaoFormOpen(true)}>
            <Plus className="w-4 h-4 mr-2" />
            Nova Evolução
          </Button>
        )}
      </div>

      <EvolucaoForm
        open={isEvolucaoFormOpen}
        onOpenChange={setIsEvolucaoFormOpen}
        onSave={handleSaveEvolucao}
      />

      <div className="grid gap-6 md:grid-cols-3">
        <Card className="shadow-card">
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium text-muted-foreground">
              Total de Prontuários
            </CardTitle>
            <FileText className="w-4 h-4 text-primary" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold text-foreground">342</div>
            <p className="text-xs text-muted-foreground">+12 este mês</p>
          </CardContent>
        </Card>

        <Card className="shadow-card">
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium text-muted-foreground">
              Evoluções Hoje
            </CardTitle>
            <Clock className="w-4 h-4 text-primary" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold text-foreground">28</div>
            <p className="text-xs text-muted-foreground">Em 6 especialidades</p>
          </CardContent>
        </Card>

        <Card className="shadow-card">
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium text-muted-foreground">
              Prontuários Ativos
            </CardTitle>
            <FileText className="w-4 h-4 text-success" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold text-foreground">298</div>
            <p className="text-xs text-muted-foreground">87% do total</p>
          </CardContent>
        </Card>
      </div>

      <Tabs defaultValue="lista" className="space-y-4">
        <TabsList>
          <TabsTrigger value="lista">Lista de Prontuários</TabsTrigger>
          <TabsTrigger value="detalhes">Detalhes</TabsTrigger>
        </TabsList>

        <TabsContent value="lista" className="space-y-4">
          <div className="relative">
            <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-muted-foreground w-4 h-4" />
            <Input
              placeholder="Buscar por nome ou CPF..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="pl-10"
            />
          </div>

          <div className="grid gap-4">
            {filteredProntuarios.map((prontuario) => (
              <Card
                key={prontuario.id}
                className="shadow-card hover:shadow-medical transition-all duration-300 cursor-pointer"
                onClick={() => setSelectedProntuario(prontuario.id)}
              >
                <CardContent className="p-6">
                  <div className="flex items-start justify-between">
                    <div className="flex-1 space-y-2">
                      <div className="flex items-center gap-3">
                        <h3 className="text-lg font-semibold text-foreground">
                          {prontuario.paciente}
                        </h3>
                        <Badge variant={prontuario.status === "active" ? "default" : "secondary"}>
                          {prontuario.status === "active" ? "Ativo" : "Inativo"}
                        </Badge>
                      </div>
                      
                      <div className="grid grid-cols-2 gap-4 text-sm">
                        <div>
                          <span className="text-muted-foreground">CPF: </span>
                          <span className="text-foreground font-medium">{prontuario.cpf}</span>
                        </div>
                        <div>
                          <span className="text-muted-foreground">Especialidade: </span>
                          <span className="text-foreground font-medium">{prontuario.especialidade}</span>
                        </div>
                        <div>
                          <span className="text-muted-foreground">Última Consulta: </span>
                          <span className="text-foreground font-medium">
                            {new Date(prontuario.ultimaConsulta).toLocaleDateString('pt-BR')}
                          </span>
                        </div>
                        <div>
                          <span className="text-muted-foreground">Total de Consultas: </span>
                          <span className="text-foreground font-medium">{prontuario.totalConsultas}</span>
                        </div>
                      </div>
                    </div>

                    <div className="flex gap-2">
                      <Button variant="outline" size="sm">
                        <Eye className="w-4 h-4" />
                      </Button>
                      <Button variant="outline" size="sm">
                        <Download className="w-4 h-4" />
                      </Button>
                    </div>
                  </div>
                </CardContent>
              </Card>
            ))}
          </div>
        </TabsContent>

        <TabsContent value="detalhes" className="space-y-4">
          {selectedProntuario ? (
            <Card className="shadow-card">
              <CardHeader>
                <CardTitle>Evolução Clínica</CardTitle>
              </CardHeader>
              <CardContent className="space-y-4">
                {mockEvolucoes.map((evolucao) => (
                  <div
                    key={evolucao.id}
                    className="p-4 border border-border rounded-lg space-y-3"
                  >
                    <div className="flex items-center justify-between">
                      <div>
                        <p className="font-semibold text-foreground">{evolucao.medico}</p>
                        <p className="text-sm text-muted-foreground">{evolucao.especialidade}</p>
                      </div>
                      <Badge variant="outline">
                        {new Date(evolucao.data).toLocaleDateString('pt-BR')}
                      </Badge>
                    </div>
                    
                    <div className="space-y-2 text-sm">
                      <div>
                        <span className="font-medium text-foreground">Queixa: </span>
                        <span className="text-muted-foreground">{evolucao.queixa}</span>
                      </div>
                      <div>
                        <span className="font-medium text-foreground">Diagnóstico: </span>
                        <span className="text-muted-foreground">{evolucao.diagnostico}</span>
                      </div>
                      <div>
                        <span className="font-medium text-foreground">Conduta: </span>
                        <span className="text-muted-foreground">{evolucao.conduta}</span>
                      </div>
                    </div>
                  </div>
                ))}
              </CardContent>
            </Card>
          ) : (
            <Card className="shadow-card">
              <CardContent className="p-12 text-center">
                <FileText className="w-12 h-12 mx-auto text-muted-foreground mb-4" />
                <p className="text-muted-foreground">
                  Selecione um prontuário para ver os detalhes
                </p>
              </CardContent>
            </Card>
          )}
        </TabsContent>
      </Tabs>
    </div>
  );
}
