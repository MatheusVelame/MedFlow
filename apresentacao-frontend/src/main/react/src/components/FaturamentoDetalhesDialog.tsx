import { Loader2, Calendar, User, FileText, DollarSign, CreditCard } from "lucide-react";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import { Badge } from "@/components/ui/badge";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Separator } from "@/components/ui/separator";
import { useObterFaturamento, mapStatusToDisplay, mapTipoProcedimentoToDisplay } from "@/api/useFaturamentosApi";
import { format } from "date-fns";

interface FaturamentoDetalhesDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  faturamentoId: string | null;
}

export function FaturamentoDetalhesDialog({
  open,
  onOpenChange,
  faturamentoId,
}: FaturamentoDetalhesDialogProps) {
  const { data: faturamento, isLoading, error } = useObterFaturamento(
    faturamentoId
  );

  const getStatusBadge = (status: string) => {
    const variants: Record<string, "default" | "secondary" | "destructive"> = {
      PENDENTE: "secondary",
      PAGO: "default",
      CANCELADO: "destructive",
      INVALIDO: "destructive",
      REMOVIDO: "secondary"
    };
    return <Badge variant={variants[status] || "default"}>{mapStatusToDisplay(status)}</Badge>;
  };

  const formatDate = (dateString: string) => {
    try {
      const date = new Date(dateString);
      return format(date, "dd/MM/yyyy 'às' HH:mm");
    } catch {
      return dateString;
    }
  };

  const formatCurrency = (value: number) => {
    return new Intl.NumberFormat('pt-BR', {
      style: 'currency',
      currency: 'BRL'
    }).format(value);
  };

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="max-w-3xl max-h-[90vh] overflow-y-auto">
        <DialogHeader>
          <DialogTitle>Detalhes do Faturamento</DialogTitle>
        </DialogHeader>

        {isLoading ? (
          <div className="flex items-center justify-center py-12">
            <Loader2 className="w-8 h-8 animate-spin text-primary" />
          </div>
        ) : error ? (
          <div className="text-center py-12 text-destructive">
            Erro ao carregar detalhes do faturamento.
          </div>
        ) : faturamento ? (
          <div className="space-y-6">
            {/* Informações Principais */}
            <Card>
              <CardHeader>
                <div className="flex items-center justify-between">
                  <CardTitle className="text-xl">Faturamento #{faturamento.id.substring(0, 8)}</CardTitle>
                  {getStatusBadge(faturamento.status)}
                </div>
              </CardHeader>
              <CardContent className="space-y-4">
                <div className="grid grid-cols-2 gap-4">
                  <div className="flex items-center gap-2">
                    <User className="h-4 w-4 text-muted-foreground" />
                    <div>
                      <p className="text-sm text-muted-foreground">Paciente ID</p>
                      <p className="font-medium">{faturamento.pacienteId}</p>
                    </div>
                  </div>
                  <div className="flex items-center gap-2">
                    <FileText className="h-4 w-4 text-muted-foreground" />
                    <div>
                      <p className="text-sm text-muted-foreground">Tipo de Procedimento</p>
                      <p className="font-medium">{mapTipoProcedimentoToDisplay(faturamento.tipoProcedimento)}</p>
                    </div>
                  </div>
                  <div className="flex items-center gap-2">
                    <DollarSign className="h-4 w-4 text-muted-foreground" />
                    <div>
                      <p className="text-sm text-muted-foreground">Valor</p>
                      <p className="font-medium text-lg text-primary">{formatCurrency(faturamento.valor)}</p>
                    </div>
                  </div>
                  <div className="flex items-center gap-2">
                    <CreditCard className="h-4 w-4 text-muted-foreground" />
                    <div>
                      <p className="text-sm text-muted-foreground">Método de Pagamento</p>
                      <p className="font-medium">{faturamento.metodoPagamento}</p>
                    </div>
                  </div>
                </div>
                <Separator />
                <div>
                  <p className="text-sm text-muted-foreground mb-2">Descrição do Procedimento</p>
                  <p className="font-medium">{faturamento.descricaoProcedimento}</p>
                </div>
                {faturamento.observacoes && (
                  <>
                    <Separator />
                    <div>
                      <p className="text-sm text-muted-foreground mb-2">Observações</p>
                      <p className="text-sm">{faturamento.observacoes}</p>
                    </div>
                  </>
                )}
                {faturamento.valorPadrao && faturamento.valorPadrao !== faturamento.valor && (
                  <>
                    <Separator />
                    <div>
                      <p className="text-sm text-muted-foreground mb-2">Valor Padrão</p>
                      <p className="font-medium">{formatCurrency(faturamento.valorPadrao)}</p>
                      {faturamento.justificativaValorDiferente && (
                        <div className="mt-2">
                          <p className="text-sm text-muted-foreground mb-1">Justificativa</p>
                          <p className="text-sm">{faturamento.justificativaValorDiferente}</p>
                        </div>
                      )}
                    </div>
                  </>
                )}
                <Separator />
                <div className="flex items-center gap-2">
                  <Calendar className="h-4 w-4 text-muted-foreground" />
                  <div>
                    <p className="text-sm text-muted-foreground">Data/Hora do Faturamento</p>
                    <p className="font-medium">{formatDate(faturamento.dataHoraFaturamento)}</p>
                  </div>
                </div>
                <div className="flex items-center gap-2">
                  <User className="h-4 w-4 text-muted-foreground" />
                  <div>
                    <p className="text-sm text-muted-foreground">Usuário Responsável</p>
                    <p className="font-medium">{faturamento.usuarioResponsavel}</p>
                  </div>
                </div>
              </CardContent>
            </Card>

            {/* Histórico */}
            {faturamento.historico && faturamento.historico.length > 0 && (
              <Card>
                <CardHeader>
                  <CardTitle className="flex items-center gap-2">
                    <FileText className="h-5 w-5" />
                    Histórico de Alterações
                  </CardTitle>
                </CardHeader>
                <CardContent>
                  <div className="space-y-4">
                    {faturamento.historico.map((entrada, index) => (
                      <div key={index} className="space-y-2">
                        <div className="flex items-start justify-between">
                          <div className="flex-1">
                            <p className="font-medium">{entrada.descricao}</p>
                            <p className="text-sm text-muted-foreground">
                              {entrada.responsavel}
                            </p>
                          </div>
                          <p className="text-sm text-muted-foreground">
                            {formatDate(entrada.dataHora)}
                          </p>
                        </div>
                        {index < faturamento.historico.length - 1 && (
                          <Separator />
                        )}
                      </div>
                    ))}
                  </div>
                </CardContent>
              </Card>
            )}
          </div>
        ) : null}
      </DialogContent>
    </Dialog>
  );
}

