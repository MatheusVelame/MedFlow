import { useEffect } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import * as z from "zod";
import { Dialog, DialogContent, DialogHeader, DialogTitle } from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import { Label } from "@/components/ui/label";
import { Textarea } from "@/components/ui/textarea";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { useListarPacientes } from "@/api/usePacientesApi";
import { useListarFuncionarios } from "@/api/useFuncionariosApi";
import { Loader2 } from "lucide-react";

const prontuarioSchema = z.object({
  pacienteId: z.string().min(1, "Selecione um paciente"),
  profissionalResponsavel: z.string().min(1, "Selecione um profissional"),
  observacoesIniciais: z.string().optional(),
});

type ProntuarioFormData = z.infer<typeof prontuarioSchema>;

interface ProntuarioFormProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  onSave: (data: ProntuarioFormData) => void;
}

export function ProntuarioForm({ open, onOpenChange, onSave }: ProntuarioFormProps) {
  const { data: pacientes = [], isLoading: isLoadingPacientes } = useListarPacientes();
  const { data: funcionarios = [], isLoading: isLoadingFuncionarios } = useListarFuncionarios();

  // Filtrar apenas funcionários ativos
  const funcionariosAtivos = funcionarios.filter(f => f.status === "ATIVO");

  const { register, handleSubmit, reset, formState: { errors }, setValue, watch } = useForm<ProntuarioFormData>({
    resolver: zodResolver(prontuarioSchema),
    defaultValues: {
      pacienteId: "",
      profissionalResponsavel: "",
      observacoesIniciais: "",
    }
  });

  const pacienteId = watch("pacienteId");
  const profissionalResponsavel = watch("profissionalResponsavel");

  useEffect(() => {
    if (!open) {
      reset({
        pacienteId: "",
        profissionalResponsavel: "",
        observacoesIniciais: "",
      });
    }
  }, [open, reset]);

  const onSubmit = (data: ProntuarioFormData) => {
    onSave(data);
    onOpenChange(false);
  };

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="max-w-2xl max-h-[90vh] overflow-y-auto">
        <DialogHeader>
          <DialogTitle>Novo Prontuário</DialogTitle>
        </DialogHeader>

        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
          <div className="space-y-2">
            <Label htmlFor="pacienteId">Paciente *</Label>
            {isLoadingPacientes ? (
              <div className="flex items-center gap-2">
                <Loader2 className="w-4 h-4 animate-spin" />
                <span className="text-sm text-muted-foreground">Carregando pacientes...</span>
              </div>
            ) : (
              <Select
                value={pacienteId}
                onValueChange={(value) => setValue("pacienteId", value)}
              >
                <SelectTrigger>
                  <SelectValue placeholder="Selecione um paciente" />
                </SelectTrigger>
                <SelectContent>
                  {pacientes.map((paciente) => (
                    <SelectItem key={paciente.id} value={String(paciente.id)}>
                      {paciente.name} - CPF: {paciente.cpf}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            )}
            {errors.pacienteId && (
              <p className="text-sm text-destructive">{errors.pacienteId.message}</p>
            )}
          </div>

          <div className="space-y-2">
            <Label htmlFor="profissionalResponsavel">Profissional Responsável *</Label>
            {isLoadingFuncionarios ? (
              <div className="flex items-center gap-2">
                <Loader2 className="w-4 h-4 animate-spin" />
                <span className="text-sm text-muted-foreground">Carregando profissionais...</span>
              </div>
            ) : (
              <Select
                value={profissionalResponsavel}
                onValueChange={(value) => setValue("profissionalResponsavel", value)}
              >
                <SelectTrigger>
                  <SelectValue placeholder="Selecione um profissional" />
                </SelectTrigger>
                <SelectContent>
                  {funcionariosAtivos.map((funcionario) => (
                    <SelectItem key={funcionario.id} value={funcionario.nome}>
                      {funcionario.nome} - {funcionario.funcao}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            )}
            {errors.profissionalResponsavel && (
              <p className="text-sm text-destructive">{errors.profissionalResponsavel.message}</p>
            )}
          </div>

          <div className="space-y-2">
            <Label htmlFor="observacoesIniciais">Observações Iniciais</Label>
            <Textarea 
              id="observacoesIniciais" 
              {...register("observacoesIniciais")} 
              rows={4}
              placeholder="Observações iniciais sobre o paciente (opcional)"
            />
            {errors.observacoesIniciais && (
              <p className="text-sm text-destructive">{errors.observacoesIniciais.message}</p>
            )}
          </div>

          <div className="flex justify-end gap-2 pt-4">
            <Button type="button" variant="outline" onClick={() => onOpenChange(false)}>
              Cancelar
            </Button>
            <Button type="submit" className="bg-gradient-primary">
              Criar Prontuário
            </Button>
          </div>
        </form>
      </DialogContent>
    </Dialog>
  );
}

