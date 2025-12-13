// Localização: apresentacao-frontend/src/main/react/src/components/ExameFormWrapper.tsx

import { useEffect, useState } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import * as z from "zod";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { useListarPacientes } from "@/api/usePacientesApi";
import { useListarFuncionarios } from "@/api/useFuncionariosApi";
import { tiposExamesApi } from "@/api";
import { Loader2 } from "lucide-react";

const exameSchema = z.object({
  pacienteId: z.number().min(1, "Paciente é obrigatório"),
  tipoExame: z.string().min(1, "Tipo de exame é obrigatório"),
  medicoId: z.number().min(1, "Médico solicitante é obrigatório"),
  laboratorio: z.string().min(1, "Laboratório é obrigatório"),
  prioridade: z.enum(["normal", "urgente"]),
  dataHora: z.string().min(1, "Data e hora é obrigatória"),
});

type ExameFormData = z.infer<typeof exameSchema>;

interface ExameFormWrapperProps {
  onSave: (data: ExameFormData & { responsavelId?: number }) => Promise<any> | void;
  initialData?: Partial<ExameFormData> | null;
}

/**
 * Wrapper do ExameForm para uso standalone (sem Dialog).
 * Adaptado para funcionar na página de agendamentos.
 */
export function ExameFormWrapper({ onSave, initialData }: ExameFormWrapperProps) {
  const { register, handleSubmit, reset, setValue, formState: { errors, isSubmitting } } = useForm<ExameFormData>({
    resolver: zodResolver(exameSchema),
    defaultValues: {
      prioridade: "normal",
      dataHora: new Date().toISOString().slice(0, 16), // datetime-local value
      laboratorio: "",
    }
  });

  const { data: pacientes = [] } = useListarPacientes();
  const { data: funcionarios = [] } = useListarFuncionarios();

  // Carregar tipos de exames
  const [tipos, setTipos] = useState<any[]>((window as any)._tiposExamesCache || []);
  useEffect(() => {
    let mounted = true;
    tiposExamesApi.listar().then(data => { 
      if (mounted) { 
        setTipos(data); 
        (window as any)._tiposExamesCache = data; 
      } 
    });
    return () => { mounted = false; };
  }, []);

  // Sincronizar initialData quando fornecido (edição)
  useEffect(() => {
    if (initialData) {
      if (initialData.pacienteId !== undefined) setValue('pacienteId', initialData.pacienteId);
      if (initialData.tipoExame !== undefined) setValue('tipoExame', initialData.tipoExame);
      if (initialData.medicoId !== undefined) setValue('medicoId', initialData.medicoId);
      if (initialData.laboratorio !== undefined) setValue('laboratorio', initialData.laboratorio);
      if (initialData.prioridade !== undefined) setValue('prioridade', initialData.prioridade);
      if (initialData.dataHora !== undefined) {
        // Backend expects YYYY-MM-DDTHH:mm:ss; input expects YYYY-MM-DDTHH:mm
        const v = initialData.dataHora.length === 19 ? initialData.dataHora.slice(0, 16) : initialData.dataHora;
        setValue('dataHora', v);
      }
    }
  }, [initialData, setValue]);

  const onSubmitForm = async (data: ExameFormData) => {
    await onSave(data);
    // Reset após sucesso
    reset({
      prioridade: "normal",
      dataHora: new Date().toISOString().slice(0, 16),
      laboratorio: "",
    });
  };

  return (
    <form onSubmit={handleSubmit(onSubmitForm)} className="space-y-4">
      <div className="grid grid-cols-2 gap-4">
        {/* Paciente */}
        <div className="col-span-2">
          <Label htmlFor="paciente">Paciente *</Label>
          <Select 
            onValueChange={(v: any) => setValue('pacienteId', Number(v))} 
            defaultValue={initialData?.pacienteId ? String(initialData.pacienteId) : undefined}
          >
            <SelectTrigger>
              <SelectValue placeholder="Selecione o paciente" />
            </SelectTrigger>
            <SelectContent>
              {pacientes.map(p => (
                <SelectItem key={p.id} value={String(p.id)}>{p.name}</SelectItem>
              ))}
            </SelectContent>
          </Select>
          {errors.pacienteId && <p className="text-sm text-destructive mt-1">{errors.pacienteId.message}</p>}
        </div>

        {/* Tipo de Exame */}
        <div className="col-span-2">
          <Label htmlFor="tipoExame">Tipo de Exame *</Label>
          <Select 
            onValueChange={(v: any) => setValue('tipoExame', v)} 
            defaultValue={initialData?.tipoExame}
          >
            <SelectTrigger>
              <SelectValue placeholder="Selecione o tipo de exame" />
            </SelectTrigger>
            <SelectContent>
              {((window as any)._tiposExamesCache || tipos).map((t: any) => (
                <SelectItem key={t.id} value={t.codigo || t.descricao || String(t.id)}>
                  {(t.codigo ? t.codigo + ' - ' : '') + (t.descricao || t.codigo)}
                </SelectItem>
              ))}
            </SelectContent>
          </Select>
          {errors.tipoExame && <p className="text-sm text-destructive mt-1">{errors.tipoExame.message}</p>}
        </div>

        {/* Médico Solicitante */}
        <div>
          <Label htmlFor="medicoId">Médico Solicitante *</Label>
          <Select 
            onValueChange={(v: any) => setValue('medicoId', Number(v))} 
            defaultValue={initialData?.medicoId ? String(initialData.medicoId) : undefined}
          >
            <SelectTrigger>
              <SelectValue placeholder="Selecione o médico" />
            </SelectTrigger>
            <SelectContent>
              {funcionarios.map(f => (
                <SelectItem key={f.id} value={String(f.id)}>{f.nome}</SelectItem>
              ))}
            </SelectContent>
          </Select>
          {errors.medicoId && <p className="text-sm text-destructive mt-1">{errors.medicoId.message}</p>}
        </div>

        {/* Laboratório */}
        <div>
          <Label htmlFor="laboratorio">Laboratório *</Label>
          <Input id="laboratorio" {...register("laboratorio")} placeholder="Ex: Lab São Paulo" />
          {errors.laboratorio && <p className="text-sm text-destructive mt-1">{errors.laboratorio.message}</p>}
        </div>

        {/* Prioridade */}
        <div>
          <Label htmlFor="prioridade">Prioridade *</Label>
          <Select 
            onValueChange={(value: any) => setValue("prioridade", value)} 
            defaultValue={initialData?.prioridade ?? "normal"}
          >
            <SelectTrigger>
              <SelectValue />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="normal">Normal</SelectItem>
              <SelectItem value="urgente">Urgente</SelectItem>
            </SelectContent>
          </Select>
          {errors.prioridade && <p className="text-sm text-destructive mt-1">{errors.prioridade.message}</p>}
        </div>

        {/* Data e Hora */}
        <div>
          <Label htmlFor="dataHora">Data e Hora *</Label>
          <Input 
            id="dataHora" 
            type="datetime-local" 
            {...register("dataHora")} 
          />
          {errors.dataHora && <p className="text-sm text-destructive mt-1">{errors.dataHora.message}</p>}
        </div>
      </div>

      {/* Botão Submit */}
      <div className="flex justify-end pt-4">
        <Button type="submit" disabled={isSubmitting} className="w-full sm:w-auto">
          {isSubmitting ? (
            <>
              <Loader2 className="mr-2 h-4 w-4 animate-spin" />
              Agendando...
            </>
          ) : (
            "Agendar Exame"
          )}
        </Button>
      </div>
    </form>
  );
}
