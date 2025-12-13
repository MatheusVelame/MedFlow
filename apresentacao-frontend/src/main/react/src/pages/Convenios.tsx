import { useState } from "react";
import { Plus, Edit, Trash2, CreditCard, Loader2 } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
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
import { ConvenioForm } from "@/components/ConvenioForm";
import { toast } from "@/hooks/use-toast";
import { useAuth } from "@/contexts/AuthContext";
import {
  useListarConvenios,
  useCadastrarConvenio,
  useAlterarNomeConvenio,
  useMudarStatusConvenio,
  useExcluirConvenio,
  type ConvenioResumo,
  type StatusConvenio
} from "@/api/useConveniosApi";

export default function Convenios() {
  const { isGestor, user } = useAuth();
  const [isFormOpen, setIsFormOpen] = useState(false);
  const [editingConvenio, setEditingConvenio] = useState<ConvenioResumo | null>(null);
  const [convenioToDelete, setConvenioToDelete] = useState<ConvenioResumo | null>(null);

  // Queries e Mutations
  const { data: convenios = [], isLoading, error } = useListarConvenios();
  const cadastrarMutation = useCadastrarConvenio();
  const alterarNomeMutation = useAlterarNomeConvenio();
  const mudarStatusMutation = useMudarStatusConvenio();
  const excluirMutation = useExcluirConvenio();

  const handleSave = (data: any) => {
    const responsavelId = parseInt(user?.id || "1");
    
    if (editingConvenio) {
      // Verifica se o nome mudou
      const nomeMudou = data.nome !== editingConvenio.nome;
      // Verifica se o status mudou
      const statusMudou = data.status && data.status !== editingConvenio.status;
      
      // Array para armazenar as promises das mutations
      const mutations: Promise<any>[] = [];
      
      if (nomeMudou) {
        mutations.push(
          new Promise((resolve, reject) => {
            alterarNomeMutation.mutate({
              id: editingConvenio.id,
              payload: {
                novoNome: data.nome,
                responsavelId: responsavelId
              }
            }, {
              onSuccess: resolve,
              onError: reject
            });
          })
        );
      }
      
      if (statusMudou) {
        mutations.push(
          new Promise((resolve, reject) => {
            mudarStatusMutation.mutate({
              id: editingConvenio.id,
              payload: {
                status: data.status as StatusConvenio,
                responsavelId: responsavelId
              }
            }, {
              onSuccess: resolve,
              onError: reject
            });
          })
        );
      }
      
      // Aguarda todas as mutations completarem
      Promise.all(mutations).then(() => {
        setEditingConvenio(null);
        setIsFormOpen(false);
      }).catch(() => {
        // Erro já foi tratado nas mutations individuais
      });
    } else {
      cadastrarMutation.mutate({
        nome: data.nome,
        codigoIdentificacao: data.codigoIdentificacao,
        responsavelId: responsavelId
      }, {
        onSuccess: () => {
          setIsFormOpen(false);
        }
      });
    }
  };

  const handleEdit = (convenio: ConvenioResumo) => {
    setEditingConvenio(convenio);
    setIsFormOpen(true);
  };

  const handleDelete = () => {
    if (convenioToDelete) {
      const responsavelId = parseInt(user?.id || "1");
      excluirMutation.mutate({
        codigoIdentificacao: convenioToDelete.codigoIdentificacao,
        payload: { responsavelId: responsavelId }
      }, {
        onSuccess: () => {
          setConvenioToDelete(null);
        }
      });
    }
  };

  return (
    <div className="space-y-6 animate-fade-in">
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-3xl font-bold tracking-tight text-foreground">Convênios</h1>
          <p className="text-muted-foreground">Gerencie os planos de saúde aceitos pela clínica</p>
        </div>
        {isGestor && (
          <Button onClick={() => setIsFormOpen(true)} className="gap-2">
            <Plus className="h-4 w-4" />
            Novo Convênio
          </Button>
        )}
      </div>

      {isLoading ? (
        <div className="flex items-center justify-center py-12">
          <Loader2 className="w-8 h-8 animate-spin text-primary" />
        </div>
      ) : error ? (
        <div className="text-center py-12 text-destructive">
          Erro ao carregar convênios. Tente novamente.
        </div>
      ) : convenios.length === 0 ? (
        <div className="text-center py-12 text-muted-foreground">
          Nenhum convênio cadastrado.
        </div>
      ) : (
        <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
          {convenios.map((convenio) => (
            <Card key={convenio.id} className="hover:shadow-lg transition-shadow">
              <CardHeader className="pb-3">
                <div className="flex items-start justify-between">
                  <div className="flex items-center gap-3">
                    <div className="p-2 bg-primary/10 rounded-lg">
                      <CreditCard className="h-5 w-5 text-primary" />
                    </div>
                    <div>
                      <CardTitle className="text-lg">{convenio.nome}</CardTitle>
                      <Badge variant={convenio.status === "ATIVO" ? "default" : "secondary"} className="mt-1">
                        {convenio.status === "ATIVO" ? "Ativo" : "Inativo"}
                      </Badge>
                    </div>
                  </div>
                </div>
              </CardHeader>
              <CardContent>
                <div className="space-y-2 mb-4">
                  <div className="flex justify-between text-sm">
                    <span className="text-muted-foreground">Código:</span>
                    <span className="font-medium">{convenio.codigoIdentificacao}</span>
                  </div>
                  <div className="flex justify-between text-sm">
                    <span className="text-muted-foreground">ID:</span>
                    <span className="font-medium">{convenio.id}</span>
                  </div>
                </div>

                {isGestor && (
                  <div className="flex gap-2">
                    <Button 
                      variant="outline" 
                      size="sm" 
                      className="flex-1"
                      onClick={() => handleEdit(convenio)}
                    >
                      <Edit className="h-4 w-4 mr-1" />
                      Editar
                    </Button>
                    <Button 
                      variant="outline" 
                      size="sm"
                      onClick={() => setConvenioToDelete(convenio)}
                    >
                      <Trash2 className="h-4 w-4" />
                    </Button>
                  </div>
                )}
              </CardContent>
            </Card>
          ))}
        </div>
      )}

      <ConvenioForm
        open={isFormOpen}
        onOpenChange={setIsFormOpen}
        onSave={handleSave}
        initialData={editingConvenio}
      />

      <AlertDialog open={!!convenioToDelete} onOpenChange={() => setConvenioToDelete(null)}>
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle>Confirmar exclusão</AlertDialogTitle>
            <AlertDialogDescription>
              Tem certeza que deseja remover este convênio? Esta ação não pode ser desfeita.
            </AlertDialogDescription>
          </AlertDialogHeader>
          <AlertDialogFooter>
            <AlertDialogCancel>Cancelar</AlertDialogCancel>
            <AlertDialogAction onClick={handleDelete}>
              Confirmar
            </AlertDialogAction>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>
    </div>
  );
}
