import { useEffect } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import * as z from "zod";
import { Dialog, DialogContent, DialogHeader, DialogTitle } from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Textarea } from "@/components/ui/textarea";
import { toast } from "sonner";

const evolucaoSchema = z.object({
  pacienteId: z.string().min(1, "Selecione um paciente"),
  medico: z.string().min(3, "Nome do médico é obrigatório"),
  especialidade: z.string().min(1, "Especialidade é obrigatória"),
  queixa: z.string().min(3, "Descreva a queixa principal"),
  diagnostico: z.string().min(3, "Diagnóstico é obrigatório"),
  conduta: z.string().min(3, "Conduta é obrigatória"),
  data: z.string().min(1, "Data é obrigatória"),
});

type EvolucaoFormData = z.infer<typeof evolucaoSchema>;

interface EvolucaoFormProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  onSave: (data: EvolucaoFormData) => void;
}

export function EvolucaoForm({ open, onOpenChange, onSave }: EvolucaoFormProps) {
  const { register, handleSubmit, reset, formState: { errors } } = useForm<EvolucaoFormData>({
    resolver: zodResolver(evolucaoSchema),
    defaultValues: {
      data: new Date().toISOString().split('T')[0]
    }
  });

  useEffect(() => {
    if (!open) {
      reset({
        pacienteId: "",
        medico: "",
        especialidade: "",
        queixa: "",
        diagnostico: "",
        conduta: "",
        data: new Date().toISOString().split('T')[0]
      });
    }
  }, [open, reset]);

  const onSubmit = (data: EvolucaoFormData) => {
    onSave(data);
    toast.success("Evolução registrada com sucesso!");
    onOpenChange(false);
  };

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="max-w-3xl max-h-[90vh] overflow-y-auto">
        <DialogHeader>
          <DialogTitle>Nova Evolução Clínica</DialogTitle>
        </DialogHeader>

        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
          <div className="grid grid-cols-2 gap-4">
            <div>
              <Label htmlFor="pacienteId">Paciente *</Label>
              <Input id="pacienteId" {...register("pacienteId")} placeholder="ID ou nome do paciente" />
              {errors.pacienteId && <p className="text-sm text-destructive">{errors.pacienteId.message}</p>}
            </div>

            <div>
              <Label htmlFor="data">Data *</Label>
              <Input id="data" type="date" {...register("data")} />
              {errors.data && <p className="text-sm text-destructive">{errors.data.message}</p>}
            </div>

            <div>
              <Label htmlFor="medico">Médico Responsável *</Label>
              <Input id="medico" {...register("medico")} />
              {errors.medico && <p className="text-sm text-destructive">{errors.medico.message}</p>}
            </div>

            <div>
              <Label htmlFor="especialidade">Especialidade *</Label>
              <Input id="especialidade" {...register("especialidade")} />
              {errors.especialidade && <p className="text-sm text-destructive">{errors.especialidade.message}</p>}
            </div>

            <div className="col-span-2">
              <Label htmlFor="queixa">Queixa Principal *</Label>
              <Textarea id="queixa" {...register("queixa")} rows={3} />
              {errors.queixa && <p className="text-sm text-destructive">{errors.queixa.message}</p>}
            </div>

            <div className="col-span-2">
              <Label htmlFor="diagnostico">Diagnóstico *</Label>
              <Textarea id="diagnostico" {...register("diagnostico")} rows={3} />
              {errors.diagnostico && <p className="text-sm text-destructive">{errors.diagnostico.message}</p>}
            </div>

            <div className="col-span-2">
              <Label htmlFor="conduta">Conduta / Tratamento *</Label>
              <Textarea id="conduta" {...register("conduta")} rows={4} />
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
