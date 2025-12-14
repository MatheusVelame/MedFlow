import { Loader2, Calendar, User, FileText, Stethoscope, Clock } from "lucide-react";
import { Dialog, DialogContent, DialogHeader, DialogTitle } from "@/components/ui/dialog";
import { Badge } from "@/components/ui/badge";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Separator } from "@/components/ui/separator";
import { useObterMedico } from "@/api/useMedicosApi";

interface MedicoDetalhesDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  medicoId: string | null;
}

const diasOrdenados = ["SEGUNDA", "TERCA", "QUARTA", "QUINTA", "SEXTA", "SABADO", "DOMINGO"] as const;

const diaNomes: Record<string, string> = {
  SEGUNDA: "Segunda-feira",
  TERCA: "Terça-feira",
  QUARTA: "Quarta-feira",
  QUINTA: "Quinta-feira",
  SEXTA: "Sexta-feira",
  SABADO: "Sábado",
  DOMINGO: "Domingo",
};

// ✅ deixa resistente a SEGUNDA_FEIRA, Segunda, monday, etc
function normalizarDia(diaSemana: string) {
  const d = String(diaSemana || "")
    .trim()
    .toUpperCase()
    .replaceAll("-", "_")
    .replaceAll("Ç", "C")
    .replaceAll("Á", "A")
    .replaceAll("Ã", "A")
    .replaceAll("Â", "A")
    .replaceAll("É", "E")
    .replaceAll("Ê", "E")
    .replaceAll("Í", "I")
    .replaceAll("Ó", "O")
    .replaceAll("Ô", "O")
    .replaceAll("Õ", "O")
    .replaceAll("Ú", "U");

  if (d.includes("SEG")) return "SEGUNDA";
  if (d.includes("TER")) return "TERCA";
  if (d.includes("QUA")) return "QUARTA";
  if (d.includes("QUI")) return "QUINTA";
  if (d.includes("SEX")) return "SEXTA";
  if (d.includes("SAB")) return "SABADO";
  if (d.includes("DOM")) return "DOMINGO";
  return d;
}

