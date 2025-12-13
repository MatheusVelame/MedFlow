import { Loader2, Calendar, User, FileText } from "lucide-react";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import { Badge } from "@/components/ui/badge";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Separator } from "@/components/ui/separator";
import { useObterFuncionario } from "@/api/useFuncionariosApi";
import { format } from "date-fns";

interface FuncionarioDetalhesDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  funcionarioId: number | null;
}

export function FuncionarioDetalhesDialog({
  open,
  onOpenChange,
  funcionarioId,
}: FuncionarioDetalhesDialogProps) {
  const { data: funcionario, isLoading, error } = useObterFuncionario(
    funcionarioId
  );

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

  const formatDate = (dateString: string) => {
    try {
      const date = new Date(dateString);
      return format(date, "dd/MM/yyyy 'às' HH:mm");
    } catch {
      return dateString;
    }
  };

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="max-w-3xl max-h-[90vh] overflow-y-auto">
        <DialogHeader>
          <DialogTitle>Detalhes do Funcionário</DialogTitle>
        </DialogHeader>

        {isLoading ? (
          <div className="flex items-center justify-center py-12">
            <Loader2 className="w-8 h-8 animate-spin text-primary" />
          </div>
        ) : error ? (
          <div className="text-center py-12 text-destructive">
            Erro ao carregar detalhes do funcionário.
          </div>
        ) : funcionario ? (
          <div className="space-y-6">
            {/* Informações Principais */}
            <Card>
              <CardHeader>
                <div className="flex items-center justify-between">
                  <CardTitle className="text-xl">{funcionario.nome}</CardTitle>
                  {getStatusBadge(funcionario.status)}
                </div>
              </CardHeader>
              <CardContent className="space-y-4">
                <div className="grid grid-cols-2 gap-4">
                  <div className="flex items-center gap-2">
                    <User className="h-4 w-4 text-muted-foreground" />
                    <div>
                      <p className="text-sm text-muted-foreground">Função</p>
                      <p className="font-medium">{funcionario.funcao}</p>
                    </div>
                  </div>
                  <div className="flex items-center gap-2">
                    <Calendar className="h-4 w-4 text-muted-foreground" />
                    <div>
                      <p className="text-sm text-muted-foreground">Contato</p>
                      <p className="font-medium">{funcionario.contato}</p>
                    </div>
                  </div>
                </div>
                <Separator />
                <div>
                  <p className="text-sm text-muted-foreground mb-2">ID</p>
                  <p className="font-medium">{funcionario.id}</p>
                </div>
              </CardContent>
            </Card>

            {/* Histórico */}
            <Card>
              <CardHeader>
                <CardTitle className="flex items-center gap-2">
                  <FileText className="h-5 w-5" />
                  Histórico de Alterações
                </CardTitle>
              </CardHeader>
              <CardContent>
                {funcionario.historico && funcionario.historico.length > 0 ? (
                  <div className="space-y-4">
                    {funcionario.historico.map((entrada, index) => (
                      <div key={index} className="space-y-2">
                        <div className="flex items-start justify-between">
                          <div className="flex-1">
                            <p className="font-medium">{entrada.descricao}</p>
                            <p className="text-sm text-muted-foreground">
                              {entrada.usuarioResponsavel}
                            </p>
                          </div>
                          <p className="text-sm text-muted-foreground">
                            {formatDate(entrada.dataHora)}
                          </p>
                        </div>
                        {index < funcionario.historico.length - 1 && (
                          <Separator />
                        )}
                      </div>
                    ))}
                  </div>
                ) : (
                  <p className="text-sm text-muted-foreground">
                    Nenhum histórico disponível.
                  </p>
                )}
              </CardContent>
            </Card>
          </div>
        ) : null}
      </DialogContent>
    </Dialog>
  );
}

