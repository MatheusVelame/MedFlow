import { useState, useMemo } from "react";
import { FileText, Search, Eye, Plus, Download, Clock, Loader2 } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { EvolucaoForm } from "@/components/EvolucaoForm";
import { useAuth } from "@/contexts/AuthContext";
import { 
  useListarProntuarios, 
  useObterProntuario, 
  useListarHistoricoClinico,
  useAdicionarHistoricoClinico,
  type ProntuarioResumo,
  type HistoricoItem
} from "@/api/useProntuariosApi";
import { useListarPacientes } from "@/api/usePacientesApi";

export default function Prontuarios() {
  const { isGestor } = useAuth();
  const [searchTerm, setSearchTerm] = useState("");
  const [selectedProntuario, setSelectedProntuario] = useState<string | null>(null);
  const [isEvolucaoFormOpen, setIsEvolucaoFormOpen] = useState(false);
  const [prontuarioIdParaEvolucao, setProntuarioIdParaEvolucao] = useState<string | null>(null);

  // Queries
  const { data: prontuarios = [], isLoading: isLoadingProntuarios, error: errorProntuarios } = useListarProntuarios();
  const { data: pacientes = [] } = useListarPacientes();
  const { data: prontuarioDetalhes, isLoading: isLoadingDetalhes } = useObterProntuario(selectedProntuario);
  const { data: historicoClinico = [], isLoading: isLoadingHistorico } = useListarHistoricoClinico(selectedProntuario);
  
  // Mutation
  const adicionarHistoricoMutation = useAdicionarHistoricoClinico();

  // Mapear pacientes para lookup rápido
  const pacientesMap = useMemo(() => {
    const map = new Map<number, { nome: string; cpf: string }>();
    pacientes.forEach(p => {
      map.set(p.id, { nome: p.name, cpf: p.cpf });
    });
    return map;
  }, [pacientes]);

  const handleSaveEvolucao = (data: any) => {
    if (!prontuarioIdParaEvolucao) return;
    
    adicionarHistoricoMutation.mutate({
      prontuarioId: prontuarioIdParaEvolucao,
      payload: {
        sintomas: data.queixa || data.sintomas,
        diagnostico: data.diagnostico,
        conduta: data.conduta,
        profissionalResponsavel: data.medico || data.profissionalResponsavel,
        anexosReferenciados: []
      }
    });
    setIsEvolucaoFormOpen(false);
    setProntuarioIdParaEvolucao(null);
  };

  const handleOpenEvolucaoForm = () => {
    if (selectedProntuario) {
      setProntuarioIdParaEvolucao(selectedProntuario);
    }
    setIsEvolucaoFormOpen(true);
  };

  // Enriquecer prontuários com dados do paciente
  const prontuariosEnriquecidos = useMemo(() => {
    return prontuarios.map(prontuario => {
      const pacienteIdNum = parseInt(prontuario.pacienteId);
      const paciente = pacientesMap.get(pacienteIdNum);
      return {
        ...prontuario,
        pacienteNome: paciente?.nome || "Paciente não encontrado",
        pacienteCpf: paciente?.cpf || "",
      };
    });
  }, [prontuarios, pacientesMap]);

  const filteredProntuarios = prontuariosEnriquecidos.filter(p =>
    p.pacienteNome.toLowerCase().includes(searchTerm.toLowerCase()) ||
    p.pacienteCpf.includes(searchTerm) ||
    p.pacienteId.includes(searchTerm)
  );

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold text-foreground">Prontuários</h1>
          <p className="text-muted-foreground">Histórico médico completo dos pacientes</p>
        </div>
        {!isGestor && (
          <Button 
            className="bg-gradient-primary text-white hover:opacity-90" 
            onClick={handleOpenEvolucaoForm}
            disabled={!selectedProntuario}
          >
            <Plus className="w-4 h-4 mr-2" />
            Nova Evolução
          </Button>
        )}
      </div>

      <EvolucaoForm
        open={isEvolucaoFormOpen}
        onOpenChange={(open) => {
          setIsEvolucaoFormOpen(open);
          if (!open) setProntuarioIdParaEvolucao(null);
        }}
        onSave={handleSaveEvolucao}
        prontuarioId={prontuarioIdParaEvolucao}
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
            <div className="text-2xl font-bold text-foreground">
              {isLoadingProntuarios ? <Loader2 className="w-6 h-6 animate-spin" /> : prontuarios.length}
            </div>
            <p className="text-xs text-muted-foreground">
              {isLoadingProntuarios ? "Carregando..." : "Total de prontuários"}
            </p>
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
            <div className="text-2xl font-bold text-foreground">
              {isLoadingHistorico ? <Loader2 className="w-6 h-6 animate-spin" /> : historicoClinico.length}
            </div>
            <p className="text-xs text-muted-foreground">
              {selectedProntuario ? "Histórico do prontuário selecionado" : "Selecione um prontuário"}
            </p>
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
            <div className="text-2xl font-bold text-foreground">
              {isLoadingProntuarios ? <Loader2 className="w-6 h-6 animate-spin" /> : prontuarios.filter(p => p.status === "ATIVO").length}
            </div>
            <p className="text-xs text-muted-foreground">
              {isLoadingProntuarios ? "Carregando..." : `${Math.round((prontuarios.filter(p => p.status === "ATIVO").length / Math.max(prontuarios.length, 1)) * 100)}% do total`}
            </p>
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

          {isLoadingProntuarios ? (
            <div className="flex items-center justify-center py-12">
              <Loader2 className="w-8 h-8 animate-spin text-primary" />
            </div>
          ) : errorProntuarios ? (
            <div className="text-center py-12 text-destructive">
              Erro ao carregar prontuários. Tente novamente.
            </div>
          ) : filteredProntuarios.length === 0 ? (
            <div className="text-center py-12 text-muted-foreground">
              {searchTerm ? "Nenhum prontuário encontrado com os filtros aplicados." : "Nenhum prontuário cadastrado."}
            </div>
          ) : (
            <div className="grid gap-4">
              {filteredProntuarios.map((prontuario) => (
                <Card
                  key={prontuario.id}
                  className={`shadow-card hover:shadow-medical transition-all duration-300 cursor-pointer ${
                    selectedProntuario === prontuario.id ? "ring-2 ring-primary" : ""
                  }`}
                  onClick={() => setSelectedProntuario(prontuario.id)}
                >
                  <CardContent className="p-6">
                    <div className="flex items-start justify-between">
                      <div className="flex-1 space-y-2">
                        <div className="flex items-center gap-3">
                          <h3 className="text-lg font-semibold text-foreground">
                            {prontuario.pacienteNome}
                          </h3>
                          <Badge variant={prontuario.status === "ATIVO" ? "default" : "secondary"}>
                            {prontuario.status}
                          </Badge>
                        </div>
                        
                        <div className="grid grid-cols-2 gap-4 text-sm">
                          <div>
                            <span className="text-muted-foreground">CPF: </span>
                            <span className="text-foreground font-medium">{prontuario.pacienteCpf || "N/A"}</span>
                          </div>
                          <div>
                            <span className="text-muted-foreground">Data Criação: </span>
                            <span className="text-foreground font-medium">
                              {new Date(prontuario.dataHoraCriacao).toLocaleDateString('pt-BR')}
                            </span>
                          </div>
                          <div>
                            <span className="text-muted-foreground">Responsável: </span>
                            <span className="text-foreground font-medium">{prontuario.profissionalResponsavel}</span>
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
          )}
        </TabsContent>

        <TabsContent value="detalhes" className="space-y-4">
          {isLoadingDetalhes || isLoadingHistorico ? (
            <Card className="shadow-card">
              <CardContent className="p-12 text-center">
                <Loader2 className="w-8 h-8 mx-auto animate-spin text-primary mb-4" />
                <p className="text-muted-foreground">Carregando detalhes...</p>
              </CardContent>
            </Card>
          ) : selectedProntuario && prontuarioDetalhes ? (
            <Card className="shadow-card">
              <CardHeader>
                <CardTitle>Evolução Clínica</CardTitle>
                <p className="text-sm text-muted-foreground">
                  Prontuário: {prontuarioDetalhes.id} | Paciente: {(() => {
                    const pacienteIdNum = parseInt(prontuarioDetalhes.pacienteId);
                    const paciente = pacientesMap.get(pacienteIdNum);
                    return paciente?.nome || "Paciente não encontrado";
                  })()}
                </p>
              </CardHeader>
              <CardContent className="space-y-4">
                {historicoClinico.length === 0 ? (
                  <div className="text-center py-8 text-muted-foreground">
                    Nenhum histórico clínico registrado para este prontuário.
                  </div>
                ) : (
                  historicoClinico.map((evolucao) => (
                    <div
                      key={evolucao.id}
                      className="p-4 border border-border rounded-lg space-y-3"
                    >
                      <div className="flex items-center justify-between">
                        <div>
                          <p className="font-semibold text-foreground">{evolucao.profissionalResponsavel}</p>
                        </div>
                        <Badge variant="outline">
                          {new Date(evolucao.dataHoraRegistro).toLocaleDateString('pt-BR')}
                        </Badge>
                      </div>
                      
                      <div className="space-y-2 text-sm">
                        <div>
                          <span className="font-medium text-foreground">Sintomas: </span>
                          <span className="text-muted-foreground">{evolucao.sintomas}</span>
                        </div>
                        <div>
                          <span className="font-medium text-foreground">Diagnóstico: </span>
                          <span className="text-muted-foreground">{evolucao.diagnostico}</span>
                        </div>
                        <div>
                          <span className="font-medium text-foreground">Conduta: </span>
                          <span className="text-muted-foreground">{evolucao.conduta}</span>
                        </div>
                        {evolucao.anexosReferenciados && evolucao.anexosReferenciados.length > 0 && (
                          <div>
                            <span className="font-medium text-foreground">Anexos: </span>
                            <span className="text-muted-foreground">{evolucao.anexosReferenciados.join(", ")}</span>
                          </div>
                        )}
                      </div>
                    </div>
                  ))
                )}
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
