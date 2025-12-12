import { useEffect } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import * as z from "zod";
import { Dialog, DialogContent, DialogHeader, DialogTitle } from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { toast } from "sonner";

const exameSchema = z.object({
  paciente: z.string().min(1, "Paciente é obrigatório"),
  tipo: z.string().min(1, "Tipo de exame é obrigatório"),
  solicitante: z.string().min(1, "Médico solicitante é obrigatório"),
  laboratorio: z.string().min(1, "Laboratório é obrigatório"),
  prioridade: z.enum(["normal", "urgente"]),
  dataSolicitacao: z.string().min(1, "Data é obrigatória"),
});

type ExameFormData = z.infer<typeof exameSchema>;

interface ExameFormProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  onSave: (data: ExameFormData) => void;
}

export function ExameForm({ open, onOpenChange, onSave }: ExameFormProps) {
  const { register, handleSubmit, reset, setValue, formState: { errors } } = useForm<ExameFormData>({
    resolver: zodResolver(exameSchema),
    defaultValues: {
      prioridade: "normal",
      dataSolicitacao: new Date().toISOString().split('T')[0]
    }
  });

  useEffect(() => {
    if (!open) {
      reset({
        paciente: "",
        tipo: "",
        solicitante: "",
        laboratorio: "",
        prioridade: "normal",
        dataSolicitacao: new Date().toISOString().split('T')[0]
      });
    }
  }, [open, reset]);

  const onSubmit = (data: ExameFormData) => {
    onSave(data);
    toast.success("Exame solicitado com sucesso!");
    onOpenChange(false);
  };

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="max-w-2xl max-h-[90vh] overflow-y-auto">
        <DialogHeader>
          <DialogTitle>Nova Solicitação de Exame</DialogTitle>
        </DialogHeader>

        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
          <div className="grid grid-cols-2 gap-4">
            <div className="col-span-2">
              <Label htmlFor="paciente">Paciente *</Label>
              <Input id="paciente" {...register("paciente")} placeholder="Nome do paciente" />
              {errors.paciente && <p className="text-sm text-destructive">{errors.paciente.message}</p>}
            </div>

            <div className="col-span-2">
              <Label htmlFor="tipo">Tipo de Exame *</Label>
              <Input id="tipo" {...register("tipo")} placeholder="Ex: Hemograma Completo" />
              {errors.tipo && <p className="text-sm text-destructive">{errors.tipo.message}</p>}
            </div>

            <div>
              <Label htmlFor="solicitante">Médico Solicitante *</Label>
              <Input id="solicitante" {...register("solicitante")} />
              {errors.solicitante && <p className="text-sm text-destructive">{errors.solicitante.message}</p>}
            </div>

            <div>
              <Label htmlFor="laboratorio">Laboratório *</Label>
              <Input id="laboratorio" {...register("laboratorio")} />
              {errors.laboratorio && <p className="text-sm text-destructive">{errors.laboratorio.message}</p>}
            </div>

            <div>
              <Label htmlFor="prioridade">Prioridade *</Label>
              <Select onValueChange={(value: any) => setValue("prioridade", value)} defaultValue="normal">
                <SelectTrigger>
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="normal">Normal</SelectItem>
                  <SelectItem value="urgente">Urgente</SelectItem>
                </SelectContent>
              </Select>
              {errors.prioridade && <p className="text-sm text-destructive">{errors.prioridade.message}</p>}
            </div>

            <div>
              <Label htmlFor="dataSolicitacao">Data da Solicitação *</Label>
              <Input id="dataSolicitacao" type="date" {...register("dataSolicitacao")} />
              {errors.dataSolicitacao && <p className="text-sm text-destructive">{errors.dataSolicitacao.message}</p>}
            </div>
          </div>

          <div className="flex justify-end gap-2 pt-4">
            <Button type="button" variant="outline" onClick={() => onOpenChange(false)}>
              Cancelar
            </Button>
            <Button type="submit" className="bg-gradient-primary">
              Solicitar Exame
            </Button>
          </div>
        </form>
      </DialogContent>
    </Dialog>
  );
}
