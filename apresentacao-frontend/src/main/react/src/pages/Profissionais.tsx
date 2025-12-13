import { useState } from "react";
import { 
  Stethoscope, 
  Search, 
  Plus, 
  Calendar, 
  Award, 
  Loader2, 
  Edit, 
  Trash2, 
  Eye,
  Filter,
  X,
  MoreVertical
} from "lucide-react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Avatar, AvatarFallback } from "@/components/ui/avatar";
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
} from "@/components/ui/alert-dialog";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { FuncionarioForm } from "@/components/FuncionarioForm";
import { FuncionarioDetalhesDialog } from "@/components/FuncionarioDetalhesDialog";
import {
  useListarFuncionarios,
  useCadastrarFuncionario,
  useAtualizarFuncionario,
  useAtualizarFuncionarioCompleto,
  useMudarStatusFuncionario,
  useExcluirFuncionario,
  type FuncionarioResumo,
  type StatusFuncionario,
} from "@/api/useFuncionariosApi";
import { useAuth } from "@/contexts/AuthContext";

export default function Profissionais() {
  const { user, isGestor } = useAuth();
  const [searchTerm, setSearchTerm] = useState("");
  const [statusFilter, setStatusFilter] = useState<StatusFuncionario | "TODOS">("TODOS");
  const [funcaoFilter, setFuncaoFilter] = useState<string>("TODAS");
  const [isFormOpen, setIsFormOpen] = useState(false);
  const [editingFuncionario, setEditingFuncionario] = useState<FuncionarioResumo | null>(null);
  const [funcionarioToDelete, setFuncionarioToDelete] = useState<FuncionarioResumo | null>(null);
  const [funcionarioToView, setFuncionarioToView] = useState<number | null>(null);
  const [funcionarioToChangeStatus, setFuncionarioToChangeStatus] = useState<{
    funcionario: FuncionarioResumo;
    novoStatus: StatusFuncionario | null;
  } | null>(null);

  // Queries e Mutations
  const { data: funcionarios = [], isLoading, error, refetch } = useListarFuncionarios();
  const cadastrarMutation = useCadastrarFuncionario();
  const atualizarMutation = useAtualizarFuncionario();
  const atualizarCompletoMutation = useAtualizarFuncionarioCompleto();
  const mudarStatusMutation = useMudarStatusFuncionario();
  const excluirMutation = useExcluirFuncionario();

  const handleSave = (data: any) => {
    const responsavelId = parseInt(user?.id || "1");

    if (editingFuncionario) {
      // Se tem status, usa atualização completa
      if (data.status) {
        atualizarCompletoMutation.mutate(
          {
            id: editingFuncionario.id,
            payload: {
              nome: data.nome,
              funcao: data.funcao,
              contato: data.contato,
              status: data.status,
              responsavelId: responsavelId,
            },
          },
          {
            onSuccess: () => {
              setEditingFuncionario(null);
              setIsFormOpen(false);
              refetch();
            },
          }
        );
      } else {
        atualizarMutation.mutate(
          {
            id: editingFuncionario.id,
            payload: {
              novoNome: data.nome,
              novaFuncao: data.funcao,
              novoContato: data.contato,
              responsavelId: responsavelId,
            },
          },
          {
            onSuccess: () => {
              setEditingFuncionario(null);
              setIsFormOpen(false);
              refetch();
            },
          }
        );
      }
    } else {
      cadastrarMutation.mutate(
        {
          nome: data.nome,
          funcao: data.funcao,
          contato: data.contato,
          responsavelId: responsavelId,
        },
        {
          onSuccess: () => {
            setIsFormOpen(false);
            // Força atualização imediata da lista
            refetch();
          },
        }
      );
    }
  };

  const handleEdit = (funcionario: FuncionarioResumo) => {
    setEditingFuncionario(funcionario);
    setIsFormOpen(true);
  };

  const handleNew = () => {
    setEditingFuncionario(null);
    setIsFormOpen(true);
  };

  const handleDelete = () => {
    if (funcionarioToDelete) {
      const responsavelId = parseInt(user?.id || "1");
      excluirMutation.mutate(
        {
          id: funcionarioToDelete.id,
          payload: { responsavelId: responsavelId },
        },
        {
          onSuccess: () => {
            setFuncionarioToDelete(null);
            refetch();
          },
        }
      );
    }
  };

  const handleMudarStatus = () => {
    if (funcionarioToChangeStatus?.funcionario && funcionarioToChangeStatus.novoStatus) {
      const responsavelId = parseInt(user?.id || "1");
      mudarStatusMutation.mutate(
        {
          id: funcionarioToChangeStatus.funcionario.id,
          novoStatus: funcionarioToChangeStatus.novoStatus,
          payload: { responsavelId: responsavelId },
        },
        {
          onSuccess: () => {
            setFuncionarioToChangeStatus(null);
            refetch();
          },
        }
      );
    }
  };

  const getInitials = (nome: string) => {
    return nome
      .split(" ")
      .map((n) => n[0])
      .join("")
      .substring(0, 2)
      .toUpperCase();
  };

  const getStatusBadge = (status: string) => {
    const statusMap: Record<string, string> = {
      ATIVO: "Ativo",
      INATIVO: "Inativo",
      FERIAS: "Férias",
      AFASTADO: "Afastado",
    };

    const configs: Record<
      string,
      {
        variant: "default" | "secondary" | "destructive" | "outline";
        className?: string;
      }
    > = {
      ATIVO: {
        variant: "default",
        className: "bg-success/10 text-success border-success/20",
      },
      FERIAS: {
        variant: "secondary",
        className: "bg-muted text-muted-foreground",
      },
      AFASTADO: {
        variant: "outline",
        className: "bg-warning/10 text-warning border-warning/20",
      },
      INATIVO: { variant: "destructive", className: "" },
    };

    const config = configs[status] || configs["ATIVO"];
    const label = statusMap[status] || status;
    return (
      <Badge variant={config.variant} className={config.className}>
        {label}
      </Badge>
    );
  };

  // Filtros
  const funcoesUnicas = Array.from(new Set(funcionarios.map((f) => f.funcao)));

  const filteredFuncionarios = funcionarios.filter((func) => {
    const matchesSearch =
      func.nome.toLowerCase().includes(searchTerm.toLowerCase()) ||
      func.funcao.toLowerCase().includes(searchTerm.toLowerCase()) ||
      func.contato.includes(searchTerm);

    const matchesStatus = statusFilter === "TODOS" || func.status === statusFilter;
    const matchesFuncao = funcaoFilter === "TODAS" || func.funcao === funcaoFilter;

    return matchesSearch && matchesStatus && matchesFuncao;
  });

  const profissionaisAtivos = funcionarios.filter((p) => p.status === "ATIVO").length;

  // Debug: log dos funcionários
  console.log("📋 Funcionários na página:", funcionarios);
  console.log("📋 Quantidade de funcionários:", funcionarios.length);
  console.log("⏳ isLoading:", isLoading);
  console.log("❌ error:", error);
  console.log("🔍 filteredFuncionarios:", filteredFuncionarios);
  console.log("🔍 Quantidade filtrada:", filteredFuncionarios.length);
  console.log("🔍 statusFilter:", statusFilter);
  console.log("🔍 funcaoFilter:", funcaoFilter);
  console.log("🔍 searchTerm:", searchTerm);

  return (
    <div className="space-y-6 animate-fade-in">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold tracking-tight text-foreground">
            Funcionários
          </h1>
          <p className="text-muted-foreground">
            Gerencie a equipe de funcionários da clínica
          </p>
        </div>
        {isGestor && (
          <Button onClick={handleNew} className="gap-2">
            <Plus className="h-4 w-4" />
            Novo Funcionário
          </Button>
        )}
      </div>

      {/* Cards de Estatísticas */}
      <div className="grid gap-6 md:grid-cols-4">
        <Card className="shadow-card">
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium text-muted-foreground">
              Total de Funcionários
            </CardTitle>
            <Stethoscope className="w-4 h-4 text-primary" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold text-foreground">
              {isLoading ? (
                <Loader2 className="w-6 h-6 animate-spin" />
              ) : (
                funcionarios.length
              )}
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
              {isLoading ? (
                <Loader2 className="w-6 h-6 animate-spin" />
              ) : (
                profissionaisAtivos
              )}
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
              {isLoading ? (
                <Loader2 className="w-6 h-6 animate-spin" />
              ) : (
                funcoesUnicas.length
              )}
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
              {isLoading ? (
                <Loader2 className="w-6 h-6 animate-spin" />
              ) : (
                funcionarios.filter((f) => f.status !== "ATIVO").length
              )}
            </div>
            <p className="text-xs text-muted-foreground">Não disponíveis</p>
          </CardContent>
        </Card>
      </div>

      {/* Filtros */}
      <div className="flex flex-col sm:flex-row gap-4">
        <div className="relative flex-1">
          <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-muted-foreground w-4 h-4" />
          <Input
            placeholder="Buscar por nome, função ou contato..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="pl-10"
          />
        </div>
        <div className="flex gap-2">
          <Select value={statusFilter} onValueChange={(value) => setStatusFilter(value as StatusFuncionario | "TODOS")}>
            <SelectTrigger className="w-[180px]">
              <Filter className="h-4 w-4 mr-2" />
              <SelectValue placeholder="Status" />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="TODOS">Todos os Status</SelectItem>
              <SelectItem value="ATIVO">Ativo</SelectItem>
              <SelectItem value="INATIVO">Inativo</SelectItem>
              <SelectItem value="FERIAS">Férias</SelectItem>
              <SelectItem value="AFASTADO">Afastado</SelectItem>
            </SelectContent>
          </Select>
          <Select value={funcaoFilter} onValueChange={setFuncaoFilter}>
            <SelectTrigger className="w-[180px]">
              <SelectValue placeholder="Função" />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="TODAS">Todas as Funções</SelectItem>
              {funcoesUnicas.map((funcao) => (
                <SelectItem key={funcao} value={funcao}>
                  {funcao}
                </SelectItem>
              ))}
            </SelectContent>
          </Select>
          {(statusFilter !== "TODOS" || funcaoFilter !== "TODAS") && (
            <Button
              variant="outline"
              size="icon"
              onClick={() => {
                setStatusFilter("TODOS");
                setFuncaoFilter("TODAS");
              }}
            >
              <X className="h-4 w-4" />
            </Button>
          )}
        </div>
      </div>

      {/* Lista de Funcionários */}
      {isLoading ? (
        <div className="flex items-center justify-center py-12">
          <Loader2 className="w-8 h-8 animate-spin text-primary" />
        </div>
      ) : error ? (
        <div className="text-center py-12 text-destructive">
          Erro ao carregar funcionários. Tente novamente.
        </div>
      ) : filteredFuncionarios.length === 0 ? (
        <div className="text-center py-12 text-muted-foreground">
          {searchTerm || statusFilter !== "TODOS" || funcaoFilter !== "TODAS" ? (
            <div>
              <p>Nenhum funcionário encontrado com os filtros aplicados.</p>
              <p className="text-xs mt-2">
                Total no banco: {funcionarios.length} | 
                Filtro Status: {statusFilter} | 
                Filtro Função: {funcaoFilter} | 
                Busca: {searchTerm || "(vazio)"}
              </p>
            </div>
          ) : (
            <div>
              <p>Nenhum funcionário cadastrado.</p>
              <p className="text-xs mt-2">
                Total retornado da API: {funcionarios.length}
              </p>
            </div>
          )}
        </div>
      ) : (
        <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
          {filteredFuncionarios.map((funcionario) => (
            <Card
              key={funcionario.id}
              className="hover:shadow-lg transition-shadow"
            >
              <CardContent className="p-6">
                <div className="flex items-start gap-4">
                  <Avatar className="w-16 h-16">
                    <AvatarFallback className="bg-gradient-primary text-white text-lg">
                      {getInitials(funcionario.nome)}
                    </AvatarFallback>
                  </Avatar>

                  <div className="flex-1 space-y-3">
                    <div>
                      <div className="flex items-center justify-between mb-1">
                        <h3 className="text-lg font-semibold text-foreground">
                          {funcionario.nome}
                        </h3>
                        {getStatusBadge(funcionario.status)}
                      </div>
                      <p className="text-sm text-muted-foreground">
                        {funcionario.funcao}
                      </p>
                    </div>

                    <div className="space-y-1 text-sm">
                      <div>
                        <span className="text-muted-foreground">Contato: </span>
                        <span className="text-foreground font-medium">
                          {funcionario.contato}
                        </span>
                      </div>
                      <div>
                        <span className="text-muted-foreground">ID: </span>
                        <span className="text-foreground font-medium">
                          {funcionario.id}
                        </span>
                      </div>
                    </div>
                  </div>
                </div>

                {isGestor && (
                  <div className="flex gap-2 mt-4">
                    <Button
                      variant="outline"
                      size="sm"
                      className="flex-1"
                      onClick={() => setFuncionarioToView(funcionario.id)}
                    >
                      <Eye className="h-4 w-4 mr-1" />
                      Ver
                    </Button>
                    <Button
                      variant="outline"
                      size="sm"
                      className="flex-1"
                      onClick={() => handleEdit(funcionario)}
                    >
                      <Edit className="h-4 w-4 mr-1" />
                      Editar
                    </Button>
                    <DropdownMenu>
                      <DropdownMenuTrigger asChild>
                        <Button variant="outline" size="sm">
                          <MoreVertical className="h-4 w-4" />
                        </Button>
                      </DropdownMenuTrigger>
                      <DropdownMenuContent align="end">
                        <DropdownMenuItem
                          onClick={() =>
                            setFuncionarioToChangeStatus({
                              funcionario,
                              novoStatus: null,
                            })
                          }
                        >
                          Mudar Status
                        </DropdownMenuItem>
                        <DropdownMenuItem
                          className="text-destructive"
                          onClick={() => setFuncionarioToDelete(funcionario)}
                        >
                          <Trash2 className="h-4 w-4 mr-2" />
                          Excluir
                        </DropdownMenuItem>
                      </DropdownMenuContent>
                    </DropdownMenu>
                  </div>
                )}
              </CardContent>
            </Card>
          ))}
        </div>
      )}

      {/* Formulário */}
      <FuncionarioForm
        open={isFormOpen}
        onOpenChange={setIsFormOpen}
        initialData={editingFuncionario}
        onSave={handleSave}
      />

      {/* Dialog de Detalhes */}
      <FuncionarioDetalhesDialog
        open={funcionarioToView !== null}
        onOpenChange={(open) => !open && setFuncionarioToView(null)}
        funcionarioId={funcionarioToView}
      />

      {/* Dialog de Exclusão */}
      <AlertDialog
        open={!!funcionarioToDelete}
        onOpenChange={() => setFuncionarioToDelete(null)}
      >
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle>Confirmar exclusão</AlertDialogTitle>
            <AlertDialogDescription>
              Tem certeza que deseja excluir o funcionário{" "}
              <strong>{funcionarioToDelete?.nome}</strong>? Esta ação não pode
              ser desfeita.
            </AlertDialogDescription>
          </AlertDialogHeader>
          <AlertDialogFooter>
            <AlertDialogCancel>Cancelar</AlertDialogCancel>
            <AlertDialogAction onClick={handleDelete}>Confirmar</AlertDialogAction>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>

      {/* Dialog de Mudança de Status */}
      <AlertDialog
        open={!!funcionarioToChangeStatus}
        onOpenChange={() => setFuncionarioToChangeStatus(null)}
      >
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle>Mudar Status</AlertDialogTitle>
            <AlertDialogDescription>
              Selecione o novo status para{" "}
              <strong>{funcionarioToChangeStatus?.funcionario.nome}</strong>
            </AlertDialogDescription>
          </AlertDialogHeader>
          <div className="space-y-4">
            <Select
              value={funcionarioToChangeStatus?.novoStatus || ""}
              onValueChange={(value) =>
                setFuncionarioToChangeStatus(
                  funcionarioToChangeStatus
                    ? {
                        ...funcionarioToChangeStatus,
                        novoStatus: value as StatusFuncionario,
                      }
                    : null
                )
              }
            >
              <SelectTrigger>
                <SelectValue placeholder="Selecione o status" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="ATIVO">Ativo</SelectItem>
                <SelectItem value="INATIVO">Inativo</SelectItem>
                <SelectItem value="FERIAS">Férias</SelectItem>
                <SelectItem value="AFASTADO">Afastado</SelectItem>
              </SelectContent>
            </Select>
          </div>
          <AlertDialogFooter>
            <AlertDialogCancel>Cancelar</AlertDialogCancel>
            <AlertDialogAction
              onClick={handleMudarStatus}
              disabled={!funcionarioToChangeStatus?.novoStatus}
            >
              Confirmar
            </AlertDialogAction>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>
    </div>
  );
}
