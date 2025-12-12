import { useEffect, useMemo } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import * as z from "zod";
import { Dialog, DialogContent, DialogHeader, DialogTitle } from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Textarea } from "@/components/ui/textarea";
import { toast } from "sonner";
import { useObterProntuario } from "@/api/useProntuariosApi";
import { useListarPacientes } from "@/api/usePacientesApi";
import { Loader2 } from "lucide-react";

const evolucaoSchema = z.object({
  sintomas: z.string().min(3, "Descreva os sintomas principais"),
  diagnostico: z.string().min(3, "Diagnóstico é obrigatório"),
  conduta: z.string().min(3, "Conduta é obrigatória"),
  profissionalResponsavel: z.string().min(3, "Nome do profissional responsável é obrigatório"),
  anexosReferenciados: z.array(z.string()).optional().default([]),
});

type EvolucaoFormData = z.infer<typeof evolucaoSchema>;

interface EvolucaoFormProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  onSave: (data: EvolucaoFormData) => void;
  prontuarioId?: string | null;
}

export function EvolucaoForm({ open, onOpenChange, onSave, prontuarioId }: EvolucaoFormProps) {
  // Buscar dados do prontuário e pacientes
  const { data: prontuario, isLoading: isLoadingProntuario } = useObterProntuario(prontuarioId);
  const { data: pacientes = [] } = useListarPacientes();

  // Encontrar o nome do paciente
  const nomePaciente = useMemo(() => {
    if (!prontuario || !pacientes.length) return null;
    const pacienteIdNum = parseInt(prontuario.pacienteId);
    const paciente = pacientes.find(p => p.id === pacienteIdNum);
    return paciente?.name || null;
  }, [prontuario, pacientes]);

  const { register, handleSubmit, reset, formState: { errors } } = useForm<EvolucaoFormData>({
    resolver: zodResolver(evolucaoSchema),
    defaultValues: {
      sintomas: "",
      diagnostico: "",
      conduta: "",
      profissionalResponsavel: "",
      anexosReferenciados: []
    }
  });

  useEffect(() => {
    if (!open) {
      reset({
        sintomas: "",
        diagnostico: "",
        conduta: "",
        profissionalResponsavel: "",
        anexosReferenciados: []
      });
    }
  }, [open, reset]);

  const onSubmit = (data: EvolucaoFormData) => {
    if (!prontuarioId) {
      toast.error("Nenhum prontuário selecionado. Selecione um prontuário primeiro.");
      return;
    }
    onSave(data);
    onOpenChange(false);
  };

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="max-w-3xl max-h-[90vh] overflow-y-auto">
        <DialogHeader>
          <DialogTitle>Nova Evolução Clínica</DialogTitle>
        </DialogHeader>

        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
          {prontuarioId && (
            <div className="p-3 bg-muted rounded-md text-sm">
              {isLoadingProntuario ? (
                <div className="flex items-center gap-2">
                  <Loader2 className="w-4 h-4 animate-spin" />
                  <span>Carregando informações do prontuário...</span>
                </div>
              ) : nomePaciente ? (
                <>
                  <strong>Paciente:</strong> {nomePaciente}
                  {prontuarioId && (
                    <span className="text-muted-foreground ml-2">
                      (Prontuário: {prontuarioId.substring(0, 8)}...)
                    </span>
                  )}
                </>
              ) : (
                <>
                  <strong>Prontuário:</strong> {prontuarioId?.substring(0, 8)}... (Paciente não encontrado)
                </>
              )}
            </div>
          )}
          
          <div className="grid grid-cols-1 gap-4">
            <div>
              <Label htmlFor="profissionalResponsavel">Profissional Responsável *</Label>
              <Input 
                id="profissionalResponsavel" 
                {...register("profissionalResponsavel")} 
                placeholder="Nome do profissional responsável"
              />
              {errors.profissionalResponsavel && (
                <p className="text-sm text-destructive">{errors.profissionalResponsavel.message}</p>
              )}
            </div>

            <div>
              <Label htmlFor="sintomas">Sintomas *</Label>
              <Textarea 
                id="sintomas" 
                {...register("sintomas")} 
                rows={3}
                placeholder="Descreva os sintomas apresentados pelo paciente"
              />
              {errors.sintomas && <p className="text-sm text-destructive">{errors.sintomas.message}</p>}
            </div>

            <div>
              <Label htmlFor="diagnostico">Diagnóstico *</Label>
              <Textarea 
                id="diagnostico" 
                {...register("diagnostico")} 
                rows={3}
                placeholder="Diagnóstico estabelecido"
              />
              {errors.diagnostico && <p className="text-sm text-destructive">{errors.diagnostico.message}</p>}
            </div>

            <div>
              <Label htmlFor="conduta">Conduta / Tratamento *</Label>
              <Textarea 
                id="conduta" 
                {...register("conduta")} 
                rows={4}
                placeholder="Conduta médica e tratamento prescrito"
              />
              {errors.conduta && <p className="text-sm text-destructive">{errors.conduta.message}</p>}
            </div>
          </div>

          <div className="flex justify-end gap-2 pt-4">
            <Button type="button" variant="outline" onClick={() => onOpenChange(false)}>
              Cancelar
            </Button>
            <Button type="submit" className="bg-gradient-primary">
              Registrar Evolução
            </Button>
          </div>
        </form>
      </DialogContent>
    </Dialog>
  );
}
