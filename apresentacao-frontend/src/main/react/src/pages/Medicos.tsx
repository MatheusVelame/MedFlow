import { useMemo, useState } from "react";
import {
  Stethoscope,
  Search,
  Plus,
  Loader2,
  Edit,
  Trash2,
  Eye,
  Filter,
  X,
  MoreVertical,
  Award,
  Calendar,
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

import { useAuth } from "@/contexts/AuthContext";
import {
  useListarMedicos,
  useCadastrarMedico,
  useAtualizarMedico,
  useExcluirMedico,
  type MedicoResumo,
  type StatusMedico,
  type CadastrarMedicoPayload,
  type AtualizarMedicoPayload,
} from "@/api/useMedicosApi";

import { MedicoForm, type MedicoFormData } from "@/components/MedicoForm";
import { MedicoDetalhesDialog } from "@/components/MedicoDetalhesDialog";

export default function Medicos() {
  const { isGestor } = useAuth();

  const [searchTerm, setSearchTerm] = useState("");
  const [statusFilter, setStatusFilter] = useState<StatusMedico | "TODOS">(
    "TODOS"
  );

  const [isFormOpen, setIsFormOpen] = useState(false);
  const [editingMedico, setEditingMedico] = useState<MedicoResumo | null>(null);

  const [medicoToDelete, setMedicoToDelete] = useState<MedicoResumo | null>(
    null
  );
  const [medicoToView, setMedicoToView] = useState<number | null>(null);

  // Queries / Mutations
  const { data: medicos = [], isLoading, error, refetch } = useListarMedicos();
  const cadastrarMutation = useCadastrarMedico();
  const atualizarMutation = useAtualizarMedico();
  const excluirMutation = useExcluirMedico();

  const handleNew = () => {
    setEditingMedico(null);
    setIsFormOpen(true);
  };

  const handleEdit = (medico: MedicoResumo) => {
    setEditingMedico(medico);
    setIsFormOpen(true);
  };

  const handleSave = (data: MedicoFormData) => {
      if (editingMedico) {
        // PUT /medicos/{id} (payload do swagger: nome, contato, dataNascimento, disponibilidades)
        const payload: AtualizarMedicoPayload = {
          nome: data.nome,
          contato: data.contato,
          // dataNascimento: data.dataNascimento || undefined, // <-- REMOVER ESTA LINHA
          disponibilidades: data.disponibilidades ?? [],
        };

      atualizarMutation.mutate(
        { id: editingMedico.id, payload },
        {
          onSuccess: () => {
            setEditingMedico(null);
            setIsFormOpen(false);
            refetch();
          },
        }
      );
    } else {
          // POST /medicos (payload: nome, contato, crmNumero, crmUf, especialidadeId, dataNascimento, disponibilidades)
          const payload: CadastrarMedicoPayload = {
            nome: data.nome,
            contato: data.contato,
            crmNumero: String(data.crmNumero ?? "").trim(),
            crmUf: String(data.crmUf ?? "").trim(),
            especialidadeId: Number(data.especialidadeId),
            // dataNascimento: data.dataNascimento || undefined, // <-- REMOVER ESTA LINHA
            disponibilidades: data.disponibilidades ?? [],
          };

      cadastrarMutation.mutate(payload, {
        onSuccess: () => {
          setIsFormOpen(false);
          refetch();
        },
      });
    }
  };

  const handleDelete = () => {
    if (!medicoToDelete) return;

    excluirMutation.mutate(medicoToDelete.id, {
      onSuccess: () => {
        setMedicoToDelete(null);
        refetch();
      },
    });
  };

  const getInitials = (nome: string) =>
    nome
      .split(" ")
      .map((n) => n[0])
      .join("")
      .substring(0, 2)
      .toUpperCase();

  const getStatusBadge = (status: string) => {
    const statusMap: Record<string, string> = {
      ATIVO: "Ativo",
      INATIVO: "Inativo",
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

  // Filtros (local) — mantém padrão do Profissionais
  const filteredMedicos = useMemo(() => {
    const term = searchTerm.trim().toLowerCase();

    return medicos.filter((m) => {
      const matchesSearch =
        term.length === 0 ||
        m.nome.toLowerCase().includes(term) ||
        m.crm.toLowerCase().includes(term) ||
        m.especialidade.toLowerCase().includes(term) ||
        (m.contato ?? "").toLowerCase().includes(term);

      const matchesStatus =
        statusFilter === "TODOS" || m.status === statusFilter;

      return matchesSearch && matchesStatus;
    });
  }, [medicos, searchTerm, statusFilter]);

  // Estatísticas
  const medicosAtivos = useMemo(
    () => medicos.filter((m) => m.status === "ATIVO").length,
    [medicos]
  );
  const medicosInativos = useMemo(
    () => medicos.filter((m) => m.status === "INATIVO").length,
    [medicos]
  );
  const especialidadesUnicas = useMemo(() => {
    const set = new Set(medicos.map((m) => m.especialidade));
    return Array.from(set);
  }, [medicos]);

  return (
    <div className="space-y-6 animate-fade-in">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold tracking-tight text-foreground">
            Médicos
          </h1>
          <p className="text-muted-foreground">
            Gerencie os médicos da clínica
          </p>
        </div>

        {isGestor && (
          <Button onClick={handleNew} className="gap-2">
            <Plus className="h-4 w-4" />
            Novo Médico
          </Button>
        )}
      </div>

      {/* Cards de estatísticas */}
      <div className="grid gap-6 md:grid-cols-4">
        <Card className="shadow-card">
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium text-muted-foreground">
              Total de Médicos
            </CardTitle>
            <Stethoscope className="w-4 h-4 text-primary" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold text-foreground">
              {isLoading ? (
                <Loader2 className="w-6 h-6 animate-spin" />
              ) : (
                medicos.length
              )}
            </div>
            <p className="text-xs text-muted-foreground">Médicos cadastrados</p>
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
                medicosAtivos
              )}
            </div>
            <p className="text-xs text-muted-foreground">Status ativo</p>
          </CardContent>
        </Card>

        <Card className="shadow-card">
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium text-muted-foreground">
              Inativos
            </CardTitle>
            <Calendar className="w-4 h-4 text-warning" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold text-foreground">
              {isLoading ? (
                <Loader2 className="w-6 h-6 animate-spin" />
              ) : (
                medicosInativos
              )}
            </div>
            <p className="text-xs text-muted-foreground">Não disponíveis</p>
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
            <div className="text-2xl font-bold text-foreground">
              {isLoading ? (
                <Loader2 className="w-6 h-6 animate-spin" />
              ) : (
                especialidadesUnicas.length
              )}
            </div>
            <p className="text-xs text-muted-foreground">
              Diferentes especialidades
            </p>
          </CardContent>
        </Card>
      </div>

      {/* Filtros */}
      <div className="flex flex-col sm:flex-row gap-4">
        <div className="relative flex-1">
          <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-muted-foreground w-4 h-4" />
          <Input
            placeholder="Buscar por nome, CRM, especialidade ou contato..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="pl-10"
          />
        </div>

        <div className="flex gap-2">
          <Select
            value={statusFilter}
            onValueChange={(value) =>
              setStatusFilter(value as StatusMedico | "TODOS")
            }
          >
            <SelectTrigger className="w-[180px]">
              <Filter className="h-4 w-4 mr-2" />
              <SelectValue placeholder="Status" />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="TODOS">Todos os Status</SelectItem>
              <SelectItem value="ATIVO">Ativo</SelectItem>
              <SelectItem value="INATIVO">Inativo</SelectItem>
            </SelectContent>
          </Select>

          {(statusFilter !== "TODOS" || searchTerm.trim().length > 0) && (
            <Button
              variant="outline"
              size="icon"
              onClick={() => {
                setStatusFilter("TODOS");
                setSearchTerm("");
              }}
              title="Limpar filtros"
            >
              <X className="h-4 w-4" />
            </Button>
          )}
        </div>
      </div>

      {/* Lista */}
      {isLoading ? (
        <div className="flex items-center justify-center py-12">
          <Loader2 className="w-8 h-8 animate-spin text-primary" />
        </div>
      ) : error ? (
        <div className="text-center py-12 text-destructive">
          Erro ao carregar médicos. Tente novamente.
        </div>
      ) : filteredMedicos.length === 0 ? (
        <div className="text-center py-12 text-muted-foreground">
          {searchTerm || statusFilter !== "TODOS" ? (
            <div>
              <p>Nenhum médico encontrado com os filtros aplicados.</p>
              <p className="text-xs mt-2">
                Total no banco: {medicos.length} | Status: {statusFilter} | Busca:{" "}
                {searchTerm || "(vazio)"}
              </p>
            </div>
          ) : (
            <div>
              <p>Nenhum médico cadastrado.</p>
              <p className="text-xs mt-2">
                Total retornado da API: {medicos.length}
              </p>
            </div>
          )}
        </div>
      ) : (
        <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
          {filteredMedicos.map((medico) => (
            <Card key={medico.id} className="hover:shadow-lg transition-shadow">
              <CardContent className="p-6">
                <div className="flex items-start gap-4">
                  <Avatar className="w-16 h-16">
                    <AvatarFallback className="bg-gradient-primary text-white text-lg">
                      {getInitials(medico.nome)}
                    </AvatarFallback>
                  </Avatar>

                  <div className="flex-1 space-y-3">
                    <div>
                      <div className="flex items-center justify-between mb-1">
                        <h3 className="text-lg font-semibold text-foreground">
                          {medico.nome}
                        </h3>
                        {getStatusBadge(medico.status)}
                      </div>
                      <p className="text-sm text-muted-foreground">
                        {medico.especialidade}
                      </p>
                    </div>

                    <div className="space-y-1 text-sm">
                      <div>
                        <span className="text-muted-foreground">CRM: </span>
                        <span className="text-foreground font-medium">
                          {medico.crm}
                        </span>
                      </div>
                      <div>
                        <span className="text-muted-foreground">Contato: </span>
                        <span className="text-foreground font-medium">
                          {medico.contato}
                        </span>
                      </div>
                      <div>
                        <span className="text-muted-foreground">ID: </span>
                        <span className="text-foreground font-medium">
                          {medico.id}
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
                      onClick={() => setMedicoToView(medico.id)}
                    >
                      <Eye className="h-4 w-4 mr-1" />
                      Ver
                    </Button>

                    <Button
                      variant="outline"
                      size="sm"
                      className="flex-1"
                      onClick={() => handleEdit(medico)}
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
                          className="text-destructive"
                          onClick={() => setMedicoToDelete(medico)}
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

      {/* Formulário (Cadastrar/Editar) */}
      <MedicoForm
        open={isFormOpen}
        onOpenChange={setIsFormOpen}
        initialData={editingMedico}
        onSave={handleSave}
      />

      {/* Dialog de Detalhes */}
      <MedicoDetalhesDialog
        open={medicoToView !== null}
        onOpenChange={(open) => !open && setMedicoToView(null)}
        medicoId={medicoToView}
      />

      {/* Dialog de Exclusão */}
      <AlertDialog
        open={!!medicoToDelete}
        onOpenChange={() => setMedicoToDelete(null)}
      >
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle>Confirmar exclusão</AlertDialogTitle>
            <AlertDialogDescription>
              Tem certeza que deseja excluir o médico{" "}
              <strong>{medicoToDelete?.nome}</strong>? Esta ação não pode ser
              desfeita.
            </AlertDialogDescription>
          </AlertDialogHeader>
          <AlertDialogFooter>
            <AlertDialogCancel>Cancelar</AlertDialogCancel>
            <AlertDialogAction
              onClick={handleDelete}
              disabled={excluirMutation.isPending}
            >
              {excluirMutation.isPending ? "Excluindo..." : "Confirmar"}
            </AlertDialogAction>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>
    </div>
  );
}