export function MedicoDetalhesDialog({ open, onOpenChange, medicoId }: MedicoDetalhesDialogProps) {
  const { data: medico, isLoading, error } = useObterMedico(medicoId);

  const getStatusBadge = (status: string) => {
    const statusMap: Record<string, string> = { ATIVO: "Ativo", INATIVO: "Inativo" };
    const configs: Record<string, { variant: "default" | "destructive"; className?: string }> = {
      ATIVO: { variant: "default", className: "bg-success/10 text-success border-success/20" },
      INATIVO: { variant: "destructive", className: "" },
    };
    const config = configs[status] || configs["ATIVO"];
    return <Badge variant={config.variant} className={config.className}>{statusMap[status] || status}</Badge>;
  };

  const horariosAgrupados =
    medico?.horariosDisponiveis?.reduce((acc: Record<string, any[]>, h: any) => {
      const chave = normalizarDia(h.diaSemana);
      acc[chave] = acc[chave] || [];
      acc[chave].push(h);
      return acc;
    }, {}) ?? {};

  const diasComHorario = diasOrdenados.filter((d) => (horariosAgrupados[d] || []).length > 0);

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="max-w-4xl max-h-[90vh] overflow-y-auto">
        <DialogHeader>
          <DialogTitle className="flex items-center gap-2 text-2xl">
            <User className="h-6 w-6" />
            Detalhes do Médico
          </DialogTitle>
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
                    <Calendar className="h-4 w-4 text-muted-foreground" />
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

                <div>
                  <p className="text-sm text-muted-foreground">ID</p>
                  <p className="font-medium">{medico.id}</p>
                </div>
              </CardContent>
            </Card>

            {/* Disponibilidades */}
            {diasComHorario.length > 0 ? (
              <Card>
                <CardHeader>
                  <CardTitle className="flex items-center gap-2">
                    <Clock className="h-5 w-5" />
                    Horários Disponíveis
                  </CardTitle>
                </CardHeader>
                <CardContent>
                  <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
                    {diasOrdenados.map((dia) => {
                      const horariosDoDia = horariosAgrupados[dia];
                      if (!horariosDoDia?.length) return null;

                      return (
                        <div key={dia} className="p-3 bg-slate-50 rounded-md">
                          <p className="font-medium text-sm mb-2">{diaNomes[dia]}</p>
                          <div className="space-y-1">
                            {horariosDoDia.map((h: any, idx: number) => (
                              <p key={idx} className="text-sm text-muted-foreground flex items-center gap-1">
                                <Clock className="h-3 w-3" />
                                {h.horaInicio} - {h.horaFim}
                              </p>
                            ))}
                          </div>
                        </div>
                      );
                    })}
                  </div>
                </CardContent>
              </Card>
            ) : (
              <Card>
                <CardHeader><CardTitle>Horários Disponíveis</CardTitle></CardHeader>
                <CardContent><p className="text-sm text-muted-foreground">Nenhuma disponibilidade cadastrada.</p></CardContent>
              </Card>
            )}

            {/* Registro Administrativo */}
            <Card>
              <CardHeader>
                <CardTitle className="flex items-center gap-2">
                  <FileText className="h-5 w-5" />
                  Registro Administrativo
                </CardTitle>
              </CardHeader>

              <CardContent className="space-y-4">
                <div>
                  <h4 className="font-semibold text-sm mb-3 text-slate-700">Eventos Cadastrais</h4>
                  <div className="space-y-2">
                    {medico.historico?.length ? (
                      medico.historico.map((evento: any, index: number) => (
                        <div key={index} className="p-3 bg-slate-50 rounded-md border border-slate-200">
                          <div className="flex items-start justify-between">
                            <div className="flex-1">
                              <p className="font-medium text-sm">{evento.descricao || "Sem descrição"}</p>
                              <p className="text-xs text-muted-foreground mt-1">
                                Responsável: {evento.responsavel || "N/A"}
                              </p>
                            </div>
                            <p className="text-xs text-muted-foreground">{evento.dataHora || ""}</p>
                          </div>
                        </div>
                      ))
                    ) : (
                      <p className="text-sm text-muted-foreground italic">Nenhum evento cadastral registrado</p>
                    )}
                  </div>
                </div>

                <Separator />

                {/* ✅ Histórico Assistencial via exists (no banco) */}
                <div>
                  <h4 className="font-semibold text-sm mb-3 text-slate-700">Histórico Assistencial</h4>

                  <div className="space-y-2">
                    <div className="flex items-center justify-between p-3 bg-slate-50 rounded-md">
                      <span className="text-sm">Consultas</span>
                      <Badge
                        variant="outline"
                        className={medico.temConsultas ? "bg-blue-50 text-blue-700 border-blue-200" : "bg-slate-50 text-slate-600 border-slate-200"}
                      >
                        {medico.temConsultas ? "Registros existentes" : "Sem registros"}
                      </Badge>
                    </div>

                    <div className="flex items-center justify-between p-3 bg-slate-50 rounded-md">
                      <span className="text-sm">Exames</span>
                      <Badge
                        variant="outline"
                        className={medico.temExames ? "bg-green-50 text-green-700 border-green-200" : "bg-slate-50 text-slate-600 border-slate-200"}
                      >
                        {medico.temExames ? "Registros existentes" : "Sem registros"}
                      </Badge>
                    </div>

                    <div className="flex items-center justify-between p-3 bg-slate-50 rounded-md">
                      <span className="text-sm">Prontuários</span>
                      <Badge
                        variant="outline"
                        className={medico.temProntuarios ? "bg-purple-50 text-purple-700 border-purple-200" : "bg-slate-50 text-slate-600 border-slate-200"}
                      >
                        {medico.temProntuarios ? "Registros existentes" : "Sem registros"}
                      </Badge>
                    </div>
                  </div>

                  <p className="text-xs text-muted-foreground mt-3 italic">
                    * Informações consolidadas da base assistencial do sistema
                  </p>
                </div>
              </CardContent>
            </Card>
          </div>
        ) : null}
      </DialogContent>
    </Dialog>
  );
}