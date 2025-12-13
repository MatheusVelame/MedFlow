import { useState, useMemo, useRef } from "react";
import { FileText, Search, Eye, Plus, Loader2 } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import { ProntuarioForm } from "@/components/ProntuarioForm";
import { useAuth } from "@/contexts/AuthContext";
import {
  useListarProntuarios,
  useObterProntuario,
  useCriarProntuario
} from "@/api/useProntuariosApi";
import { useListarPacientes } from "@/api/usePacientesApi";

export default function Prontuarios() {
  const { isGestor, isMedico } = useAuth();
  const [searchTerm, setSearchTerm] = useState("");
  const [selectedProntuario, setSelectedProntuario] = useState<string | null>(null);
  const [isProntuarioFormOpen, setIsProntuarioFormOpen] = useState(false);

  // Estado para o Dialog de Visualização
  const [isVisualizarOpen, setIsVisualizarOpen] = useState(false);

  const listaRef = useRef<HTMLDivElement>(null);

  // Queries
  const { data: prontuarios = [], isLoading: isLoadingProntuarios, error: errorProntuarios } = useListarProntuarios();
  const { data: pacientes = [] } = useListarPacientes();

  // Query para detalhes (usada no Dialog de Visualização)
  const { data: prontuarioDetalhes, isLoading: isLoadingDetalhes } = useObterProntuario(selectedProntuario);

  // Mutations
  const criarProntuarioMutation = useCriarProntuario();

  // Mapear pacientes para lookup rápido
  const pacientesMap = useMemo(() => {
    const map = new Map<number, { nome: string; cpf: string }>();
    pacientes.forEach(p => {
      map.set(p.id, { nome: p.name, cpf: p.cpf });
    });
    return map;
  }, [pacientes]);

  const handleSaveProntuario = (data: any) => {
    criarProntuarioMutation.mutate({
      pacienteId: data.pacienteId,
      atendimentoId: null,
      profissionalResponsavel: data.profissionalResponsavel,
      observacoesIniciais: data.observacoesIniciais || null,
    });
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

  // Lógica de filtro da Busca
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
          <p className="text-muted-foreground">Gestão de histórico médico</p>
        </div>
        <div className="flex gap-2">
          <Button
            variant="default"
            onClick={() => setIsProntuarioFormOpen(true)}
            className="bg-primary text-white hover:bg-primary/90"
          >
            <Plus className="w-4 h-4 mr-2" />
            Novo Prontuário
          </Button>
        </div>
      </div>

      <ProntuarioForm
        open={isProntuarioFormOpen}
        onOpenChange={setIsProntuarioFormOpen}
        onSave={handleSaveProntuario}
      />

      {/* Cards de Estatísticas Rápidas */}
      <div className="grid gap-6 md:grid-cols-2">
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
          </CardContent>
        </Card>
      </div>

      {/* Área de Busca e Lista */}
      <div className="space-y-4">
        <div className="relative">
          <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-muted-foreground w-4 h-4" />
          <Input
            placeholder="Buscar por nome do paciente ou CPF..."
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
            {searchTerm ? "Nenhum prontuário encontrado." : "Nenhum prontuário cadastrado."}
          </div>
        ) : (
          <div ref={listaRef} className="grid gap-4">
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

                    <div className="flex gap-2" onClick={(e) => e.stopPropagation()}>
                      {/* Botão de Visualizar */}
                      <Button
                        variant="outline"
                        size="sm"
                        onClick={() => {
                          setSelectedProntuario(prontuario.id);
                          setIsVisualizarOpen(true);
                        }}
                        title="Visualizar Detalhes"
                      >
                        <Eye className="w-4 h-4" />
                      </Button>
                    </div>
                  </div>
                </CardContent>
              </Card>
            ))}
          </div>
        )}
      </div>

      {/* Dialog de Visualização de Detalhes */}
      <Dialog open={isVisualizarOpen} onOpenChange={setIsVisualizarOpen}>
        <DialogContent className="max-w-3xl">
          <DialogHeader>
            <DialogTitle>Detalhes do Prontuário</DialogTitle>
            <DialogDescription>Informações completas do registro médico</DialogDescription>
          </DialogHeader>

          {isLoadingDetalhes ? (
            <div className="flex justify-center py-8">
              <Loader2 className="w-8 h-8 animate-spin text-primary" />
            </div>
          ) : prontuarioDetalhes ? (
            <div className="space-y-6">
              <div className="grid grid-cols-2 gap-4 p-4 bg-muted/20 rounded-lg">
                <div>
                  <p className="text-sm font-medium text-muted-foreground">Paciente</p>
                  <p className="text-lg font-semibold">
                    {pacientesMap.get(parseInt(prontuarioDetalhes.pacienteId))?.nome || "Nome não encontrado"}
                  </p>
                </div>
                <div>
                  <p className="text-sm font-medium text-muted-foreground">CPF</p>
                  <p className="text-base">
                    {pacientesMap.get(parseInt(prontuarioDetalhes.pacienteId))?.cpf || "CPF não encontrado"}
                  </p>
                </div>
                <div>
                  <p className="text-sm font-medium text-muted-foreground">Status</p>
                  <Badge variant={prontuarioDetalhes.status === "ATIVO" ? "default" : "secondary"}>
                    {prontuarioDetalhes.status}
                  </Badge>
                </div>
                <div>
                  <p className="text-sm font-medium text-muted-foreground">Data de Criação</p>
                  <p className="text-base">
                     {new Date(prontuarioDetalhes.dataHoraCriacao).toLocaleDateString('pt-BR')}
                  </p>
                </div>
              </div>

              <div>
                <h4 className="font-semibold mb-2">Observações Iniciais</h4>
                <div className="p-4 border rounded-md min-h-[100px] bg-background">
                  {prontuarioDetalhes.observacoesIniciais || "Nenhuma observação registrada."}
                </div>
              </div>

              <div>
                 <p className="text-sm text-muted-foreground">
                   Profissional Responsável: <span className="text-foreground">{prontuarioDetalhes.profissionalResponsavel}</span>
                 </p>
              </div>
            </div>
          ) : (
             <div className="text-center py-4 text-muted-foreground">
                Não foi possível carregar os detalhes.
             </div>
          )}
        </DialogContent>
      </Dialog>
    </div>
  );
}