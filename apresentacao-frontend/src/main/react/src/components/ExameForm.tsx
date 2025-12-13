import { useEffect, useState } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import * as z from "zod";
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogDescription } from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { toast } from "sonner";
import { useListarPacientes } from "@/api/usePacientesApi";
import { useListarFuncionarios } from "@/api/useFuncionariosApi";
import { tiposExamesApi } from "@/api";

const exameSchema = z.object({
  pacienteId: z.number().min(1, "Paciente é obrigatório"),
  tipoExame: z.string().min(1, "Tipo de exame é obrigatório"),
  medicoId: z.number().min(1, "Médico solicitante é obrigatório"),
  laboratorio: z.string().min(1, "Laboratório é obrigatório"),
  prioridade: z.enum(["normal", "urgente"]),
  dataHora: z.string().min(1, "Data e hora é obrigatória"),
});

type ExameFormData = z.infer<typeof exameSchema>;

interface ExameFormProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  onSave: (data: ExameFormData) => Promise<any> | void;
  initialData?: Partial<ExameFormData> | null;
}

export function ExameForm({ open, onOpenChange, onSave, initialData }: ExameFormProps) {
  const { register, handleSubmit, reset, setValue, setError, formState: { errors } } = useForm<ExameFormData>({
    resolver: zodResolver(exameSchema),
    defaultValues: {
      prioridade: "normal",
      dataHora: new Date().toISOString().slice(0,16), // datetime-local value
      laboratorio: "",
    }
  });

  const { data: pacientes = [] } = useListarPacientes();
  const { data: funcionarios = [] } = useListarFuncionarios();

  // carregar tipos de exames para seleção
  const [tipos, setTipos] = useState<any[]>((window as any)._tiposExamesCache || []);
  useEffect(() => {
    let mounted = true;
    tiposExamesApi.listar().then(data => { if(mounted){ setTipos(data); (window as any)._tiposExamesCache = data; } });
    return () => { mounted = false; };
  }, []);

  useEffect(() => {
    if (!open) {
      reset({
        pacienteId: 0,
        tipoExame: "",
        medicoId: 0,
        laboratorio: "",
        prioridade: "normal",
        dataHora: new Date().toISOString().slice(0,16),
      });
    }
  }, [open, reset]);

  // sincronizar initialData quando fornecido (edição)
  useEffect(() => {
    if (initialData) {
      if (initialData.pacienteId !== undefined) setValue('pacienteId', initialData.pacienteId);
      if (initialData.tipoExame !== undefined) setValue('tipoExame', initialData.tipoExame);
      if (initialData.medicoId !== undefined) setValue('medicoId', initialData.medicoId);
      if (initialData.laboratorio !== undefined) setValue('laboratorio', initialData.laboratorio);
      if (initialData.prioridade !== undefined) setValue('prioridade', initialData.prioridade);
      if (initialData.dataHora !== undefined) {
        // backend expects YYYY-MM-DDTHH:mm:ss; input expects YYYY-MM-DDTHH:mm
        const v = initialData.dataHora.length === 19 ? initialData.dataHora.slice(0,16) : initialData.dataHora;
        setValue('dataHora', v);
      }
    }
  }, [initialData, setValue]);

  const onSubmit = async (data: ExameFormData) => {
    // Garantir que dataHora esteja no formato YYYY-MM-DDTHH:mm:ss (sem timezone)
    const formatDataHora = (v: string) => {
      if (!v) return v;
      // datetime-local typically returns YYYY-MM-DDTHH:mm
      if (/^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}$/.test(v)) return `${v}:00`;
      if (/^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}$/.test(v)) return v;
      // fallback: try to truncate or append seconds
      return v.length >= 19 ? v.slice(0, 19) : `${v}:00`;
    };

    const payload: ExameFormData = {
      ...data,
      dataHora: formatDataHora(data.dataHora),
    };
    try {
      await onSave(payload);
      toast.success(initialData ? "Exame atualizado com sucesso!" : "Exame solicitado com sucesso!");
      reset();
      onOpenChange(false);
    } catch (e: any) {
      const payloadErr = e?.response?.data;
      if (payloadErr?.errors && typeof payloadErr.errors === 'object') {
        Object.entries(payloadErr.errors).forEach(([field, message]) => {
          try { setError(field as any, { type: 'server', message: String(message) }); } catch { /* ignore */ }
        });
        return;
      }
      toast.error(payloadErr?.message ?? e?.message ?? 'Erro ao salvar exame');
      throw e;
    }
  };

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="max-w-2xl max-h-[90vh] overflow-y-auto">
        <DialogHeader>
          <DialogTitle>{initialData ? "Editar Solicitação de Exame" : "Nova Solicitação de Exame"}</DialogTitle>
          <DialogDescription>Preencha os campos para solicitar o exame.</DialogDescription>
        </DialogHeader>

        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
          <div className="grid grid-cols-2 gap-4">
            <div className="col-span-2">
              <Label htmlFor="paciente">Paciente *</Label>
              <Select onValueChange={(v: any) => setValue('pacienteId', Number(v))} defaultValue={initialData?.pacienteId ? String(initialData.pacienteId) : undefined}>
                <SelectTrigger>
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  {pacientes.map(p => (
                    <SelectItem key={p.id} value={String(p.id)}>{p.name}</SelectItem>
                  ))}
                </SelectContent>
              </Select>
              {errors.pacienteId && <p className="text-sm text-destructive">{errors.pacienteId.message}</p>}
            </div>

            <div className="col-span-2">
              <Label htmlFor="tipoExame">Tipo de Exame *</Label>
              <Select onValueChange={(v: any) => setValue('tipoExame', v)} defaultValue={initialData?.tipoExame}>
                <SelectTrigger>
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  {((window as any)._tiposExamesCache || tipos).map((t: any) => (
                    <SelectItem key={t.id} value={t.codigo || t.descricao || String(t.id)}>{(t.codigo ? t.codigo + ' - ' : '') + (t.descricao || t.codigo)}</SelectItem>
                  ))}
                </SelectContent>
              </Select>
              {errors.tipoExame && <p className="text-sm text-destructive">{errors.tipoExame.message}</p>}
            </div>

            <div>
              <Label htmlFor="medicoId">Médico Solicitante *</Label>
              <Select onValueChange={(v: any) => setValue('medicoId', Number(v))} defaultValue={initialData?.medicoId ? String(initialData.medicoId) : undefined}>
                <SelectTrigger>
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  {funcionarios.map(f => (
                    <SelectItem key={f.id} value={String(f.id)}>{f.nome}</SelectItem>
                  ))}
                </SelectContent>
              </Select>
              {errors.medicoId && <p className="text-sm text-destructive">{errors.medicoId.message}</p>}
            </div>

            <div>
              <Label htmlFor="laboratorio">Laboratório *</Label>
              <Input id="laboratorio" {...register("laboratorio")} />
              {errors.laboratorio && <p className="text-sm text-destructive">{errors.laboratorio.message}</p>}
            </div>

            <div>
              <Label htmlFor="prioridade">Prioridade *</Label>
              <Select onValueChange={(value: any) => setValue("prioridade", value)} defaultValue={initialData?.prioridade ?? "normal"}>
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
              <Label htmlFor="dataHora">Data e Hora *</Label>
              <Input id="dataHora" type="datetime-local" {...register("dataHora")} />
              {errors.dataHora && <p className="text-sm text-destructive">{errors.dataHora.message}</p>}
            </div>
          </div>

          <div className="flex justify-end gap-2 pt-4">
            <Button type="button" variant="outline" onClick={() => onOpenChange(false)}>
              Cancelar
            </Button>
            <Button type="submit" className="bg-gradient-primary">
              {initialData ? "Salvar" : "Solicitar Exame"}
            </Button>
          </div>
        </form>
      </DialogContent>
    </Dialog>
  );
}