import { useEffect, useMemo, useState } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import * as z from "zod";
import { Dialog, DialogContent, DialogHeader, DialogTitle } from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import { Label } from "@/components/ui/label";
import { Textarea } from "@/components/ui/textarea";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { toast } from "sonner";
import { useListarProntuarios, useObterProntuario } from "@/api/useProntuariosApi";
import { useListarPacientes } from "@/api/usePacientesApi";
import { useListarFuncionarios } from "@/api/useFuncionariosApi";
import { Loader2 } from "lucide-react";

const evolucaoSchema = z.object({
  prontuarioId: z.string().min(1, "Selecione um prontuário"),
  sintomas: z.string().min(3, "Descreva os sintomas principais"),
  diagnostico: z.string().min(3, "Diagnóstico é obrigatório"),
  conduta: z.string().min(3, "Conduta é obrigatória"),
  profissionalResponsavel: z.string().min(1, "Selecione um profissional"),
  anexosReferenciados: z.array(z.string()).optional().default([]),
});

type EvolucaoFormData = z.infer<typeof evolucaoSchema>;

interface EvolucaoFormProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  onSave: (data: EvolucaoFormData) => void;
  prontuarioIdInicial?: string | null;
}

export function EvolucaoForm({ open, onOpenChange, onSave, prontuarioIdInicial }: EvolucaoFormProps) {
  const { data: funcionarios = [], isLoading: isLoadingFuncionarios } = useListarFuncionarios();
  const { data: prontuarios = [], isLoading: isLoadingProntuarios } = useListarProntuarios();
  const { data: pacientes = [] } = useListarPacientes();

  // Filtrar apenas funcionários ativos
  const funcionariosAtivos = funcionarios.filter(f => f.status === "ATIVO");

  // Filtrar apenas prontuários ativos
  const prontuariosAtivos = useMemo(() => {
    return prontuarios.filter(p => p.status === "ATIVO");
  }, [prontuarios]);

  // Criar mapa de pacientes para exibir nome
  const pacientesMap = useMemo(() => {
    const map = new Map<number, { nome: string; cpf: string }>();
    pacientes.forEach(p => {
      map.set(p.id, { nome: p.name, cpf: p.cpf });
    });
    return map;
  }, [pacientes]);

  const { register, handleSubmit, reset, formState: { errors }, setValue, watch } = useForm<EvolucaoFormData>({
    resolver: zodResolver(evolucaoSchema),
    defaultValues: {
      prontuarioId: prontuarioIdInicial || "",
      sintomas: "",
      diagnostico: "",
      conduta: "",
      profissionalResponsavel: "",
      anexosReferenciados: []
    }
  });

  const prontuarioIdSelecionado = watch("prontuarioId");
  const profissionalResponsavel = watch("profissionalResponsavel");

  // Buscar prontuário selecionado para exibir informações
  const prontuarioSelecionado = useMemo(() => {
    return prontuariosAtivos.find(p => p.id === prontuarioIdSelecionado);
  }, [prontuariosAtivos, prontuarioIdSelecionado]);

  // Preencher prontuarioId inicial quando o form abrir
  useEffect(() => {
    if (open && prontuarioIdInicial) {
      setValue("prontuarioId", prontuarioIdInicial);
    }
  }, [open, prontuarioIdInicial, setValue]);

  useEffect(() => {
    if (!open) {
      reset({
        prontuarioId: "",
        sintomas: "",
        diagnostico: "",
        conduta: "",
        profissionalResponsavel: "",
        anexosReferenciados: []
      });
    }
  }, [open, reset]);

  const onSubmit = (data: EvolucaoFormData) => {
    if (!data.prontuarioId) {
      toast.error("Selecione um prontuário primeiro.");
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
          <div>
            <Label htmlFor="prontuarioId">Prontuário *</Label>
            {isLoadingProntuarios ? (
              <div className="flex items-center gap-2">
                <Loader2 className="w-4 h-4 animate-spin" />
                <span className="text-sm text-muted-foreground">Carregando prontuários...</span>
              </div>
            ) : (
              <Select
                value={prontuarioIdSelecionado}
                onValueChange={(value) => setValue("prontuarioId", value)}
              >
                <SelectTrigger>
                  <SelectValue placeholder="Selecione um prontuário" />
                </SelectTrigger>
                <SelectContent>
                  {prontuariosAtivos.length === 0 ? (
                    <SelectItem value="empty" disabled>
                      Nenhum prontuário ativo encontrado
                    </SelectItem>
                  ) : (
                    prontuariosAtivos.map((prontuario) => {
                      const pacienteIdNum = parseInt(prontuario.pacienteId);
                      const paciente = pacientesMap.get(pacienteIdNum);
                      const nomePaciente = paciente?.nome || `Paciente ID: ${prontuario.pacienteId}`;
                      return (
                        <SelectItem key={prontuario.id} value={prontuario.id}>
                          {nomePaciente} - Prontuário: {prontuario.id.substring(0, 8)}...
                        </SelectItem>
                      );
                    })
                  )}
                </SelectContent>
              </Select>
            )}
            {errors.prontuarioId && (
              <p className="text-sm text-destructive">{errors.prontuarioId.message}</p>
            )}
            {prontuarioSelecionado && (
              <div className="p-2 bg-muted rounded-md text-sm mt-2">
                <strong>Prontuário selecionado:</strong> {prontuarioSelecionado.id.substring(0, 8)}...
                {(() => {
                  const pacienteIdNum = parseInt(prontuarioSelecionado.pacienteId);
                  const paciente = pacientesMap.get(pacienteIdNum);
                  return paciente ? (
                    <span className="text-muted-foreground ml-2">
                      (Paciente: {paciente.nome} - CPF: {paciente.cpf})
                    </span>
                  ) : null;
                })()}
              </div>
            )}
          </div>

          <div className="grid grid-cols-1 gap-4">
            <div>
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
