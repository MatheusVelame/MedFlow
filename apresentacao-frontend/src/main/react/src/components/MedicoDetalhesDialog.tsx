import { Loader2, User, FileText, Stethoscope } from "lucide-react";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import { Badge } from "@/components/ui/badge";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Separator } from "@/components/ui/separator";
import { format } from "date-fns";
import { useObterMedico } from "@/api/useMedicosApi";

interface MedicoDetalhesDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  medicoId: number | null;
}

export function MedicoDetalhesDialog({
  open,
  onOpenChange,
  medicoId,
}: MedicoDetalhesDialogProps) {
  const { data: medico, isLoading, error } = useObterMedico(medicoId);

  const getStatusBadge = (status: string) => {
    const statusMap: Record<string, string> = {
      ATIVO: "Ativo",
      INATIVO: "Inativo",
    };

    const configs: Record<
      string,
      { variant: "default" | "secondary" | "destructive" | "outline"; className?: string }
    > = {
      ATIVO: { variant: "default", className: "bg-success/10 text-success border-success/20" },
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

  const formatDateTime = (dateString: string) => {
    try {
      return format(new Date(dateString), "dd/MM/yyyy 'às' HH:mm");
    } catch {
      return dateString;
    }
  };

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="max-w-3xl max-h-[90vh] overflow-y-auto">
        <DialogHeader>
          <DialogTitle>Detalhes do Médico</DialogTitle>
        </DialogHeader>

        {isLoading ? (
          <div className="flex items-center justify-center py-12">
            <Loader2 className="w-8 h-8 animate-spin text-primary" />
          </div>
        ) : error ? (
          <div className="text-center py-12 text-destructive">
            Erro ao carregar detalhes do médico.
          </div>
        ) : medico ? (
          <div className="space-y-6">
            {/* Informações principais */}
            <Card>
              <CardHeader>
                <div className="flex items-center justify-between">
                  <CardTitle className="text-xl">{medico.nome}</CardTitle>
                  {getStatusBadge(medico.status)}
                </div>
              </CardHeader>

              <CardContent className="space-y-4">
                <div className="grid grid-cols-2 gap-4">
                  <div className="flex items-center gap-2">
                    <User className="h-4 w-4 text-muted-foreground" />
                    <div>
                      <p className="text-sm text-muted-foreground">Função</p>
                      <p className="font-medium">{medico.funcao}</p>
                    </div>
                  </div>

                  <div className="flex items-center gap-2">
                    <div>
                      <p className="text-sm text-muted-foreground">Contato</p>
                      <p className="font-medium">{medico.contato}</p>
                    </div>
                  </div>

                  <div className="flex items-center gap-2">
                    <Stethoscope className="h-4 w-4 text-muted-foreground" />
                    <div>
                      <p className="text-sm text-muted-foreground">Especialidade</p>
                      <p className="font-medium">{medico.especialidade}</p>
                    </div>
                  </div>

                  <div>
                    <p className="text-sm text-muted-foreground">CRM</p>
                    <p className="font-medium">{medico.crm}</p>
                  </div>
                </div>

                <Separator />

                <div className="space-y-2">
                  <div>
                    <p className="text-sm text-muted-foreground">ID</p>
                    <p className="font-medium">{medico.id}</p>
                  </div>
                </div>
              </CardContent>
            </Card>

            {/* Disponibilidades */}
            <Card>
              <CardHeader>
                <CardTitle>Horários Disponíveis</CardTitle>
              </CardHeader>
              <CardContent>
                {medico.horariosDisponiveis && medico.horariosDisponiveis.length > 0 ? (
                  <div className="space-y-3">
                    {medico.horariosDisponiveis.map((h, idx) => (
                      <div key={idx} className="flex items-center justify-between">
                        <div>
                          <p className="font-medium">{h.diaSemana}</p>
                          <p className="text-sm text-muted-foreground">
                            {h.horaInicio} - {h.horaFim}
                          </p>
                        </div>
                        {idx < medico.horariosDisponiveis.length - 1 && <Separator />}
                      </div>
                    ))}
                  </div>
                ) : (
                  <p className="text-sm text-muted-foreground">Nenhuma disponibilidade cadastrada.</p>
                )}
              </CardContent>
            </Card>

            {/* Histórico */}
            <Card>
              <CardHeader>
                <CardTitle className="flex items-center gap-2">
                  <FileText className="h-5 w-5" />
                  Histórico
                </CardTitle>
              </CardHeader>
              <CardContent>
                {medico.historico && medico.historico.length > 0 ? (
                  <div className="space-y-4">
                    {medico.historico.map((entrada, index) => (
                      <div key={index} className="space-y-2">
                        <div className="flex items-start justify-between gap-4">
                          <div className="flex-1">
                            <p className="font-medium">{entrada.descricao}</p>
                            <p className="text-sm text-muted-foreground">
                              {entrada.responsavel}
                            </p>
                          </div>
                          <p className="text-sm text-muted-foreground whitespace-nowrap">
                            {formatDateTime(entrada.dataHora)}
                          </p>
                        </div>
                        {index < medico.historico.length - 1 && <Separator />}
                      </div>
                    ))}
                  </div>
                ) : (
                  <p className="text-sm text-muted-foreground">Nenhum histórico disponível.</p>
                )}
              </CardContent>
            </Card>
          </div>
        ) : null}
      </DialogContent>
    </Dialog>
  );
}