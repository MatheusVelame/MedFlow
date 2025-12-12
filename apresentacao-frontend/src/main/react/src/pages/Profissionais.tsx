import { useState } from "react";
import { Stethoscope, Search, Plus, Calendar, Award, Loader2 } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Avatar, AvatarFallback } from "@/components/ui/avatar";
import { ProfessionalForm } from "@/components/ProfessionalForm";
import { 
  useListarFuncionarios, 
  useCadastrarFuncionario, 
  useAtualizarFuncionario,
  type FuncionarioResumo 
} from "@/api/useFuncionariosApi";
import { useAuth } from "@/contexts/AuthContext";

export default function Profissionais() {
  const { user } = useAuth();
  const [searchTerm, setSearchTerm] = useState("");
  const [isFormOpen, setIsFormOpen] = useState(false);
  const [editingProfessional, setEditingProfessional] = useState<FuncionarioResumo | undefined>();

  // Queries e Mutations
  const { data: funcionarios = [], isLoading, error } = useListarFuncionarios();
  const cadastrarMutation = useCadastrarFuncionario();
  const atualizarMutation = useAtualizarFuncionario();

  const handleSaveProfessional = (data: any) => {
    const responsavelId = parseInt(user?.id || "1");
    
    if (editingProfessional) {
      atualizarMutation.mutate({
        id: editingProfessional.id,
        payload: {
          novoNome: data.nome,
          novaFuncao: data.funcao,
          novoContato: data.contato,
          responsavelId: responsavelId
        }
      }, {
        onSuccess: () => {
          setEditingProfessional(undefined);
          setIsFormOpen(false);
        }
      });
    } else {
      cadastrarMutation.mutate({
        nome: data.nome,
        funcao: data.funcao,
        contato: data.contato,
        responsavelId: responsavelId
      }, {
        onSuccess: () => {
          setIsFormOpen(false);
        }
      });
    }
  };

  const handleEditProfessional = (professional: FuncionarioResumo) => {
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
    const statusMap: Record<string, string> = {
      "ATIVO": "Ativo",
      "INATIVO": "Inativo",
      "FERIAS": "Férias",
      "AFASTADO": "Afastado"
    };
    
    const configs: Record<string, { variant: "default" | "secondary" | "destructive" | "outline"; className?: string }> = {
      "ATIVO": { variant: "default", className: "bg-success/10 text-success border-success/20" },
      "FERIAS": { variant: "secondary", className: "bg-muted text-muted-foreground" },
      "AFASTADO": { variant: "outline", className: "bg-warning/10 text-warning border-warning/20" },
      "INATIVO": { variant: "destructive", className: "" }
    };
    
    const config = configs[status] || configs["ATIVO"];
    const label = statusMap[status] || status;
    return <Badge variant={config.variant} className={config.className}>{label}</Badge>;
  };

  const filteredProfissionais = funcionarios.filter(prof =>
    prof.nome.toLowerCase().includes(searchTerm.toLowerCase()) ||
    prof.funcao.toLowerCase().includes(searchTerm.toLowerCase()) ||
    prof.contato.includes(searchTerm)
  );

  const profissionaisAtivos = funcionarios.filter(p => p.status === "ATIVO").length;

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
            <div className="text-2xl font-bold text-foreground">
              {isLoading ? <Loader2 className="w-6 h-6 animate-spin" /> : funcionarios.length}
            </div>
            <p className="text-xs text-muted-foreground">Funcionários cadastrados</p>
          </CardContent>
        </Card>

        <Card className="shadow-card">
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium text-muted-foreground">
              Ativos
            </CardTitle>
            <Award className="w-4 h-4 text-success" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold text-foreground">
              {isLoading ? <Loader2 className="w-6 h-6 animate-spin" /> : profissionaisAtivos}
            </div>
            <p className="text-xs text-muted-foreground">Status ativo</p>
          </CardContent>
        </Card>

        <Card className="shadow-card">
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium text-muted-foreground">
              Funções
            </CardTitle>
            <Stethoscope className="w-4 h-4 text-primary" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold text-foreground">
              {isLoading ? <Loader2 className="w-6 h-6 animate-spin" /> : new Set(funcionarios.map(f => f.funcao)).size}
            </div>
            <p className="text-xs text-muted-foreground">Diferentes funções</p>
          </CardContent>
        </Card>

        <Card className="shadow-card">
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium text-muted-foreground">
              Inativos/Afastados
            </CardTitle>
            <Calendar className="w-4 h-4 text-warning" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold text-foreground">
              {isLoading ? <Loader2 className="w-6 h-6 animate-spin" /> : funcionarios.filter(f => f.status !== "ATIVO").length}
            </div>
            <p className="text-xs text-muted-foreground">Não disponíveis</p>
          </CardContent>
        </Card>
      </div>

      <div className="relative">
        <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-muted-foreground w-4 h-4" />
        <Input
          placeholder="Buscar por nome, função ou contato..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          className="pl-10"
        />
      </div>

      {isLoading ? (
        <div className="flex items-center justify-center py-12">
          <Loader2 className="w-8 h-8 animate-spin text-primary" />
        </div>
      ) : error ? (
        <div className="text-center py-12 text-destructive">
          Erro ao carregar funcionários. Tente novamente.
        </div>
      ) : filteredProfissionais.length === 0 ? (
        <div className="text-center py-12 text-muted-foreground">
          {searchTerm ? "Nenhum funcionário encontrado com os filtros aplicados." : "Nenhum funcionário cadastrado."}
        </div>
      ) : (
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
                      <p className="text-sm text-muted-foreground">{profissional.funcao}</p>
                    </div>

                    <div className="grid grid-cols-2 gap-3 text-sm">
                      <div>
                        <span className="text-muted-foreground">Contato: </span>
                        <span className="text-foreground font-medium">
                          {profissional.contato}
                        </span>
                      </div>
                      <div>
                        <span className="text-muted-foreground">ID: </span>
                        <span className="text-foreground font-medium">
                          {profissional.id}
                        </span>
                      </div>
                    </div>
                  </div>

                  <div className="flex flex-col gap-2">
                    <Button variant="outline" size="sm" onClick={() => handleEditProfessional(profissional)}>
                      Editar
                    </Button>
                  </div>
                </div>
              </CardContent>
            </Card>
          ))}
        </div>
      )}
    </div>
  );
}
